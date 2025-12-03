package model;

public class Manager {

	private int managerId;
	private String firstName;
	private String lastName;
	private String adress;
	private String city;
	private int postalNr;
	private int phone;
	private String email;

	public Manager(int managerId, String firstName, String lastName, String adress, String city, int postalNr,
			int phone, String email) {
		this.managerId = managerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.adress = adress;
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

	public String getLatName() {
		return lastName;
	}

	public String getAdress() {
		return adress;
	}

	public String getCity() {
		return city;
	}

	public int getPostalNr() {
		return postalNr;
	}

	public int getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}
}
