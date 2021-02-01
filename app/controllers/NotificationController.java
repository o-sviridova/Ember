package controllers;

import models.Notification;
import models.Role;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Tabs;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class NotificationController extends Controller implements AccessInterface{
    Set roleSet0 = new HashSet();

    NotificationController(){
        roleSet0.add(Role.POSTMAN);
    }

    public boolean checkRole(Set<Role> roleSet){
        Role accRole = Secured.getAccount(ctx()).role;
        Logger.info("Check access for Notification Controller, role " + accRole.name);
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
        Notification notification = Notification.find.byId(id);
        if (notification == null) {
            return notFound("Notification is not found");
        }
        Notification.accept(notification);
        return redirect(routes.LoginController.tab(Tabs.MESSAGES.name));
    }

    public String[] getParseJson(Notification notification){

        String messageDetails[] = new String[2];
        //habitant

        //vacancy

        return messageDetails;
    }
}
