package controllers;

import models.Role;

import java.util.HashSet;
import java.util.Set;

public interface AccessInterface {
    public boolean checkRole(Set<Role> roleSet);
}
