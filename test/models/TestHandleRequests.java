package models;

import com.fasterxml.jackson.databind.JsonNode;
import exceptions.ModelException;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.Logger;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.test.WithApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.TestCase.assertTrue;


public class TestHandleRequests extends WithApplication {

    public Habitant chief;
    public Habitant candidate1;
    public Habitant worker1;
    public Organization organization;
    public Vacancy vacancy;
    public VacancyType vacancyType;
    public String draftTypeName1 = "newType1";
    public String draftTypeName2 = "newType2";
    public int posCount1 = 1;
    public int posCount2 = 2;
    public static Request requestVacancyType1;
    public Request requestVacancyType2;
    public Request requestVacancy1;
    public Request requestVacancy2;
    public Request requestHireCandidate1;
    public Request requestHireCandidate2;
    public Request requestFreeVacancy1;
    public Request requestFreeVacancy2;
    VacancyType newDraftType;
    VacancyType existDraftType;
    Vacancy draftVacancy1;
    Vacancy draftVacancy2;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Before
    public void setUp() throws ModelException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            date = dateFormat.parse("23/09/1985");
        } catch (Exception e) {
            date = new Date(System.currentTimeMillis());
        }
        chief = Habitant.create("Pep", "Guardiola", "Bat'kovich", Sex.MALE,
                date, true, null, "test_chief_2", "qwerty", Role.CHIEF);
        candidate1 = Habitant.create("Petr", "Petrov", "Petrovich", Sex.MALE,
                date, true, null, "test_habitant3", "qwerty", Role.HABITANT);
        worker1 = Habitant.create("Petr", "Petrov", "Petrovich", Sex.MALE,
                date, true, null, "test_habitant4", "qwerty", Role.HABITANT);
        organization = Organization.create("organization2", chief);

        vacancyType = VacancyType.create("vacancyType1", organization);
        vacancy = Vacancy.create(vacancyType);

        newDraftType = new VacancyType();
        newDraftType.name = draftTypeName1;
        newDraftType.organization = organization;

        existDraftType = new VacancyType();
        existDraftType.name = vacancyType.name;
        existDraftType.organization = vacancyType.organization;

        draftVacancy1 = new Vacancy();
        draftVacancy1.vacancyType = vacancyType;
        draftVacancy1.positionCount = posCount1;

        draftVacancy2 = new Vacancy();
        draftVacancy2.vacancyType = vacancyType;
        draftVacancy2.positionCount = posCount2;

        requestVacancyType1 = Request.requestVacancyType(organization, newDraftType);
        requestVacancyType2 = Request.requestVacancyType(organization, existDraftType);

        requestVacancy1 = Request.requestVacancy(organization, draftVacancy1);
        requestVacancy2 = Request.requestVacancy(organization, draftVacancy2);

        requestHireCandidate1 = Request.requestHiringCandidate(organization, candidate1.id, candidate1.name, vacancy.id, vacancy.vacancyType.name);

        requestHireCandidate2 = Request.requestHiringCandidate(organization, worker1.id, worker1.name, vacancy.id, vacancy.vacancyType.name);

        requestFreeVacancy1 = Request.requestFreeVacancy(organization, worker1.name, vacancy.id, vacancy.vacancyType.name);
        requestFreeVacancy2 = Request.requestFreeVacancy(organization, candidate1.name, vacancy.id, vacancy.vacancyType.name);

    }

    @Test
    public void testHandleRequest_CREATE_VACANCY_TYPE_ACCEPTED() throws ModelException {
        vacancyType = VacancyType.create("vacancyType0", organization);

        Request request = Request.handleRequest(requestVacancyType1);
        VacancyType createdType = VacancyType.getVacancyTypeByName(draftTypeName1, organization.id);
        assertTrue("Request is not accepted",
                request.status.equals(RequestStatus.ACCEPTED) && createdType != null);

    }

    @Test
    public void testHandleRequest_CREATE_VACANCY_TYPE_DENIED() {
        Request request = Request.handleRequest(requestVacancyType2);
        assertTrue("Request is not denied",
                request.status.equals(RequestStatus.DENIED));

    }

    @Test
    public void testHandleRequest_CREATE_VACANCY_ACCEPTED_1() {
        int beforeSize = Vacancy.getOpenVacanciesByOrganization(organization.id).size();
        Request request = Request.handleRequest(requestVacancy1);
        int afterSize = Vacancy.getOpenVacanciesByOrganization(organization.id).size();
        assertTrue("Count of position does not match specified request",
                request.status.equals(RequestStatus.ACCEPTED) && afterSize - beforeSize == posCount1);

    }

    @Test
    public void testHandleRequest_CREATE_VACANCY_ACCEPTED_2() {
        int beforeSize = Vacancy.getOpenVacanciesByOrganization(organization.id).size();
        Request request = Request.handleRequest(requestVacancy2);
        int afterSize = Vacancy.getOpenVacanciesByOrganization(organization.id).size();
        assertTrue("Count of position does not match specified request",
                request.status.equals(RequestStatus.ACCEPTED) && afterSize - beforeSize == posCount2);

    }

    @Test
    public void testHandleRequest_HIRE_CANDIDATE_ACCEPTED() {

        int beforeSize = Vacancy.getOpenVacanciesByOrganization(organization.id).size();
        Request request = Request.handleRequest(requestHireCandidate1);

        int afterSize = Vacancy.getOpenVacanciesByOrganization(organization.id).size();
        assertTrue("Count of position does not match specified request",
                request.status.equals(RequestStatus.ACCEPTED) && (beforeSize - afterSize == 1));

    }
    @Test
    public void testHandleRequest_HIRE_CANDIDATE_DENIED() throws ModelException{
        Vacancy.assignWorker(vacancy,worker1);
        int beforeSize = Vacancy.getOpenVacanciesByOrganization(organization.id).size();
        Logger.info("size {}", beforeSize);
        Request request = Request.handleRequest(requestHireCandidate2);

        int afterSize = Vacancy.getOpenVacanciesByOrganization(organization.id).size();
        Logger.info("size {}", afterSize);
        assertTrue("Count of position does not match specified request",
                request.status.equals(RequestStatus.DENIED)  && (beforeSize == afterSize));

    }

    @Test
    public void testHandleRequest_FREE_VACANCY_ACCEPTED() throws ModelException{
        Vacancy.assignWorker(vacancy,candidate1);
        Request request = Request.handleRequest(requestFreeVacancy1);
        assertTrue("Vacancy is not accepted",
                request.status.equals(RequestStatus.ACCEPTED) && candidate1.getCurrentWork()==null);

    }

    @Test
    public void testHandleRequest_FREE_VACANCY_DENIED_1() throws ModelException{
        Vacancy.assignWorker(vacancy,worker1);
        Vacancy.closeVacancy(vacancy);
        Request request = Request.handleRequest(requestFreeVacancy2);
        assertTrue("Request is not denied",
                request.status.equals(RequestStatus.DENIED) && candidate1.getCurrentWork()==null);

    }

}
