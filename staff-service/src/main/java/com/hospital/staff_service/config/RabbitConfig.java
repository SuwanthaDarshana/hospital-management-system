package com.hospital.staff_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // ================= AUTH → STAFF =================
    // Staff created event
    public static final String STAFF_QUEUE = "staff.queue";
    public static final String STAFF_EXCHANGE = "staff.exchange";
    public static final String STAFF_ROUTING_KEY = "staff.created";

    // ================= STAFF → AUTH =================
    // Staff profile update → Auth update
    public static final String STAFF_UPDATE_QUEUE = "staff.update.queue";
    public static final String STAFF_UPDATE_EXCHANGE = "staff.update.exchange";
    public static final String STAFF_UPDATE_ROUTING_KEY = "staff.updated";

    // ---------- Staff Queue (Auth → Staff) ----------
    @Bean
    public Queue staffQueue() {
        return new Queue(STAFF_QUEUE, true);
    }

    @Bean
    public TopicExchange staffExchange() {
        return new TopicExchange(STAFF_EXCHANGE);
    }

    @Bean
    public Binding staffBinding() {
        return BindingBuilder
                .bind(staffQueue())
                .to(staffExchange())
                .with(STAFF_ROUTING_KEY);
    }

    // ---------- Auth Queue (Staff → Auth) ----------
    @Bean
    public Queue authQueue() {
        return new Queue(STAFF_UPDATE_QUEUE, true);
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(STAFF_UPDATE_EXCHANGE);
    }

    @Bean
    public Binding authBinding() {
        return BindingBuilder
                .bind(authQueue())
                .to(authExchange())
                .with(STAFF_UPDATE_ROUTING_KEY);
    }

    // JSON serialization
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
