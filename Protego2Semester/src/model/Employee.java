package model;

import java.util.ArrayList;
import java.util.List;

public class Employee implements Observer {
	private int employeeId;
	private String firstName;
	private String lastName;
	private String adress;
	private String city;
	private int postalNr;
	private String phone;
	private String email;
	private ArrayList<Shift> shifts = new ArrayList<>();
	private List<Shift> relevantShifts = new ArrayList<>();
	private List<Shift> observedShifts = new ArrayList<>();



	public Employee(int employeeId, String firstName, String lastName, String adress, String city, int postalNr,
			String phone, String email) {
		this.employeeId = employeeId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.adress = adress;
		this.city = city;
		this.postalNr = postalNr;
		this.phone = phone;
		this.email = email;
		this.shifts = new ArrayList<>();

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getPostalNr() {
		return postalNr;
	}

	public void setPostalNr(int postalNr) {
		this.postalNr = postalNr;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	// bare en almen adder til en liste af vagter en employee har.
	public void connectShiftToEmployee(Shift shift) {
		if (shift == null) {
			throw new IllegalArgumentException("må ikke være null");
		}
		if (shifts.contains(shift)) {
			throw new IllegalArgumentException("må ikke være den samme vagt");
		}
		shifts.add(shift);
	}

// her er nogle ting som hører til observerPattern.
	@Override
	public void updateObserver() {
		relevantShifts();

	}
	public void attachShift(Shift shift) {
	    shift.attach(this);   // Shift er Subject
	    observedShifts.add(shift);
	}


	private void relevantShifts() {
	    relevantShifts.clear();
	    for (Shift s : observedShifts) {
	        relevantShifts.add(s);
	    }
	}

}
