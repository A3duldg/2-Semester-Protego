package controller;

import database.DataAccessException;
import model.Manager;

public class LoginValidator   {
	
	private ManagerController managerController;
	private EmployeeController employeeController;
	
	public LoginValidator() throws DataAccessException{
		this.managerController = new ManagerController();
	//	this.employeeController = new EmployeeController();
	}
	
	public boolean validate(String id)throws DataAccessException {
		try {
			// we first check for if it's a manager
			int parsedId = Integer.parseInt(id);
			Manager manager = managerController.findActiveManager(parsedId);
			return manager != null; // Success if manager is found aka not null
			
			// then we check for if it's an employee
		//	int employeeId = Integer.parseInt(id);
		//	Employee employee = employeeController.findActiveEmployee(parsedId);
		//	return employee != null;
			
		} catch (NumberFormatException e){
			return false; // Someone typed an invalid id format
			
		}
	}
}