package com.jobmatrix.jm_proposal.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalQuestionAnswerDTO {
    private Long questionId;
    private String answer;
}
