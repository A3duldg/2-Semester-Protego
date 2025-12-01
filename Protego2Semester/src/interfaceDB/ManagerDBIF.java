package interfaceDB;

import model.Manager;

//Interface for Manager DB operationer.

public interface ManagerDBIF {

    Manager findManager(int managerId);

    default Manager findActiveManager(int managerId) {
        return findManager(managerId);
    }
}
