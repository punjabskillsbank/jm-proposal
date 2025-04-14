package com.jobmatrix.jm_proposal.test_utils.factory;

import com.jobmatrix.jm_proposal.dto.ProposalAttachmentDto;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionRequest;
import com.jobmatrix.jm_proposal.entity.Proposal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

public class ProposalTestDataFactory {
    private static final int JOB_ID = 101;
    private static final BigDecimal PROPOSED_BID_AMOUNT = BigDecimal.valueOf(5000);
    private static final String COVER_LETTER = "Please consider my proposal for this job. I have relevant experience.";
    private static final String FILE_URL = "https://example.com/file.pdf";
    private static final String FILE_NAME = "proposal_attachment.pdf";

    public static ProposalSubmissionRequest createProposalSubmissionRequest(UUID freelancerId, UUID clientId) {
        return ProposalSubmissionRequest.builder()
                .jobId(JOB_ID)
                .freelancerId(freelancerId)
                .clientId(clientId)
                .proposedBidAmount(PROPOSED_BID_AMOUNT)
                .coverLetter(COVER_LETTER)
                .attachments(Collections.singletonList(
                        ProposalAttachmentDto.builder()
                                .fileUrl(FILE_URL)
                                .fileName(FILE_NAME)
                                .build()
                ))
                .build();
    }

    public static Proposal createProposalEntity(Long id, UUID freelancerId, UUID clientId) {
        return Proposal.builder()
                .id(id)
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
