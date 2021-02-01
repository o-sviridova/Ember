package models;

public enum Role {
    HABITANT(0, "HABITANT"),
    MAYOR(1, "MAYOR"),
    CHIEF(2, "CHIEF"),
    POSTMAN(3, "POSTMAN")
    ;

    public final int id;
    public final String name;

    Role(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
