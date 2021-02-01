package models;

public enum RequestStatus {
    OPEN(0, "OPEN"),
    ACCEPTED(1, "ACCEPTED"),
    ABORTED(2, "ABORTED"),
    DENIED(3, "DENIED")
    ;

    public final int id;
    public final String name;

    RequestStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
