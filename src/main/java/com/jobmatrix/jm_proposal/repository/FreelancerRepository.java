package com.jobmatrix.jm_proposal.repository;

import com.common.entity.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

public interface FreelancerRepository extends JpaRepository<Freelancer, UUID> {
}
