package controllers;

import models.Account;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

/**
 * Implement authorization for this system.
 * getUserName() and onUnauthorized override superclass methods to restrict
 * access to the profile() page to logged in users.
 *
 * getUser(), isLoggedIn(), and getAccount() provide static helper methods so that controllers
 * can know if there is a logged in user.
 *
 */
public class Secured extends Security.Authenticator {

    /**
     * Used by authentication annotation to determine if user is logged in.
     * @param ctx The context.
     * @return The email address of the logged in user, or null if not logged in.
     */
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("userId");
    }

    /**
     * Instruct authenticator to automatically redirect to login page if unauthorized.
     * @param context The context.
     * @return The login page.
     */
    @Override
    public Result onUnauthorized(Context context) {
        return redirect(routes.LoginController.login());
    }

    /**
     * Return the email of the logged in user, or null if no logged in user.
     *
     * @param ctx the context containing the session
     * @return The email of the logged in user, or null if user is not logged in.
     */
    public static String getUserId(Context ctx) {
        return ctx.session().get("userId");
    }

    /**
     * True if there is a logged in user, false otherwise.
     * @param ctx The context.
     * @return True if user is logged in.
     */
    public static boolean isLoggedIn(Context ctx) {
        return (getUserId(ctx) != null);
    }

    /**
     * Return the Account of the logged in user, or null if no user is logged in.
     * @param ctx The context.
     * @return The Account, or null.
     */
    public static Account getAccount(Context ctx) {
        return (isLoggedIn(ctx) ? Account.findById(Integer.parseInt(getUserId(ctx))) : null);
    }
}
