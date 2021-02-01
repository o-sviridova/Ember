package services;

import com.fasterxml.jackson.databind.JsonNode;
import models.Request;
import models.RequestType;
import play.libs.Json;

import java.util.HashMap;
import java.util.List;

public class RequestService {
    public static List<Request> getRequests(){
        final List<Request> requests = Request.getRequests();
        return requests;
    }

    public static List<Request> getRequests(int organization){
        final List<Request> requests = Request.getRequests(organization);
        return requests;
    }
}
