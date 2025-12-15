package model;

import java.util.ArrayList;
import java.time.LocalDate;


public class Shift extends AbstractSubject {

	private int startTime;
	private int endTime;
	private int guardAmount;
	private String shiftLocation;
	private String type;
	private boolean availability;
	private int shiftId;
	private int contractId;
	private LocalDate shiftDate;


	private ArrayList<String> certifications;

	public Shift(int startTime, int endTime, int guardAmount, String shiftLocation, boolean availability, int shiftId) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.guardAmount = guardAmount;
		this.shiftLocation = shiftLocation;
		this.availability = availability;
		this.shiftId = shiftId;
	
		
	
		// initializing of certifications list & hard coding of brand vagt type
		certifications = new ArrayList<>();
		certifications.add("Brandvagt");
		certifications.add("Servicevagt");
		certifications.add("Byggepladsvagt");
		certifications.add("Dørmand");
		certifications.add("Fastvagt");
		certifications.add("Centervagt");

	}
	
	// The if statement is used to ensure that the type exists within the list,
	// and if it does, set the chosen type to that type and return true;
	// otherwise, return false.

	public boolean setShiftType(String type) {
		if (certifications.contains(type)) {
			this.type = type;
			notifyObservers(); //observerting
			return true;
		}
		return false;
	}

	public String getType() {
		return type;
	}


	public int getShiftId() {
		return shiftId;
	}


	public void addCertification(String Certifications) {
		certifications.add(Certifications);
	}

	public ArrayList<String> getCertifications() {
		return certifications;

	}
	
	public void setContractId(int contractId) {
	    this.contractId = contractId;
	}


	public int getStartTime() {
		return startTime;
	}
	
	public int getContract() {
		return contractId;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getGuardAmount() {
		return guardAmount;
	}

	public void setGuardAmount(int guardAmount) {
		this.guardAmount = guardAmount;
	}

	public String getShiftLocation() {
		return shiftLocation;
	}

	public void setShiftLocation(String shiftLocation) {
		this.shiftLocation = shiftLocation;
	}

	public boolean isAvailable() {
		return availability;
	}

	public void setAvailability(boolean availability) {
		this.availability = availability;
		notifyObservers(); //observer
	}

	public boolean bookShift() {
		if (availability) {
			availability = false;
			notifyObservers(); //observer
			return true;
		}
		return false;
	} 
	public LocalDate getShiftDate() {
	    return shiftDate;
	}

	public void setShiftDate(LocalDate shiftDate) {
	    this.shiftDate = shiftDate;
	}


}
