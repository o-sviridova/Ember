package utils;

public enum Tabs {
    PROFILE("profile"),
    HABITANTS("habitants"),
    ORGANIZATIONS("organizations"),
    MAYOR_REQUESTS("mayor_requests"),
    MAYOR_VACANCIES("mayor_vacancies"),
    WORKERS("workers"),
    REQUESTS("requests"),
    VACANCIES("vacancies"),
    MESSAGES("messages")
    ;
    public final String name;

    Tabs(String name) {
        this.name = name;
    }

    public static Tabs getValue(String tab) {
        for(Tabs e: Tabs.values()) {
            if(e.name.equals(tab)) {
                return e;
            }
        }
        return null;
    }
}
