package com.jobmatrix.jm_proposal.controller;

import com.jobmatrix.jm_proposal.dto.PresignedUrlRequestDTO;
import com.jobmatrix.jm_proposal.dto.PresignedUrlResponseDTO;
import com.jobmatrix.jm_proposal.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/presigned_url")
@RequiredArgsConstructor
public class PresignedUrlController {

    private final FileService fileService;

    @PostMapping("/upload/proposal_attachment")
    public ResponseEntity<List<PresignedUrlResponseDTO>> generateUploadUrlsForJobAttachments(
            @RequestBody PresignedUrlRequestDTO request) {

        List<PresignedUrlResponseDTO> responses = fileService.generateMultipleJobAttachmentUrls(
                request.getProposal_id(),
                request.getFile()
        );
        return ResponseEntity.ok(responses);
    }
}