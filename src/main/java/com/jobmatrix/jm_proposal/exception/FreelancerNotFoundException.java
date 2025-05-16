package com.jobmatrix.jm_proposal.exception;

import java.util.UUID;

public class FreelancerNotFoundException extends RuntimeException {
    public FreelancerNotFoundException(UUID freelancerId) {
        super("Freelancer not found with Freelancer ID: " + freelancerId);
    }
}
