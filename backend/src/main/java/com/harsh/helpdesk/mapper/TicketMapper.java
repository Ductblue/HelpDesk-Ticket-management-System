
package com.harsh.helpdesk.mapper;

import com.harsh.helpdesk.dto.request.TicketRequestDTO;
import com.harsh.helpdesk.dto.response.TicketResponseDTO;
import com.harsh.helpdesk.entity.Ticket;

public class TicketMapper {

    // Convert TicketRequestDTO into Ticket entity
    public static Ticket toEntity(TicketRequestDTO dto) {

        return Ticket.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getTicketPriority())
                .build();
    }

    // Convert Ticket entity into TicketResponseDTO
    public static TicketResponseDTO toResponseDTO(Ticket ticket) {

        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .ticketStatus(ticket.getStatus())
                .priority(ticket.getPriority())

                .createdById(ticket.getCreatedBy().getId())
                .createdByName(ticket.getCreatedBy().getName())

                .assignedToId(
                        ticket.getAssignedTo() != null
                                ? ticket.getAssignedTo().getId()
                                : null
                )

                .assignedToName(
                        ticket.getAssignedTo() != null
                                ? ticket.getAssignedTo().getName()
                                : null
                )

                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
