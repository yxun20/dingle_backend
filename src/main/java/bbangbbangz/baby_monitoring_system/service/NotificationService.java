package bbangbbangz.baby_monitoring_system.service;

import bbangbbangz.baby_monitoring_system.domain.Notification;
import bbangbbangz.baby_monitoring_system.domain.Notification.NotificationStatus;
import bbangbbangz.baby_monitoring_system.domain.Notification.NotificationType;
import bbangbbangz.baby_monitoring_system.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void sendNotification(Notification notification) {
        try {
            if (notification == null || notification.getNotificationType() == null) {
                throw new IllegalArgumentException("Notification and its type are required");
            }

            switch (notification.getNotificationType()) {
                case SMS:
                    sendSms(notification);
                    break;
                case PUSH:
                    sendPush(notification);
                    break;
                case CALL:
                    sendCall(notification);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported notification type: " + notification.getNotificationType());
            }

            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            if (notification != null) {
                notification.setStatus(NotificationStatus.FAILED);
            }
            System.err.println("Failed to send notification: " + e.getMessage());
        } finally {
            if (notification != null) {
                notificationRepository.save(notification);
            }
        }
    }

    private void sendSms(Notification notification) {
        if (notification.getContact() == null || notification.getContact().getPhoneNumber() == null) {
            throw new IllegalArgumentException("Contact phone number is required for SMS notifications");
        }
        // Mock SMS sending logic (replace with real SMS API integration)
        System.out.println("Sending SMS to: " + notification.getContact().getPhoneNumber());
    }

    private void sendPush(Notification notification) {
        if (notification.getContact() == null || notification.getContact().getEmail() == null) {
            throw new IllegalArgumentException("Contact email is required for push notifications");
        }
        // Mock Push notification logic (replace with Firebase or similar service)
        System.out.println("Sending Push Notification to: " + notification.getContact().getEmail());
    }

    private void sendCall(Notification notification) {
        if (notification.getContact() == null || notification.getContact().getPhoneNumber() == null) {
            throw new IllegalArgumentException("Contact phone number is required for call notifications");
        }
        // Mock Call notification logic (replace with a VoIP API like Twilio)
        System.out.println("Making Call to: " + notification.getContact().getPhoneNumber());
    }
}