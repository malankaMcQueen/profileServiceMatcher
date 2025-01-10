package com.example.matcher.profileservice.configuration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {
    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("delete_profile").build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("create_profile").build();
    }

    @Bean
    public NewTopic topic3() {
        return TopicBuilder.name("update_profile").build();
    }

    public NewTopic topic4() {
        return TopicBuilder.name("status_connection_update").build();
    }
}
