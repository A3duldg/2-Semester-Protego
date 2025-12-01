package controller;

import interfaceDB.ShiftDBIF;
import model.Contract;
import model.Manager;
import model.Shift;
import database.ShiftDB;

public class ShiftController {
	private ManagerController managerCtr;
	private ContractController contractCtr;
	private ShiftDBIF shiftDB;

	public ShiftController() {
		shiftDB = new ShiftDB();
	}

	public Contract findActiveContract() {
		return contractCtr.findActiveContract(0);
	}

	public Manager findActiveManager() {
		return managerCtr.findActiveManager(0);
	}

	public int createShift(Shift shift) {
		return shiftDB.createShift(shift);

	}
	public boolean setShiftType(Shift shift) {
		return shiftDB.setShiftType(shift);
	}
	
	public Contract confirmContract() {
		return contractCtr.confirmContract();
	}
}
