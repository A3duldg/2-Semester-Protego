package Model;

public class Manager {

private int managerId;
String  firstName;
String lastName; 
String adress;
String city; 
int postalNr;
int phone;
String email;

	public Manager(int managerId, String firstName, String lastName, String adress, String city, int postalNr, int phone, String email) {
		this.managerId = managerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.adress = adress;
		this.city = city;
		this.postalNr = postalNr;
		this.phone = phone;
		this.email = email;
		
	}

	public int getManager() {
		return managerId;
	}
}
