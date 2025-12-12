package interfaceDB;


import java.util.ArrayList;

import database.DataAccessException;
import model.Shift;

public interface ShiftDBIF {


ArrayList<Shift> findShiftByAvailability(boolean Availability)throws DataAccessException;

int createShift(Shift shift)throws DataAccessException;

boolean setShiftType(Shift shift)throws DataAccessException;

boolean bookShift(Shift shift)throws DataAccessException;

int countEmployeesForShift(int shiftId) throws DataAccessException;




}
