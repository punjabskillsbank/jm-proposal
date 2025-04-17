package com.jobmatrix.jm_proposal.service;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;

public interface ProposalService {
    ProposalSubmission submitProposal(ProposalSubmissionDTO proposalRequest);
}
