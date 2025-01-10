package com.example.matcher.profileservice.configuration.kafka;

import com.example.matcher.profileservice.model.StatusConnectionUpdate;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (record, exception) -> {
            String topic = record.topic();
            String dlqTopic = topic + ".DLT";
            logger.error("Error in topic '{}', sending to DLQ: {}", topic, dlqTopic, exception);
            return new org.apache.kafka.common.TopicPartition(dlqTopic, record.partition());
        });
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                deadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(1000L, 3) // Задержка 1 секунда, максимум 3 попытки
        );
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class, NullPointerException.class);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (deliveryAttempt > 0) {
                logger.warn("Retry attempt {} for record {} with exception {}",
                        deliveryAttempt, record.value(), ex.getMessage());
            }
        });
        return errorHandler;
    }

    private Map<String, Object> baseConsumerConfig() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 40000);
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);
        configProps.put("reconnect.backoff.ms", 1000);
        configProps.put("reconnect.backoff.max.ms", 10000);
        return configProps;
    }


    @Bean
    public ConsumerFactory<String, StatusConnectionUpdate> jsonConsumerFactory() {
        Map<String, Object> configProps = baseConsumerConfig();

        JsonDeserializer<StatusConnectionUpdate> jsonDeserializer = new JsonDeserializer<>(StatusConnectionUpdate.class);
        jsonDeserializer.addTrustedPackages("*"); // Укажите конкретные пакеты для безопасности
        jsonDeserializer.setRemoveTypeHeaders(false);
        jsonDeserializer.setUseTypeMapperForKey(true);

        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);

        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StatusConnectionUpdate> jsonKafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, StatusConnectionUpdate> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jsonConsumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}

