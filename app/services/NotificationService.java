package services;

import models.Notification;
import models.NotificationStatus;

import java.util.List;

public class NotificationService {
    public static List<Notification> getOpenNotifications(){
        final List<Notification> notifications = Notification.getNotificationsByStatus(NotificationStatus.OPEN);
        return notifications;
    }
}
