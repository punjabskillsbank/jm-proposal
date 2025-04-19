package com.jobmatrix.jm_proposal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proposals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long proposalId;

    @Column(name = "job_id", nullable = false)
    private int jobId;

    @Column(name = "freelancer_id", nullable = false)
    private UUID freelancerId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "proposed_bid_amount", nullable = false)
    private BigDecimal proposedBidAmount;

    @Column(name = "proposal_status")
    private String proposalStatus;

    @Column(name = "cover_letter", columnDefinition = "text")
    private String coverLetter;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
