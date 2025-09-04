package com.jobmatrix.jm_proposal.controller;

import com.common.dto.AttachmentUpdateDTO;
import com.common.dto.PresignedUrlResponseDTO;
import com.common.dto.ProposalSubmissionDTO;
import com.common.dto.ProposalSubmissionResponseDTO;
import com.common.enums.ProposalStatus;
import com.common.util.EnumUtils;
import com.common.util.S3FileUtil;
import com.jobmatrix.jm_proposal.dto.PresignedUrlRequestDTO;
import com.jobmatrix.jm_proposal.service.ProposalService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class ProposalController {
    private final ProposalService proposalService;
    private final S3FileUtil fileService;


    @GetMapping("/{jobPostingId}")
    public ResponseEntity<List<ProposalSubmissionResponseDTO>> getProposalsByJobPostingId(@PathVariable Long jobPostingId) {
        List<ProposalSubmissionResponseDTO> proposals = proposalService.getProposalsByJobPostingId(jobPostingId);
        return ResponseEntity.ok(proposals);
    }

    @PostMapping("/create_proposal")
    public ResponseEntity<ProposalSubmissionResponseDTO> submitProposal(
            @Valid @RequestBody ProposalSubmissionDTO proposalRequest) {
        log.info("submitProposal called. Attachments present? {} count={} ",
                proposalRequest.getAttachmentUrls() != null && !proposalRequest.getAttachmentUrls().isEmpty(),
                proposalRequest.getAttachmentUrls() == null ? 0 : proposalRequest.getAttachmentUrls().size());
        ProposalSubmissionResponseDTO response = proposalService.submitProposal(proposalRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{freelancerId}/statuses/{statuses}")
    public ResponseEntity<Map<ProposalStatus, List<ProposalSubmissionResponseDTO>>> getProposalsByFreelancerId(
            @PathVariable UUID freelancerId,
            @PathVariable String statuses) {

        List<ProposalStatus> statusList = EnumUtils.parseEnumList(statuses, ProposalStatus.class);
        Map<ProposalStatus, List<ProposalSubmissionResponseDTO>> proposals = proposalService.getProposalsByStatus(freelancerId, statusList);
        return ResponseEntity.ok(proposals);
    }

    @PostMapping("/upload/proposal_attachment")
    public ResponseEntity<List<PresignedUrlResponseDTO>> generateUploadUrlsForJobAttachments(
            @Valid @RequestBody PresignedUrlRequestDTO request) {
        List<PresignedUrlResponseDTO> responses = fileService.generateMultipleJobAttachmentUrls(
                request.getProposal_id(),
                request.getFile()
        );
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{proposalId}/attachments")
    public ResponseEntity<Void> addAttachmentsToJobPosting(
            @PathVariable Long proposalId,
            @RequestBody AttachmentUpdateDTO dto
    ) {
        log.info("addAttachmentsToJobPosting called for proposalId {}. Attachments present? {} count={}",
                proposalId,
                dto.getAttachmentUrls() != null && !dto.getAttachmentUrls().isEmpty(),
                dto.getAttachmentUrls() == null ? 0 : dto.getAttachmentUrls().size());
        proposalService.saveProposalAttachments(proposalId, dto.getAttachmentUrls());
        return ResponseEntity.ok().build();
    }
}
