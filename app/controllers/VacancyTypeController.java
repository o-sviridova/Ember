package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Organization;
import models.Request;
import models.Role;
import models.VacancyType;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Tabs;
import views.html.vacancy_types.create;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VacancyTypeController extends Controller implements AccessInterface{
    Set roleSet0 = new HashSet();
    Set roleSet1 = new HashSet();

    VacancyTypeController(){
        roleSet0.add(Role.MAYOR);
        roleSet0.add(Role.CHIEF);
        roleSet0.add(Role.HABITANT);
        roleSet0.add(Role.POSTMAN);
        roleSet1.add(Role.CHIEF);
    }

    public boolean checkRole(Set<Role> roleSet){
        Role accRole = Secured.getAccount(ctx()).role;
        Logger.info("Check access for VacancyType Controller, role " + accRole.name);
        if (roleSet.contains(accRole))
            return true;
        else return false;
    }

    @Inject
    FormFactory formFactory;

    @Security.Authenticated(Secured.class)
    public Result create() {
        if (!checkRole(roleSet1))
            return forbidden("No access for create VacancyType");
        Form<VacancyType> vacancyTypeForm = formFactory.form(VacancyType.class);
        return ok(create.render(vacancyTypeForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public Result save() {
        if (!checkRole(roleSet1))
            return forbidden("No access for save VacancyType");
        Form<VacancyType> vacancyTypeForm = formFactory.form(VacancyType.class).bindFromRequest();
        if (vacancyTypeForm.hasErrors()) {
            return null;
            //return badRequest(create.render(vacancyForm, provider));
        }
        VacancyType draftVacancyType = vacancyTypeForm.get();

        Organization organization = Organization.find.byId(draftVacancyType.organization.id);
        if (organization == null) {
            return notFound("Organization is not found");
        }
        Request.requestVacancyType(organization, draftVacancyType);
        return redirect(routes.LoginController.tab(Tabs.REQUESTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result getVacancyTypesByOrganization(int organizationId) {
        if (!checkRole(roleSet0))
            return forbidden("No access for get VacancyType for organization");
        ObjectNode organizationNode = Json.newObject();
        ArrayNode typeNodes = organizationNode.putArray("vacancyTypes");

        Organization organization = Organization.find.byId(organizationId);
        if (organization == null) return ok(organizationNode);

        List<VacancyType> vacancyTypes = VacancyType.getVacancyTypesByOrganizationId(organization.id);
        for (VacancyType vacancyType : vacancyTypes) {
            ObjectNode typeNode = Json.newObject();
            typeNode.put("name", vacancyType.name);
            typeNode.put("id", vacancyType.id);
            typeNodes.add(typeNode);
        }

        return ok(organizationNode);
    }
}
