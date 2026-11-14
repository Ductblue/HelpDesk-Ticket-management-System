package com.harsh.helpdesk.dto.response;

import com.harsh.helpdesk.entity.TicketPriority;
import com.harsh.helpdesk.entity.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketResponseDTO {
    private Long id;
    private String title;
    private String description;
    private TicketStatus ticketStatus;
    private TicketPriority priority;
    private Long createdById;
    private String createdByName;
    private Long assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
