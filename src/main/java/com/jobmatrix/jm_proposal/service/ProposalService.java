package com.jobmatrix.jm_proposal.service;

import com.common.dto.ProposalSubmissionDTO;
import com.common.entity.ProposalSubmission;
import com.common.enums.ProposalStatus;



import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProposalService {
    ProposalSubmission submitProposal(ProposalSubmissionDTO proposalRequest);

    Map<ProposalStatus, List<ProposalSubmissionDTO>> getProposalsByStatus(UUID freelancerId, List<ProposalStatus> statusList);
    List<ProposalSubmissionDTO> getProposalsByJobPostingId(Long jobPostingId);
}
