package com.jobmatrix.jm_proposal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.service.ProposalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.List;
import java.util.UUID;

import org.springframework.test.web.servlet.MockMvc;

import com.jobmatrix.jm_proposal.exception.FreelancerNotFoundException;
import com.jobmatrix.jm_proposal.test_utils.factory.ProposalTestDataFactory;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(jsonPath("$.jobPostingId").value(savedProposal.getJobPostingId()))
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

    @Test
    void testGetProposalsByFreelancerId() throws Exception {
        // given
        UUID freelancerId = UUID.randomUUID();
        // Create test proposals
        ProposalSubmission proposal1 = ProposalTestDataFactory.createProposalEntity(1L, freelancerId, UUID.randomUUID());
        ProposalSubmission proposal2 = ProposalTestDataFactory.createProposalEntity(2L, freelancerId, UUID.randomUUID());
        List<ProposalSubmission> proposals = List.of(proposal1, proposal2);
        
        when(proposalService.getProposalsByFreelancerId(freelancerId)).thenReturn(proposals);
        
        // when/then
        mockMvc.perform(get("/api/v1/proposals/freelancer/" + freelancerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].proposalId").value(1))
                .andExpect(jsonPath("$[1].proposalId").value(2));
                
        verify(proposalService).getProposalsByFreelancerId(freelancerId);
    }

}
