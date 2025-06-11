package com.jobmatrix.jm_proposal.controller;

import com.common.enums.ProposalStatus;
import com.common.util.EnumUtils;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
public class ProposalController {
    private final ProposalService proposalService;

    @GetMapping("/{jobPostingId}")
    public ResponseEntity<List<ProposalSubmissionDTO>> getProposalsByJobPostingId(@PathVariable Long jobPostingId) {
        List<ProposalSubmissionDTO> proposals = proposalService.getProposalsByJobPostingId(jobPostingId);
        return ResponseEntity.ok(proposals);
    }

    @PostMapping("/create_proposal")
    public ResponseEntity<ProposalSubmission> submitProposal(
            @Valid @RequestBody ProposalSubmissionDTO proposalRequest) {
        ProposalSubmission savedProposal = proposalService.submitProposal(proposalRequest);
        return new ResponseEntity<>(savedProposal, HttpStatus.CREATED);
    }

    @GetMapping("/{freelancerId}/statuses/{statuses}")
    public ResponseEntity<Map<ProposalStatus, List<ProposalSubmissionDTO>>> getProposalsByFreelancerId(
            @PathVariable UUID freelancerId,
            @PathVariable String statuses) {

        List<ProposalStatus> statusList = EnumUtils.parseEnumList(statuses, ProposalStatus.class);
        Map<ProposalStatus, List<ProposalSubmissionDTO>> proposals = proposalService.getProposalsByStatus(freelancerId, statusList);
        return ResponseEntity.ok(proposals);
    }
}
