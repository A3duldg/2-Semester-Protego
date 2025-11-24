package Database;

import interfaceDB.ManagerDBIF;
import Model.Manager;
import java.sql.*;

public class ManagerDB implements ManagerDBIF {

    private static ManagerDB instance;

    private static final String FIND_ACTIVE_MANAGER_Q = 
        "SELECT * FROM managers WHERE managerId = ?";

    private PreparedStatement findActiveManager;

    private ManagerDB() throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        findActiveManager = conn.prepareStatement(FIND_ACTIVE_MANAGER_Q);
    }

    public static ManagerDB getInstance() {
        if (instance == null) {
            try {
                instance = new ManagerDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    @Override
    public Manager findActiveManager(int managerId) {
        try {
            findActiveManager.setInt(1, managerId);
            ResultSet rs = findActiveManager.executeQuery();

            if (rs.next()) {
                return buildObject(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Manager buildObject(ResultSet rs) throws SQLException {
        return new Manager(
            rs.getInt("managerId"),
            rs.getString("firstName"),
            rs.getString("lastName"),
            rs.getString("adress"),
            rs.getString("city"),
            rs.getInt("postalNr"),
            rs.getInt("phone"),
            rs.getString("email")
        );
    }
}
