package com.hospital.doctor_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String DOCTOR_QUEUE = "doctor.queue";
    public static final String DOCTOR_EXCHANGE = "doctor.exchange";
    public static final String DOCTOR_ROUTING_KEY = "doctor.created";

    @Bean
    public Queue doctorQueue() {
        return new Queue(DOCTOR_QUEUE);
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

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
