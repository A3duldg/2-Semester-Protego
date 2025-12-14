package database;

import java.sql.*;
import java.util.ArrayList;

import interfaceDB.EmployeeDBIF;
import model.Employee;
import model.Shift;

// Jeg syntes at det ser fint ud har ikke kunne spotte nogle mærkelige ting selv har kun fjernet en masse gamle kode.
public class EmployeeDB implements EmployeeDBIF {
	private final DBConnection db;
	private static final String CONNECT_SHIFT_TO_EMPLOYEE_Q = "INSERT INTO EmployeeShift (employeeId, shiftId) VALUES (?, ?)";

	private static final String GET_ALL_EMPLOYEE_Q = "SELECT e.employeeId, p.firstName, p.lastName, p.phone, p.email, a.address, a.city, a.postalNr FROM Employee e JOIN Person p ON e.employeeId = p.personId JOIN AddressCityPostal a ON p.addressId = a.addressId";

	private static final String CHECK_EMPLOYEE_EXISTS_Q = "SELECT COUNT(*) FROM Employee WHERE employeeId = ?";

	private static final String GET_SHIFT_INFO_Q = "SELECT availability, contractId FROM Shift WHERE shiftId = ?";

	private static final String COUNT_EMPLOYEES_FOR_SHIFTS_Q = "SELECT COUNT(*) FROM EmployeeShift WHERE shiftId = ?";

	private static final String GET_SHIFT_GUARD_AMOUNT_Q = "SELECT guardAmount FROM Contract WHERE contractId = ?";

	private static final String GET_CONTRACT_GUARD_AMOUNT_Q = "SELECT guardAmount FROM Contract WHERE contractId = ?";

	private static final String CHECK_EMPLOYEE_ALREADY_ASSIGNED_Q = "SELECT COUNT(*) FROM EmployeeShift WHERE employeeId = ? AND shiftId = ?";

	private static final String GET_EMPLOYEE_BY_ID_Q = "SELECT e.employeeId, p.firstName, p.lastName, p.phone, p.email, a.address, a.city, a.postalNr FROM Employee e JOIN Person p ON e.employeeId = p.personId JOIN AddressCityPostal a ON p.addressId = a.addressId WHERE e.employeeId = ?";

