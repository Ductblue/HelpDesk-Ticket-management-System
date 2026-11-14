package com.harsh.helpdesk.controller;

import com.harsh.helpdesk.dto.request.CommentRequestDTO;
import com.harsh.helpdesk.dto.response.CommentResponseDTO;
import com.harsh.helpdesk.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/tickets/{ticketId}")
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable Long ticketId, @Valid @RequestBody CommentRequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(commentService.addComment(ticketId,userDetails.getUsername(),requestDTO));
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTicket(@PathVariable Long ticketId, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(commentService.getCommentsByTicket(ticketId, userDetails.getUsername()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CommentResponseDTO>> getMyComments(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(commentService.getMyComments(userDetails.getUsername()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetails userDetails){
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
