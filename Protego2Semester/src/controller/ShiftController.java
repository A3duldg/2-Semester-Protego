package controller;

import interfaceDB.ShiftDBIF;
import model.Contract;
import model.Manager;
import model.Shift;

import java.util.List;

import database.DataAccessException;
import database.ShiftDB;

public class ShiftController {
	private ManagerController managerCtr;
	private ContractController contractCtr;
	private ShiftDBIF shiftDB;

	public ShiftController() throws DataAccessException{
		shiftDB = new ShiftDB();
	}

	public Contract findActiveContract() {
		return contractCtr.findActiveContract(0);
	}

	public Manager findActiveManager() {
		return managerCtr.findActiveManager(0);
	}

	public int createShift(Shift shift) throws DataAccessException {

		return shiftDB.createShift(shift);

	}

	public boolean setShiftType(Shift shift) throws DataAccessException {
		return shiftDB.setShiftType(shift);
	}

	public Contract confirmContract() {
		return contractCtr.confirmContract();
	}
	boolean bookShift(Shift shift) throws DataAccessException {
		return shiftDB.bookShift(shift);
	}
	List<Shift> findShiftByAvailability(boolean Availability) throws DataAccessException {
		return shiftDB.findShiftByAvailability(Availability);
	}

}
