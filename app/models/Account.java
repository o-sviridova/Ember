package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.Logger;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import utils.HashHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Constraints.Required
    @Column(unique = true)
    public String username;

    @Constraints.Required
    public String password;

    @Transient
    @Constraints.Required
    public String rePassword;

    @Enumerated(EnumType.STRING)
    public Role role;

    public Account(String username, String password, Role role) {
        this.username = username;
        this.password = HashHelper.createPassword(password);
        this.role = role;
    }

    public static Account create(String username, String password, Role role) {
        final Account account = new Account(username, password, role);
        account.save();
        Logger.info("create acc {}", account.id);
        return account;
    }

    public static Account update(Account account, String username, String password) {
        account.username = username;
        account.password = HashHelper.createPassword(password);
        account.update();
        return account;
    }

    public static Account updateRole(Account account, Role role) {
        account.role = role;
        account.update();
        return account;
    }

    public static Finder<Integer, Account> find = new Finder<>(Account.class);

    public static Account findById(final Integer id) {
        return find.byId(id);
    }

    public static Account findByUsername(final String username) {
        return find.query().where().eq("username", username).findOne();
    }

    public static boolean isValid(final String username, final String password) {
        Logger.info("Hello, {}", username);
        Account account = findByUsername(username);
        if (account == null) return false;
        return HashHelper.checkPassword(password, account.password);
    }

    public static boolean isRepeatSuccessfully(final String password, final String rePassword) {
        if (password == null || rePassword ==null) return false;
        return password.equals(rePassword);
    }

    /**
     * Validates Form<LoginFormData>.
     * Called automatically in the controller by bindFromRequest().
     * Checks to see that email and password are valid credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    //validation while editing
    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();
        Account duplicateAccount = Account.findByUsername(username);

        if (duplicateAccount!=null && !duplicateAccount.id.equals(id)) {
            errors.add(new ValidationError("username", "username is already used"));
        }

        if (!isRepeatSuccessfully(password, rePassword)) {
            errors.add(new ValidationError("password", "repeat is not successful"));
        }

        return (errors.size() > 0) ? errors : null;
    }


}