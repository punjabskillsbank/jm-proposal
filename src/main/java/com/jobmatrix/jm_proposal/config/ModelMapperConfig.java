package com.jobmatrix.jm_proposal.config;

import com.jobmatrix.jm_proposal.dto.ProposalSubmissionRequest;
import com.jobmatrix.jm_proposal.entity.Proposal;
import com.jobmatrix.jm_proposal.entity.ProposalAttachment;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Custom mapping for ProposalSubmissionRequest -> Proposal
        TypeMap<ProposalSubmissionRequest, Proposal> typeMap = mapper.createTypeMap(
                ProposalSubmissionRequest.class, Proposal.class);

        typeMap.setPostConverter(context -> {
            ProposalSubmissionRequest source = context.getSource();
            Proposal destination = context.getDestination();

            if (source.getAttachments() != null) {
                var attachments = source.getAttachments().stream().map(dto -> {
                    ProposalAttachment attachment = new ProposalAttachment();
                    attachment.setFileName(dto.getFileName());
                    attachment.setFileUrl(dto.getFileUrl());
                    attachment.setProposal(destination);
                    return attachment;
                }).collect(Collectors.toList());

                destination.setAttachments(attachments);
            }

            return destination;
        });

        return mapper;
    }
}
