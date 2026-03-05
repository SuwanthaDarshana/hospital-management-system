package com.hospital.auth_service.config;

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


    // ================= AUTH → PATIENT =================
    // Patient created event
    public static final String PATIENT_QUEUE = "patient.queue";
    public static final String PATIENT_EXCHANGE = "patient.exchange";
    public static final String PATIENT_ROUTING_KEY = "patient.created";

    // ================= PATIENT → AUTH (Updates) =================
    // Patient profile update → Auth update
    public static final String PATIENT_UPDATE_QUEUE = "patient.update.queue";
    public static final String PATIENT_UPDATE_EXCHANGE = "patient.update.exchange";
    public static final String PATIENT_UPDATE_ROUTING_KEY = "patient.updated";

    // ================= AUTH → STAFF =================
    // Patient created event
    public static final String STAFF_QUEUE = "staff.queue";
    public static final String STAFF_EXCHANGE = "staff.exchange";
    public static final String STAFF_ROUTING_KEY = "staff.created";

    // ================= STAFF → AUTH (Updates) =================
    // Patient profile update → Auth update
    public static final String STAFF_UPDATE_QUEUE = "staff.update.queue";
    public static final String STAFF_UPDATE_EXCHANGE = "staff.update.exchange";
    public static final String STAFF_UPDATE_ROUTING_KEY = "staff.updated";





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

    // ---------- Patient Queue (Auth → Patient) ----------
    @Bean
    public Queue patientQueue() {
        return new Queue(PATIENT_QUEUE, true);
    }

    @Bean
    public TopicExchange patientExchange() {
        return new TopicExchange(PATIENT_EXCHANGE);
    }

    @Bean
    public Binding patientBinding() {
        return BindingBuilder
                .bind(patientQueue())
                .to(patientExchange())
                .with(PATIENT_ROUTING_KEY);
    }


    // ---------- Patient Update Queue ----------
    @Bean
    public Queue patientUpdateQueue() {
        return new Queue(PATIENT_UPDATE_QUEUE, true);
    }

    @Bean
    public TopicExchange patientUpdateExchange() {
        return new TopicExchange(PATIENT_UPDATE_EXCHANGE);
    }

    @Bean
    public Binding patientUpdateBinding() {
        return BindingBuilder
                .bind(patientUpdateQueue())
                .to(patientUpdateExchange())
                .with(PATIENT_UPDATE_ROUTING_KEY);
    }


    // ---------- Staff Queue (Auth → Patient) ----------
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


    // ---------- Staff Update Queue ----------
    @Bean
    public Queue staffUpdateQueue() {
        return new Queue(STAFF_UPDATE_QUEUE, true);
    }

    @Bean
    public TopicExchange staffUpdateExchange() {
        return new TopicExchange(STAFF_UPDATE_EXCHANGE);
    }

    @Bean
    public Binding staffUpdateBinding() {
        return BindingBuilder
                .bind(staffUpdateQueue())
                .to(staffUpdateExchange())
                .with(STAFF_UPDATE_ROUTING_KEY);
    }





    // JSON serialization
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
