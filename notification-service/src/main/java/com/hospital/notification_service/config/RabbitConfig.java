package com.hospital.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ================= AUTH → NOTIFICATION =================
    public static final String PASSWORD_RESET_QUEUE    = "password.reset.queue";
    public static final String PASSWORD_RESET_EXCHANGE = "password.reset.exchange";
    public static final String PASSWORD_RESET_ROUTING_KEY = "password.reset";

    // ================= PAYMENT → NOTIFICATION =================
    public static final String PAYMENT_COMPLETED_QUEUE    = "payment.completed.notification.queue";
    public static final String PAYMENT_COMPLETED_EXCHANGE = "payment.completed.exchange";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";

    // ================= DOCTOR EVENTS (auth-service → doctor-service + WS) =================
    public static final String DOCTOR_WS_QUEUE  = "doctor.ws.notification.queue";
    public static final String DOCTOR_EXCHANGE  = "doctor.exchange";
    public static final String DOCTOR_CREATED_KEY = "doctor.created";

    // ================= DOCTOR UPDATE EVENTS (doctor-service → auth-service + WS) =================
    public static final String DOCTOR_UPDATE_WS_QUEUE  = "doctor.update.ws.notification.queue";
    public static final String DOCTOR_UPDATE_EXCHANGE  = "doctor.update.exchange";
    public static final String DOCTOR_UPDATED_KEY      = "doctor.updated";

    // ================= PATIENT EVENTS (auth-service → patient-service + WS) =================
    public static final String PATIENT_WS_QUEUE   = "patient.ws.notification.queue";
    public static final String PATIENT_EXCHANGE   = "patient.exchange";
    public static final String PATIENT_CREATED_KEY = "patient.created";

    // ================= PATIENT UPDATE EVENTS (patient-service → auth-service + WS) =================
    public static final String PATIENT_UPDATE_WS_QUEUE  = "patient.update.ws.notification.queue";
    public static final String PATIENT_UPDATE_EXCHANGE  = "patient.update.exchange";
    public static final String PATIENT_UPDATED_KEY      = "patient.updated";

    // ================= STAFF EVENTS (auth-service → staff-service + WS) =================
    public static final String STAFF_WS_QUEUE   = "staff.ws.notification.queue";
    public static final String STAFF_EXCHANGE   = "staff.exchange";
    public static final String STAFF_CREATED_KEY = "staff.created";

    // ================= STAFF UPDATE EVENTS (staff-service → auth-service + WS) =================
    public static final String STAFF_UPDATE_WS_QUEUE  = "staff.update.ws.notification.queue";
    public static final String STAFF_UPDATE_EXCHANGE  = "staff.update.exchange";
    public static final String STAFF_UPDATED_KEY      = "staff.updated";

    // ================= APPOINTMENT EVENTS (appointment-service + WS) =================
    public static final String APPOINTMENT_WS_QUEUE  = "appointment.ws.notification.queue";
    public static final String APPOINTMENT_EXCHANGE  = "appointment.exchange";
    public static final String APPOINTMENT_ALL_KEY   = "appointment.*";

    // ── Existing: password reset ──────────────────────────────────────────────
    @Bean public Queue passwordResetQueue() { return new Queue(PASSWORD_RESET_QUEUE, true); }
    @Bean public TopicExchange passwordResetExchange() { return new TopicExchange(PASSWORD_RESET_EXCHANGE); }
    @Bean public Binding passwordResetBinding() {
        return BindingBuilder.bind(passwordResetQueue()).to(passwordResetExchange()).with(PASSWORD_RESET_ROUTING_KEY);
    }

    // ── Existing: payment completed ──────────────────────────────────────────
    @Bean public Queue paymentCompletedQueue() { return new Queue(PAYMENT_COMPLETED_QUEUE, true); }
    @Bean public TopicExchange paymentCompletedExchange() { return new TopicExchange(PAYMENT_COMPLETED_EXCHANGE); }
    @Bean public Binding paymentCompletedBinding() {
        return BindingBuilder.bind(paymentCompletedQueue()).to(paymentCompletedExchange()).with(PAYMENT_COMPLETED_ROUTING_KEY);
    }

    // ── WS: doctor created ───────────────────────────────────────────────────
    @Bean public Queue doctorWsQueue() { return new Queue(DOCTOR_WS_QUEUE, true); }
    @Bean public TopicExchange doctorExchange() { return new TopicExchange(DOCTOR_EXCHANGE); }
    @Bean public Binding doctorWsBinding() {
        return BindingBuilder.bind(doctorWsQueue()).to(doctorExchange()).with(DOCTOR_CREATED_KEY);
    }

    // ── WS: doctor updated (availability + profile changes) ──────────────────
    @Bean public Queue doctorUpdateWsQueue() { return new Queue(DOCTOR_UPDATE_WS_QUEUE, true); }
    @Bean public TopicExchange doctorUpdateExchange() { return new TopicExchange(DOCTOR_UPDATE_EXCHANGE); }
    @Bean public Binding doctorUpdateWsBinding() {
        return BindingBuilder.bind(doctorUpdateWsQueue()).to(doctorUpdateExchange()).with(DOCTOR_UPDATED_KEY);
    }

    // ── WS: patient created ──────────────────────────────────────────────────
    @Bean public Queue patientWsQueue() { return new Queue(PATIENT_WS_QUEUE, true); }
    @Bean public TopicExchange patientExchange() { return new TopicExchange(PATIENT_EXCHANGE); }
    @Bean public Binding patientWsBinding() {
        return BindingBuilder.bind(patientWsQueue()).to(patientExchange()).with(PATIENT_CREATED_KEY);
    }

    // ── WS: patient updated ──────────────────────────────────────────────────
    @Bean public Queue patientUpdateWsQueue() { return new Queue(PATIENT_UPDATE_WS_QUEUE, true); }
    @Bean public TopicExchange patientUpdateExchange() { return new TopicExchange(PATIENT_UPDATE_EXCHANGE); }
    @Bean public Binding patientUpdateWsBinding() {
        return BindingBuilder.bind(patientUpdateWsQueue()).to(patientUpdateExchange()).with(PATIENT_UPDATED_KEY);
    }

    // ── WS: staff created ────────────────────────────────────────────────────
    @Bean public Queue staffWsQueue() { return new Queue(STAFF_WS_QUEUE, true); }
    @Bean public TopicExchange staffExchange() { return new TopicExchange(STAFF_EXCHANGE); }
    @Bean public Binding staffWsBinding() {
        return BindingBuilder.bind(staffWsQueue()).to(staffExchange()).with(STAFF_CREATED_KEY);
    }

    // ── WS: staff updated ────────────────────────────────────────────────────
    @Bean public Queue staffUpdateWsQueue() { return new Queue(STAFF_UPDATE_WS_QUEUE, true); }
    @Bean public TopicExchange staffUpdateExchange() { return new TopicExchange(STAFF_UPDATE_EXCHANGE); }
    @Bean public Binding staffUpdateWsBinding() {
        return BindingBuilder.bind(staffUpdateWsQueue()).to(staffUpdateExchange()).with(STAFF_UPDATED_KEY);
    }

    // ── WS: appointments (created + updated + cancelled) ─────────────────────
    @Bean public Queue appointmentWsQueue() { return new Queue(APPOINTMENT_WS_QUEUE, true); }
    @Bean public TopicExchange appointmentExchange() { return new TopicExchange(APPOINTMENT_EXCHANGE); }
    @Bean public Binding appointmentWsBinding() {
        return BindingBuilder.bind(appointmentWsQueue()).to(appointmentExchange()).with(APPOINTMENT_ALL_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
