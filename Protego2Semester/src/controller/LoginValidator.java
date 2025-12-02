package controller;

import database.DataAccessException;
import model.Manager;

public class LoginValidator   {
	
	private ManagerController managerController = new ManagerController() ;
	
	
	
	public boolean validate(String id) throws DataAccessException{
		try {
			int managerId = Integer.parseInt(id);
			Manager manager = managerController.findActiveManager(managerId);
			return manager != null; // Success if manager is found aka not null
		} catch (NumberFormatException e){
			return false; // Someone typed an invalid id format
		} catch (DataAccessException e) {
			
		}
	}
}