package interfaceDB;


import java.util.List;

import model.Shift;

public interface ShiftDBIF {


List<Shift> findShiftByAvailability(boolean Availability);

int createShift(Shift shift);

boolean setShiftType(Shift shift);

boolean bookShift(Shift shift);






}
