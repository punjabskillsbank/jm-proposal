package com.jobmatrix.jm_proposal.service;

import com.common.enums.ProposalStatus;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProposalService {
    ProposalSubmission submitProposal(ProposalSubmissionDTO proposalRequest);

    Map<ProposalStatus, List<ProposalSubmissionDTO>> getProposalsByStatus(UUID freelancerId, List<ProposalStatus> statusList);

    ProposalSubmissionDTO getProposalByJobPostingId(Long jobPostingId);
}
