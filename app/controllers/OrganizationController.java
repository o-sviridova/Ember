package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Habitant;
import models.Organization;
import models.Role;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Tabs;
import views.html.organizations.create;
import views.html.organizations.edit;
import views.html.organizations.reopen;
import views.html.organizations.search_result;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganizationController extends Controller implements AccessInterface{
    Set<Role> roleSet0 = new HashSet();
    Set<Role> roleSet1 = new HashSet();
    Set<Role> roleSet2 = new HashSet();

    OrganizationController(){
        roleSet0.add(Role.MAYOR);
        roleSet1.add(Role.MAYOR);
        roleSet1.add(Role.CHIEF);
        roleSet2.add(Role.MAYOR);
        roleSet2.add(Role.CHIEF);
        roleSet2.add(Role.HABITANT);
        roleSet2.add(Role.POSTMAN);
    }

    @Override
    public boolean checkRole(Set<Role> roleSet) {
        Role accRole = Secured.getAccount(ctx()).role;
        Logger.info("Check access for Organization Controller, role " + accRole.name);
        if (roleSet.contains(accRole))
            return true;
        else return false;
    }

    @Inject
    FormFactory formFactory;

    @Security.Authenticated(Secured.class)
    public Result create() {
        if (!checkRole(roleSet0))
            return forbidden("No access for create Organization");
        Form<Organization> organizationForm = formFactory.form(Organization.class);
        return ok(create.render(organizationForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public Result save() {
        if (!checkRole(roleSet0))
            return forbidden("No access for save Organization");
        Form<Organization> organizationForm = formFactory.form(Organization.class).bindFromRequest();
        if (organizationForm.hasErrors()) {
            return badRequest(create.render(organizationForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
        }
        Organization draftOrganization = organizationForm.get();

        Habitant chief = Habitant.find.byId(draftOrganization.chief.id);
        if (chief == null) {
            return notFound("Habitant is not found");
        }

        Organization organization = Organization.create(draftOrganization.name, chief);

        return redirect(routes.LoginController.tab(Tabs.ORGANIZATIONS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result edit(Integer id) {
        if (!checkRole(roleSet0))
            return forbidden("No access for edit Organization");
        Organization organization = Organization.find.byId(id);
        if (organization == null) {
            return notFound("Organization not found");
        }
        Form<Organization> organizationForm = formFactory.form(Organization.class).fill(organization);
        return ok(edit.render(organizationForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public Result update() {
        if (!checkRole(roleSet0))
            return forbidden("No access for update Organization");
        Form<Organization> organizationForm = formFactory.form(Organization.class).bindFromRequest();
        Organization draftOrganization = organizationForm.get();
        if (draftOrganization.chief == null || draftOrganization.chief.id == null)
            return notFound("Chief is not found");

        Habitant newChief = Habitant.find.byId(draftOrganization.chief.id);
        if (newChief == null) return notFound("New chief is not found");

        Organization organization = Organization.find.byId(draftOrganization.id);
        if (organization == null) return notFound("Organization not found");

        Organization.update(organization, draftOrganization.name, newChief);

        return redirect(routes.LoginController.tab(Tabs.ORGANIZATIONS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result show(Integer id) {
        if (!checkRole(roleSet1))
            return forbidden("No access for save Organization");
        Organization organization = Organization.find.byId(id);
        if (organization == null) {
            return notFound("User not found");
        }
        return ok();
    }

    @Security.Authenticated(Secured.class)
    public Result reopen(Integer id) {
        if (!checkRole(roleSet0))
            return forbidden("No access for reopen Organization");
        Organization organization = Organization.find.byId(id);
        if (organization == null) {
            return notFound("Organization not found");
        }
        Form<Organization> organizationForm = formFactory.form(Organization.class).fill(organization);
        return ok(reopen.render(organizationForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public Result recover() {
        if (!checkRole(roleSet0))
            return forbidden("No access for recover Organization");
        Form<Organization> organizationForm = formFactory.form(Organization.class).bindFromRequest();
        Organization draftOrganization = organizationForm.get();
        if (draftOrganization.chief == null || draftOrganization.chief.id == null)
            return notFound("New chief is not found");

        Organization organization = Organization.find.byId(draftOrganization.id);
        if (organization == null) return notFound("Organization not found");

        //if (!organization.isClosed) return notFound("Organization is already open");

        Habitant newChief = Habitant.find.byId(draftOrganization.chief.id);
        if (newChief == null) return notFound("New chief is not found");

        Organization.reopen(organization, newChief);
        return redirect(routes.LoginController.tab(Tabs.ORGANIZATIONS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result close(Integer id) {
        if (!checkRole(roleSet0))
            return forbidden("No access for close Organization");
        Organization organization = Organization.find.byId(id);
        if (organization == null) return notFound("Organization is not found");

        //todo transfer checks to create and hanle exception
        if (organization.isClosed) return notFound("Organization is already closed");
        List<Habitant> habitants = Habitant.getHabitantsByOrganizationId(id);
        if (habitants.size() > 1) {
            return notFound("Organization has workers (not only chief), hire them first");
        }

        Organization.close(organization);

        return redirect(routes.LoginController.tab(Tabs.ORGANIZATIONS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result searchOrganizationsBy(String query) {
        if (!checkRole(roleSet2))
            return forbidden("No access for search Organization");
        Logger.info("searchOrganizationsBy: query: " + query + ";");
        List<Organization> organizations = Organization.searchOrganizationsBy(query);
        return ok(search_result.render(organizations));
    }

    @Security.Authenticated(Secured.class)
    public Result getOrganizations() {
        if (!checkRole(roleSet2))
            return forbidden("No access for get set of Organizations");
        ObjectNode result = Json.newObject();
        List<Organization> organizations = Organization.getAllOrganizations();

        ArrayNode organizationNodes = result.putArray("organizations");
        for (Organization organization : organizations) {
            ObjectNode organizationNode = Json.newObject();
            organizationNode.put("id", organization.id);
            organizationNode.put("name", organization.name);
            organizationNodes.add(organizationNode);
        }

        return ok(result);
    }
}
