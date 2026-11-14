
package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.request.TicketRequestDTO;
import com.harsh.helpdesk.dto.response.TicketResponseDTO;
import com.harsh.helpdesk.entity.TicketPriority;
import com.harsh.helpdesk.entity.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService {

    TicketResponseDTO createTicket(TicketRequestDTO request, String email);

    TicketResponseDTO getTicketById(Long ticketId, String email);

    Page<TicketResponseDTO> getAllTickets(
            String email,
            TicketStatus status,
            TicketPriority priority,
            Pageable pageable
    );

    List<TicketResponseDTO> getMyTickets(String email);

    TicketResponseDTO assignTicket(Long ticketId, Long agentId, String email);

    TicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus status, String email);

    TicketResponseDTO updateTicketPriority(Long ticketId, TicketPriority priority, String email);

    void deleteTicket(Long ticketId, String email);

    Page<TicketResponseDTO> getMyTickets(
            String email,
            TicketStatus status,
            TicketPriority priority,
            Pageable pageable
    );
}
