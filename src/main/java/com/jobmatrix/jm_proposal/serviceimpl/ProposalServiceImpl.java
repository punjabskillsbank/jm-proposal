package com.jobmatrix.jm_proposal.serviceimpl;

import com.common.dto.ProposalQuestionAnswerDTO;
import com.common.dto.ProposalSubmissionDTO;
import com.common.dto.ProposalSubmissionResponseDTO;
import com.common.entity.JobPostingQuestion;
import com.common.entity.ProposalAttachment;
import com.common.entity.ProposalQuestionAnswer;
import com.common.entity.ProposalSubmission;
import com.common.enums.ProposalStatus;
import com.common.event.ProposalSubmittedEvent;
import com.common.exceptionHandling.FreelancerNotFoundException;
import com.common.exceptionHandling.ProposalNotFoundException;
import com.jobmatrix.jm_proposal.exception.AnswerTooLongException;
import com.jobmatrix.jm_proposal.repository.FreelancerRepository;
import com.jobmatrix.jm_proposal.repository.ProposalRepository;
import com.jobmatrix.jm_proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final FreelancerRepository freelancerRepository;
    private final ModelMapper modelMapper;
    private final ProposalEventPublisher proposalEventPublisher;

    private static final int MAX_CHAR_LIMIT = 5000;
    @Override
    @Transactional
    public ProposalSubmissionResponseDTO submitProposal(ProposalSubmissionDTO proposalDTO) {
        ProposalSubmission proposal = modelMapper.map(proposalDTO, ProposalSubmission.class);
        proposal.setProposalId(null);
        
        // Map answers manually and set back-reference
        List<ProposalQuestionAnswerDTO> answerDTOs = proposalDTO.getQuestionAnswers();
        if (answerDTOs != null && !answerDTOs.isEmpty()) {
            for (ProposalQuestionAnswerDTO dto : answerDTOs) {
                validateAnswerLength(dto.getAnswer());
            }
            List<ProposalQuestionAnswer> answers = new ArrayList<>();
            for (ProposalQuestionAnswerDTO dto : answerDTOs) {
                ProposalQuestionAnswer answer = new ProposalQuestionAnswer();
                answer.setAnswer(dto.getAnswer());
                answer.setProposalSubmission(proposal);
                
                // Set the question reference with just the ID
                JobPostingQuestion question = new JobPostingQuestion();
                question.setQuestionId(dto.getQuestionId());
                answer.setJobPostingQuestion(question);
                
                answers.add(answer);
            }
            proposal.setQuestionAnswers(answers);
        }

        // Save the proposal first to get an ID
        ProposalSubmission savedProposal = proposalRepository.save(proposal);

        // Publish the event after saving the proposal
        ProposalSubmittedEvent event = ProposalSubmittedEvent.builder()
                .proposalId(savedProposal.getProposalId())
                .jobPostingId(savedProposal.getJobPostingId())
                .clientId(savedProposal.getClientId())
                .freelancerId(savedProposal.getFreelancerId())
                .build();
        proposalEventPublisher.publish(event);

        return mapToResponseDTO(savedProposal);
    }

    private ProposalSubmissionResponseDTO mapToResponseDTO(ProposalSubmission proposal) {
        ProposalSubmissionResponseDTO response = modelMapper.map(proposal, ProposalSubmissionResponseDTO.class);
        response.setProposalId(proposal.getProposalId());
        
        if (proposal.getQuestionAnswers() != null) {
            List<ProposalQuestionAnswerDTO> answerDTOs = proposal.getQuestionAnswers().stream()
                    .map(answer -> ProposalQuestionAnswerDTO.builder()
                            .questionId(answer.getJobPostingQuestion().getQuestionId())
                            .answer(answer.getAnswer())
                            .build())
                    .toList();
            response.setQuestionAnswers(answerDTOs);
        }

        return response;
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
    public Map<ProposalStatus, List<ProposalSubmissionResponseDTO>> getProposalsByStatus(UUID freelancerId, List<ProposalStatus> statuses) {
        if (!freelancerRepository.existsById(freelancerId)) {
            throw new FreelancerNotFoundException(freelancerId);
        }
        Map<ProposalStatus, List<ProposalSubmissionResponseDTO>> result = new HashMap<>();
        for (ProposalStatus status : statuses) {
            List<ProposalSubmission> jobPostings = proposalRepository.findByFreelancerIdAndProposalStatus(freelancerId, status);
            List<ProposalSubmissionResponseDTO> dtoList = jobPostings.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
            result.put(status, dtoList);
        }
        return result;
    }
    @Override
    public List<ProposalSubmissionResponseDTO> getProposalsByJobPostingId(Long jobPostingId) {
        List<ProposalSubmission> proposals = proposalRepository.findByJobPostingIdAndProposalStatusOrderByCreatedAtDesc(
            jobPostingId, ProposalStatus.SUBMITTED);
        return proposals.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void saveProposalAttachments(Long proposalId, List<String> ProposalAttachmentS3Keys) {
        if (ProposalAttachmentS3Keys == null || ProposalAttachmentS3Keys.isEmpty()) {
            return;
        }

        ProposalSubmission proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ProposalNotFoundException(proposalId));

        log.info("Processing {} attachment(s) for proposalId {}", ProposalAttachmentS3Keys.size(), proposalId);

        List<ProposalAttachment> attachments = ProposalAttachmentS3Keys.stream()
                .map(s3Key -> {
                    ProposalAttachment attachment = new ProposalAttachment();
                    attachment.setProposal_attachment_s3_key(s3Key);
                    attachment.setProposalSubmission(proposal);
                    return attachment;
                })
                .collect(Collectors.toList());
        
        if (proposal.getProposalAttachments() == null) {
            proposal.setProposalAttachments(attachments);
        } else {
            proposal.getProposalAttachments().addAll(attachments);
        }
        
        proposalRepository.save(proposal);
    }

}
