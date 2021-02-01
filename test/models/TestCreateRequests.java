package models;

import com.fasterxml.jackson.databind.JsonNode;
import exceptions.ModelException;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.test.WithApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.TestCase.assertTrue;


public class TestCreateRequests extends WithApplication {

    public Habitant chief;
    public Habitant candidate1;
    public Organization organization;
    public Vacancy vacancy;
    public VacancyType vacancyType;
    public String draftTypeName1 = "newType1";
    public String draftTypeName2 = "newType2";
    public int posCount1 = 1;
    public int posCount2 = 2;
    VacancyType draftType;
    Vacancy draftVacancy1;
    Vacancy draftVacancy2;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

   @Before
    public void setUp() throws ModelException{
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            date = dateFormat.parse("23/09/1985");
        } catch (Exception e) {
            date = new Date(System.currentTimeMillis());
        }
        chief = Habitant.create("Pep", "Guardiola", "Bat'kovich", Sex.MALE,
                date, true, null, "test_chief", "qwerty", Role.CHIEF);
        candidate1 = Habitant.create("Petr", "Petrov", "Petrovich", Sex.MALE,
                date, true, null, "test_habitant1", "qwerty", Role.HABITANT);
        organization = Organization.create("organization1", chief);

        vacancyType = VacancyType.create("vacancyType1", organization);
        vacancy = Vacancy.create(vacancyType);


        draftType = new VacancyType();
        draftType.name = draftTypeName1;

        draftVacancy1 = new Vacancy();
        draftVacancy1.vacancyType = vacancyType;
        draftVacancy1.positionCount = posCount1;

        draftVacancy2 = new Vacancy();
        draftVacancy2.vacancyType = vacancyType;
        draftVacancy2.positionCount = posCount2;

    }


    @Test
    public void testRequestVacancyType_type() {
        Request requestVacancyType1 = Request.requestVacancyType(organization, draftType);

        assertTrue("Type does not match specified request",requestVacancyType1.requestType.equals(RequestType.CREATE_VACANCY_TYPE));
    }

    @Test
    public void testRequestVacancyType_json() {
        Request  requestVacancyType2 = Request.requestVacancyType(organization, draftType);

        JsonNode jsonNode = Json.parse(requestVacancyType2.json);
        JsonNode vacTypeName = jsonNode.get("name");

        assertTrue("JSON does not match the set values",draftType.name.equals(vacTypeName.textValue()));
    }

    @Test
    public void testRequestVacancy_type() {
        Request requestVacancy1 = Request.requestVacancy(organization, draftVacancy1);

        assertTrue("Type does not match specified request",requestVacancy1.requestType.equals(RequestType.CREATE_VACANCY));
    }

    @Test
    public void testRequestVacancy_json() {
        Request requestVacancy2 = Request.requestVacancy(organization, draftVacancy2);

        JsonNode jsonNode = Json.parse(requestVacancy2.json);
        JsonNode vacPosCount = jsonNode.get("positionCount");
        JsonNode vacType = jsonNode.get("vacancyType");
        JsonNode vacTypeId = vacType.get("id");


        assertTrue("JSON does not match the set values",
                draftVacancy2.positionCount.equals(vacPosCount.intValue()) &&
                        draftVacancy2.vacancyType.id.equals(vacTypeId.intValue()));
    }

    @Test
    public void testRequestHiringCandidate_type() {
        Request requestHireCandidate1 = Request.requestHiringCandidate(organization, candidate1.id, candidate1.name, vacancy.id, vacancy.vacancyType.name);

        assertTrue("Type does not match specified request",requestHireCandidate1.requestType.equals(RequestType.HIRE_CANDIDATE));
    }

    @Test
    public void testRequestHiringCandidate_json() {
        Request requestHireCandidate2 = Request.requestHiringCandidate(organization, candidate1.id, candidate1.name, vacancy.id, vacancy.vacancyType.name);

        JsonNode jsonNode = Json.parse(requestHireCandidate2.json);
        JsonNode habitantId = jsonNode.get("habitantId");
        JsonNode habitantName = jsonNode.get("habitantName");
        JsonNode vacHireId = jsonNode.get("vacancyId");
        JsonNode vacHireTitle = jsonNode.get("vacancyTypeTitle");


        assertTrue("JSON does not match the set values",
                candidate1.id.equals(habitantId.intValue()) && candidate1.name.equals(habitantName.textValue()) &&
                vacancy.id.equals(vacHireId.intValue()) && vacancy.vacancyType.name.equals(vacHireTitle.textValue()));
    }

    @Test
    public void testRequestFreeVacancy_type() {
        Request requestFreeVacancy1 = Request.requestHiringCandidate(organization, candidate1.id, candidate1.name, vacancy.id, vacancy.vacancyType.name);

        assertTrue("Type does not match specified request",requestFreeVacancy1.requestType.equals(RequestType.HIRE_CANDIDATE));
    }

    @Test
    public void testRequestFreeVacancy_json() {
        Request requestFreeVacancy2 = Request.requestFreeVacancy(organization, candidate1.name, vacancy.id, vacancy.vacancyType.name);

        JsonNode jsonNode = Json.parse(requestFreeVacancy2.json);
        JsonNode habitantName = jsonNode.get("habitantName");
        JsonNode vacFreeId = jsonNode.get("vacancyId");
        JsonNode vacFreeTitle = jsonNode.get("vacancyTypeTitle");


        assertTrue("JSON does not match the set values",
                candidate1.name.equals(habitantName.textValue()) &&
                        vacancy.id.equals(vacFreeId.intValue()) && vacancy.vacancyType.name.equals(vacFreeTitle.textValue()));
    }

}
