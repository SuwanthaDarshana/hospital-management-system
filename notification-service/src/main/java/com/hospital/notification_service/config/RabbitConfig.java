package com.hospital.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ================= AUTH → NOTIFICATION =================
    public static final String PASSWORD_RESET_QUEUE = "password.reset.queue";
    public static final String PASSWORD_RESET_EXCHANGE = "password.reset.exchange";
    public static final String PASSWORD_RESET_ROUTING_KEY = "password.reset";

    // ================= PAYMENT → NOTIFICATION =================
    public static final String PAYMENT_COMPLETED_QUEUE = "payment.completed.notification.queue";
    public static final String PAYMENT_COMPLETED_EXCHANGE = "payment.completed.exchange";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";

    @Bean
    public Queue passwordResetQueue() {
        return new Queue(PASSWORD_RESET_QUEUE, true);
    }

    @Bean
    public TopicExchange passwordResetExchange() {
        return new TopicExchange(PASSWORD_RESET_EXCHANGE);
    }

    @Bean
    public Binding passwordResetBinding() {
        return BindingBuilder
                .bind(passwordResetQueue())
                .to(passwordResetExchange())
                .with(PASSWORD_RESET_ROUTING_KEY);
    }

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
