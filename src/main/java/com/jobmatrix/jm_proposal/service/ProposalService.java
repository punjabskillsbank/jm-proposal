package com.jobmatrix.jm_proposal.service;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionRequest;
import com.jobmatrix.jm_proposal.entity.Proposal;

public interface ProposalService {
    Proposal submitProposal(ProposalSubmissionRequest dto);
}
