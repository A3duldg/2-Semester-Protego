package interfaceDB;

import database.DataAccessException;
import model.Manager;

//Interface for Manager DB operationer.

public interface ManagerDBIF {

    Manager findManagerId(int managerId);

    default Manager findActiveManager(int managerId) throws DataAccessException { //lidt det samme som i EmployeeDBIF kan ikke forstå der er en return value
        return findManagerId(managerId);
    }
}
