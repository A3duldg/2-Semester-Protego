package Controller;

import interfaceDB.ManagerDBIF;
import Database.ManagerDB;
import Model.Manager;

public class ManagerController {

    private ManagerDBIF managerDB;

    public ManagerController() {
        managerDB = ManagerDB.getInstance();
    }

    public Manager findActiveManager(int managerId) {
        return managerDB.findActiveManager(managerId);
    }
}