package com.harsh.helpdesk.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long id;
    private String content;
    private Long userId;
    private Long ticketId;
    private String userName;
    private LocalDateTime createdAt;
}
