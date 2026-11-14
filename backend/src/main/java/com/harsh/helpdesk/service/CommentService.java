package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.request.CommentRequestDTO;
import com.harsh.helpdesk.dto.response.CommentResponseDTO;
import com.harsh.helpdesk.entity.Comment;

import java.util.List;

public interface CommentService {

    CommentResponseDTO addComment(Long ticketId, String email, CommentRequestDTO request);

    List<CommentResponseDTO> getCommentsByTicket(Long ticketId, String email);

    List<CommentResponseDTO> getMyComments(String email);

    void deleteComment(Long commentId, String email);
}