	public EmployeeDB() throws DataAccessException {
		db = DBConnection.getInstance();

		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not intialize EmployeeDB", e);
		}
	}

	public ArrayList<Employee> getAllEmployees() throws DataAccessException {
		ArrayList<Employee> employees = new ArrayList<>();

		try (Connection con = db.getConnection();
				PreparedStatement stmt = con.prepareStatement(GET_ALL_EMPLOYEE_Q);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Employee e = new Employee(rs.getInt("employeeId"), rs.getString("firstName"), rs.getString("lastName"),
						rs.getString("address"), rs.getString("city"), rs.getInt("postalNr"), rs.getString("phone"),
						rs.getString("email"));
				employees.add(e);
			}
		} catch (SQLException ex) {
			throw new DataAccessException("Error reading employees from database", ex);
		}

		return employees;
	}

	@Override
	public void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException {

		// we get the shiftId
		final int shiftId = shift.getShiftId();

		Connection con = null;
		PreparedStatement checkEmployeeStmt = null;
		PreparedStatement checkShiftStmt = null;
		PreparedStatement checkExistingStmt = null;
		PreparedStatement insertStmt = null;

		// We synchronize so only one thread can perform check and insert for the shift
		// at the same time
		System.out.println("[" + Thread.currentThread().getName() + "] waiting to enter booking section");
		synchronized (this) {

			// Critical section
			System.out.println("[" + Thread.currentThread().getName() + " Acquired lock for shiftId=" + shiftId);

			// ----------------- IMPORTANT! ---------------------------
			// Here we do a thread sleep to make blocking more visible. We should comment
			// the line below when we dont want to test
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}
			// -------------------------------------------------------------------------------------------

			try {
				con = db.getConnection();
				db.startTransaction(con);

				// 1) Check if employee exists

				checkEmployeeStmt = con.prepareStatement(CHECK_EMPLOYEE_EXISTS_Q);
				checkEmployeeStmt.setInt(1, employee.getEmployeeId()); // 1 is the first ? in the prepared statement / sql string
				try (ResultSet rsEmp = checkEmployeeStmt.executeQuery()) {
					if (rsEmp.next() && rsEmp.getInt(1) == 0) {
						throw new DataAccessException(
								"Employee does not exist (employeeId=" + employee.getEmployeeId() + ")", null);
					}
				}

				// 2) Checks if contract exists and gets contractId + availability for shift
				checkShiftStmt = con.prepareStatement(GET_SHIFT_INFO_Q);
				checkShiftStmt.setInt(1, shift.getShiftId());
				boolean availability;
				int contractId;
				try (ResultSet rsShift = checkShiftStmt.executeQuery()) {
					if (!rsShift.next()) {
						throw new DataAccessException("Shift does not exist (id=" + shift.getShiftId() + ")", null);
					}
					availability = rsShift.getBoolean("availability");
				    if (rsShift.wasNull()) {
				        throw new DataAccessException(
				            "Shift.availability is NULL (shiftId=" + shift.getShiftId() + ")", null);
				    }

				    contractId = rsShift.getInt("contractId");
				    if (rsShift.wasNull()) {
				        throw new DataAccessException(
				            "Shift has no contract (shiftId=" + shift.getShiftId() + ")", null);
				}
				}

				// 3) If the shift is not available, we decline the booking
				if (!availability) {
					throw new DataAccessException("Shift is not available (shiftId=" + shift.getShiftId() + ")", null);
				}

				// 4) Count the amount of employees booked on this shift
				int bookedForThisShift = 0;
				try (PreparedStatement countShiftStmt = con.prepareStatement(COUNT_EMPLOYEES_FOR_SHIFTS_Q)) {
					countShiftStmt.setInt(1, shift.getShiftId());
					try (ResultSet rs = countShiftStmt.executeQuery()) {
						if (rs.next())
							bookedForThisShift = rs.getInt(1);
					}
				}

				// 5) Sets the effective limit:
				// If shift is connected to a contract, then it uses contract.guardAmount as
				// limit for bookings on the shift
				// otherwise we get an error
				int contractGuardLimit;
				
					try (PreparedStatement guardStmt = con.prepareStatement(GET_CONTRACT_GUARD_AMOUNT_Q)) {
						guardStmt.setInt(1, contractId);
						try (ResultSet rsG = guardStmt.executeQuery()) {
							if (!rsG.next()) {
								throw new DataAccessException(
										"Referenced contract not found (contractId=" + contractId + ")", null);
							}
							contractGuardLimit = rsG.getInt("guardAmount");
					        if (rsG.wasNull() || contractGuardLimit <= 0) {
					            throw new DataAccessException(
					                "Invalid guardAmount for contractId=" + contractId, null);
								}
						
								
							}
						}

				int effectiveLimit = contractGuardLimit;

// ---------------------------------- DEBUG ------------------------------------------------------------------------
				// (valgfri) diagnostic - kan fjernes når alt er testet
				System.out.println("DEBUG booking check: shiftId=" + shift.getShiftId() + ", contractId=" + contractId
						+ ", bookedForThisShift=" + bookedForThisShift + ", effectiveLimit="
						+ (effectiveLimit == Integer.MAX_VALUE ? "NO_LIMIT" : effectiveLimit));
// ----------------------------------------------------------------------------------------------------------

				// 6) Check if the shift is fully booked or not:
//	         allow if bookedForThisShift < effectiveLimit
//	         reject if bookedForThisShift >= effectiveLimit
				if (bookedForThisShift >= effectiveLimit) {
					throw new DataAccessException(
							"This shift is fully staffed. No more bookings allowed for this shift.", null);
				}

				// 7) Checks if the employee has already booked the shift
				checkExistingStmt = con.prepareStatement(CHECK_EMPLOYEE_ALREADY_ASSIGNED_Q);
				checkExistingStmt.setInt(1, employee.getEmployeeId());
				checkExistingStmt.setInt(2, shift.getShiftId());
				try (ResultSet rsExisting = checkExistingStmt.executeQuery()) {
					if (rsExisting.next() && rsExisting.getInt(1) > 0) {
						throw new DataAccessException("Employee is already assigned to this shift", null);
					}
				}

				// 8) Insert booking
				insertStmt = con.prepareStatement(CONNECT_SHIFT_TO_EMPLOYEE_Q);
				insertStmt.setInt(1, employee.getEmployeeId());
				insertStmt.setInt(2, shift.getShiftId());
				int rows = insertStmt.executeUpdate();
				if (rows == 0) {
					throw new DataAccessException("Insert failed: no rows affected", null);
				}

				// 9) Commit transaction
				db.commitTransaction(con);
				System.out.println("Shift assigned successfully: employeeId=" + employee.getEmployeeId() + ", shiftId="
						+ shift.getShiftId());

			} catch (SQLException e) {
				if (con != null)
					db.rollbackTransaction(con);
				throw new DataAccessException("SQL error while connecting shift to employee: " + e.getMessage(), e);
			} catch (DataAccessException dae) {
				if (con != null)
					db.rollbackTransaction(con);
				throw dae;
			} catch (Exception e) {
				if (con != null)
					db.rollbackTransaction(con);
				throw new DataAccessException("Unexpected error while connecting shift to employee: " + e.getMessage(),
						e);
			} finally {
				// Cleanup of resources
				try {
					if (checkEmployeeStmt != null)
						checkEmployeeStmt.close();
					if (checkShiftStmt != null)
						checkShiftStmt.close();
					if (checkExistingStmt != null)
						checkExistingStmt.close();
					if (insertStmt != null)
						insertStmt.close();
				} catch (SQLException ignore) {
				}

				if (con != null)
					db.releaseConnection(con);

			}
		}

	}

	@Override
	public Employee getEmployeeId(int employeeId) throws DataAccessException {
		Employee employee = null;

		try (Connection conn = db.getConnection();
				PreparedStatement stmt = conn.prepareStatement(GET_EMPLOYEE_BY_ID_Q)) {

			stmt.setInt(1, employeeId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					employee = new Employee(rs.getInt("employeeId"), rs.getString("firstName"),
							rs.getString("lastName"), rs.getString("address"), rs.getString("city"),
							rs.getInt("postalNr"), rs.getString("phone"), rs.getString("email"));
				}
			}

		} catch (SQLException e) {
			throw new DataAccessException("Error loading employee employeeId=" + employeeId, e);
		}

		return employee;
	}

}
