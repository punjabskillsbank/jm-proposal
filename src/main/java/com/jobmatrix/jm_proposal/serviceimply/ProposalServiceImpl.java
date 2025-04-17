package com.jobmatrix.jm_proposal.serviceimply;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.repository.ProposalRepository;
import com.jobmatrix.jm_proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProposalSubmission submitProposal(ProposalSubmissionDTO proposalRequest) {
        ProposalSubmission proposal = modelMapper.map(proposalRequest, ProposalSubmission.class);
        proposal.setProposalId(null);
        return proposalRepository.save(proposal);
    }
}
