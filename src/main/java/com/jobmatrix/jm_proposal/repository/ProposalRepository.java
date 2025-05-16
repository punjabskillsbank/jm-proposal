package com.jobmatrix.jm_proposal.repository;

import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProposalRepository extends JpaRepository<ProposalSubmission, Long> {
    List<ProposalSubmission> findByFreelancerId(UUID freelancerId);
}
