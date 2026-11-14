
package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.request.CommentRequestDTO;
import com.harsh.helpdesk.dto.response.CommentResponseDTO;
import com.harsh.helpdesk.entity.Comment;
import com.harsh.helpdesk.entity.Role;
import com.harsh.helpdesk.entity.Ticket;
import com.harsh.helpdesk.entity.User;
import com.harsh.helpdesk.exception.ResourceNotFoundException;
import com.harsh.helpdesk.mapper.CommentMapper;
import com.harsh.helpdesk.repository.CommentRepo;
import com.harsh.helpdesk.repository.TicketRepo;
import com.harsh.helpdesk.repository.UserRepo;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepository;
    private final TicketRepo ticketRepository;
    private final UserRepo userRepository;

    public CommentServiceImpl(
            CommentRepo commentRepository,
            TicketRepo ticketRepository,
            UserRepo userRepository) {

        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentResponseDTO addComment(Long ticketId, String email, CommentRequestDTO request) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify that the user can access this ticket
        checkTicketAccess(ticket, user);

        Comment comment = CommentMapper.toEntity(request, ticket, user);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toResponseDTO(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByTicket(Long ticketId, String email) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify that the user can access this ticket
        checkTicketAccess(ticket, user);

        return commentRepository.findByTicketId(ticketId)
                .stream()
                .map(CommentMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getMyComments(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return commentRepository.findByUserId(user.getId())
                .stream()
                .map(CommentMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Only the author or ADMIN can delete
        Boolean isAuthor=comment.getUser().getId().equals(user.getId());
        Boolean isAdmin=user.getRole()== Role.ADMIN;

        if(!isAdmin && !isAuthor){
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }

//    private User findUserByEmail(String email) {
//        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));
//    }
//
//    private Ticket findTicketById(Long ticketId) {
//        return ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found"));
//    }

    private void checkTicketAccess(Ticket ticket, User user) {

        if (user.getRole() == Role.ADMIN) {
            return;
        }

        boolean isCreator = ticket.getCreatedBy().getId().equals(user.getId());

        boolean isAssignedAgent = ticket.getAssignedTo() != null && ticket.getAssignedTo().getId().equals(user.getId());

        if (!isCreator && !isAssignedAgent) {
            throw new org.springframework.security.access.AccessDeniedException("You are not allowed to access this ticket");
        }
    }
}
