package com.jobmatrix.jm_proposal.service;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;

import java.util.List;
import java.util.UUID;

public interface ProposalService {
    ProposalSubmission submitProposal(ProposalSubmissionDTO proposalRequest);

    List<ProposalSubmission> getProposalsByFreelancerId(UUID freelancerId);
}
