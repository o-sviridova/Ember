package models;

import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.Where;
import play.Logger;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "habitants")
public class Habitant extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String name;
    public String surname;
    public String patronymic;
    @Enumerated(EnumType.STRING)
    public Sex sex;

    //todo change desired job will be broken by it -> rewrite edit
    //@Constraints.Required
    public Date birthday;
    public Boolean workingCapacity;

    @OneToOne
    public Account account;

    @ManyToOne
    public VacancyType careerObjective;

    @OneToMany(mappedBy = "worker")
    @Where(clause = "to_date is null")
    public List<Vacancy> currentwork;

    public Vacancy getCurrentWork() {
        return (currentwork.size() == 0) ? null : currentwork.get(0);
    }

    public static Habitant getHabitantByAcc(int accountId) {
        Habitant habitant = Habitant.find.query().where().eq("account_id", accountId).findOne();
        return habitant;
    }

    public static Habitant create(String name,
                                  String surname,
                                  String patronymic,
                                  Sex sex,
                                  Date birthday,
                                  Boolean workingCapacity,
                                  VacancyType vacType,
                                  String username,
                                  String password,
                                  Role role) {
        final Account account = Account.create(username, password, role);
        final Habitant habitant = new Habitant();
        habitant.careerObjective = vacType;
        habitant.name = name;
        habitant.surname = surname;
        habitant.patronymic = patronymic;
        habitant.sex = sex;
        habitant.birthday = birthday;
        habitant.workingCapacity = workingCapacity;
        habitant.account = account;
        habitant.save();
        Logger.info("new habitant: {}", habitant.id);
        return habitant;
    }

    public static Habitant update(Habitant habitant,
                                  String name,
                                  String surname,
                                  String patronymic,
                                  Sex sex,
                                  Date birthday,
                                  Boolean workingCapacity) {
        habitant.name = name;
        habitant.surname = surname;
        habitant.patronymic = patronymic;
        habitant.sex = sex;
        habitant.birthday = birthday;
        habitant.workingCapacity = workingCapacity;
        habitant.update();
        return habitant;
    }

    public static Habitant updateCareerObjective(Habitant habitant, VacancyType careerObjective) {
        habitant.careerObjective = careerObjective;
        habitant.update();
        return habitant;
    }

    public static Habitant updateRole(Habitant habitant, Role role) {
        Account.updateRole(habitant.account, role);
        return habitant;
    }

    public static Finder<Integer, Habitant> find = new Finder<>(Habitant.class);

    //todo handle sql exceptions
    public static List<Habitant> searchHabitantsBy(Boolean workingCapacity,
                                                   Boolean isWorking,
                                                   Integer organizationId,
                                                   String query) {
        List<Habitant> habitants = getHabitants();

        if (workingCapacity != null)
            habitants = filterHabitantByWorkingCapacity(habitants, workingCapacity);

        if (isWorking != null)
            habitants = filterWorkingHabitant(habitants, isWorking);

        if (organizationId != null)
            habitants = filterHabitantByOrganizationId(habitants, organizationId);

        if (query != null)
            habitants = filterHabitantByQuery(habitants, query);

        return habitants;
    }

    public static List<Habitant> searchCandidates(int vacancyTypeId) {

        VacancyType vacancyType = VacancyType.find.byId(vacancyTypeId);

        final List<Habitant> habitants = Habitant.searchHabitantsBy(true, false, null, null);

        List<Habitant> result = habitants.stream()
                .filter(hab -> hab.careerObjective != null)
                .filter(hab -> hab.careerObjective.id == vacancyTypeId).collect(Collectors.toList());

        List<Habitant> secondPriorityHabitants = habitants.stream()
                .filter(hab -> hab.careerObjective != null)
                .filter(habitant -> habitant.careerObjective.id != vacancyTypeId)
                .filter(habitant -> habitant.careerObjective.organization.id.equals(vacancyType.organization.id))
                .collect(Collectors.toList());

        List<Habitant> thirdPriorityHabitants = habitants.stream()
                .filter(hab -> hab.careerObjective == null)
                .collect(Collectors.toList());

        List<Habitant> fourthPriorityHabitants = habitants.stream()
                .filter(hab -> hab.careerObjective != null)
                .filter(hab -> hab.careerObjective.id != vacancyTypeId)
                .filter(habitant -> !habitant.careerObjective.organization.id.equals(vacancyType.organization.id))
                .collect(Collectors.toList());

        result.addAll(secondPriorityHabitants);
        result.addAll(thirdPriorityHabitants);
        result.addAll(fourthPriorityHabitants);

        return result;
    }

    //todo throw exception
    public static List<Habitant> getHabitants() {
        final List<Habitant> habitants = Habitant.find.all();
        return habitants;
    }

    public static List<Habitant> getHabitantsByOrganizationId(int organizationId) {
        List<Habitant> habitants = Habitant.find.all();
        habitants = filterHabitantByOrganizationId(habitants, organizationId);
        return habitants;
    }

    private static List<Habitant> filterHabitantByWorkingCapacity(List<Habitant> habitants, boolean workingCapacity) {
        List<Habitant> filteredHabitants =
                habitants.stream().filter(habitant -> habitant.workingCapacity.equals(workingCapacity)).collect(Collectors.toList());

        return filteredHabitants;
    }

    private static List<Habitant> filterHabitantByQuery(List<Habitant> habitants, String query) {
        List<Habitant> filteredHabitants = new ArrayList<>();
        for (Habitant habitant : habitants) {
            String s = habitant.surname + " " + habitant.name + " " + habitant.patronymic;
            if (!s.contains(query)) {
                continue;
            }
            filteredHabitants.add(habitant);
        }
        return filteredHabitants;
    }

    private static List<Habitant> filterWorkingHabitant(List<Habitant> habitants, boolean isWorking) {
        List<Habitant> filteredHabitants = habitants.stream()
                .filter(habitant -> (habitant.getCurrentWork() == null) ^ isWorking).collect(Collectors.toList());

        return filteredHabitants;
    }

    private static List<Habitant> filterHabitantByOrganizationId(List<Habitant> habitants, int organizationId) {
        List<Habitant> filteredHabitants = habitants.stream()
                .filter(habitant -> habitant.getCurrentWork() != null)
                .filter(habitant -> habitant.getCurrentWork().vacancyType != null) //for mayor and postman
                .filter(habitant -> habitant.getCurrentWork().vacancyType.organization != null)
                .filter(habitant -> habitant.getCurrentWork().vacancyType.organization.id.equals(organizationId))
                .collect(Collectors.toList());
        return filteredHabitants;
    }

    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();
        if(id!=null) {
            Habitant habitant = Habitant.find.byId(id);
            Vacancy vacancy = habitant.getCurrentWork();
            if ((workingCapacity == null || !workingCapacity) && vacancy != null) {
                errors.add(new ValidationError("workingCapacity", "Habitant is still working"));
            }
        }

        if (account == null) {
            return (errors.size() > 0) ? errors : null;
        }
        //validation while creating
        String username = account.username;
        Account duplicateAccount = Account.findByUsername(username);

        if (duplicateAccount != null) {
            errors.add(new ValidationError("username", "Username is already used"));
        }

        if (!Account.isRepeatSuccessfully(account.password, account.rePassword)) {
            errors.add(new ValidationError("password", "Repeat is not successful"));
        }

        return (errors.size() > 0) ? errors : null;
    }

    public String getFullName(){
        return this.surname + " " + this.name + " " + this.patronymic;
    }
}