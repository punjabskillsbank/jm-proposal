package com.jobmatrix.jm_proposal.serviceimpl;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.repository.ProposalRepository;
import com.jobmatrix.jm_proposal.serviceimply.ProposalServiceImpl;
import com.jobmatrix.jm_proposal.test_utils.factory.ProposalTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalServiceImplTest {

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProposalServiceImpl proposalService;

    private ProposalSubmissionDTO requestDto;
    private ProposalSubmission mappedProposal;
    private ProposalSubmission savedProposal;

    @BeforeEach
    void setUp() {
        UUID freelancerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        
        requestDto = ProposalTestDataFactory.createProposalSubmissionRequest(freelancerId, clientId);
        mappedProposal = ProposalTestDataFactory.createProposalEntity(null, freelancerId, clientId);
        savedProposal = ProposalTestDataFactory.createProposalEntity(1L, freelancerId, clientId);
    }

    @Test
    void submitProposal_shouldSaveAndReturnProposal() {
        // given
        when(modelMapper.map(requestDto, ProposalSubmission.class)).thenReturn(mappedProposal);
        when(proposalRepository.save(mappedProposal)).thenReturn(savedProposal);

        // when
        ProposalSubmission result = proposalService.submitProposal(requestDto);

        // then
        assertNotNull(result);
        assertEquals(savedProposal.getProposalId(), result.getProposalId());
        assertEquals(savedProposal.getJobId(), result.getJobId());
        assertEquals(savedProposal.getCoverLetter(), result.getCoverLetter());

        verify(modelMapper).map(requestDto, ProposalSubmission.class);
        verify(proposalRepository).save(mappedProposal);
    }
}
