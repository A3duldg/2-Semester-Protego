package database;

import java.sql.*;
import java.util.ArrayList;

import interfaceDB.ShiftDBIF;
import model.Shift;

public class ShiftDB implements ShiftDBIF {

	private Connection con;
	private final DBConnection db;

	private static final String FIND_SHIFT_BY_AVAILABILITY_Q = "SELECT shiftId, shiftDate, startTime, endTime, guardAmount, shiftLocation, type, availability, contractId FROM Shift WHERE availability = ?";

	private static final String CREATE_SHIFT_Q = "INSERT INTO Shift (shiftDate, startTime, endTime, guardAmount, shiftLocation, type, availability, contractId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String SET_SHIFT_TYPE_Q = "UPDATE Shift SET type = ? WHERE shiftId = ?";

	private static final String BOOK_SHIFT_Q = "UPDATE Shift SET availability = 0 WHERE shiftId = ?";
	
	private static final String COUNT_EMPLOYEES_FOR_SHIFT_Q = "SELECT COUNT(*) FROM EmployeeShift WHERE shiftId = ?";

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
					Date d = rs.getDate("shiftDate");
				    if (d != null) {
				        shift.setShiftDate(d.toLocalDate());
				    }
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
		}

		return list;
	}

	@Override
	public int createShift(Shift shift) {
		int newId = -1;

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(CREATE_SHIFT_Q, Statement.RETURN_GENERATED_KEYS)) {
			if (shift.getShiftDate() != null) {
			    stmt.setDate(1, java.sql.Date.valueOf(shift.getShiftDate()));
			} else {
			    stmt.setNull(1, java.sql.Types.DATE);
			}

			stmt.setInt(2, shift.getStartTime()); 
			stmt.setInt(3, shift.getEndTime());
			stmt.setInt(4, shift.getGuardAmount()); 
			stmt.setString(5, shift.getShiftLocation()); 
			stmt.setString(6, shift.getType()); 
			stmt.setBoolean(7, shift.isAvailable()); 
			int cid = shift.getContract(); 
			if (cid <= 0) {
				throw new IllegalArgumentException("Shift must be linked to a contract");
			}
		    stmt.setInt(8, cid);
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
	    } 
	    	
	    
	    return newId;
	}

	@Override
	public boolean setShiftType(Shift shift) {
		boolean result = false;
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(SET_SHIFT_TYPE_Q)) {

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
				PreparedStatement stmt = con.prepareStatement(BOOK_SHIFT_Q)) {

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
	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(COUNT_EMPLOYEES_FOR_SHIFT_Q)) {
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