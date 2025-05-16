package com.jobmatrix.jm_proposal.controller;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
public class ProposalController {
    private final ProposalService proposalService;

    @PostMapping("/create_proposal")
    public ResponseEntity<ProposalSubmission> submitProposal(
            @Valid @RequestBody ProposalSubmissionDTO proposalRequest) {
        ProposalSubmission savedProposal = proposalService.submitProposal(proposalRequest);
        return new ResponseEntity<>(savedProposal, HttpStatus.CREATED);
    }

    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<ProposalSubmission>> getProposalsByFreelancerId(
            @PathVariable UUID freelancerId) {
        List<ProposalSubmission> proposals = proposalService.getProposalsByFreelancerId(freelancerId);
        return ResponseEntity.ok(proposals);
    }
}
