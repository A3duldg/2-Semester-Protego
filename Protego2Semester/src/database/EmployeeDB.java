package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

	public List<Employee> getAllEmployees() throws DataAccessException {
		List<Employee> employees = new ArrayList<>();
		String sql = "SELECT e.Id, p.firstName, p.lastName, p.phone, p.email, a.address, a.city, a.postalNr FROM Employee e JOIN Person p ON e.Id = p.id JOIN AddressCityPostal a ON p.addressId = a.addressId";

		try (Connection con = db.getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Employee e = new Employee(rs.getInt("Id"), rs.getString("firstName"), rs.getString("lastName"),
						rs.getString("address"), rs.getString("city"), rs.getInt("postalNr"), rs.getString("phone"),
						rs.getString("email"));
				employees.add(e);
			}
		} catch (SQLException ex) {
			throw new DataAccessException("Error reading employees from database", ex);
		}

		return employees;
	}

	/*
	 * @Override public void connectShiftToEmployee(Employee employee, Shift shift)
	 * throws DataAccessException { Connection con = db.getConnection();
	 * PreparedStatement checkEmployeeStmt = null; PreparedStatement checkShiftStmt
	 * = null; PreparedStatement checkExistingStmt = null; PreparedStatement
	 * insertStmt = null;
	 * 
	 * try { // Start transaktion via DBConnection db.startTransaction(con);
	 * 
	 * // Check om employee findes String employeeSql =
	 * "SELECT COUNT(*) FROM Employee WHERE Id = ?"; checkEmployeeStmt =
	 * con.prepareStatement(employeeSql); checkEmployeeStmt.setInt(1,
	 * employee.getEmployeeId()); ResultSet rsEmp =
	 * checkEmployeeStmt.executeQuery(); if (rsEmp.next() && rsEmp.getInt(1) == 0) {
	 * throw new IllegalStateException("Employee does not exist"); }
	 * 
	 * // Check om shift findes String shiftSql =
	 * "SELECT COUNT(*) FROM Shift WHERE shiftId = ?"; checkShiftStmt =
	 * con.prepareStatement(shiftSql); checkShiftStmt.setInt(1, shift.getShiftId());
	 * ResultSet rsShift = checkShiftStmt.executeQuery(); if (rsShift.next() &&
	 * rsShift.getInt(1) == 0) { throw new
	 * IllegalStateException("Shift does not exist"); }
	 * 
	 * // Check om employee allerede er på shift String checkExistingSql =
	 * "SELECT COUNT(*) FROM EmployeeShift WHERE Id = ? AND shiftId = ?";
	 * checkExistingStmt = con.prepareStatement(checkExistingSql);
	 * checkExistingStmt.setInt(1, employee.getEmployeeId());
	 * checkExistingStmt.setInt(2, shift.getShiftId()); ResultSet rsExisting =
	 * checkExistingStmt.executeQuery(); if (rsExisting.next() &&
	 * rsExisting.getInt(1) > 0) { throw new
	 * IllegalStateException("Employee is already assigned to this shift"); }
	 * 
	 * // Insert i EmployeeShift insertStmt =
	 * con.prepareStatement(CONNECT_SHIFT_TO_EMPLOYEE_Q); insertStmt.setInt(1,
	 * employee.getEmployeeId()); insertStmt.setInt(2, shift.getShiftId()); int rows
	 * = insertStmt.executeUpdate();
	 * 
	 * if (rows == 0) { throw new SQLException("Insert failed: no rows affected"); }
	 * 
	 * // Commit transaktion db.commitTransaction(con);
	 * 
	 * System.out.println("Shift assigned successfully: employeeId=" +
	 * employee.getEmployeeId() + ", shiftId=" + shift.getShiftId());
	 * 
	 * } catch (Exception e) { // Rollback ved fejl db.rollbackTransaction(con);
	 * throw new DataAccessException("Error connecting shift to employee", e);
	 * 
	 * } finally { // Oprydning af ressourcer db.releaseConnection(con);
	 * 
	 * try { if (checkEmployeeStmt != null) checkEmployeeStmt.close(); if
	 * (checkShiftStmt != null) checkShiftStmt.close(); if (checkExistingStmt !=
	 * null) checkExistingStmt.close(); if (insertStmt != null) insertStmt.close();
	 * // Connection håndteres af DBConnection vi lukker ikke her } catch
	 * (SQLException e) { e.printStackTrace(); } } }
	 * 
	 * @SuppressWarnings("unused") private Employee buildObject(ResultSet rs) throws
	 * SQLException { Employee employee = new Employee(rs.getInt("Id"),
	 * rs.getString("firstName"), rs.getString("lastName"), rs.getString("address"),
	 * rs.getString("city"), rs.getInt("postalNr"), rs.getString("phone"),
	 * rs.getString("email"));
	 * 
	 * return employee; }
	 */

	@Override
	public void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException {
		Connection con = null;
		PreparedStatement checkEmployeeStmt = null;
		PreparedStatement checkShiftStmt = null;
		PreparedStatement checkExistingStmt = null;
		PreparedStatement insertStmt = null;

		try {
			con = db.getConnection();
			db.startTransaction(con);

			// 1) Check om employee findes
			String employeeSql = "SELECT COUNT(*) FROM Employee WHERE Id = ?";
			checkEmployeeStmt = con.prepareStatement(employeeSql);
			checkEmployeeStmt.setInt(1, employee.getEmployeeId());
			try (ResultSet rsEmp = checkEmployeeStmt.executeQuery()) {
				if (rsEmp.next() && rsEmp.getInt(1) == 0) {
					throw new DataAccessException("Employee does not exist (id=" + employee.getEmployeeId() + ")",
							null);
				}
			}

			// 2) Check om shift findes OG hent contractId + availability for shift
			String shiftSql = "SELECT availability, contractId FROM Shift WHERE shiftId = ?";
			checkShiftStmt = con.prepareStatement(shiftSql);
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

			// --- BEGIN replacement: booking limit check per SHIFT using
			// contract.guardAmount as upper limit ---

			// 4) Tæl bookede employees for DENNE SHIFT
			int bookedForThisShift = 0;
			String countShiftSql = "SELECT COUNT(*) FROM EmployeeShift WHERE shiftId = ?";
			try (PreparedStatement countShiftStmt = con.prepareStatement(countShiftSql)) {
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
				String guardSql = "SELECT guardAmount FROM Contract WHERE contractId = ?";
				try (PreparedStatement guardStmt = con.prepareStatement(guardSql)) {
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
				String shiftGuardSql = "SELECT guardAmount FROM Shift WHERE shiftId = ?";
				try (PreparedStatement shiftGuardStmt = con.prepareStatement(shiftGuardSql)) {
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

			// 6) sammenlign efter jeres forretningsregel:
//	         allow hvis bookedForThisShift < effectiveLimit
//	         afvis hvis bookedForThisShift >= effectiveLimit
			if (bookedForThisShift >= effectiveLimit) {
				throw new DataAccessException("This shift is fully staffed. No more bookings allowed for this shift.",
						null);
			}

			// --- END replacement ---

			// 6) Check om employee allerede er på shift
			String checkExistingSql = "SELECT COUNT(*) FROM EmployeeShift WHERE Id = ? AND shiftId = ?";
			checkExistingStmt = con.prepareStatement(checkExistingSql);
			checkExistingStmt.setInt(1, employee.getEmployeeId());
			checkExistingStmt.setInt(2, shift.getShiftId());
			try (ResultSet rsExisting = checkExistingStmt.executeQuery()) {
				if (rsExisting.next() && rsExisting.getInt(1) > 0) {
					throw new DataAccessException("Employee is already assigned to this shift", null);
				}
			}

			// 7) Insert i EmployeeShift
			insertStmt = con.prepareStatement(CONNECT_SHIFT_TO_EMPLOYEE_Q);
			insertStmt.setInt(1, employee.getEmployeeId());
			insertStmt.setInt(2, shift.getShiftId());
			int rows = insertStmt.executeUpdate();
			if (rows == 0) {
				throw new DataAccessException("Insert failed: no rows affected", null);
			}

			// 8) Commit transaktion
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
			throw new DataAccessException("Unexpected error while connecting shift to employee: " + e.getMessage(), e);
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

	@Override
	public Employee getEmployeeId(int employeeId) throws DataAccessException {
		Employee employee = null;
		String sql = "SELECT e.Id, p.firstName, p.lastName, p.phone, p.email, a.address, a.city, a.postalNr "
				+ "FROM Employee e " + "JOIN Person p ON e.Id = p.id "
				+ "JOIN AddressCityPostal a ON p.addressId = a.addressId " + "WHERE e.Id = ?";

		try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, employeeId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					employee = new Employee(rs.getInt("Id"), rs.getString("firstName"), rs.getString("lastName"),
							rs.getString("address"), rs.getString("city"), rs.getInt("postalNr"), rs.getString("phone"),
							rs.getString("email"));
				}
			}

		} catch (SQLException e) {
			throw new DataAccessException("Error loading employee id=" + employeeId, e);
		}

		return employee;
	}

}
