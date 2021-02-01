package models;

import exceptions.ModelException;
import io.ebean.Finder;
import io.ebean.Model;
import play.Logger;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static services.VacancyTypeService.getVacancyTypesByOrganizationId;

@Entity
@Table(name = "vacancies")
public class Vacancy extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OneToMany(mappedBy = "vacancyId")
    public Integer id;

    public Date fromDate;
    public Date toDate;
    public boolean isArchived;

    public final static int MIN_POS_COUNT = 1;
    public final static int MAX_POS_COUNT = 100;

    @Transient
    @Constraints.Required(message = "Enter count of position!")
    @Constraints.Min(MIN_POS_COUNT)
    @Constraints.Max(MAX_POS_COUNT)
    public Integer positionCount;

    @ManyToOne
    public Habitant worker;

    @ManyToOne
    @Constraints.Required(message = "Enter vacancy type!")
    public VacancyType vacancyType;

    public static Vacancy create(VacancyType vacancyType) {
        final Vacancy vacancy = new Vacancy();
        vacancy.isArchived = false;
        vacancy.vacancyType = vacancyType;
        vacancy.save();
        return vacancy;
    }

    public static List<Vacancy> create(VacancyType vacancyType, Integer positionCount) {
        final List<Vacancy> vacancies = new ArrayList<>();
        if (!checkPositionCount(positionCount)) return vacancies;
        for (int i = 0; i < positionCount; i++) {
            Vacancy vacancy = new Vacancy();
            vacancy.isArchived = false;
            vacancy.vacancyType = vacancyType;
            vacancies.add(vacancy);
        }
        Vacancy.db().saveAll(vacancies);
        return vacancies;
    }

    public static Vacancy assignWorker(Vacancy vacancy, Habitant candidate) throws ModelException {
        if(candidate.getCurrentWork() != null) {
            throw new ModelException("Candidate is already worker");
        }
        if(vacancy.worker != null) {
            throw new ModelException("Vacancy is already busy");
        }
        Logger.info("old role {}", candidate.account.role);
        vacancy.isArchived = false;
        vacancy.worker = candidate;
        vacancy.fromDate = new Date(System.currentTimeMillis());
        vacancy.save();
        if (vacancy.vacancyType.name.equals(VacancyType.CHIEF_TYPE))
            Habitant.updateRole(candidate, Role.CHIEF);
        Logger.info("assignWorker: {} {}", vacancy.vacancyType, vacancy.worker.id);
        return vacancy;
    }

    public static Vacancy closeVacancy(Vacancy vacancy) throws ModelException{
        if(vacancy.isArchived) {
            throw new ModelException("Vacancy #[{"+Integer.toString(vacancy.id)+"}] is already archived");
        }
        vacancy.toDate = new Date(System.currentTimeMillis());
        vacancy.isArchived = true;
        vacancy.update();
        if (vacancy.vacancyType.name.equals(VacancyType.CHIEF_TYPE))
            Habitant.updateRole(vacancy.worker, Role.HABITANT);
        return vacancy;
    }

    public static boolean checkPositionCount(int positionCount) {
        return (positionCount >= MIN_POS_COUNT
                && positionCount <= MAX_POS_COUNT);
    }

    public static Finder<Integer, Vacancy> find = new Finder<>(Vacancy.class);

    public static List<Vacancy> getWorkHistoryByHabitantId(int habitantId) {
        final List<Vacancy> vacancies = Vacancy.find.query().where().eq("worker_id", habitantId).findList();
        return vacancies;
    }

    public static List<Vacancy> getOpenVacanciesByOrganization(int id) {
        List<Vacancy> vacancies = Vacancy.find.query()
                    .fetch("vacancyType")
                    .where()
                    .eq("organization_id", id)
                    .eq("from_date", null)
                    .eq("worker_id", null).findList();
        return vacancies;
    }

    public static List<Vacancy> getVacanciesByOrganizations(int id) {

        List<Vacancy> vacancies = Vacancy.find.query()
                .fetch("vacancyType")
                .where()
                .eq("organization_id", id)
                .eq("to_date", null)
                .ne("worker_id", null).findList();
        return vacancies;
    }


}
