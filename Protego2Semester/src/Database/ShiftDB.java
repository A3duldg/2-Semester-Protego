package Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import Database.DBConnection;
import interfaceDB.ShiftDBIF;
import Model.Shift;

public class ShiftDB implements ShiftDBIF {


public ShiftDB() {
}
@Override
public boolean setShiftType(Shift shift) {
	int rowsAffected;
	String template = "UPDATE shifts SET shiftType = ?, WHERE shiftId = ?;";
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
