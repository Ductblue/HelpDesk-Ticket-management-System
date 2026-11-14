package com.harsh.helpdesk.controller;

import com.harsh.helpdesk.dto.request.TicketRequestDTO;
import com.harsh.helpdesk.dto.response.TicketResponseDTO;
import com.harsh.helpdesk.entity.TicketPriority;
import com.harsh.helpdesk.entity.TicketStatus;
import com.harsh.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@Valid @RequestBody TicketRequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ticketService.createTicket(requestDTO, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponseDTO>> getAllTickets(
            @AuthenticationPrincipal UserDetails userDetails,

            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = createPageable(page, size, sortBy, direction);

        return ResponseEntity.ok(ticketService.getAllTickets(userDetails.getUsername(), status, priority, pageable));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Long ticketId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ticketService.getTicketById(ticketId, userDetails.getUsername()));
    }

//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<TicketResponseDTO>> getTicketsByUser(@PathVariable Long userId){
//        return ResponseEntity.ok(ticketService.getTicketsByUser(userId));
//    }
//
//    @GetMapping("/agent/{agentId}")
//    public ResponseEntity<List<TicketResponseDTO>> getTicketsAssignedToAgent(@PathVariable Long agentId){
//        return ResponseEntity.ok(ticketService.getTicketsAssignedToAgent(agentId));
//    }

    @GetMapping("/my")
    public ResponseEntity<Page<TicketResponseDTO>> getMyTickets(
            @AuthenticationPrincipal UserDetails userDetails,

            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = createPageable(page, size, sortBy, direction);

        return ResponseEntity.ok(ticketService.getMyTickets(userDetails.getUsername(), status, priority, pageable));
    }

    @PutMapping("/{ticketId}/assign/{agentId}")
    public ResponseEntity<TicketResponseDTO> assignTicket(@PathVariable Long ticketId, @PathVariable Long agentId, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ticketService.assignTicket(ticketId,agentId, userDetails.getUsername()));
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponseDTO> updateTicketStatus(@PathVariable Long ticketId, @RequestParam TicketStatus ticketStatus, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId,ticketStatus, userDetails.getUsername()));
    }

    @PutMapping("/{ticketId}/priority")
    public ResponseEntity<TicketResponseDTO> updateTicketPriority(@PathVariable Long ticketId, @RequestParam TicketPriority priority, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ticketService.updateTicketPriority(ticketId,priority, userDetails.getUsername()));
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId, @AuthenticationPrincipal UserDetails userDetails){
        ticketService.deleteTicket(ticketId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {

        if (page<0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (size<1 || size>100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        List<String> allowedFields = List.of("createdAt", "updatedAt", "title", "priority", "status");

        if (!allowedFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sorting field: " + sortBy);
        }

        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Direction must be either asc or desc");
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }
}
