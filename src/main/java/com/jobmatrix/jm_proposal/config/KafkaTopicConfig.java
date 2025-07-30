package com.jobmatrix.jm_proposal.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.proposal-submitted}")
    private String topicName;

    private static final int PARTITIONS = 1;
    private static final int REPLICAS = 1;
    private static final String RETENTION_MS = "604800000";

    @Bean
    public NewTopic topic(){

        return TopicBuilder.name(topicName)
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .config(TopicConfig.RETENTION_MS_CONFIG, RETENTION_MS) // 7 days
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }




}
