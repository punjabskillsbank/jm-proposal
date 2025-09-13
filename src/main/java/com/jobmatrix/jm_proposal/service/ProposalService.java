package com.jobmatrix.jm_proposal.service;

import com.common.dto.ProposalSubmissionDTO;
import com.common.dto.ProposalSubmissionResponseDTO;
import com.common.enums.ProposalStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProposalService {
    ProposalSubmissionResponseDTO submitProposal(ProposalSubmissionDTO proposalRequest);

    Map<ProposalStatus, List<ProposalSubmissionResponseDTO>> getProposalsByStatus(UUID freelancerId, List<ProposalStatus> statusList);
    List<ProposalSubmissionResponseDTO> getProposalsByJobPostingId(Long jobPostingId);
    void saveProposalAttachments(Long proposalId, List<String> s3Keys);
}
