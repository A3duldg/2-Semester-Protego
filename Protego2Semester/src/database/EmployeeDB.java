package database;

import java.sql.*;
import java.util.ArrayList;

import interfaceDB.EmployeeDBIF;
import model.Employee;
import model.Shift;

public class EmployeeDB implements EmployeeDBIF {
	private final DBConnection db;
	private static final String CONNECT_SHIFT_TO_EMPLOYEE_Q = "INSERT INTO EmployeeShift (Id, shiftId) VALUES (?, ?)";

	// gamle kode jeg fjerner.
//	private PreparedStatement connectShiftToEmployeestmt;

	// private Connection con;

	public EmployeeDB() throws DataAccessException {
		db = DBConnection.getInstance();

		try {
			// this.con = DBConnection.getInstance().getConnection();

			// connectShiftToEmployeestmt =
			// con.prepareStatement(CONNECT_SHIFT_TO_EMPLOYEE_Q);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not intialize EmployeeDB", e);
		}
	}

	@Override
	public void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException {
		DBConnection db = DBConnection.getInstance();
		Connection con = null;
		PreparedStatement checkEmployeeStmt = null;
		PreparedStatement checkShiftStmt = null;
		PreparedStatement checkExistingStmt = null;
		PreparedStatement insertStmt = null;

		try {
			// Start transaktion via DBConnection
			db.startTransaction();
			con = db.getConnection();

			// Check om employee findes
			String employeeSql = "SELECT COUNT(*) FROM Employee WHERE Id = ?";
			checkEmployeeStmt = con.prepareStatement(employeeSql);
			checkEmployeeStmt.setInt(1, employee.getEmployeeId());
			ResultSet rsEmp = checkEmployeeStmt.executeQuery();
			if (rsEmp.next() && rsEmp.getInt(1) == 0) {
				throw new IllegalStateException("Employee does not exist");
			}

			// Check om shift findes
			String shiftSql = "SELECT COUNT(*) FROM Shift WHERE shiftId = ?";
			checkShiftStmt = con.prepareStatement(shiftSql);
			checkShiftStmt.setInt(1, shift.getShiftId());
			ResultSet rsShift = checkShiftStmt.executeQuery();
			if (rsShift.next() && rsShift.getInt(1) == 0) {
				throw new IllegalStateException("Shift does not exist");
			}

			// Check om employee allerede er på shift
			String checkExistingSql = "SELECT COUNT(*) FROM EmployeeShift WHERE Id = ? AND shiftId = ?";
			checkExistingStmt = con.prepareStatement(checkExistingSql);
			checkExistingStmt.setInt(1, employee.getEmployeeId());
			checkExistingStmt.setInt(2, shift.getShiftId());
			ResultSet rsExisting = checkExistingStmt.executeQuery();
			if (rsExisting.next() && rsExisting.getInt(1) > 0) {
				throw new IllegalStateException("Employee is already assigned to this shift");
			}

			// Insert i EmployeeShift
			insertStmt = con.prepareStatement(CONNECT_SHIFT_TO_EMPLOYEE_Q);
			insertStmt.setInt(1, employee.getEmployeeId());
			insertStmt.setInt(2, shift.getShiftId());
			int rows = insertStmt.executeUpdate();

			if (rows == 0) {
				throw new SQLException("Insert failed: no rows affected");
			}

			// Commit transaktion
			db.commitTransaction();

			System.out.println("Shift assigned successfully: employeeId=" + employee.getEmployeeId() + ", shiftId="
					+ shift.getShiftId());

		} catch (Exception e) {
			// Rollback ved fejl
			db.rollbackTransaction();
			throw new DataAccessException("Error connecting shift to employee", e);

		} finally {
			// Oprydning af ressourcer
			try {
				if (checkEmployeeStmt != null)
					checkEmployeeStmt.close();
				if (checkShiftStmt != null)
					checkShiftStmt.close();
				if (checkExistingStmt != null)
					checkExistingStmt.close();
				if (insertStmt != null)
					insertStmt.close();
				// Connection håndteres af DBConnection vi lukker ikke her
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
