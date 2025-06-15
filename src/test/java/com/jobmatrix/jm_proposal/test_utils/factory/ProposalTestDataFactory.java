
package com.jobmatrix.jm_proposal.test_utils.factory;

import com.jobmatrix.jm_proposal.dto.ProposalQuestionAnswerDTO;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.common.enums.ProposalStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    public static List<ProposalQuestionAnswerDTO> createValidAnswerDTOs() {
        return List.of(
                ProposalQuestionAnswerDTO.builder()
                        .questionId(1L)
                        .answer("This is a valid short answer to question 1.")
                        .build(),
                ProposalQuestionAnswerDTO.builder()
                        .questionId(2L)
                        .answer("This is a valid short answer to question 2.")
                        .build(),
                ProposalQuestionAnswerDTO.builder()
                        .questionId(3L)
                        .answer("Another valid answer.")
                        .build()
        );
    }

    public static List<ProposalQuestionAnswerDTO> createInvalidAnswerDTOs() {
        String longAnswer = String.join(" ", Collections.nCopies(201, "word"));
        return List.of(
                ProposalQuestionAnswerDTO.builder()
                        .questionId(1L)
                        .answer(longAnswer) // Too long
                        .build(),
                ProposalQuestionAnswerDTO.builder()
                        .questionId(2L)
                        .answer("") // Empty answer
                        .build()
        );
    }
}
