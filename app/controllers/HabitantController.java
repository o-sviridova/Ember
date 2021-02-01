package controllers;

import models.*;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import play.mvc.Security;
import utils.Tabs;
import views.html.habitants.*;

import javax.inject.Inject;
import java.util.*;
import java.util.List;
import java.util.Optional;

public class HabitantController extends Controller implements AccessInterface{
    Set<Role> roleSet0 = new HashSet();
    Set<Role> roleSet1 = new HashSet();
    Set<Role> roleSet2 = new HashSet();
    Role accRole;

    HabitantController() {
        roleSet0.add(Role.HABITANT);
        roleSet0.add(Role.CHIEF);
        roleSet0.add(Role.MAYOR);
        roleSet0.add(Role.POSTMAN);
        roleSet1.add(Role.MAYOR);
        roleSet2.add(Role.CHIEF);
        roleSet2.add(Role.MAYOR);
    }

    @Override
    public boolean checkRole(Set<Role> roleSet) {
        accRole = Secured.getAccount(ctx()).role;
        Logger.info("Check access for Habitant Controller, role " + accRole.name);
        if (roleSet.contains(accRole))
            return true;
        else return false;
    }

    @Inject
    FormFactory formFactory;

    @Security.Authenticated(Secured.class)
    public Result create() {
        if (!checkRole(roleSet1))
            return forbidden("No access for create Habitant");
        Form<Habitant> habitantForm = formFactory.form(Habitant.class);
        //Form<Account> accountForm = formFactory.form(Account.class);
        return ok(create.render(habitantForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public Result save() {
        if (!checkRole(roleSet1))
            return forbidden("No access for save Habitant");
        Form<Habitant> habitantForm = formFactory.form(Habitant.class).bindFromRequest();
        if (habitantForm.hasErrors()) {
            flash("error", habitantForm.allErrors().get(0).message());
            return badRequest(create.render(habitantForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
        }
        Habitant draftHabitant = habitantForm.get();

        Sex sex = Sex.valueOf(draftHabitant.sex.name);
        boolean workingCapacity =
                (draftHabitant.workingCapacity == null) ? false : draftHabitant.workingCapacity;

        Habitant.create(
                draftHabitant.name,
                draftHabitant.surname,
                draftHabitant.patronymic,
                sex,
                draftHabitant.birthday,
                workingCapacity,
                draftHabitant.careerObjective,
                draftHabitant.account.username,
                draftHabitant.account.password,
                Role.HABITANT);

        return redirect(routes.LoginController.tab(Tabs.HABITANTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result edit(Integer id) {
        if (!checkRole(roleSet1)) {
            return forbidden("No access for edit Habitant");
        }

        Habitant habitant = Habitant.find.byId(id);
        if (habitant == null) {
            return notFound("Habitant not found");
        }

		Integer accId = Secured.getAccount(ctx()).id;
        if (!accRole.equals(Role.MAYOR) && !accId.equals(habitant.account.id)) {
            return forbidden("No access for edit THIS Habitant");
        }

        Form<Habitant> habitantForm = formFactory.form(Habitant.class).fill(habitant);
        Form<Account> accountForm = formFactory.form(Account.class).fill(habitant.account);

        return ok(edit.render(habitantForm, accountForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public Result update() {
        if (!checkRole(roleSet1))
            return forbidden("No access for update Habitant");
        Form<Habitant> habitantForm = formFactory.form(Habitant.class).bindFromRequest();
        if (habitantForm.hasErrors()) {
            Optional<String> idStr = habitantForm.field("id").getValue();
            if(idStr.isPresent()) {
                Habitant habitant = Habitant.find.byId(Integer.parseInt(idStr.get()));
                if (habitant == null) {
                    return notFound("Habitant is not found");
                }
                Form<Account> accountForm = formFactory.form(Account.class).fill(habitant.account);
                flash("error", habitantForm.allErrors().get(0).message());
                return badRequest(edit.render(habitantForm, accountForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
            }
            else return notFound("Habitant is not found");
        }
        Habitant draftHabitant = habitantForm.get();

        Habitant habitant = Habitant.find.byId(draftHabitant.id);
        if (habitant == null) {
            return notFound("Habitant is not found");
        }

        Sex sex = Sex.valueOf(draftHabitant.sex.name);
        boolean workingCapacity =
                (draftHabitant.workingCapacity == null) ? false : draftHabitant.workingCapacity;

        Habitant.update(
                habitant,
                draftHabitant.name,
                draftHabitant.surname,
                draftHabitant.patronymic,
                sex,
                draftHabitant.birthday,
                workingCapacity
        );

        return redirect(routes.LoginController.tab(Tabs.HABITANTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result updateAccount() {
        Form<Account> accountForm = formFactory.form(Account.class).bindFromRequest();
        if (accountForm.hasErrors()) {
            Optional<String> idStr = accountForm.field("id").getValue();
            if(idStr.isPresent()) {
                Account account = Account.find.byId(Integer.parseInt(idStr.get()));
                if (account == null) {
                    return notFound("Account is not found");
                }
                Habitant habitant = Habitant.getHabitantByAcc(account.id);
                Form<Habitant> habitantForm = formFactory.form(Habitant.class).fill(habitant);
                flash("error", accountForm.allErrors().get(0).message());
                return badRequest(edit.render(habitantForm, accountForm, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
            }
            else return notFound("Account is not found");
        }
        Account draftAccount = accountForm.get();

        Account account = Account.find.byId(draftAccount.id);
        if (account == null) {
            return notFound("Account is not found");
        }

        Account.update(
                account,
                draftAccount.username,
                draftAccount.password
        );

        return redirect(routes.LoginController.tab(Tabs.HABITANTS.name));
    }

    @Security.Authenticated(Secured.class)
    public Result editVacancyType(Integer id) {
        if (!checkRole(roleSet0)) {
            return forbidden("No access for edit Habitant's vacancy type");
        }

        Habitant habitant = Habitant.find.byId(id);
        if (habitant == null) {
            return notFound("Habitant is not found");
        }

		Integer accId = Secured.getAccount(ctx()).id;
        if (!accRole.equals(Role.MAYOR) && !accId.equals(habitant.account.id)) {
            return forbidden("No access for edit THIS Habitant");
        }
        
        CareerObjectiveForm form = new CareerObjectiveForm();
        form.id = id;
        Form<CareerObjectiveForm> formData = formFactory.form(CareerObjectiveForm.class).fill(form);
        return ok(edit_desired_vacancy.render(formData, Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public Result updateVacancyType() {
        if (!checkRole(roleSet0))
            return forbidden("No access for update Habitant's vacancy type");
        Form<CareerObjectiveForm> habitantForm = formFactory.form(CareerObjectiveForm.class).bindFromRequest();
        CareerObjectiveForm draftHabitant = habitantForm.get();

        Habitant habitant = Habitant.find.byId(draftHabitant.id);
        if (habitant == null) return notFound("Habitant is not found");

        VacancyType careerObjective = null;
        if (draftHabitant.careerObjective != null) {
            careerObjective = VacancyType.find.byId(draftHabitant.careerObjective.id);
        }

        Habitant.updateCareerObjective(habitant, careerObjective);
        return redirect(routes.LoginController.tab(Tabs.PROFILE.name));
    }

    @Security.Authenticated(Secured.class)
    public Result searchHabitantsBy(String query, String strId, String strWorkingCapacity) {
        if (!checkRole(roleSet2))
            return forbidden("No access for search Habitant");
        List<Habitant> habitants;
        Logger.info("searchHabitantsBy: query: " + query + "; id: " + strId + "; strWorkingCapacity: " + strWorkingCapacity);
        Boolean workingCapacity = (strWorkingCapacity == null) ? null : Boolean.valueOf(strWorkingCapacity);
        Integer organizationId = (strId == null) ? null : Integer.parseInt(strId);

        if (organizationId != null) {
            if (organizationId == -1) {
                organizationId = null;
            } else {
                Organization organization = Organization.find.byId(organizationId);
                if (organization == null) {
                    return notFound("Organization not found");
                }
            }
        }

        habitants = Habitant.searchHabitantsBy(workingCapacity, null, organizationId, query);
        return ok(search_result.render(habitants));
    }

    @Security.Authenticated(Secured.class)
    public Result searchLoafers(String query) {
        if (!checkRole(roleSet2))
            return forbidden("No access for search Habitant as loafers");
        Logger.info("searchLoafers: query: " + query + ";");
        List<Habitant> habitants =
                Habitant.searchHabitantsBy(true, false, null, query);
        return ok(search_result_edit_chief.render(habitants));
    }
}
