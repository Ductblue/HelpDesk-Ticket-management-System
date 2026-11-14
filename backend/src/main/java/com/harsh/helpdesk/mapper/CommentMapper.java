
package com.harsh.helpdesk.mapper;

import com.harsh.helpdesk.dto.request.CommentRequestDTO;
import com.harsh.helpdesk.dto.response.CommentResponseDTO;
import com.harsh.helpdesk.entity.Comment;
import com.harsh.helpdesk.entity.Ticket;
import com.harsh.helpdesk.entity.User;

public class CommentMapper {

    // Convert request DTO into Comment entity
    public static Comment toEntity(
            CommentRequestDTO dto,
            Ticket ticket,
            User user) {

        return Comment.builder()
                .content(dto.getContent())
                .ticket(ticket)
                .user(user)
                .build();
    }

    // Convert Comment entity into response DTO
    public static CommentResponseDTO toResponseDTO(Comment comment) {

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .ticketId(comment.getTicket().getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
