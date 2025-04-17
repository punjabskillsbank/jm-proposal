package com.jobmatrix.jm_proposal.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProposalResponse {
    private Long proposalId;
    private int jobId;
    private UUID freelancerId;
    private UUID clientId;
    private BigDecimal proposedBidAmount;
    private String proposalStatus;
    private String coverLetter;
    private LocalDateTime createdAt;

}
