package database;

import java.sql.*;
import java.util.ArrayList;

import interfaceDB.EmployeeDBIF;
import model.Employee;
import model.Shift;

public class EmployeeDB implements EmployeeDBIF {
	private static final String CONNECT_SHIFT_TO_EMPLOYEE_Q = "INSERT INTO EmployeeShift (employee_id, shift_id) VALUES (?, ?)";

	private PreparedStatement connectShiftToEmployeestmt;

	private Connection con;

	public EmployeeDB() {
		try {
			this.con = DBConnection.getInstance().getConnection();

			connectShiftToEmployeestmt = con.prepareStatement(CONNECT_SHIFT_TO_EMPLOYEE_Q);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not intialize EmployeeDB", e);
		}
	}

	@Override
	public void connectShiftToEmployee(Employee employee, Shift shift) {
		if (con == null) {
			System.out.println("No database connection available");
			return;
		}

		try {
			connectShiftToEmployeestmt.setInt(1, employee.getEmployeeId());
			connectShiftToEmployeestmt.setInt(2, shift.getShiftId());
			int rowsAffected = connectShiftToEmployeestmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Shift assigned successfully: employeeId=" + employee.getEmployeeId() + ", shiftId="
						+ shift.getShiftId());
			} else {
				System.out.println("Failed to assign shift to employee.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private Employee buildObject(ResultSet rs) throws SQLException {
		Employee employee = new Employee(rs.getInt("employeeId"), rs.getString("firstName"), rs.getString("lastName"),
				rs.getString("adress"), rs.getString("city"), rs.getInt("postalNr"), rs.getInt("phone"),
				rs.getString("email"));

		return employee;
	}

}
