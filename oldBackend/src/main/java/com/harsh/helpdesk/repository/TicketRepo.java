package com.harsh.helpdesk.repository;

import com.harsh.helpdesk.entity.Ticket;
import com.harsh.helpdesk.entity.TicketPriority;
import com.harsh.helpdesk.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket,Long> {

    List<Ticket> findByCreatedById(Long userId);
    List<Ticket> findByAssignedToId(Long agentId);
    List<Ticket> findByStatus(TicketStatus ticketStatus);
    List<Ticket> findByPriority(TicketPriority ticketPriority);
    List<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority);

    Page<Ticket> findAll(Pageable pageable);
    Page<Ticket> findByCreatedById(Long userId, Pageable pageable);
    Page<Ticket> findByAssignedToId(Long agentId, Pageable pageable);
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
    Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);
    Page<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority, Pageable pageable);

    @Query("""
    SELECT t FROM Ticket t
    WHERE (
        :isAdmin = true
        OR (:isAgent = true AND t.assignedTo.id = :userId)
        OR (:isAdmin = false AND :isAgent = false
            AND t.createdBy.id = :userId)
    )
    AND (:status IS NULL OR t.status = :status)
    AND (:priority IS NULL OR t.priority = :priority)
    """)
    Page<Ticket> findFilteredTickets(
            @Param("userId") Long userId,
            @Param("isAdmin") boolean isAdmin,
            @Param("isAgent") boolean isAgent,
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority,
            Pageable pageable
    );
}
