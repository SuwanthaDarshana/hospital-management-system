package com.hospital.doctor_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    // ================= AUTH → DOCTOR =================
    // Doctor created event
    public static final String DOCTOR_QUEUE = "doctor.queue";
    public static final String DOCTOR_EXCHANGE = "doctor.exchange";
    public static final String DOCTOR_ROUTING_KEY = "doctor.created";

    // ================= DOCTOR → AUTH =================
    // Doctor profile update → Auth update
    public static final String DOCTOR_UPDATE_QUEUE = "doctor.update.queue";
    public static final String DOCTOR_UPDATE_EXCHANGE = "doctor.update.exchange";
    public static final String DOCTOR_UPDATE_ROUTING_KEY = "doctor.updated";

    // ---------- Doctor Queue (Auth → Doctor) ----------
    @Bean
    public Queue doctorQueue() {
        return new Queue(DOCTOR_QUEUE, true);
    }

    @Bean
    public TopicExchange doctorExchange() {
        return new TopicExchange(DOCTOR_EXCHANGE);
    }

    @Bean
    public Binding doctorBinding() {
        return BindingBuilder
                .bind(doctorQueue())
                .to(doctorExchange())
                .with(DOCTOR_ROUTING_KEY);
    }

    // ---------- Auth Queue (Doctor → Auth) ----------
    @Bean
    public Queue authQueue() {
        return new Queue(DOCTOR_UPDATE_QUEUE, true);
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(DOCTOR_UPDATE_EXCHANGE);
    }

    @Bean
    public Binding authBinding() {
        return BindingBuilder
                .bind(authQueue())
                .to(authExchange())
                .with(DOCTOR_UPDATE_ROUTING_KEY);
    }

    // JSON serialization
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
