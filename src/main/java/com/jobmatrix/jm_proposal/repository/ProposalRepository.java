package com.jobmatrix.jm_proposal.repository;

import com.jobmatrix.jm_proposal.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
