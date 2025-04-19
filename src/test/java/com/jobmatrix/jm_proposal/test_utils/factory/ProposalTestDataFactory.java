package com.jobmatrix.jm_proposal.test_utils.factory;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProposalTestDataFactory {
    private static final int JOB_ID = 101;
    private static final BigDecimal PROPOSED_BID_AMOUNT = BigDecimal.valueOf(5000);
    private static final String COVER_LETTER = "Please consider my proposal for this job. I have relevant experience.";

    public static ProposalSubmissionDTO createProposalSubmissionRequest(UUID freelancerId, UUID clientId) {
        return ProposalSubmissionDTO.builder()
                .jobId(JOB_ID)
                .freelancerId(freelancerId)
                .clientId(clientId)
                .proposedBidAmount(PROPOSED_BID_AMOUNT)
                .coverLetter(COVER_LETTER)
                .build();
    }

    public static ProposalSubmission createProposalEntity(Long proposalId, UUID freelancerId, UUID clientId) {
        return ProposalSubmission.builder()
                .proposalId(proposalId)
                .jobId(JOB_ID)
                .freelancerId(freelancerId)
                .clientId(clientId)
                .proposedBidAmount(PROPOSED_BID_AMOUNT)
                .coverLetter(COVER_LETTER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
