package com.harsh.helpdesk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @JoinColumn(name = "assigned_to")
    @ManyToOne(fetch = FetchType.LAZY)
    private User assignedTo;

    @PrePersist
    public void onCreate(){
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();

        if(status==null){
            status=TicketStatus.OPEN;
        }
        if(priority==null){
            priority=TicketPriority.MEDIUM;
        }
    }

    @PreUpdate
    public void onUpdate(){
        updatedAt=LocalDateTime.now();
    }
}
