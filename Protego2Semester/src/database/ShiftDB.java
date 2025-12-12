package database;

import java.sql.*;
import java.util.ArrayList;

import interfaceDB.ShiftDBIF;
import model.Shift;

public class ShiftDB implements ShiftDBIF {

	private Connection con;
	private final DBConnection db;



	private static final String FIND_SHIFT_BY_AVAILABILITY_Q = "SELECT shiftId, startTime, endTime, guardAmount, shiftLocation, type, availability, contractId FROM Shift WHERE availability = ?";

	private static final String CREATE_SHIFT_Q = "INSERT INTO Shift (startTime, endTime, guardAmount, shiftLocation, type, availability, contractId) VALUES (?, ?, ?, ?, ?, ?, ?)";

	private static final String SET_SHIFT_TYPE_Q = "UPDATE Shift SET type = ? WHERE shiftId = ?";



	public ShiftDB() throws DataAccessException {

		db = DBConnection.getInstance();

	}


	@Override
	public ArrayList<Shift> findShiftByAvailability(boolean availability) throws DataAccessException {
		ArrayList<Shift> list = new ArrayList<>();

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(FIND_SHIFT_BY_AVAILABILITY_Q)) {

			stmt.setBoolean(1, availability);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Shift shift = new Shift(rs.getInt("startTime"), rs.getInt("endTime"), rs.getInt("guardAmount"),
							rs.getString("shiftLocation"), rs.getBoolean("availability"), rs.getInt("shiftId"));
					shift.setShiftType(rs.getString("type"));
					Integer contractIdObj = null;
                    try {
                        contractIdObj = rs.getObject("contractId", Integer.class);
                    } catch (SQLException ignore) {
                        // Hvis kolonnen ikke findes, forbliver contractIdObj null
                    }
                    int contractId = (contractIdObj != null) ? contractIdObj.intValue() : 0;
                    shift.setContractId(contractId); // Tjek den her
					list.add(shift);
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding shifts", e);
		} finally {
			if (con !=null) {
				db.releaseConnection(con);
			}
		}

		return list;
	}

	@Override
	public int createShift(Shift shift) {
		int newId = -1;

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(CREATE_SHIFT_Q, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, shift.getStartTime());
			stmt.setInt(2, shift.getEndTime());
			stmt.setInt(3, shift.getGuardAmount());
			stmt.setString(4, shift.getShiftLocation());
			stmt.setString(5, shift.getType());
			stmt.setBoolean(6, shift.isAvailable());
			stmt.setInt(7, shift.getContract());

			int cid = shift.getContract();
	        if (cid > 0) {
	            stmt.setInt(7, cid);
	        } else {
	            stmt.setNull(7, java.sql.Types.INTEGER);
	        }

	        int rows = stmt.executeUpdate();

	        if (rows > 0) {
	            try (ResultSet rs = stmt.getGeneratedKeys()) {
	                if (rs.next()) {
	                    newId = rs.getInt(1);
	                }
	            }
	        }

	    } catch (SQLException | DataAccessException e) {
	        // log fejlen (evt. vis venlig besked i UI)
	        e.printStackTrace();
	    } finally {
	    	if (con !=null) {
	    		db.releaseConnection(con);
	    	}
	    }
	    return newId;
	}

	@Override
	public boolean setShiftType(Shift shift) {
		boolean result = false;
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement("UPDATE Shift SET type = ? WHERE shiftId = ?")) {

			stmt.setString(1, shift.getType());
			stmt.setInt(2, shift.getShiftId());

			int rows = stmt.executeUpdate();
			result = rows > 0;

		} catch (SQLException | DataAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean bookShift(Shift shift) {
		boolean result = false;

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement("UPDATE Shift SET availability = 0 WHERE shiftId = ?")) {

			stmt.setInt(1, shift.getShiftId());

			int rows = stmt.executeUpdate();
			result = rows > 0;

			if (result) {
				shift.setAvailability(false);
			}
		} catch (SQLException | DataAccessException e) {

		}
		return result;
	}
	
	public int countEmployeesForShift(int shiftId) throws DataAccessException {
	    String sql = "SELECT COUNT(*) FROM EmployeeShift WHERE shiftId = ?";
	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql)) {
	        stmt.setInt(1, shiftId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error counting employees for shift", e);
	    }
	    return 0;
	}

}