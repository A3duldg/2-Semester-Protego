package Database;

import interfaceDB.ManagerDBIF;

public class ManagerDB implements ManagerDBIF{

	public static ManagerDB getInstance() {
		return null;
}

	public static int getManager(int managerId) {
		return managerId;
	}

}