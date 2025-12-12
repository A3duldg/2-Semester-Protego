package model;

import java.util.ArrayList;

public class Shift extends AbstractSubject {

	private int startTime;
	private int endTime;
	private int guardAmount;
	private String shiftLocation;
	private String type;
	private boolean availability;
	private int shiftId;
	private int contractId;

	private ArrayList<String> certifications;

	public Shift(int startTime, int endTime, int guardAmount, String shiftLocation, boolean availability, int shiftId) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.guardAmount = guardAmount;
		this.shiftLocation = shiftLocation;
		this.availability = availability;
		this.shiftId = shiftId;
	
		

	// initialisering af certifications listen & hard coding af brand vagt typen
		certifications = new ArrayList<>();
		certifications.add("Brandvagt");
		certifications.add("Servicevagt");
		certifications.add("Byggepladsvagt");
		certifications.add("Dørmand");
		certifications.add("Fastvagt");
		certifications.add("Centervagt");

	}
	
	// if statementen er til for at sikre at type eksistere inden i listen og så
	// hvis den gør sætte typen vi har valgt til type og returnerer true ellers
	// returnerer false.
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

	// getter til shiftId.
	public int getShiftId() {
		return shiftId;
	}

	// dette er kun fordi jeg tænker det vil give mening men kan fjernes det er bare
	// sådan at man kan tilfører en ny certifications hvis der skulle opstå en
	// mangel.
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
		notifyObservers(); //observerting
	}

	public boolean bookShift() {
		if (availability) {
			availability = false;
			notifyObservers(); //observerting
			return true;
		}
		return false;
	} 

}
