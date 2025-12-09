package controller;

import database.DataAccessException;
import database.EmployeeDB;
import database.ManagerDB;
import model.Employee;
import model.Manager;

public class LoginValidator {

	/*
	 * private ManagerController managerController; private EmployeeController
	 * employeeController;
	 * 
	 * public LoginValidator() throws DataAccessException{ this.managerController =
	 * new ManagerController(); // this.employeeController = new
	 * EmployeeController(); }
	 * 
	 * public boolean validate(String id)throws DataAccessException { try { // we
	 * first check for if it's a manager int parsedId = Integer.parseInt(id);
	 * Manager manager = managerController.findActiveManager(parsedId); return
	 * manager != null; // Success if manager is found aka not null
	 * 
	 * // then we check for if it's an employee // int employeeId =
	 * Integer.parseInt(id); // Employee employee =
	 * employeeController.findActiveEmployee(parsedId); // return employee != null;
	 * 
	 * } catch (NumberFormatException e){ return false; // Someone typed an invalid
	 * id format
	 * 
	 * } }
	 */ // old code

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
        Employee employee = employeeDB.getEmployeeId(parseId); // ✅ use findEmployeeId
        if (employee != null) {
            return Role.EMPLOYEE;
        }

        return Role.NONE;
    }
}
