package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import exceptions.ModelException;
import io.ebean.Finder;
import io.ebean.Model;
import play.Logger;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@SequenceGenerator(name = "req_vacancy", initialValue = 1000, allocationSize = 100)
@Table(name = "requests")
public class Request extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Lob
    @Column( length = 10000 )
    public String json;

    @Enumerated(EnumType.STRING)
    public RequestStatus status;

    @Enumerated(EnumType.STRING)
    public RequestType requestType;

    @ManyToOne
    public Organization organization;

    public static Request create(RequestType requestType,
                                 Organization organization,
                                 String json) {
        final Request req = new Request();
        req.requestType = requestType;
        req.organization = organization;
        req.json = json;
        req.status = RequestStatus.OPEN;
        req.save();
        return req;
    }

    public static Request requestVacancyType(Organization organization,
                                             VacancyType draftVacancyType) {
        String vacancyTypeJson = Json.toJson(draftVacancyType).toString();
        Request req = Request.create(RequestType.CREATE_VACANCY_TYPE, organization, vacancyTypeJson);
        return req;
    }

    public static Request requestVacancy(Organization organization,
                                         Vacancy draftVacancy) {
        String vacancyJson = Json.toJson(draftVacancy).toString();
        Request req = Request.create(RequestType.CREATE_VACANCY, organization, vacancyJson);
        return req;
    }

    public static Request requestHiringCandidate(Organization organization,
                                                 int habitantId,
                                                 String habitantName,
                                                 int vacancyId,
                                                 String vacancyTypeTitle) {
        String json = generateHireCandidateJSONString(habitantId, habitantName, vacancyId, vacancyTypeTitle);
        Request req = Request.create(RequestType.HIRE_CANDIDATE, organization, json);
        return req;
    }

    public static Request requestFreeVacancy(Organization organization,
                                              String habitantName,
                                              int vacancyId,
                                              String vacancyTypeTitle) {
        String json = generateFreeVacancyJSONString(habitantName, vacancyId, vacancyTypeTitle);
        Request req = Request.create(RequestType.FREE_VACANCY, organization, json);
        return req;
    }


    public static Request accept(Request request) {
        if (request.status != RequestStatus.OPEN) return request;
        request.status = RequestStatus.ACCEPTED;
        request.update();
        return request;
    }

    public static Request deny(Request request) {
        if (request.status != RequestStatus.OPEN) return request;
        request.status = RequestStatus.DENIED;
        request.update();
        return request;
    }

    public static Request abort(Request request) {
        if (request.status != RequestStatus.OPEN) return request;
        request.status = RequestStatus.ABORTED;
        request.update();
        return request;
    }

    public static Request handleRequest(Request request) {
        switch (request.requestType) {
            case CREATE_VACANCY_TYPE:
                return createVacancyType(request);
            case CREATE_VACANCY:
                return createVacancy(request);
            case HIRE_CANDIDATE:
                return hireCandidate(request);
            case FREE_VACANCY:
                return freeVacancy(request);
        }
        return request;
    }

    private static Request createVacancyType(Request request) {
        JsonNode json = Json.parse(request.json);
        VacancyType draftVacancyType = Json.fromJson(json, VacancyType.class);
        VacancyType vacancyType;

        Logger.info("createVacancyType: {}",draftVacancyType.name);

        try {
            vacancyType = VacancyType.create(draftVacancyType.name, draftVacancyType.organization);
        } catch (ModelException e) {
            Logger.info("createVacancyType: {}", e.getMessage());
            vacancyType = null;
        }

        //todo handle sql exception of create
        if (vacancyType != null)
            Request.accept(request);
        else Request.deny(request);

        return request;
    }

    private static Request createVacancy(Request request) {
        JsonNode json = Json.parse(request.json);
        Vacancy draftVacancy = Json.fromJson(json, Vacancy.class);
        Logger.info("accepted json: {}", json);
        Vacancy.create(draftVacancy.vacancyType, draftVacancy.positionCount);
        Request.accept(request);
        return request;
    }

    private static Request hireCandidate(Request request) {
        JsonNode jsonNode = Json.parse(request.json);
        JsonNode habitantNode = jsonNode.get("habitantId");
        JsonNode vacancyNode = jsonNode.get("vacancyId");
        Integer habitantId = habitantNode.asInt();
        Integer vacancyId = vacancyNode.asInt();
        //todo check ?
        Habitant habitant = Habitant.find.byId(habitantId);
        Vacancy vacancy = Vacancy.find.byId(vacancyId);

        try {
            Vacancy.assignWorker(vacancy, habitant);
        } catch (ModelException e) {
            Logger.info("hireCandidate: {}", e.getMessage());
            Request.deny(request);
            return request;
        }
        Request.accept(request);
        Notification.create(request.getParseJson());
        return request;
    }

    private static Request freeVacancy(Request request) {
        JsonNode jsonNode = Json.parse(request.json);
        JsonNode vacancyNode = jsonNode.get("vacancyId");
        Integer vacancyId = vacancyNode.asInt();

        Vacancy vacancy = Vacancy.find.byId(vacancyId);
        try {
            Vacancy.closeVacancy(vacancy);
        } catch (ModelException e) {
            Logger.info(e.getMessage());
            Request.deny(request);
        }
        Request.accept(request);
        Notification.create(request.getParseJson());
        return request;
    }

    private static String generateHireCandidateJSONString(int habitantId, String habitantName,
                                                          int vacancyId, String vacancyTypeTitle) {
        ObjectNode typeNode = Json.newObject();
        typeNode.put("type", "Hire");
        typeNode.put("habitantId", habitantId);
        typeNode.put("habitantName", habitantName);
        typeNode.put("vacancyId", vacancyId);
        typeNode.put("vacancyTypeTitle", vacancyTypeTitle);
        return typeNode.toString();
    }

    private static String generateFreeVacancyJSONString(String habitantName,
                                                        int vacancyId, String vacancyTypeTitle) {
        ObjectNode typeNode = Json.newObject();
        typeNode.put("type", "Fire");
        typeNode.put("habitantName", habitantName);
        typeNode.put("vacancyId", vacancyId);
        typeNode.put("vacancyTypeTitle", vacancyTypeTitle);
        return typeNode.toString();
    }

    public static Finder<Integer, Request> find = new Finder<>(Request.class);

    public static List<Request> getRequests(){
        final List<Request> requests = Request.find.all();
        return requests;
    }

    public static List<Request> getRequests(int organization){
        final List<Request> requests = Request.find.query().where()
                .eq("organization_id", organization)
                .findList();
        return requests;
    }

    public String getParseJson(){
        ArrayList<String> messageDetails = new ArrayList();

        JsonNode jsonNode = Json.parse(this.json);
        switch (this.requestType) {
            case CREATE_VACANCY_TYPE:
                JsonNode vacTypeName = jsonNode.get("name");
                messageDetails.add("Count of position: " + vacTypeName.asText(""));
                break;
            case CREATE_VACANCY:
                JsonNode vacPosCount = jsonNode.get("positionCount");
                messageDetails.add("Count of position: " + vacPosCount.asText(""));
                JsonNode vacType = jsonNode.get("vacancyType");
                JsonNode vacTypeDesc = vacType.get("name");
                messageDetails.add("Vacancy type: " + vacTypeDesc.asText(""));
                break;
            case HIRE_CANDIDATE:
            case FREE_VACANCY:
                JsonNode habitantName = jsonNode.get("habitantName");
                messageDetails.add("Habitant name: " + habitantName.asText(""));
                JsonNode vacTitle = jsonNode.get("vacancyTypeTitle");
                messageDetails.add("Vacancy type: " + vacTitle.asText(""));

                break;
        }
        return String.join(", ", messageDetails);
    }
}
