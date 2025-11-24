package Controller;

import interfaceDB.ManagerDBIF;
import Database.ManagerDB;
import Model.Manager;


public class ManagerController {

    private ManagerDBIF managerDB;

    public ManagerController() {
        managerDB = new ManagerDB();
        }

    public Manager findActiveManager(int managerId) {
        return managerDB.findManager(managerId);
    }
}