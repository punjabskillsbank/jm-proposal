package com.jobmatrix.jm_proposal.serviceimpl;

import com.common.event.ProposalSubmittedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProposalEventPublisherTest {

    @Mock
    private KafkaTemplate<String, ProposalSubmittedEvent> kafkaTemplate;

    @Mock
    private NewTopic topic;

    @InjectMocks
    private ProposalEventPublisher publisher;

    @Test
    void shouldPublishProposalSubmittedEventToKafka() {
        // Given
        ProposalSubmittedEvent event = ProposalSubmittedEvent.builder()
                .proposalId(1L)
                .jobPostingId(123L)
                .clientId(UUID.randomUUID())
                .freelancerId(UUID.randomUUID())
                .build();

        Mockito.when(topic.name()).thenReturn("proposal-submitted-topic");

        // When
        publisher.publish(event);

        // Then
        ArgumentCaptor<Message<ProposalSubmittedEvent>> messageCaptor =
                ArgumentCaptor.forClass(Message.class);

        Mockito.verify(kafkaTemplate).send(messageCaptor.capture());

        Message<ProposalSubmittedEvent> sentMessage = messageCaptor.getValue();

        Assertions.assertEquals(event, sentMessage.getPayload());
        Assertions.assertEquals("proposal-submitted-topic", sentMessage.getHeaders().get(KafkaHeaders.TOPIC));
    }
}

