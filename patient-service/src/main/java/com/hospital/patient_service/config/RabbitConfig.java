package com.hospital.patient_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // 1. Listening to Auth Service (Registration)
    public static final String PATIENT_QUEUE = "patient.queue";
    public static final String PATIENT_EXCHANGE = "patient.exchange";
    public static final String PATIENT_ROUTING_KEY = "patient.created";

    // 2. Sending Updates to Auth Service (Synchronization)
    public static final String PATIENT_UPDATE_QUEUE = "patient.update.queue";
    public static final String PATIENT_UPDATE_EXCHANGE = "patient.update.exchange";
    public static final String PATIENT_UPDATE_ROUTING_KEY = "patient.updated";

    // Define the Queue for listening (Consumer side)
    @Bean
    public Queue patientQueue() {
        return new Queue(PATIENT_QUEUE, true);
    }

    @Bean
    public TopicExchange patientExchange() {
        return new TopicExchange(PATIENT_EXCHANGE);
    }

    @Bean
    public Binding doctorBinding() {
        return BindingBuilder
                .bind(patientQueue())
                .to(patientExchange())
                .with(PATIENT_ROUTING_KEY);
    }

    // Define Exchange and Queue for sending updates (Producer side)
    @Bean
    public TopicExchange patientUpdateExchange() {
        return new TopicExchange(PATIENT_UPDATE_EXCHANGE);
    }

    @Bean
    public Queue patientUpdateQueue() {
        return new Queue(PATIENT_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding patientUpdateBinding() {
        return BindingBuilder
                .bind(patientUpdateQueue())
                .to(patientUpdateExchange())
                .with(PATIENT_UPDATE_ROUTING_KEY);
    }

    // Critical: Ensures JSON messages are converted to Java Objects automatically
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
