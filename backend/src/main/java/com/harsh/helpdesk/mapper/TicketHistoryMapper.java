
package com.harsh.helpdesk.mapper;

import com.harsh.helpdesk.dto.response.TicketHistoryResponseDTO;
import com.harsh.helpdesk.entity.TicketHistory;

public class TicketHistoryMapper {

    // Convert TicketHistory entity into response DTO
    public static TicketHistoryResponseDTO toResponseDTO(
            TicketHistory history) {

        return TicketHistoryResponseDTO.builder()
                .id(history.getId())
                .ticketId(history.getTicket().getId())
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .changedById(history.getChangedBy().getId())
                .changedByName(history.getChangedBy().getName())
                .changedAt(history.getChangedAt())
                .build();
    }
}
