package com.jobmatrix.jm_proposal.serviceimpl;

import com.common.enums.ProposalStatus;
import com.common.exceptionHandling.FreelancerNotFoundException;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.repository.FreelancerRepository;
import com.jobmatrix.jm_proposal.repository.ProposalRepository;
import com.jobmatrix.jm_proposal.test_utils.factory.ProposalTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalServiceImplTest {

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private FreelancerRepository clientRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    private ProposalServiceImpl proposalService;

    @BeforeEach
    void setUp() {
        UUID freelancerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        proposalService = new ProposalServiceImpl(proposalRepository, freelancerRepository, modelMapper);

        ProposalSubmissionDTO requestDto = ProposalTestDataFactory.createProposalSubmissionRequest(freelancerId, clientId);
        ProposalSubmission savedProposal = ProposalTestDataFactory.createProposalEntity(1L, freelancerId, clientId);
    }

    @Test
    void submitProposal_shouldSaveProposal() {
        ProposalSubmissionDTO request = ProposalSubmissionDTO.builder()
                .jobPostingId(1L)
                .freelancerId(UUID.randomUUID())
                .clientId(UUID.randomUUID())
                .proposedBidAmount(1000)
                .coverLetter("Test cover letter")
                .build();

        ProposalSubmission savedProposal = modelMapper.map(request, ProposalSubmission.class);
        savedProposal.setProposalId(1L);

        when(proposalRepository.save(any(ProposalSubmission.class))).thenReturn(savedProposal);
        ProposalSubmission result = proposalService.submitProposal(request);

        assertEquals(1L, result.getProposalId());
        assertEquals(request.getJobPostingId(), result.getJobPostingId());
        assertEquals(request.getFreelancerId(), result.getFreelancerId());
        assertEquals(request.getClientId(), result.getClientId());
        assertEquals(1000, request.getProposedBidAmount());
        assertEquals(request.getCoverLetter(), result.getCoverLetter());

        verify(proposalRepository).save(any(ProposalSubmission.class));
    }
    @Test
    void getProposalsByJobPostingId_shouldReturnListOfSubmittedProposalsSortedByCreatedAtDesc() {
        Long jobPostingId = 1L;
        UUID freelancerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        ProposalSubmission proposal1 = ProposalTestDataFactory.createTestProposal(jobPostingId, freelancerId, clientId);
        proposal1.setProposalStatus(ProposalStatus.SUBMITTED);

        ProposalSubmission proposal2 = ProposalTestDataFactory.createTestProposal(jobPostingId, freelancerId, clientId);
        proposal2.setProposalStatus(ProposalStatus.SUBMITTED);


        List<ProposalSubmission> mockProposals = List.of(proposal2, proposal1); // Sorted: most recent first

        when(proposalRepository.findByJobPostingIdAndProposalStatusOrderByCreatedAtDesc(jobPostingId, ProposalStatus.SUBMITTED))
                .thenReturn(mockProposals);

        List<ProposalSubmissionDTO> result = proposalService.getProposalsByJobPostingId(jobPostingId);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(proposalRepository).findByJobPostingIdAndProposalStatusOrderByCreatedAtDesc(jobPostingId, ProposalStatus.SUBMITTED);
    }
    @Test
    void getProposalsByStatus_shouldThrowFreelancerNotFoundException_whenFreelancerDoesNotExist() {
        UUID invalidFreelancerId = UUID.randomUUID();
        List<ProposalStatus> statuses = List.of(ProposalStatus.SUBMITTED);

        when(freelancerRepository.existsById(invalidFreelancerId)).thenReturn(false);

        assertThrows(FreelancerNotFoundException.class, () ->
                proposalService.getProposalsByStatus(invalidFreelancerId, statuses)
        );

        verify(freelancerRepository).existsById(invalidFreelancerId);
        verifyNoMoreInteractions(proposalRepository);
    }
    @Test
    void getProposalsByJobPostingId_shouldReturnEmptyList_whenJobPostingIdIsInvalid() {
        Long invalidJobPostingId = 999L;
        when(proposalRepository.findByJobPostingIdAndProposalStatusOrderByCreatedAtDesc(invalidJobPostingId, ProposalStatus.SUBMITTED))
                .thenReturn(Collections.emptyList());

        List<ProposalSubmissionDTO> result = proposalService.getProposalsByJobPostingId(invalidJobPostingId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(proposalRepository).findByJobPostingIdAndProposalStatusOrderByCreatedAtDesc(invalidJobPostingId, ProposalStatus.SUBMITTED);
    }

    @Test
    void getProposalsByStatuses_Success() {

        // given
        UUID freelancerId = UUID.randomUUID();
        List<ProposalStatus> statuses = Arrays.asList(ProposalStatus.SUBMITTED, ProposalStatus.ACCEPTED);

        ProposalSubmission proposal1 = ProposalTestDataFactory.createProposalEntity(1L, freelancerId, UUID.randomUUID());
        ProposalSubmission proposal2 = ProposalTestDataFactory.createProposalEntity(2L, freelancerId, UUID.randomUUID());

        when(freelancerRepository.existsById(freelancerId)).thenReturn(true);

        // Mock repository calls for each status
        for (ProposalStatus status : statuses) {
            List<ProposalSubmission> proposals = List.of(proposal1, proposal2);
            when(proposalRepository.findByFreelancerIdAndProposalStatus(freelancerId, status))
                    .thenReturn(proposals);
        }

        // when
        Map<ProposalStatus, List<ProposalSubmissionDTO>> result =
                proposalService.getProposalsByStatus(freelancerId, statuses);

        // then
        assertNotNull(result);
        assertTrue(result.containsKey(ProposalStatus.SUBMITTED));
        assertTrue(result.containsKey(ProposalStatus.ACCEPTED));

        // Verify each status has the correct number of proposals
        for (ProposalStatus status : statuses) {
            List<ProposalSubmissionDTO> proposals = result.get(status);
            assertNotNull(proposals);
            assertEquals(2, proposals.size());
        }

        // Verify repository interactions
        for (ProposalStatus status : statuses) {
            verify(proposalRepository).findByFreelancerIdAndProposalStatus(freelancerId, status);
        }
        verify(freelancerRepository).existsById(freelancerId);
    }

    @Test
    void getProposalsByStatuses_WithEmptyStatusList() {
        // given
        UUID freelancerId = UUID.randomUUID();
        List<ProposalStatus> statuses = Collections.emptyList();

        when(freelancerRepository.existsById(freelancerId)).thenReturn(true);

        // when
        Map<ProposalStatus, List<ProposalSubmissionDTO>> result =
                proposalService.getProposalsByStatus(freelancerId, statuses);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(freelancerRepository).existsById(freelancerId);
        verifyNoMoreInteractions(proposalRepository);
    }

    @Test
    void getProposalsByStatuses_EmptyResults() {
        // given
        UUID freelancerId = UUID.randomUUID();
        List<ProposalStatus> statuses = Arrays.asList(ProposalStatus.SUBMITTED, ProposalStatus.ACCEPTED);

        when(freelancerRepository.existsById(freelancerId)).thenReturn(true);

        // Mock repository calls to return empty lists
        for (ProposalStatus status : statuses) {
            when(proposalRepository.findByFreelancerIdAndProposalStatus(freelancerId, status))
                    .thenReturn(List.of());
        }

        // when
        Map<ProposalStatus, List<ProposalSubmissionDTO>> result =
                proposalService.getProposalsByStatus(freelancerId, statuses);

        // then
        assertNotNull(result);
        assertTrue(result.containsKey(ProposalStatus.SUBMITTED));
        assertTrue(result.containsKey(ProposalStatus.ACCEPTED));

        // Verify each status has empty list
        for (ProposalStatus status : statuses) {
            List<ProposalSubmissionDTO> proposals = result.get(status);
            assertNotNull(proposals);
            assertTrue(proposals.isEmpty());
        }

        // Verify repository interactions
        verify(freelancerRepository).existsById(freelancerId);
        for (ProposalStatus status : statuses) {
            verify(proposalRepository).findByFreelancerIdAndProposalStatus(freelancerId, status);
        }
    }

    @Test
    void getProposalsByStatuses_ThrowsFreelancerNotFoundException() {
        // given
        UUID freelancerId = UUID.randomUUID();
        List<ProposalStatus> statuses = Arrays.asList(ProposalStatus.SUBMITTED);

        when(freelancerRepository.existsById(freelancerId)).thenReturn(false);

        // when & then
        FreelancerNotFoundException exception = assertThrows(
                FreelancerNotFoundException.class,
                () -> proposalService.getProposalsByStatus(freelancerId, statuses)
        );

        assertEquals("Freelancer not found with ID: " + freelancerId, exception.getMessage());

        verify(freelancerRepository).existsById(freelancerId);
        verifyNoMoreInteractions(proposalRepository);
    }
}