package model;

public class Manager {

	private int managerId;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private int postalNr;
	private String phone;
	private String email;

	public Manager(int managerId, String firstName, String lastName, String address, String city, int postalNr,
			String phone, String email) {
		this.managerId = managerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.postalNr = postalNr;
		this.phone = phone;
		this.email = email;
	}

	public int getManagerId() {
		return managerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public int getPostalNr() {
		return postalNr;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}
	
	public String getFullName() {
		return firstName + "" + lastName;
	}
}
