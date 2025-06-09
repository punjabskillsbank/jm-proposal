package com.jobmatrix.jm_proposal.repository;

import com.common.enums.ProposalStatus;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProposalRepository extends JpaRepository<ProposalSubmission, Long> {
    List<ProposalSubmission> findByFreelancerIdAndProposalStatus(UUID freelancerId, ProposalStatus proposalStatus);
    List<ProposalSubmission> findByJobPostingIdAndProposalStatusOrderByCreatedAtDesc(Long jobPostingId, ProposalStatus proposalStatus);
}
