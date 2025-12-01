package controller;

import interfaceDB.ManagerDBIF;
import model.Manager;
import database.ManagerDB;


public class ManagerController {

    private ManagerDBIF managerDB;

    public ManagerController() {
        managerDB = new ManagerDB();
        }

    public Manager findActiveManager(int managerId) {
        return managerDB.findManager(managerId);
    }
}