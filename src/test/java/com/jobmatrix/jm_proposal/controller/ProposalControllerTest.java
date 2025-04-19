package com.jobmatrix.jm_proposal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.service.ProposalService;
import com.jobmatrix.jm_proposal.test_utils.factory.ProposalTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProposalController.class)
class ProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProposalService proposalService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProposalSubmissionDTO requestDto;
    private ProposalSubmission savedProposal;

    @BeforeEach
    void setup() {
        UUID freelancerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        requestDto = ProposalTestDataFactory.createProposalSubmissionRequest(freelancerId, clientId);
        savedProposal = ProposalTestDataFactory.createProposalEntity(1L, freelancerId, clientId);
    }

    @Test
    void submitProposal_returnsCreatedProposal() throws Exception {
        when(proposalService.submitProposal(ArgumentMatchers.any(ProposalSubmissionDTO.class)))
                .thenReturn(savedProposal);

        mockMvc.perform(post("/api/v1/proposals/create_proposal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proposalId").value(savedProposal.getProposalId()))
                .andExpect(jsonPath("$.jobId").value(savedProposal.getJobId()))
                .andExpect(jsonPath("$.coverLetter").value(savedProposal.getCoverLetter()));
    }

    @Test
    void submitProposal_missingCoverLetter_returnsBadRequest() throws Exception {
        requestDto.setCoverLetter(""); // invalid: @NotBlank violation

        mockMvc.perform(post("/api/v1/proposals/create_proposal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.coverLetter").value("Cover letter is required"));
    }
}
