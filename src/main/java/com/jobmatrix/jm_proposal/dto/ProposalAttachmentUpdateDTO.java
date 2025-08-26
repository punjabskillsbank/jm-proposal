package com.jobmatrix.jm_proposal.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProposalAttachmentUpdateDTO {

    private List<String> attachmentUrls;

}
