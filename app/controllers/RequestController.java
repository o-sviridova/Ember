package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Request;
import models.Role;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Tabs;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RequestController extends Controller implements AccessInterface {
    Set roleSet0 = new HashSet();
    Set roleSet1 = new HashSet();

    RequestController() {
        roleSet0.add(Role.MAYOR);
        roleSet1.add(Role.CHIEF);
    }

    public boolean checkRole(Set<Role> roleSet) {
        Role accRole = Secured.getAccount(ctx()).role;
        Logger.info("Check access for Request Controller, role " + accRole.name);
        if (roleSet.contains(accRole))
            return true;
        else return false;
    }

    @Inject
    FormFactory formFactory;

    @Security.Authenticated(Secured.class)
    public Result accept(Integer id) {
        if (!checkRole(roleSet0))
            return forbidden("No access for accept Notification");
        Request request = Request.find.byId(id);
        if (request == null) {
            return notFound("Request is not found");
        }
        Request.handleRequest(request);
        return redirect(routes.LoginController.tab(Tabs.MAYOR_REQUESTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result deny(Integer id) {
        if (!checkRole(roleSet0))
            return forbidden("No access for deny Notification");
        Request request = Request.find.byId(id);
        if (request == null) {
            return notFound("Request is not found");
        }
        Request.deny(request);
        return redirect(routes.LoginController.tab(Tabs.MAYOR_REQUESTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result abort(Integer id) {
        if (!checkRole(roleSet1))
            return forbidden("No access for abort Notification");
        Request request = Request.find.byId(id);
        if (request == null) {
            return notFound("Request is not found");
        }
        Request.abort(request);
        return redirect(routes.LoginController.tab(Tabs.REQUESTS.name));
    }
}
