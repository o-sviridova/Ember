package services;

import models.Habitant;

import java.util.List;

public class HabitantService {

    public static Habitant getHabitantByAcc(int accountId) {
        Habitant habitant = Habitant.getHabitantByAcc(accountId);
        return habitant;
    }

    public static List<Habitant> getHabitants(){
        final List<Habitant> habitants = Habitant.getHabitants();
        return habitants;
    }

    public static List<Habitant> getCapableHabitants(int vacancyTypeId){
        final List<Habitant> habitants = Habitant.searchCandidates(vacancyTypeId);
        return habitants;
    }

    public static List<Habitant> getLoafers(){
        return Habitant.searchHabitantsBy(true,false,null,null);
    }

    public static List<Habitant> getHabitantsByOrganization(int organizationId){
        List<Habitant> habitants = Habitant.getHabitantsByOrganizationId(organizationId);
        return habitants;
    }

}
