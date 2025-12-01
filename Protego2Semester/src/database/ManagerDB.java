package database;

import interfaceDB.ManagerDBIF;
import model.Manager;

import java.sql.*;

public class ManagerDB implements ManagerDBIF {

    private final DBConnection db;

    public ManagerDB() {
        db = DBConnection.getInstance();
    }

    @Override
    public Manager findManager(int managerId) {
        Manager manager = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = db.getConnection();

            String sql = "SELECT managerId, firstName, lastName, address, city, postalNr, phone, email " +
                         "FROM Manager WHERE managerId = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, managerId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                manager = new Manager(
                        rs.getInt("managerId"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getInt("postalNr"),
                        rs.getInt("phone"),
                        rs.getString("email")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources and return connection to pool
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        return manager;
    }

    @Override
    public Manager findActiveManager(int managerId) {
        // Example: if you want to check an "active" flag, extend SQL above.
        return findManager(managerId);
    }
}