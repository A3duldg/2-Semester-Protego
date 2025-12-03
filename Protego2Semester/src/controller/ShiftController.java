package controller;

import interfaceDB.ShiftDBIF;
import model.Contract;
import model.Manager;
import model.Shift;

import java.sql.SQLException;
import java.util.ArrayList;
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
	
	public List<Shift> findAvailableShifts(List<Shift> allShifts) {
		List<Shift> available = new ArrayList<>();
		
		// Implementering Lambda for hver vagt, tjek om den er ledig
		allShifts.forEach(shift -> {
			if (shift.isAvailable()) {
				available.add(shift);
			}
		});
		
		return available;
	}
	
	public List<Shift> sortShiftsByStartTime(List<Shift> shifts) {
		List<Shift> sorted = new ArrayList<>(shifts);
		
		//Implementing af en funktion som skal sortere vagter udfra starttider
		sorted.sort((shift1, shift2) ->
			Integer.compare(shift1.getStartTime(), shift2.getStartTime())
			);
		
		return sorted;
	}
	
	public int calculateTotalHours(List<Shift> shifts) {
		int totalHours = 0;
		
		//Implementering af en Lambda der beregner timerne for hver vagt
		for (Shift shift: shifts) {
			totalHours += (shift.getEndTime() - shift.getStartTime());
		}
		
		return totalHours;
	}

}
