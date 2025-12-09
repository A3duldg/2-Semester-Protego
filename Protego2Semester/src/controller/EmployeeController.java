package controller;

import database.DataAccessException;
import interfaceDB.EmployeeDBIF;
import model.Employee;
import model.Shift;

public class EmployeeController {

	private EmployeeDBIF employeeDB;

	public EmployeeController(EmployeeDBIF employeeDB) throws DataAccessException {
		this.employeeDB = employeeDB;
	}

	public void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException {
		if (employee == null || shift == null) {
			System.out.println("Employee or Shift cannot be null");
			return;
		}

		employeeDB.connectShiftToEmployee(employee, shift);


	}

}
