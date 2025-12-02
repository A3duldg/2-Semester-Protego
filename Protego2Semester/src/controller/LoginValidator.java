package controller;

public class LoginValidator {
	
	public boolean validate(String id, String password ) {
		//Test with fixed values
		return "admin".equals(id) && "Password".equals(password);
	}

}
