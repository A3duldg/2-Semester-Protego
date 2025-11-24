package Controller;

import interfaceDB.ManagerDBIF;
import ManagerDB;
import Model.Manager;

public class ManagerController {
	private ManagerDBIF ManagerDB;

	public ManagerController() {
		ManagerDB = ManagerDB.getInstance();
}
    public Manager findActiveManager(int managerId) {
        return ManagerDB.findActiveManager(managerId);
    }
	
}
	