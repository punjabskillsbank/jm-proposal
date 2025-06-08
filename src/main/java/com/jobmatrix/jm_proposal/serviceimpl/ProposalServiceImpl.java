package com.jobmatrix.jm_proposal.serviceimpl;

import com.common.enums.ProposalStatus;
import com.common.exceptionHandling.FreelancerNotFoundException;
import com.jobmatrix.jm_proposal.dto.ProposalSubmissionDTO;
import com.jobmatrix.jm_proposal.entity.ProposalSubmission;
import com.jobmatrix.jm_proposal.exception.ProposalNotFoundException;
import com.jobmatrix.jm_proposal.repository.FreelancerRepository;
import com.jobmatrix.jm_proposal.repository.ProposalRepository;
import com.jobmatrix.jm_proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final FreelancerRepository freelancerRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProposalSubmission submitProposal(ProposalSubmissionDTO proposalRequest) {
        ProposalSubmission proposal = modelMapper.map(proposalRequest, ProposalSubmission.class);
        proposal.setProposalId(null);
        return proposalRepository.save(proposal);
    }

    @Override
    public Map<ProposalStatus, List<ProposalSubmissionDTO>> getProposalsByStatus(UUID freelancerId, List<ProposalStatus> statusList) {
        if (!freelancerRepository.existsById(freelancerId)) {
            throw new FreelancerNotFoundException(freelancerId);
        }
        Map<ProposalStatus, List<ProposalSubmissionDTO>> result = new HashMap<>();
        for (ProposalStatus status : statusList) {
            List<ProposalSubmission> jobPostings = proposalRepository.findByFreelancerIdAndProposalStatus(freelancerId, status);
            List<ProposalSubmissionDTO> dtoList = new ArrayList<>();
            for (ProposalSubmission jobPosting : jobPostings) {
                ProposalSubmissionDTO jobPostingDTO = modelMapper.map(jobPosting, ProposalSubmissionDTO.class);
                dtoList.add(jobPostingDTO);
            }
            result.put(status, dtoList);
        }
        return result;
    }
    @Override
    public ProposalSubmissionDTO getProposalByJobPostingId(Long jobPostingId)
    {
        ProposalSubmission proposal = proposalRepository.findByJobPostingId(jobPostingId)
                .orElseThrow(() -> new ProposalNotFoundException(jobPostingId));

        if (proposal.getProposalStatus() != ProposalStatus.SUBMITTED) {
            throw new ProposalNotFoundException(jobPostingId);
        }

        return modelMapper.map(proposal, ProposalSubmissionDTO.class);
    }

}
