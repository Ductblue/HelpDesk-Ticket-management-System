
package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.response.TicketHistoryResponseDTO;
import com.harsh.helpdesk.entity.Role;
import com.harsh.helpdesk.entity.Ticket;
import com.harsh.helpdesk.entity.User;
import com.harsh.helpdesk.exception.ResourceNotFoundException;
import com.harsh.helpdesk.mapper.TicketHistoryMapper;
import com.harsh.helpdesk.repository.TicketHistoryRepo;
import com.harsh.helpdesk.repository.TicketRepo;
import com.harsh.helpdesk.repository.UserRepo;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketHistoryServiceImpl implements TicketHistoryService {

    private final TicketHistoryRepo ticketHistoryRepository;
    private final TicketRepo ticketRepository;
    private final UserRepo userRepository;

    public TicketHistoryServiceImpl(
            TicketHistoryRepo ticketHistoryRepository,
            TicketRepo ticketRepository,
            UserRepo userRepository) {

        this.ticketHistoryRepository = ticketHistoryRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketHistoryResponseDTO> getHistoryByTicket(Long ticketId, String email) {

        User user=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
        Ticket ticket=ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found with id: "+ticketId));

        checkTicketAccess(ticket, user);

        return ticketHistoryRepository.findByTicketId(ticketId)
                .stream()
                .map(TicketHistoryMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketHistoryResponseDTO> getMyHistory(String email) {

        User user=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));

        List<Ticket> tickets;

        if(user.getRole()== Role.ADMIN){
            tickets=ticketRepository.findAll();
        } else if (user.getRole()==Role.USER) {
            tickets=ticketRepository.findByCreatedById(user.getId());
        }
        else {
            tickets=ticketRepository.findByAssignedToId(user.getId());
        }

        return tickets.stream()
                .flatMap(ticket->ticketHistoryRepository.findByTicketId(ticket.getId()).stream())
                .map(TicketHistoryMapper::toResponseDTO)
                .toList();
    }

    // Check whether logged-in user can access this ticket
    private void checkTicketAccess(Ticket ticket, User user) {

        if (user.getRole() == Role.ADMIN) {
            return;
        }

        boolean isCreator = ticket.getCreatedBy().getId()
                .equals(user.getId());

        boolean isAssignedAgent = ticket.getAssignedTo() != null
                && ticket.getAssignedTo().getId().equals(user.getId());

        if (!isCreator && !isAssignedAgent) {
            throw new AccessDeniedException(
                    "You are not allowed to access this ticket history");
        }
    }
}