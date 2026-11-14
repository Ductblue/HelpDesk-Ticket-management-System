
package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.request.TicketRequestDTO;
import com.harsh.helpdesk.dto.response.TicketResponseDTO;
import com.harsh.helpdesk.entity.*;
import com.harsh.helpdesk.exception.ResourceNotFoundException;
import com.harsh.helpdesk.mapper.TicketMapper;
import com.harsh.helpdesk.repository.TicketRepo;
import com.harsh.helpdesk.repository.UserRepo;
import com.harsh.helpdesk.repository.TicketHistoryRepo;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepo ticketRepository;
    private final UserRepo userRepository;
    private final TicketHistoryRepo ticketHistoryRepository;

    public TicketServiceImpl(
            TicketRepo ticketRepository,
            UserRepo userRepository,
            TicketHistoryRepo ticketHistoryRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
    }

    @Override
    @Transactional
    public TicketResponseDTO createTicket(TicketRequestDTO request, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));

        Ticket ticket = TicketMapper.toEntity(request);

        ticket.setCreatedBy(user);
        ticket.setStatus(TicketStatus.OPEN);

        if (ticket.getPriority() == null) {
            ticket.setPriority(TicketPriority.MEDIUM);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        return TicketMapper.toResponseDTO(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(Long ticketId, String email) {

        User currentUser=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found"));

        // ADMIN can access every ticket
        if(currentUser.getRole()==Role.ADMIN){
            return TicketMapper.toResponseDTO(ticket);
        }

        // USER can access only tickets they created
        if(currentUser.getRole()==Role.USER && ticket.getCreatedBy().getId().equals(currentUser.getId())){
            return TicketMapper.toResponseDTO(ticket);
        }

        // SUPPORT_AGENT can access only tickets assigned to them
        if(currentUser.getRole()==Role.SUPPORT_AGENT && ticket.getAssignedTo()!=null && ticket.getAssignedTo().getId().equals(currentUser.getId())){
            return TicketMapper.toResponseDTO(ticket);
        }

        throw new org.springframework.security.access.AccessDeniedException("You are not allowed to access this ticket");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getAllTickets(String email, TicketStatus status, TicketPriority priority, Pageable pageable) {

        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPPORT_AGENT) {
            throw new AccessDeniedException("You are not allowed to view all tickets");
        }

        boolean isAdmin = user.getRole() == Role.ADMIN;

        Page<Ticket> tickets = ticketRepository.findFilteredTickets(user.getId(), isAdmin, !isAdmin, status, priority, pageable);

        return tickets.map(TicketMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getMyTickets(String email) {
        User currentUser=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));

        List<Ticket> tickets;

        if(currentUser.getRole()==Role.ADMIN){
            tickets=ticketRepository.findAll();
        } else if (currentUser.getRole()==Role.USER) {
            tickets=ticketRepository.findByCreatedById(currentUser.getId());
        }
        else {
            tickets=ticketRepository.findByAssignedToId(currentUser.getId());
        }

        return tickets.stream().map(TicketMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public TicketResponseDTO assignTicket(Long ticketId, Long agentId, String email) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found"));

        User currentUser = userRepository.findById(agentId).orElseThrow(()->new ResourceNotFoundException("Agent not found"));

        if(currentUser.getRole()!=Role.ADMIN){
            throw new AccessDeniedException("Only ADMIN can assign tickets");
        }

        User agent=userRepository.findById(agentId).orElseThrow(()->new UsernameNotFoundException("Agent not found"));

        if(agent.getRole()!=Role.SUPPORT_AGENT){
            throw new IllegalArgumentException("Selected user is not a support agent");
        }

        ticket.setAssignedTo(agent);

        return TicketMapper.toResponseDTO(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public TicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus status, String email) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found"));

        User currentUser = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));

        checkAgentAccess(ticket, currentUser);

        TicketStatus oldStatus = ticket.getStatus();

        if (oldStatus != status) {
            ticket.setStatus(status);
            TicketHistory history = TicketHistory.builder()
                    .oldStatus(oldStatus)
                    .newStatus(status)
                    .ticket(ticket)
                    .changedBy(currentUser)
                    .build();
            ticketHistoryRepository.save(history);
        }
        return TicketMapper.toResponseDTO(ticket);
    }

    private void checkAgentAccess(Ticket ticket, User user) {

        if (user.getRole() == Role.ADMIN) {
            return;
        }

        boolean isAssignedAgent =
                user.getRole() == Role.SUPPORT_AGENT
                        && ticket.getAssignedTo() != null
                        && ticket.getAssignedTo().getId().equals(user.getId());

        if (!isAssignedAgent) {
            throw new AccessDeniedException("Only ADMIN or the assigned support agent can modify this ticket");
        }
    }

    @Override
    @Transactional
    public TicketResponseDTO updateTicketPriority(Long ticketId, TicketPriority priority, String email) {

        User currentUser=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found"));

        checkAgentAccess(ticket, currentUser);

        ticket.setPriority(priority);

        return TicketMapper.toResponseDTO(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public void deleteTicket(Long ticketId, String email) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket not found"));

        User currentUser=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));

        if(currentUser.getRole()!=Role.ADMIN){
            throw new AccessDeniedException("Only ADMIN can delete tickets");
        }

        ticketRepository.delete(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getMyTickets(String email, TicketStatus status, TicketPriority priority, Pageable pageable){

        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));

        Boolean isAdmin=user.getRole()==Role.ADMIN;
        Boolean isAgent=user.getRole()==Role.SUPPORT_AGENT;

        Page<Ticket> tickets = ticketRepository.findFilteredTickets(
                user.getId(),
                isAdmin,
                isAgent,
                status,
                priority,
                pageable
        );

//        if (user.getRole() == Role.ADMIN) {
//            tickets = ticketRepository.findAll(pageable);
//        } else if (user.getRole() == Role.USER) {
//            tickets = ticketRepository.findByCreatedById(user.getId(), pageable);
//        } else {
//            tickets = ticketRepository.findByAssignedToId(user.getId(), pageable);
//        }
        return tickets.map(TicketMapper::toResponseDTO);
    }
}
