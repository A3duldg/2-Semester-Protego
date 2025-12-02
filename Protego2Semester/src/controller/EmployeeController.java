package controller;

import interfaceDB.EmployeeDBIF;
import model.Employee;
import model.Shift;

public class EmployeeController {

	private EmployeeDBIF employeeDB;

	public EmployeeController(EmployeeDBIF employeeDB) {
		this.employeeDB = employeeDB;
	}

	public void ConnectShiftToEmployee(Employee employee, Shift shift) {
		if (employee == null || shift == null) {
			System.out.println("Employee or Shift cannot be null");
			return;
		}

		employeeDB.connectShiftToEmployee(employee, shift);


	}

}
