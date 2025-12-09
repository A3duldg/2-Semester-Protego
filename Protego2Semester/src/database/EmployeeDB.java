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

	public EmployeeDB() throws DataAccessException{
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
		Employee employee = new Employee(rs.getInt("Id"), rs.getString("firstName"), rs.getString("lastName"),
				rs.getString("address"), rs.getString("city"), rs.getInt("postalNr"), rs.getInt("phone"),
				rs.getString("email"));

		return employee;
	}
	
	@Override
    public Employee getEmployeeId(int employeeId) {
        Employee employee = null;
        String sql = "SELECT e.Id, p.firstName, p.lastName, p.phone, p.email, " +
                     "a.address, a.city, a.postalNr " +
                     "FROM Employee e " +
                     "JOIN Person p ON e.Id = p.id " +
                     "JOIN AddressCityPostal a ON p.addressId = a.addressId " +
                     "WHERE e.Id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee(
                        rs.getInt("Id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getInt("postalNr"),
                        rs.getInt("phone"),   // ⚠️ use getString if phone is VARCHAR
                        rs.getString("email")
                    );
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
