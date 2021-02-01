package models;

import exceptions.ModelException;
import io.ebean.DuplicateKeyException;
import io.ebean.Finder;
import io.ebean.Model;
import play.Logger;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(
        name = "vacancy_types",
        uniqueConstraints = {@UniqueConstraint(
                columnNames = {"name", "organization_id"}
        )
        }
)
public class VacancyType extends Model {

    public static String MAYOR_TYPE = "MAYOR";
    public static String CHIEF_TYPE = "CHIEF";
    public static String POSTMAN_TYPE = "POSTMAN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OneToMany(mappedBy = "vacancyType")
    public Integer id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    @ManyToOne
    public Organization organization;

    @Deprecated
    public static VacancyType create(String name) {
        final VacancyType reqType = new VacancyType();
        reqType.name = name;
        reqType.save();
        return reqType;
    }

    public static VacancyType create(String name, Organization organization) throws ModelException {
        final VacancyType reqType = new VacancyType();
        reqType.name = name;
        reqType.organization = organization;
        try {
            reqType.save();
        } catch (DuplicateKeyException e) {
            throw new ModelException("Vacancy type name is already used for this organization.");
        }
        return reqType;
    }

    public static Vacancy createChiefVacancy(Organization organization) {
        VacancyType vacancyType = getChiefVacancyTypeByOrganizationId(organization.id);
        try {
            vacancyType = (vacancyType == null) ? create(CHIEF_TYPE, organization) : vacancyType;
        } catch (ModelException e) {
            Logger.warn("CHIEF TYPE is already existed for organization with id [{}]", organization.id);
        }
        Vacancy vacancy = Vacancy.create(vacancyType);
        return vacancy;
    }

    public static Finder<Integer, VacancyType> find = new Finder<>(VacancyType.class);

    public static VacancyType getVacancyTypeByName(String name, int organizationId) {
        VacancyType vacancyType = VacancyType.find.query().where()
                .eq("name", name)
                .eq("organization_id", organizationId).findOne();
        return vacancyType;
    }

    public static VacancyType getChiefVacancyTypeByOrganizationId(int organizationId) {
        return getVacancyTypeByName(VacancyType.CHIEF_TYPE, organizationId);
    }

    public static List<VacancyType> getVacancyTypesByOrganizationId(int organizationId) {
        final List<VacancyType> vacancyTypes = VacancyType.find.query().where()
                .eq("organization_id", organizationId).findList();
        return vacancyTypes;
    }

    public static Map<String, String> getVacancyTypesStr(int organizationId) {
        List<VacancyType> vacancyTypes = getVacancyTypesByOrganizationId(organizationId);
        Map<String, String> vacancyTypesStr = new HashMap<>();

        for (VacancyType type : vacancyTypes) {
            if(!type.name.equals(VacancyType.CHIEF_TYPE))
            vacancyTypesStr.put(type.id.toString(), type.name);
        }
        return vacancyTypesStr;
    }

}
