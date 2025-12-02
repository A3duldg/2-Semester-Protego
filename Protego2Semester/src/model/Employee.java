package model;

import java.util.ArrayList;

public class Employee {
	private int employeeId;
	private String firstName;
	private String lastName;
	private String adress;
	private String city;
	private int postalNr;
	private int phone;
	private String email;
	private ArrayList<Shift> shifts = new ArrayList<>();

	public Employee(int employeeId, String firstName, String lastName, String adress, String city, int postalNr,int phone, String email) {
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

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
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

}
