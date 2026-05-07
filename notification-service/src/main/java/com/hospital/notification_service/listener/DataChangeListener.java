package com.hospital.notification_service.listener;

import com.hospital.notification_service.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataChangeListener {

    private static final Logger log = LoggerFactory.getLogger(DataChangeListener.class);

    @Autowired
    @Lazy
    private SimpMessagingTemplate messaging;

    @RabbitListener(queues = {RabbitConfig.DOCTOR_WS_QUEUE, RabbitConfig.DOCTOR_UPDATE_WS_QUEUE})
    public void onDoctorChange(Message msg) {
        log.debug("Doctor change event — notifying WebSocket clients");
        messaging.convertAndSend("/topic/doctors", "changed");
    }

    @RabbitListener(queues = {RabbitConfig.PATIENT_WS_QUEUE, RabbitConfig.PATIENT_UPDATE_WS_QUEUE})
    public void onPatientChange(Message msg) {
        log.debug("Patient change event — notifying WebSocket clients");
        messaging.convertAndSend("/topic/patients", "changed");
    }

    @RabbitListener(queues = {RabbitConfig.STAFF_WS_QUEUE, RabbitConfig.STAFF_UPDATE_WS_QUEUE})
    public void onStaffChange(Message msg) {
        log.debug("Staff change event — notifying WebSocket clients");
        messaging.convertAndSend("/topic/staff", "changed");
    }

    @RabbitListener(queues = RabbitConfig.APPOINTMENT_WS_QUEUE)
    public void onAppointmentChange(Message msg) {
        log.debug("Appointment change event — notifying WebSocket clients");
        messaging.convertAndSend("/topic/appointments", "changed");
    }
}
