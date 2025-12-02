package interfaceDB;

import model.Manager;

//Interface for Manager DB operationer.

public interface ManagerDBIF {

    Manager findManagerId(int managerId);

    default Manager findActiveManager(int managerId) {
        return findManagerId(managerId);
    }
}
