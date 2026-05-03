package com.hospital.notification_service.repository;

import com.hospital.notification_service.entity.NotificationLog;
import com.hospital.notification_service.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByRecipientEmailOrderByCreatedAtDesc(String email);
    List<NotificationLog> findByTypeOrderByCreatedAtDesc(NotificationType type);
}
