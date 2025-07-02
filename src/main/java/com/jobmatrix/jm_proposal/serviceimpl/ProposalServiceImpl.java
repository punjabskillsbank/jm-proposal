package com.jobmatrix.jm_proposal.serviceimpl;

import com.common.dto.ProposalQuestionAnswerDTO;
import com.common.dto.ProposalSubmissionDTO;
import com.common.entity.JobPostingQuestion;
import com.common.entity.ProposalQuestionAnswer;
import com.common.entity.ProposalSubmission;
import com.common.enums.ProposalStatus;
import com.common.exceptionHandling.FreelancerNotFoundException;
import com.jobmatrix.jm_proposal.exception.AnswerTooLongException;
import com.jobmatrix.jm_proposal.repository.FreelancerRepository;
import com.jobmatrix.jm_proposal.repository.ProposalRepository;
import com.jobmatrix.jm_proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final FreelancerRepository freelancerRepository;
    private final ModelMapper modelMapper;

    private static final int MAX_CHAR_LIMIT = 5000;
    @Override
    @Transactional
    public ProposalSubmissionDTO submitProposal(ProposalSubmissionDTO proposalRequest) {
        ProposalSubmission proposal = modelMapper.map(proposalRequest, ProposalSubmission.class);
        proposal.setProposalId(null);
        // Map answers manually and set back-reference
        List<ProposalQuestionAnswerDTO> answerDTOs = proposalRequest.getQuestionAnswers();
        if (answerDTOs != null && !answerDTOs.isEmpty()) {
            for (ProposalQuestionAnswerDTO dto : answerDTOs) {
                validateAnswerLength(dto.getAnswer());
            }
            List<ProposalQuestionAnswer> answers = answerDTOs.stream()
                    .map(dto -> {
                        ProposalQuestionAnswer answer = modelMapper.map(dto, ProposalQuestionAnswer.class);
                        answer.setAnswerId(null);
                        answer.setProposalSubmission(proposal);
                        JobPostingQuestion jobPostingQuestion = new JobPostingQuestion();
                        jobPostingQuestion.setQuestionId(dto.getQuestionId());
                        answer.setJobPostingQuestion(jobPostingQuestion);
                        return answer;
                    }).toList();
            proposal.setQuestionAnswers(answers);
        }
        ProposalSubmission savedProposal = proposalRepository.save(proposal);
        return mapToResponseDTO(savedProposal);
    }
    private ProposalSubmissionDTO mapToResponseDTO(ProposalSubmission proposal) {
        ProposalSubmissionDTO dto = modelMapper.map(proposal, ProposalSubmissionDTO.class);
        if (proposal.getQuestionAnswers() != null) {
            List<ProposalQuestionAnswerDTO> answerDTOs = proposal.getQuestionAnswers().stream()
                    .map(answer -> ProposalQuestionAnswerDTO.builder()
                            .questionId(answer.getJobPostingQuestion().getQuestionId())
                            .answer(answer.getAnswer())
                            .build())
                    .toList();
            dto.setQuestionAnswers(answerDTOs);
        }
        return dto;
    }
    private void validateAnswerLength(String answer) {
        if (answer != null) {
            int charCount = answer.trim().length();
            if (charCount > MAX_CHAR_LIMIT) {
                throw new AnswerTooLongException(MAX_CHAR_LIMIT);
            }
        }
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
    public List<ProposalSubmissionDTO> getProposalsByJobPostingId(Long jobPostingId) {
        List<ProposalSubmission> proposals = proposalRepository.findByJobPostingIdAndProposalStatusOrderByCreatedAtDesc(jobPostingId, ProposalStatus.SUBMITTED);

        return proposals.stream().map(proposal -> modelMapper
                .map(proposal, ProposalSubmissionDTO.class))
                .collect(Collectors.toList());
    }

}
