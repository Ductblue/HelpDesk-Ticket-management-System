package com.harsh.helpdesk.dto.response;

import com.harsh.helpdesk.entity.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketHistoryResponseDTO {
    private Long id;
    private Long ticketId;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private Long changedById;
    private String changedByName;
    private LocalDateTime changedAt;
}
