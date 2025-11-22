package Controller;
import Model.*;
import interfaceDB.ManagerDB;
import model.Medarbejder;

public class ManagerController {
	private ManagerDB ManagerDBIF;

	public ManagerController() {
		ManagerDBIF = ManagerDB.getInstance();
}
    public Manager findActiveManager(int managerId) {
        return ManagerDB.getManager(managerId);
    }
	
}
	