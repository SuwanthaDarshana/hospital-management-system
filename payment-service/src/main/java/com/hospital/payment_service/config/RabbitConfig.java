package com.hospital.payment_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ================= PAYMENT → NOTIFICATION =================
    public static final String PAYMENT_COMPLETED_EXCHANGE = "payment.completed.exchange";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";
    public static final String PAYMENT_COMPLETED_QUEUE = "payment.completed.queue";

    @Bean
    public Queue paymentCompletedQueue() {
        return new Queue(PAYMENT_COMPLETED_QUEUE, true);
    }

    @Bean
    public TopicExchange paymentCompletedExchange() {
        return new TopicExchange(PAYMENT_COMPLETED_EXCHANGE);
    }

    @Bean
    public Binding paymentCompletedBinding() {
        return BindingBuilder
                .bind(paymentCompletedQueue())
                .to(paymentCompletedExchange())
                .with(PAYMENT_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
