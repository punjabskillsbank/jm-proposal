package com.jobmatrix.jm_proposal.service;

import com.jobmatrix.jm_proposal.dto.PresignedUrlResponseDTO;

import java.util.List;

public interface FileService {

    /**
     * Generates presigned URLs for uploading and downloading job posting attachements
     *
     * @param jobId      the ID of the job post
     * @param originalFilenames the MIME type of the file
     * @return an array containing [uploadUrl, downloadUrl]
     */
    List<PresignedUrlResponseDTO> generateMultipleJobAttachmentUrls(Long proposalId, List<String> originalFilenames);

}
