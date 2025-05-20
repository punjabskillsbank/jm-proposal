package com.jobmatrix.jm_proposal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.common.enums.ProposalStatus;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.service.ProposalService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpStatus;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

import java.util.*;

import com.jobmatrix.jm_proposal.test_utils.factory.ProposalTestDataFactory;
import com.common.enums.ProposalStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProposalController.class)
class ProposalControllerTest {
    private static final int JOB_POSTING_ID = 101;

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
    void getProposalsByStatus() throws Exception {
        UUID freelancerId = UUID.randomUUID();
        
        ProposalSubmissionDTO draftProposalDTO = ProposalTestDataFactory.createProposalSubmissionRequest(freelancerId, freelancerId);
        ProposalSubmissionDTO openProposalDTO = ProposalTestDataFactory.createProposalSubmissionRequest(freelancerId, freelancerId);
        openProposalDTO.setProposalStatus(ProposalStatus.ACCEPTED);

        Map<ProposalStatus, List<ProposalSubmissionDTO>> mockResult = Map.of(
            ProposalStatus.SUBMITTED, List.of(draftProposalDTO),
            ProposalStatus.ACCEPTED, List.of(openProposalDTO)
        );

        when(proposalService.getProposalsByStatus(
                Mockito.eq(freelancerId),
                Mockito.argThat(list ->
                        list.containsAll(Arrays.asList(ProposalStatus.SUBMITTED, ProposalStatus.ACCEPTED))
                )
        )).thenReturn(mockResult);

        mockMvc.perform(get("/api/v1/proposals/" + freelancerId + "/statuses/SUBMITTED,ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.SUBMITTED[0].jobPostingId").value(JOB_POSTING_ID))
                .andExpect(jsonPath("$.ACCEPTED[0].jobPostingId").value(JOB_POSTING_ID));
    }

    @Test
    void getProposalsByStatuses_EmptyResult() throws Exception {
        UUID freelancerId = UUID.randomUUID();
        String statuses = "SUBMITTED,ACCEPTED";

        Mockito.when(proposalService.getProposalsByStatus(
                Mockito.eq(freelancerId),
                Mockito.anyList()
        )).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/v1/proposals/{freelancerId}/statuses/{statuses}",
                        freelancerId, statuses))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getProposalsByStatuses_EmptyStatuses() throws Exception {
        UUID freelancerId = UUID.randomUUID();
        String statuses = " ";
        Mockito.when(proposalService.getProposalsByStatus(
                Mockito.eq(freelancerId),
                Mockito.eq(Collections.emptyList())
        )).thenReturn(Collections.emptyMap());
        mockMvc.perform(get("/api/v1/proposals/{freelancerId}/statuses/{statuses}",
                        freelancerId, statuses))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void getProposalsByStatuses_InvalidEnumValue() throws Exception {
        UUID freelancerId = UUID.randomUUID();
        String statuses = "SUBMITTED,INVALID_STATUS";

        mockMvc.perform(get("/api/v1/proposals/{freelancerId}/statuses/{statuses}",
                        freelancerId, statuses))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid value 'INVALID_STATUS' for enum: ProposalStatus"));
    }


}
