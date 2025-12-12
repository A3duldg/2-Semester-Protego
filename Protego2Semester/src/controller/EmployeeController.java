package controller;
import java.util.ArrayList;
import database.DataAccessException;
import interfaceDB.EmployeeDBIF;
import model.Employee;
import model.Shift;

public class EmployeeController {

	private EmployeeDBIF employeeDB;

	public EmployeeController(EmployeeDBIF employeeDB) throws DataAccessException {
		this.employeeDB = employeeDB;
	}
	
	public Employee getEmployeeId(int employeeId) throws DataAccessException {
	    return employeeDB.getEmployeeId(employeeId);
	}
	
	public void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException {
		if (employee == null || shift == null) {
			System.out.println("Employee or Shift cannot be null");
			return;
		}

		employeeDB.connectShiftToEmployee(employee, shift);


	}
	 public ArrayList<Employee> getAllEmployees() {
	        try {
	            return employeeDB.getAllEmployees();
	        } catch (DataAccessException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 public void attachAllEmployeesToShift(Shift shift) {
	        ArrayList<Employee> allEmployees = getAllEmployees();
	        if (allEmployees != null) {
	            for (Employee e : allEmployees) {
	                shift.attachObserver(e); // Employee implementerer Observer
	            }
	        }
	    }
	

}
