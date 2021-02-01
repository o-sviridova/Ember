package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;

public class RouteController extends Controller {
    public Result javascriptRoutes() {
        return ok(JavaScriptReverseRouter.create("jsRoutes",
                routes.javascript.OrganizationController.getOrganizations(),
                routes.javascript.VacancyTypeController.getVacancyTypesByOrganization(),
                routes.javascript.HabitantController.searchHabitantsBy(),
                routes.javascript.OrganizationController.searchOrganizationsBy(),
                routes.javascript.HabitantController.searchLoafers(),
                routes.javascript.VacancyController.searchCandidatesByVacancy()
                )
        ).as("text/javascript");
    }
}