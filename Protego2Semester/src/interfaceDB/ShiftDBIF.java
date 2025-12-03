package interfaceDB;


import java.util.List;

import database.DataAccessException;
import model.Shift;

public interface ShiftDBIF {


List<Shift> findShiftByAvailability(boolean Availability)throws DataAccessException;

int createShift(Shift shift)throws DataAccessException;

boolean setShiftType(Shift shift)throws DataAccessException;

boolean bookShift(Shift shift)throws DataAccessException;






}
