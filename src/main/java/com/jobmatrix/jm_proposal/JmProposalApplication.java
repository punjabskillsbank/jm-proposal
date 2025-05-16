package com.jobmatrix.jm_proposal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.common.entity", "com.jobmatrix.entity", "com.jobmatrix.jm_proposal.entity"})
public class JmProposalApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmProposalApplication.class, args);
	}

}
