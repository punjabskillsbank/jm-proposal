package com.jobmatrix.jm_proposal.serviceimpl;



import com.common.event.ProposalSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProposalEventPublisher {

    private final KafkaTemplate<String, ProposalSubmittedEvent> kafkaTemplate;
    private final NewTopic topic;

    public void publish(ProposalSubmittedEvent event) {

        Message<ProposalSubmittedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        kafkaTemplate.send(message);
        log.info("Published event to Kafka: {}", event);
    }
}
