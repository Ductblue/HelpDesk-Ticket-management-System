package com.harsh.helpdesk.controller;

import com.harsh.helpdesk.dto.response.TicketHistoryResponseDTO;
import com.harsh.helpdesk.service.TicketHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-history")
public class TicketHistoryController {
    private final TicketHistoryService ticketHistoryService;

    public TicketHistoryController(TicketHistoryService ticketHistoryService) {
        this.ticketHistoryService = ticketHistoryService;
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<List<TicketHistoryResponseDTO>> getHistoryByTicket(@PathVariable Long ticketId, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ticketHistoryService.getHistoryByTicket(ticketId, userDetails.getUsername()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketHistoryResponseDTO>> getMyHistory(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ticketHistoryService.getMyHistory(userDetails.getUsername()));
    }
}
