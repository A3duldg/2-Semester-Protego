package controller;

import database.DataAccessException;
import database.EmployeeDB;
import database.ManagerDB;
import model.Employee;
import model.Manager;

public class LoginValidator {



	public enum Role {
        MANAGER, EMPLOYEE, NONE
    }

    public Role validate(String id) throws DataAccessException {
        int parseId;

        try {
            parseId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return Role.NONE;
        }

        ManagerDB managerDB = new ManagerDB();
        Manager manager = managerDB.findManagerId(parseId);
        if (manager != null) {
            return Role.MANAGER;
        }

        EmployeeDB employeeDB = new EmployeeDB();
        Employee employee = employeeDB.getEmployeeId(parseId);
        if (employee != null) {
            return Role.EMPLOYEE;
        }

        return Role.NONE;
    }
}
