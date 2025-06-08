package com.jobmatrix.jm_proposal.exception;

public class ProposalNotFoundException extends RuntimeException {
    public ProposalNotFoundException(String message) {
        super(message);
    }
}