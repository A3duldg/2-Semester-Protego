package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.Shift;
import interfaceDB.ShiftDBIF;


public class ShiftDB implements ShiftDBIF {

	public ShiftDB() {
	}



	@Override
	public int createShift(Shift shift) {
		int rowsAffected, newId = -1;
		String template = "INSERT INTO Shifts (startTime, endTime, guardAmount, shiftLocation, shiftType, availability) VALUES (?, ?, ?, ?, ?, ?);";

		try (PreparedStatement sql = DBConnection.getInstance().getConnection().prepareStatement(template,
				Statement.RETURN_GENERATED_KEYS)) {

			sql.setInt(1, shift.getStartTime());
			sql.setInt(2, shift.getEndTime());
			sql.setInt(3, shift.getGuardAmount());
			sql.setString(4, shift.getShiftLocation());
			sql.setString(5, shift.getType());
			sql.setBoolean(6, shift.isAvailability());

			rowsAffected = sql.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = sql.getGeneratedKeys();
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
		int rowsAffected;
		String template = "UPDATE Shifts SET shiftType = ?, WHERE shiftId = ?;";
		try (PreparedStatement sql = DBConnection.getInstance().getConnection().prepareStatement(template)) {

			sql.setString(1, shift.getType());
			sql.setInt(2, shift.getShiftId());
			rowsAffected = sql.executeUpdate();
			if (rowsAffected > 0) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}