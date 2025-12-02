package controller;

import interfaceDB.ManagerDBIF;
import model.Manager;
import database.DataAccessException;
import database.ManagerDB;


public class ManagerController {

    private ManagerDBIF managerDB;

    public ManagerController() throws DataAccessException {
        managerDB = new ManagerDB() ;
        }

    public Manager findActiveManager(int managerId)throws DataAccessException {
        return managerDB.findManagerId(managerId);
    }
}