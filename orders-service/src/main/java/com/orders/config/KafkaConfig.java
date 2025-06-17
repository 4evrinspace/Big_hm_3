package com.orders.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public static final String PAYMENT_REQUEST_TOPIC = "payment-requests";
    public static final String PAYMENT_RESPONSE_TOPIC = "payment-responses";

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic paymentRequestTopic() {
        return new NewTopic(PAYMENT_REQUEST_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic paymentResponseTopic() {
        return new NewTopic(PAYMENT_RESPONSE_TOPIC, 1, (short) 1);
    }
} 