package com.harsh.helpdesk.repository;

import com.harsh.helpdesk.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment,Long> {

    List<Comment> findByTicketId(Long ticketId);
    List<Comment> findByUserId(Long userId);
}
