package models;

public enum NotificationStatus {
    OPEN(0, "OPEN"),
    ACCEPTED(1, "ACCEPTED")
    ;

    public final int id;
    public final String name;

    NotificationStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
