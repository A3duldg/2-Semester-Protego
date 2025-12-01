package model;
import java.util.ArrayList;

public class Employee {
	private int employeeId;
	private String  firstName;
	private String lastName; 
	private String adress;
	private String city; 
	private int postalNr;
	private int phone;
	private String email;
	private ArrayList<Shift>shifts = new ArrayList<>();

public Employee(int employeeId, String firstName, String lastName, String adress, String city, int postalNr, int phone, String email) {
this.employeeId = employeeId;
this.firstName = firstName;
this.lastName = lastName;
this.adress = adress;
this.city = city;
this.postalNr = postalNr;
this.phone = phone;
this.email = email;


	}

 public void connectShiftToEmployee(Shift shift) {
	 if (shift == null)
		 throw new IllegalArgumentException("må ikke være null");
	 shifts.add(shift);
 }

}
