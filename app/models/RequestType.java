package models;

import io.ebean.Finder;

public enum RequestType {
    CREATE_VACANCY_TYPE(0, "CREATE_VACANCY_TYPE"),
    CREATE_VACANCY(1, "CREATE_VACANCY"),
    HIRE_CANDIDATE(2, "HIRE_CANDIDATE"),
    FREE_VACANCY(3, "FREE_VACANCY")
    ;

    public final int id;
    public final String name;

    RequestType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Finder<Integer, RequestType> find = new Finder<>(RequestType.class);
}
