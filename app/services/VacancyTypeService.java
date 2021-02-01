package services;

import models.VacancyType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VacancyTypeService {
    public static List<VacancyType> getVacancyTypesByOrganizationId(int organizationId) {
        final List<VacancyType> vacancyTypes = VacancyType.getVacancyTypesByOrganizationId(organizationId);
        return vacancyTypes;
    }

    public static Map<String, String> getVacancyTypesStr(int organizationId) {
        return VacancyType.getVacancyTypesStr(organizationId);
    }

}
