package services;

import models.Organization;

import java.util.List;

public class OrganizationService {
    public static List<Organization> getAllOrganizations() {
        final List<Organization> organizations = Organization.getAllOrganizations();
        return organizations;
    }

    public static Organization getOrganizationByChief(int chiefId) {
        final Organization organization = Organization.getOrganizationByChief(chiefId);
        return organization;
    }
}
