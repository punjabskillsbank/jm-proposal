package com.jobmatrix.jm_proposal;

import com.common.config.S3Config;
import com.common.config.CorsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.common.entity", "com.jobmatrix.entity", "com.jobmatrix.jm_proposal.entity"})
@Import({S3Config.class, CorsConfig.class})
@ComponentScan({"com.jobmatrix.jm_proposal", "com.common.util"})
public class JmProposalApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmProposalApplication.class, args);
	}

}
