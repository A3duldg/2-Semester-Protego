package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import interfaceDB.ShiftDBIF;
import model.Shift;

public class ShiftDB implements ShiftDBIF {

	private Connection con;
	private final DBConnection db;



	private static final String FIND_SHIFT_BY_AVAILABILITY_Q = "SELECT shiftId, startTime, endTime, guardAmount, shiftLocation, type, availability FROM Shift WHERE availability = ?";

	private static final String CREATE_SHIFT_Q = "INSERT INTO Shift (startTime, endTime, guardAmount, shiftLocation, type, availability) VALUES (?, ?, ?, ?, ?, ?)";

	private static final String SET_SHIFT_TYPE_Q = "UPDATE Shift SET type = ? WHERE shiftId = ?";

	private static final String BOOK_SHIFT_Q = "UPDATE Shift SET availability = 0 WHERE shiftId = ?";

	public ShiftDB() throws DataAccessException {

		db = DBConnection.getInstance();

	}


	@Override
	public List<Shift> findShiftByAvailability(boolean availability) throws DataAccessException {
		List<Shift> list = new ArrayList<>();

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(
						"SELECT shiftId, startTime, endTime, guardAmount, shiftLocation, type, availability FROM Shift WHERE availability = ?")) {

			stmt.setBoolean(1, availability);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Shift shift = new Shift(rs.getInt("startTime"), rs.getInt("endTime"), rs.getInt("guardAmount"),
							rs.getString("shiftLocation"), rs.getBoolean("availability"), rs.getInt("shiftId"));
					shift.setShiftType(rs.getString("type"));
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
				PreparedStatement stmt = con.prepareStatement(
						"INSERT INTO Shift (startTime, endTime, guardAmount, shiftLocation, type, availability) VALUES (?, ?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, shift.getStartTime());
			stmt.setInt(2, shift.getEndTime());
			stmt.setInt(3, shift.getGuardAmount());
			stmt.setString(4, shift.getShiftLocation());
			stmt.setString(5, shift.getType());
			stmt.setBoolean(6, shift.isAvailable());

			int rows = stmt.executeUpdate();

			if (rows > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						newId = rs.getInt(1);
					}
				}
			}

		} catch (SQLException | DataAccessException e) {
			e.printStackTrace();
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

}