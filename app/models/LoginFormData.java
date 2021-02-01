package models;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Backing class for the login form.
 */
public class LoginFormData {

    /**
     * The submitted email.
     */
    public String username = "";
    /**
     * The submitted password.
     */
    public String password = "";

    /**
     * Required for form instantiation.
     */
    public LoginFormData() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Validates Form<LoginFormData>.
     * Called automatically in the controller by bindFromRequest().
     * Checks to see that email and password are valid credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();
        if (!Account.isValid(username, password)) {
            errors.add(new ValidationError("username", ""));
            errors.add(new ValidationError("password", ""));
        }

        return (errors.size() > 0) ? errors : null;
    }

}