package models;

import exceptions.ModelException;
import io.ebean.Finder;
import io.ebean.Model;
import play.Logger;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Organization extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @Constraints.Required
    public String name;
    public boolean isClosed;

    @OneToOne
    @Constraints.Required
    public Habitant chief;

    public static Organization create(String name, Habitant chief) {
        final Organization organization = new Organization();
        //todo create vacancies and another staff ???
        organization.name = name;
        organization.chief = chief;
        organization.save();

        Vacancy vacancy = VacancyType.createChiefVacancy(organization);
        try {
            Vacancy.assignWorker(vacancy, chief);
        } catch (ModelException e) {
            Logger.warn(e.getMessage());
        }

        return organization;
    }

    public static Organization update(Organization organization, String name) {
        organization.name = name;
        organization.update();
        return organization;
    }

    public static Organization update(Organization organization, String name, Habitant chief) {
        if (!organization.chief.id.equals(chief.id)) {

            Vacancy oldChiefVacancyRecord = organization.chief.getCurrentWork();
            Vacancy candidateVacancyRecord = chief.getCurrentWork();
            //todo exception if candidateVacancyRecord is not null?
            //fire old chief
            try {
                Vacancy.closeVacancy(oldChiefVacancyRecord);
            } catch (ModelException e) {
                Logger.warn(e.getMessage());
            }
            //create new vacancy record
            Vacancy chiefVacancy = VacancyType.createChiefVacancy(organization);

            //hire new chief
            try {
                Vacancy.assignWorker(chiefVacancy, chief);
            } catch (ModelException e) {
                Logger.warn(e.getMessage());
            }

            organization.chief = chief;
            organization.name = name;
            organization.update();
        } else Organization.update(organization, name);
        return organization;
    }

    public static Organization close(Organization organization) {
        Vacancy oldChiefVacancyRecord = organization.chief.getCurrentWork();
        try {
            Vacancy.closeVacancy(oldChiefVacancyRecord);
        } catch (ModelException e) {
            Logger.warn(e.getMessage());
        }

        organization.isClosed = true;
        organization.chief = null;
        organization.update();
        return organization;
    }

    public static Organization reopen(Organization organization, Habitant chief) {
        //todo check organization is closed -> exception
        //todo exception if candidateVacancyRecord is not null?

        Vacancy chiefVacancy = VacancyType.createChiefVacancy(organization);
        try {
            Vacancy.assignWorker(chiefVacancy, chief);
        } catch (ModelException e) {
            Logger.warn(e.getMessage());
        }
        organization.isClosed = false;
        organization.update();
        return organization;
    }

    public static Finder<Integer, Organization> find = new Finder<>(Organization.class);

    public static List<Organization> searchOrganizationsBy(String query) {
        Logger.info("searchOrganizationsBy: query: " + query + ";");
        List<Organization> organizations = getAllOrganizations();

        if (query != null) {
            organizations = filterOrganizationByQuery(organizations, query);
        }

        return organizations;
    }

    public static Organization getOrganizationByChief(int chiefId) {
        final Organization organization = Organization.find.query().where()
                .eq("chief_id", chiefId).findOne();
        return organization;
    }

    public static List<Organization> getAllOrganizations() {
        final List<Organization> organizations = Organization.find.all();
        return organizations;
    }

    private static List<Organization> filterOrganizationByQuery(List<Organization> organizations, String query) {
        List<Organization> filteredOrganizations = organizations.stream()
                .filter(organization -> organization.name.contains(query))
                .collect(Collectors.toList());
        return filteredOrganizations;
    }
}
