package com.jobmatrix.jm_proposal.exception;

import java.util.UUID;

public class ProposalNotFoundException extends RuntimeException {
    public ProposalNotFoundException(Long jobPostingId) {
        super("Proposal not found for job posting ID: " + jobPostingId);
    }
}
