
package com.jobmatrix.jm_proposal.test_utils.factory;


import com.common.dto.ProposalSubmissionDTO;
import com.common.entity.ProposalSubmission;
import com.common.enums.ProposalStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProposalTestDataFactory {
    private static final int JOB_POSTING_ID = 101;
    private static final int PROPOSED_BID_AMOUNT = 5000;
    private static final String COVER_LETTER = "Please consider my proposal for this job. I have relevant experience.";

    public static ProposalSubmissionDTO createProposalSubmissionRequest(UUID freelancerId, UUID clientId) {
        return ProposalSubmissionDTO.builder()
                .jobPostingId((long) JOB_POSTING_ID)
                .freelancerId(freelancerId)
                .clientId(clientId)
                .proposedBidAmount(PROPOSED_BID_AMOUNT)
                .coverLetter(COVER_LETTER)
                .proposalStatus(ProposalStatus.SUBMITTED)
                .build();
    }

    public static ProposalSubmission createProposalEntity(Long proposalId, UUID freelancerId, UUID clientId) {
        return ProposalSubmission.builder()
                .proposalId(proposalId)
                .jobPostingId(JOB_POSTING_ID)
                .freelancerId(freelancerId)
                .clientId(clientId)
                .proposedBidAmount(PROPOSED_BID_AMOUNT)
                .coverLetter(COVER_LETTER)
                .proposalStatus(ProposalStatus.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static ProposalSubmission createTestProposal(Long id, UUID freelancerId, UUID clientId) {
        return ProposalSubmission.builder()
                .proposalId(id)
                .jobPostingId(1)
                .freelancerId(freelancerId)
                .clientId(clientId)
                .proposedBidAmount(1000)
                .coverLetter("Test cover letter")
                .proposalStatus(ProposalStatus.SUBMITTED)  // <-- Add this
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
