package models;

public enum Sex {
    MALE(0, "MALE"),
    FEMALE(1, "FEMALE"),
    ;

    public final int id;
    public final String name;

    Sex(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
