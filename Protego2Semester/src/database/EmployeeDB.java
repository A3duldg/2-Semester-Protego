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
	
	private static final String GET_EMPLOYEE_BY_ID_Q = "SELECT e.employeeId, p.firstName, p.lastName, p.phone, p.email, a.address, a.city, a.postalNr FROM Employee e " + "JOIN Person p ON e.employeeId = p.personId JOIN AddressCityPostal a ON p.addressId = a.addressId WHERE e.employeeId = ?";
	

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
		// we get or create a lock for this shift
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
			// kritisk sektion
			System.out.println("[" + Thread.currentThread().getName() + " Acquired lock for shiftId=" + shiftId);
			// ----------------- IMPORTANT! ---------------------------
			// Here we do a thread sleep to make blocking more visible. We should comment
			// the line below when we dont want to test
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}

			try {
				con = db.getConnection();
				db.startTransaction(con);

				// 1) Check om employee findes
				
				checkEmployeeStmt = con.prepareStatement(CHECK_EMPLOYEE_EXISTS_Q);
				checkEmployeeStmt.setInt(1, employee.getEmployeeId());
				try (ResultSet rsEmp = checkEmployeeStmt.executeQuery()) {
					if (rsEmp.next() && rsEmp.getInt(1) == 0) {
						throw new DataAccessException(
								"Employee does not exist (employeeId=" + employee.getEmployeeId() + ")", null);
					}
				}

				// 2) Check om shift findes OG hent contractId + availability for shift
				checkShiftStmt = con.prepareStatement(GET_SHIFT_INFO_Q);
				checkShiftStmt.setInt(1, shift.getShiftId());
				Boolean availability = null;
				Integer contractId = null;
				try (ResultSet rsShift = checkShiftStmt.executeQuery()) {
					if (rsShift.next()) {
						availability = rsShift.getBoolean("availability");
						// contractId kan være NULL i DB -> læs som Integer
						contractId = rsShift.getObject("contractId", Integer.class);
					} else {
						throw new DataAccessException("Shift does not exist (id=" + shift.getShiftId() + ")", null);
					}
				}

				// 3) Hvis shift ikke er available, afviser vi
				if (availability != null && !availability) {
					throw new DataAccessException("Shift is not available (shiftId=" + shift.getShiftId() + ")", null);
				}

				// 4) Tæl bookede employees for DENNE SHIFT
				int bookedForThisShift = 0;
				try (PreparedStatement countShiftStmt = con.prepareStatement(COUNT_EMPLOYEES_FOR_SHIFTS_Q)) {
					countShiftStmt.setInt(1, shift.getShiftId());
					try (ResultSet rs = countShiftStmt.executeQuery()) {
						if (rs.next())
							bookedForThisShift = rs.getInt(1);
					}
				}

				// 5) Bestem effective limit:
//	         - hvis shift er knyttet til en contract: brug contract.guardAmount som limit (maks antal bookings for denne shift)
//	         - ellers fallback til Shift.guardAmount hvis den findes i DB
				int contractGuardLimit = -1;
				if (contractId != null && contractId > 0) {
					try (PreparedStatement guardStmt = con.prepareStatement(GET_CONTRACT_GUARD_AMOUNT_Q)) {
						guardStmt.setInt(1, contractId);
						try (ResultSet rsG = guardStmt.executeQuery()) {
							if (rsG.next()) {
								Integer guardAmountObj = rsG.getObject("guardAmount", Integer.class);
								if (guardAmountObj != null) {
									contractGuardLimit = guardAmountObj.intValue();
								} else {
									throw new DataAccessException(
											"Contract.guardAmount is not set for contractId=" + contractId, null);
								}
							} else {
								throw new DataAccessException(
										"Referenced contract not found (contractId=" + contractId + ")", null);
							}
						}
					}
				}

				// fallback: hvis ingen contractGuardLimit, brug shift.guardAmount fra DB (hvis
				// tilgængelig)
				int effectiveLimit;
				if (contractGuardLimit > 0) {
					effectiveLimit = contractGuardLimit;
				} else {
					Integer shiftGuardObj = null;
					try (PreparedStatement shiftGuardStmt = con.prepareStatement(GET_SHIFT_GUARD_AMOUNT_Q)) {
						shiftGuardStmt.setInt(1, shift.getShiftId());
						try (ResultSet rsS = shiftGuardStmt.executeQuery()) {
							if (rsS.next()) {
								shiftGuardObj = rsS.getObject("guardAmount", Integer.class);
							}
						}
					}
					if (shiftGuardObj != null && shiftGuardObj > 0) {
						effectiveLimit = shiftGuardObj.intValue();
					} else {
						// ingen limit fundet => ingen begrænsning
						effectiveLimit = Integer.MAX_VALUE;
					}
				}

				// (valgfri) diagnostic - kan fjernes når alt er testet
				System.out.println("DEBUG booking check: shiftId=" + shift.getShiftId() + ", contractId=" + contractId
						+ ", bookedForThisShift=" + bookedForThisShift + ", effectiveLimit="
						+ (effectiveLimit == Integer.MAX_VALUE ? "NO_LIMIT" : effectiveLimit));

				// 6) sammenlign:
//	         allow hvis bookedForThisShift < effectiveLimit
//	         afvis hvis bookedForThisShift >= effectiveLimit
				if (bookedForThisShift >= effectiveLimit) {
					throw new DataAccessException(
							"This shift is fully staffed. No more bookings allowed for this shift.", null);
				}

				// 7) Check om employee allerede er på shift
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

				// 9) Commit transaktion
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

		try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(GET_EMPLOYEE_BY_ID_Q)) {

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
