package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Notification extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String message;

    @Enumerated(EnumType.STRING)
    public NotificationStatus status;

    public static Notification create(String message) {
        Notification notification = new Notification();
        notification.message = message;
        notification.status = NotificationStatus.OPEN;
        notification.save();
        return notification;
    }

    public static Notification accept(Notification notification) {
        notification.status = NotificationStatus.ACCEPTED;
        notification.save();
        return notification;
    }

    public static Finder<Integer, Notification> find = new Finder<>(Notification.class);

    public static List<Notification> getNotificationsByStatus(NotificationStatus status){
        final List<Notification> notifications = Notification.find.query().where()
                .eq("status", status)
                .findList();
        return notifications;
    }
}
