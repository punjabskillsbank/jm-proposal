package com.jobmatrix.jm_proposal.controller;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionRequest;
import com.jobmatrix.jm_proposal.dto.ProposalResponse;
import com.jobmatrix.jm_proposal.entity.Proposal;
import com.jobmatrix.jm_proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping("/create_proposal")
    public ResponseEntity<Proposal> submitProposal(
            @Valid @RequestBody ProposalSubmissionRequest dto) {
        Proposal savedProposal = proposalService.submitProposal(dto);
        return new ResponseEntity<>(savedProposal, HttpStatus.CREATED);
    }
}
