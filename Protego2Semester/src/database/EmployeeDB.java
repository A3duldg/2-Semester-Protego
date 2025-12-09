package database;

import java.sql.*;
import java.util.ArrayList;

import interfaceDB.EmployeeDBIF;
import model.Employee;
import model.Shift;

public class EmployeeDB implements EmployeeDBIF {
	private final DBConnection db;
	private static final String CONNECT_SHIFT_TO_EMPLOYEE_Q = "INSERT INTO EmployeeShift (Id, shiftId) VALUES (?, ?)";

	private PreparedStatement connectShiftToEmployeestmt;

	private Connection con;

	public EmployeeDB() throws DataAccessException {
		db = DBConnection.getInstance();

		try {
			this.con = DBConnection.getInstance().getConnection();

			connectShiftToEmployeestmt = con.prepareStatement(CONNECT_SHIFT_TO_EMPLOYEE_Q);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not intialize EmployeeDB", e);
		}
	}

	@Override
	// transaction
	public void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException {
		String sql = "INSERT INTO EmployeeShift (Id, shiftId) VALUES (?, ?)";
		String checkSql = "SELECT COUNT(*) FROM EmployeeShift WHERE Id = ? AND shiftId = ?";

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement checkStmt = con.prepareStatement(checkSql)) {

			checkStmt.setInt(1, employee.getEmployeeId());
			checkStmt.setInt(2, shift.getShiftId());
			try (ResultSet rs = checkStmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					throw new IllegalStateException(" You are already assigned to this shift");
				}
			}

			try (PreparedStatement insertStmt = con.prepareStatement(sql)) {

				insertStmt.setInt(1, employee.getEmployeeId());
				insertStmt.setInt(2, shift.getShiftId());
				int rowsAffected = insertStmt.executeUpdate();

				if (rowsAffected > 0) {
					System.out.println("Shift assigned successfully: employeeId=" + employee.getEmployeeId()
							+ ", shiftId=" + shift.getShiftId());
				} else {
					System.out.println("Failed to assign shift to employee.");
				}
			}

		} catch (SQLException e) {
			throw new DataAccessException("Error connecting shift to employee", e);
		}
	}

	@SuppressWarnings("unused")
	private Employee buildObject(ResultSet rs) throws SQLException {
		Employee employee = new Employee(rs.getInt("Id"), rs.getString("firstName"), rs.getString("lastName"),
				rs.getString("address"), rs.getString("city"), rs.getInt("postalNr"), rs.getString("phone"),
				rs.getString("email"));

		return employee;
	}

	@Override
	public Employee getEmployeeId(int employeeId) {
		Employee employee = null;
		String sql = "SELECT e.Id, p.firstName, p.lastName, p.phone, p.email, a.address, a.city, a.postalNr FROM Employee e JOIN Person p ON e.Id = p.id JOIN AddressCityPostal a ON p.addressId = a.addressId WHERE e.Id = ?";

		try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, employeeId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					employee = new Employee(rs.getInt("Id"), rs.getString("firstName"), rs.getString("lastName"),
							rs.getString("address"), rs.getString("city"), rs.getInt("postalNr"), rs.getString("phone"),
							rs.getString("email"));
				}
			}
		} catch (DataAccessException dae) {
			dae.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employee;
	}

}
