package Controller;
import Model.*;
import Database.*;
import interfaceDB.*;

public class ManagerController {
	private ManagerDB ManagerDBIF;

	public ManagerController() {
		ManagerDBIF = ManagerDB.getInstance();
}
    public Manager findActiveManager(int managerId) {
        return ManagerDB.getManager(managerId);
    }
	
}
	