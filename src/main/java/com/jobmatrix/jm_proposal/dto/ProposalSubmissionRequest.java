package com.jobmatrix.jm_proposal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProposalSubmissionRequest {
    @NotNull(message = "Job ID is required")
    private int jobId;

    @NotNull(message = "Freelancer ID is required")
    private UUID freelancerId;

    @NotNull(message = "Client ID is required")
    private UUID clientId;

    @NotNull(message = "Proposed bid amount is required")
    @Positive(message = "Bid amount must be positive")
    private BigDecimal proposedBidAmount;

    @NotBlank(message = "Cover letter is required")
    private String coverLetter;

    private List<ProposalAttachmentDto> attachments;
}
