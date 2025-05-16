package com.jobmatrix.jm_proposal.serviceimpl;

import com.common.entity.Freelancer;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.exception.FreelancerNotFoundException;
import com.jobmatrix.jm_proposal.repository.FreelancerRepository;
import com.jobmatrix.jm_proposal.repository.ProposalRepository;
import com.jobmatrix.jm_proposal.test_utils.factory.ProposalTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalServiceImplTest {

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    @InjectMocks
    private ProposalServiceImpl proposalService;

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        UUID freelancerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        
        modelMapper = new ModelMapper();
        
        ProposalSubmissionDTO requestDto = ProposalTestDataFactory.createProposalSubmissionRequest(freelancerId, clientId);
        ProposalSubmission savedProposal = ProposalTestDataFactory.createProposalEntity(1L, freelancerId, clientId);
    }

    @Test
    void submitProposal_shouldSaveProposal() {
        // given
        ProposalSubmissionDTO request = ProposalSubmissionDTO.builder()
                .jobPostingId(1)
                .freelancerId(UUID.randomUUID())
                .clientId(UUID.randomUUID())
                .proposedBidAmount(new BigDecimal("1000.00"))
                .coverLetter("Test cover letter")
                .build();

        ProposalSubmission savedProposal = modelMapper.map(request, ProposalSubmission.class);
        savedProposal.setProposalId(1L);

        when(proposalRepository.save(any(ProposalSubmission.class))).thenReturn(savedProposal);

        // when
        ProposalSubmission result = proposalService.submitProposal(request);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getProposalId());
        assertEquals(request.getJobPostingId(), result.getJobPostingId());
        assertEquals(request.getFreelancerId(), result.getFreelancerId());
        assertEquals(request.getClientId(), result.getClientId());
        assertEquals(0, request.getProposedBidAmount().compareTo(result.getProposedBidAmount()));
        assertEquals(request.getCoverLetter(), result.getCoverLetter());

        verify(proposalRepository).save(any(ProposalSubmission.class));
    }

    @Test
    void getProposalsByFreelancerId_whenProposalsExist_shouldReturnProposals() {
        // given
        UUID freelancerId = UUID.randomUUID();
        List<ProposalSubmissionDTO> expectedDtoList = List.of(
                ProposalTestDataFactory.createProposalSubmissionRequest(freelancerId, UUID.randomUUID()),
                ProposalTestDataFactory.createProposalSubmissionRequest(freelancerId, UUID.randomUUID())
        );
        List<ProposalSubmission> expectedProposals = expectedDtoList.stream()
                .map(dto -> modelMapper.map(dto, ProposalSubmission.class))
                .toList();

        when(freelancerRepository.findById(freelancerId))
                .thenReturn(Optional.of(new Freelancer()));

        when(proposalRepository.findByFreelancerId(freelancerId))
                .thenReturn(expectedProposals);

        // when
        List<ProposalSubmission> result = proposalService.getProposalsByFreelancerId(freelancerId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedProposals.size(), result.size());
        assertTrue(expectedProposals.containsAll(result) && result.containsAll(expectedProposals));
        verify(freelancerRepository).findById(freelancerId);
        verify(proposalRepository).findByFreelancerId(freelancerId);
    }


    @Test
    void getProposalsByFreelancerId_whenNoProposals_shouldReturnEmptyList() {
        // given
        UUID freelancerId = UUID.randomUUID();

        when(freelancerRepository.findById(freelancerId))
                .thenReturn(Optional.of(new Freelancer()));

        List<ProposalSubmissionDTO> emptyDtoList = Collections.emptyList();
        when(proposalRepository.findByFreelancerId(freelancerId))
                .thenReturn(emptyDtoList.stream()
                        .map(dto -> modelMapper.map(dto, ProposalSubmission.class))
                        .toList());

        // when
        List<ProposalSubmission> result = proposalService.getProposalsByFreelancerId(freelancerId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(freelancerRepository).findById(freelancerId);
        verify(proposalRepository).findByFreelancerId(freelancerId);
    }

    @Test
    void getProposalsByFreelancerId_whenFreelancerDoesNotExist_shouldThrowException() {
        // given
        UUID freelancerId = UUID.randomUUID();
        when(freelancerRepository.findById(freelancerId)).thenReturn(Optional.empty());

        // when + then
        assertThrows(FreelancerNotFoundException.class, () -> {
            proposalService.getProposalsByFreelancerId(freelancerId);
        });

        verify(freelancerRepository).findById(freelancerId);
        verifyNoInteractions(proposalRepository);
    }


}
