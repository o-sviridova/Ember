package services;

import models.Vacancy;

import java.util.List;

public class VacancyService {

    public static List<Vacancy> getWorkHistoryByHabitantId(int id) {
        final List<Vacancy> vacancies = Vacancy.getWorkHistoryByHabitantId(id);
        return vacancies;
    }

    public static List<Vacancy> getOpenVacanciesByOrganization(int id) {
        return Vacancy.getOpenVacanciesByOrganization(id);
    }

    public static List<Vacancy> getVacanciesByOrganizations(int id) {
        return Vacancy.getVacanciesByOrganizations(id);
    }

}
