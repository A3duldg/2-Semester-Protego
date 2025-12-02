package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import interfaceDB.ShiftDBIF;
import model.Shift;

public class ShiftDB implements ShiftDBIF {

	private Connection con;

	// PreparedStatements
	private PreparedStatement findShiftByAvailabilityStmt;
	private PreparedStatement createShiftStms;
	private PreparedStatement setShiftTypeStms;
	private PreparedStatement bookShiftStms;
	// private PreparedStatement confirmShiftStms;

	private static final String FIND_SHIFT_BY_AVAILABILITY_Q = "SELECT shiftId, startTime, endTime, guardAmount, shiftLocation, shiftType, availability"
			+ "FROM Shift" + "WHERE availability = 1";

	private static final String CREATE_SHIFT_Q = "INSERT INTO Shift (startTime, endTime, guardAmount, shiftLocation, shiftType, availability) VALUES (?, ?, ?, ?, ?, ?)";

	private static final String SET_SHIFT_TYPE_Q = "UPDATE Shift" + "SET shiftType = ?" + "WHERE shiftId = ?";

	private static final String BOOK_SHIFT_Q = "UPDATE Shift" + "SET availability = 0" + "WHERE shiftId = ?";
	// jeg ved ikke helt hvordan jeg skal fikse det nu har bare skrevet den ind for nu
	//private static final String CONFIRM_SHIFT_Q = "UPDATE Shift" + "WHERE shiftId = ? AND availability = 0";
	public ShiftDB() {
		try {
			this.con = DBConnection.getInstance().getConnection();
			
			findShiftByAvailabilityStmt = con.prepareStatement(FIND_SHIFT_BY_AVAILABILITY_Q);
			createShiftStms= con.prepareStatement(CREATE_SHIFT_Q);
			setShiftTypeStms = con.prepareStatement(SET_SHIFT_TYPE_Q);
			bookShiftStms = con.prepareStatement(BOOK_SHIFT_Q);
	} 		catch (SQLException e) {
				e.printStackTrace();
	}

}

	@Override
	public List<Shift> findShiftByAvailability(boolean Availability) {
		List<Shift> list = new ArrayList<>();
		
		try {
			findShiftByAvailabilityStmt.setBoolean(1, Availability);
			ResultSet rs = findShiftByAvailabilityStmt.executeQuery();
		
		
			while (rs.next()) {
				Shift shift = new Shift(
						rs.getInt("startTime"),
						rs.getInt("endTime"),
						rs.getInt("guardAmount"),
						rs.getString("shiftLocation"),
						rs.getBoolean("availability"),
						rs.getInt("shiftId")
						);
				list.add(shift);
			}		
		
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	@Override
	public int createShift(Shift shift) {
		int newId = -1;
		
		String sql = "INSERT INTO Shift (startTime, endTime, guardAmount, shiftLocation, shiftType, availability)" + 
		"VALUES (?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, shift.getStartTime());
			stmt.setInt(2, shift.getEndTime());
			stmt.setInt(3, shift.getGuardAmount());
			stmt.setString(4, shift.getShiftLocation());
			stmt.setString(5, shift.getType());
			stmt.setBoolean(6, shift.isAvailable());
			
			int rows = stmt.executeUpdate();
		
			if (rows > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					newId = rs.getInt(1);
				}
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newId;
	}
		
	@Override
	public boolean setShiftType(Shift shift) {
		boolean result = false;
		
		String sql = "UPDATE Shift" + "SET shiftType = ?" + "WHERE shiftId = ?";
		
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, shift.getType());
			stmt.setInt(2, shift.getShiftId());
		
		
			int rows = stmt.executeUpdate();
			result = rows > 0;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	return result;
	}

	
	@Override
	public boolean bookShift(Shift shift) {
		boolean result = false;
		
		String sql = "UPDATE Shift" + "SET availability = 0" + "WHERE shiftId = ?";
		
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, shift.getShiftId());
			
			int rows = stmt.executeUpdate();
			result = rows > 0;
		
			if (result) {
				shift.setAvailability(false);
			}
		} catch (SQLException e) {
			
		}
		return result;
	}
		

	
}