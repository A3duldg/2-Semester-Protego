package model;

public class Diploma {
private Employee employee;
private Certified type;
private String date;
private String validPeriode;



public Diploma(Employee employee, Certified type, String date, String validPeriode) {
	this.employee = employee;
	this.type = type;
	this.date = date;
	this.validPeriode = validPeriode;
}


public Employee getEmployee() {
	return employee;
}
public void setEmployee(Employee employee) {
	this.employee = employee;
}
public Certified getType() {
	return type;
}
public void setType(Certified type) {
	this.type = type;
}
public String getDate() {
	return date;
}
public void setDate(String date) {
	this.date = date;
}
public String getValidPeriode() {
	return validPeriode;
}
public void setValidPeriode(String validPeriode) {
	this.validPeriode = validPeriode;
}
}
