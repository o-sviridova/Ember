package controllers;

import models.Account;
import models.LoginFormData;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Tabs;
import views.html.index;
import views.html.login;
import views.html.home;

import javax.inject.Inject;

public class LoginController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result index() {
        return ok(index.render(Secured.isLoggedIn(ctx()), Secured.getAccount(ctx())));
    }

    /**
     * Provides the Login page (only to unauthenticated users).
     * @return The Login page.
     */
    public Result login() {
        Logger.info("login");
        Form<LoginFormData> formData = formFactory.form(LoginFormData.class);
        if(Secured.isLoggedIn(ctx()))
            return redirect(routes.LoginController.home());
        else
            return ok(login.render("Login", Secured.isLoggedIn(ctx()), Secured.getAccount(ctx()), formData));
    }

    /**
     * Processes a login form submission from an unauthenticated user.
     * First we bind the HTTP POST data to an instance of LoginFormData.
     * The binding process will invoke the LoginFormData.validate() method.
     * If errors are found, re-render the page, displaying the error data.
     * If errors not found, render the page with the good data.
     * @return The index page with the results of validation.
     */
    public Result postLogin() {
        // Get the submitted form data from the request object, and run validation.
        Form<LoginFormData> formData = formFactory.form(LoginFormData.class).bindFromRequest();
        //Logger.info(formData.get().username + formData.get().password + "ioi");
        if (formData.hasErrors()) {
            flash("error", "Login credentials not valid.");
            return badRequest(login.render("Login", Secured.isLoggedIn(ctx()), Secured.getAccount(ctx()), formData));
        }

        session().clear();
        Account account = Account.findByUsername(formData.get().username);
        session("userId", Integer.toString(account.id));
        return redirect(routes.LoginController.home());
    }

    /**
     * Logs out (only for authenticated users) and returns them to the Index page.
     * @return A redirect to the Index page.
     */
    @Security.Authenticated(Secured.class)
    public Result logout() {
        session().clear();
        return redirect(routes.LoginController.index());
    }

    /**
     * Provides the Profile page (only to authenticated users).
     * @return The Profile page.
     */
    @Security.Authenticated(Secured.class)
    public Result home() {
        return ok(home.render(Secured.isLoggedIn(ctx()), Secured.getAccount(ctx()), Tabs.PROFILE.name));
    }

    @Security.Authenticated(Secured.class)
    public Result tab(String tabstr) {
        Tabs tab = Tabs.getValue(tabstr);
        //todo notFound
        if(tab==null) return null;
        return ok(home.render(Secured.isLoggedIn(ctx()), Secured.getAccount(ctx()), tab.name));
    }
}
