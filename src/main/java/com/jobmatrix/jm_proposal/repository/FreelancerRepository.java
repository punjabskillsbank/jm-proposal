package com.jobmatrix.jm_proposal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.common.entity.Freelancer;

import java.util.UUID;

public interface FreelancerRepository extends JpaRepository<Freelancer, UUID> {
}
