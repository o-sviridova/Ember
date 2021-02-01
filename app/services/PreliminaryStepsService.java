package services;

import exceptions.ModelException;
import models.*;
import play.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PreliminaryStepsService {
    private static AtomicBoolean isInited = new AtomicBoolean(false);

    public PreliminaryStepsService() {
        if (isInited.compareAndSet(false, true)) {
            if (VacancyType.find.all().isEmpty()) {
                init();
            }
            //volumeTest();
        }
    }

    private static void volumeTest() {
        List<Organization> orgs = Organization.getAllOrganizations();
        Organization organization = orgs.get(0);
        VacancyType type = new VacancyType();
        try {
            //vacancy types
            for (int i = 4; i < 1004; i++) {

                type = VacancyType.create("work" + i, organization);
            }

            for (int j = 56; j < 20056; j++) {
                Habitant.create("Petra", "Petrova", "Petrovna", Sex.FEMALE,
                        new Date(System.currentTimeMillis()), true, type, "habitant" + j, "qwerty", Role.HABITANT);
            }
        }catch (ModelException e) {
                e.printStackTrace();
        }
    }

    public static void init() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date;
            try {
                date = dateFormat.parse("23/09/1985");
            } catch (Exception e) {
                date = new Date(System.currentTimeMillis());
            }

            Habitant mayor = Habitant.create("Petr", "Petrovich", "Bat'kovich", Sex.MALE,
                    date, true, null, "mayor", "qwerty", Role.MAYOR);
            Habitant postman = Habitant.create("I", "Forgot", "Her'name", Sex.FEMALE,
                    date, true, null, "postman", "qwerty", Role.POSTMAN);
            //fiction work
            //todo how it can be init ??
            VacancyType mayorType = VacancyType.create(VacancyType.MAYOR_TYPE);
            VacancyType postmanType = VacancyType.create(VacancyType.POSTMAN_TYPE);
            Vacancy mayorVac = Vacancy.create(mayorType);
            Vacancy postmanVac = Vacancy.create(postmanType);
            Vacancy.assignWorker(mayorVac, mayor);
            Vacancy.assignWorker(postmanVac, postman);

            //normal flows
            Habitant chief = Habitant.create("Pep", "Guardiola", "Bat'kovich", Sex.MALE,
                    date, true, null, "chief", "qwerty", Role.CHIEF);

            Organization organization = Organization.create("Organization1", chief);

            VacancyType type1 = VacancyType.create("work1", organization);
            VacancyType type2 = VacancyType.create("work2", organization);
            VacancyType type3 = VacancyType.create("work3", organization);

            //create first normal worker
            Habitant worker1 = Habitant.create("Petr", "Petrov", "Petrovich", Sex.MALE,
                    date, true, type2, "habitant1", "qwerty", Role.HABITANT);

            //hire and fire normal worker
            Vacancy vac1 = Vacancy.create(type1);
            Vacancy.assignWorker(vac1, worker1);
            Vacancy.closeVacancy(vac1);

            //open vacancies
            Vacancy vac2 = Vacancy.create(type2);
            Vacancy.create(type1, 2);
            Vacancy.create(type2);

            Habitant worker2 = Habitant.create("Petra", "Petrova", "Petrovna", Sex.FEMALE,
                    date, true, type3, "habitant2", "qwerty", Role.HABITANT);

            //hire candidate
            Vacancy.assignWorker(vac2, worker2);

            Habitant worker3 = Habitant.create("Lionel", "Messi", "--", Sex.MALE,
                    date, true, type2, "habitant3", "qwerty", Role.HABITANT);
            Habitant worker4 = Habitant.create("Cris", "Ronaldo", "--", Sex.FEMALE,
                    date, true, type2, "habitant4", "qwerty", Role.HABITANT);
        } catch (ModelException e) {
            Logger.error("Error while initialization: {}", e.getMessage());
        }
    }

}
