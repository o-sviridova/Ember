package controllers;

import models.*;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Tabs;
import views.html.habitants.search_result_candidates;
import views.html.vacancies.create;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VacancyController extends Controller implements AccessInterface{
    Set roleSet0 = new HashSet();

    VacancyController(){
        roleSet0.add(Role.CHIEF);
    }

    public boolean checkRole(Set<Role> roleSet){
        Role accRole = Secured.getAccount(ctx()).role;
        Logger.info("Check access for Vacancy Controller, role " + accRole.name);
        if (roleSet.contains(accRole))
            return true;
        else return false;
    }

    @Inject
    FormFactory formFactory;

    @Security.Authenticated(Secured.class)
    public Result create() {
        if (!checkRole(roleSet0))
            return forbidden("No access for create Vacancy");
        Form<Vacancy> vacancyForm = formFactory.form(Vacancy.class);
        return ok(create.render(vacancyForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public Result save() {
        if (!checkRole(roleSet0))
            return forbidden("No access for save Vacancy");
        Form<Vacancy> vacancyForm = formFactory.form(Vacancy.class).bindFromRequest();
        if (vacancyForm.hasErrors()) {
            //todo
            flash("error", vacancyForm.allErrors().get(0).message());
            return ok(create.render(vacancyForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
            //return badRequest(create.render(vacancyForm, provider));
        }
        Vacancy draftVacancy = vacancyForm.get();

        if (draftVacancy.vacancyType.id == null) {
            flash("error","Enter vacancy type!" );
            return ok(create.render(vacancyForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
        }

        VacancyType vacancyType = VacancyType.find.byId(draftVacancy.vacancyType.id);
        if (vacancyType == null) {
            flash("error","Unknown vacancy type!" );
            return ok(create.render(vacancyForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
        }
        if (vacancyType.name.equals(VacancyType.CHIEF_TYPE)) {
            flash("error","You can't create CHIEF vacancy!" );
            return ok(create.render(vacancyForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
        }
        draftVacancy.vacancyType.name = vacancyType.name;
        Request.requestVacancy(vacancyType.organization, draftVacancy);
        return redirect(routes.LoginController.tab(Tabs.REQUESTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result hireCandidate(int habitantId, int vacancyId) {
        if (!checkRole(roleSet0))
            return forbidden("No access for hire candidate on Vacancy");
        Habitant habitant = Habitant.find.byId(habitantId);
        if (habitant == null) return notFound("Habitant is not found");

        Vacancy vacancy = Vacancy.find.byId(vacancyId);
        if (vacancy == null) return notFound("Vacancy is not found");

        Request.requestHiringCandidate(vacancy.vacancyType.organization, habitantId, habitant.getFullName(), vacancyId, vacancy.vacancyType.name);
        return redirect(routes.LoginController.tab(Tabs.REQUESTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result freeVacancy(int habitantId, int vacancyId) {
        if (!checkRole(roleSet0))
            return forbidden("No access for free Vacancy");
        Habitant habitant = Habitant.find.byId(habitantId);
        if (habitant == null) return notFound("Habitant is not found");

        Vacancy vacancy = Vacancy.find.byId(vacancyId);
        if (vacancy == null) return notFound("Vacancy is not found");

        Request.requestFreeVacancy(vacancy.vacancyType.organization, habitant.getFullName(), vacancyId, vacancy.vacancyType.name);
        return redirect(routes.LoginController.tab(Tabs.REQUESTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result searchCandidatesByVacancy(int vacancyId) {
        if (!checkRole(roleSet0))
            return forbidden("No access for search candidate on Vacancy");
        List<Habitant> habitants;

        Vacancy vacancy = Vacancy.find.byId(vacancyId);
        if (vacancy == null) {
            habitants = new ArrayList<>();
        } else
            habitants = Habitant.searchCandidates(vacancy.vacancyType.id);

        return ok(search_result_candidates.render(habitants, vacancy));
    }

}
