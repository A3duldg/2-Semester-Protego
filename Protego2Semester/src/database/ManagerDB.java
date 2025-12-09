package database;

import interfaceDB.ManagerDBIF;
import model.Manager;

import java.sql.*;

public class ManagerDB implements ManagerDBIF {

	private final DBConnection db;

	public ManagerDB() throws DataAccessException {
		db = DBConnection.getInstance();
	}

	private Manager mapManager(ResultSet rs) throws SQLException {
		return new Manager(rs.getInt("managerId"), rs.getString("firstName"), rs.getString("lastName"),
				rs.getString("address"), rs.getString("city"), rs.getInt("postalNr"), rs.getString("phone"),
				rs.getString("email"));
	}

	@Override
	public Manager findManagerId(int managerId) {
		Manager manager = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = db.getConnection();

			
			String sql = "SELECT m.managerId, p.firstName, p.lastName, p.phone, p.email, "
					+ "a.address, a.city, a.postalNr " + "FROM Manager m " + "JOIN Person p ON m.managerId = p.id "
					+ "JOIN AddressCityPostal a ON p.addressId = a.addressId " + "WHERE m.managerId = ?";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, managerId);
			rs = stmt.executeQuery();

			if (rs.next()) {

				manager = mapManager(rs);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();
			} catch (Exception ignored) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ignored) {
			}
		}

		return manager;
	}

	@Override
	public Manager findActiveManager(int managerId) {

		return findManagerId(managerId);
	}
}
