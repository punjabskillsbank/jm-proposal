package com.jobmatrix.jm_proposal.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequestDTO {

    private Long proposal_id;
    private List<String> file;

}
