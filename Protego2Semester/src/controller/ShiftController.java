package controller;

import interfaceDB.ShiftDBIF;
import model.Contract;
import model.Manager;
import model.Shift;


import java.util.ArrayList;

import database.DataAccessException;
import database.ShiftDB;

public class ShiftController {
	private ManagerController managerCtr;
	private ContractController contractCtr;
	private ShiftDBIF shiftDB;

	// Thread safe list to track shifts under processing.
	private final ArrayList<Shift> processingShifts;
	private final Object shiftLock = new Object();

	// Thread safe counter
	private int totalShiftsCreated = 0;
	private final Object counterLock = new Object();

	public ShiftController() {
		try {
			shiftDB = new ShiftDB();
			managerCtr = new ManagerController();
			contractCtr = new ContractController();
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize controllers", e);
		}
		processingShifts = new ArrayList<>();
	}

	 public Contract findActiveContract() {
	        try {
	            return contractCtr.findActiveContract(0);
	        } catch (DataAccessException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }


	 public Manager findActiveManager() {
	        try {
	            return managerCtr.findActiveManager(0);
	        } catch (DataAccessException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }


	 public int createShift(Shift shift) throws DataAccessException{
		// Validate time interval
		 if (shift.getEndTime() <= shift.getStartTime()) {
		     throw new IllegalArgumentException("End time must be later than start time");
		 }

	        try {
	            return shiftDB.createShift(shift);
	        } catch (DataAccessException e) {
	            e.printStackTrace();
	            return -1; // fallback ID
	        }
	    }


	 public boolean setShiftType(Shift shift) {
	        try {
	            return shiftDB.setShiftType(shift);
	        } catch (DataAccessException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }


	// The observer has been slightly modified to ensure it notifies the observer.
	 public boolean bookShift(Shift shift) {
	        try {
	            boolean result = shiftDB.bookShift(shift);
	            if (result) {
	            	shift.bookShift();
	            }
	            return result;
	        } catch (DataAccessException e) {
	            e.printStackTrace();
	           return false;
	        }
	    }


	public ArrayList<Shift> findShiftByAvailability(boolean availability) throws DataAccessException {
	        return shiftDB.findShiftByAvailability(availability);
	   
	 
	}


	public ArrayList<Shift> findAvailableShifts(ArrayList<Shift> allShifts) {
		ArrayList<Shift> available = new ArrayList<>();

		// Lambda implementation to check availability for each shift.
		allShifts.forEach(shift -> {
			if (shift.isAvailable()) {
				available.add(shift);
			}
		});

		return available;
	}

	public ArrayList<Shift> sortShiftsByStartTime(ArrayList<Shift> shifts) {
		ArrayList<Shift> sorted = new ArrayList<>(shifts);

		// Function implementation to sort shifts based on start times.
		sorted.sort((shift1, shift2) -> Integer.compare(shift1.getStartTime(), shift2.getStartTime()));

		return sorted;
	}

	public int calculateTotalHours(ArrayList<Shift> shifts) {
	    if (shifts == null || shifts.isEmpty()) {
	        return 0;
	    }

	    int totalMinutes = 0;

	    for (Shift shift : shifts) {
	        int start = shift.getStartTime();
	        int end = shift.getEndTime();

	        if (!isValidHHmm(start) || !isValidHHmm(end)) {
	            throw new IllegalArgumentException("Invalid time format (HHmm).");
	        }

	        int startMin = hhmmToMinutes(start);
	        int endMin = hhmmToMinutes(end);

	        if (endMin <= startMin) {
	            throw new IllegalArgumentException("End time must be after start time.");
	        }

	        totalMinutes += (endMin - startMin);
	    }

	    return totalMinutes / 60;
	}

	

	public void addToProcessing(Shift shift) {
		synchronized (shiftLock) {
			processingShifts.add(shift);
			System.out.println("Vagt tilføjet til processering. Total: " + processingShifts.size() + " (Thread: "
					+ Thread.currentThread().getName() + ")");
		}
	}

	public Shift removeFromProcessing() {
		synchronized (shiftLock) {
			if (!processingShifts.isEmpty()) {
				Shift shift = processingShifts.remove(0);
				System.out.println("Vagt fjernet fra processering. Tilbage: " + processingShifts.size());
				return shift;
			}
			return null;
		}
	}

	public int getTotalShiftsCreated() {
		synchronized (counterLock) {
			return totalShiftsCreated;
		}
	}

	// Producer consumer that creates multiple shifts and distributes them.
	public void bulkShiftCreation(ArrayList<Shift> shifts) {
		System.out.println("Starter oprettelse af " + shifts.size() + "vagter");

		// Producer adds additional shifts to the list.
		Thread producer = new Thread(() -> {
			for (Shift shift : shifts) {
				addToProcessing(shift);

				try {
					Thread.sleep(100);// Set to 100 milliseconds because it should work faster
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Oprettelse færdig.");
		});

		// The consumer takes the shifts and creates them.
		Thread consumer = new Thread(() -> {
			int processed = 0;
			while (processed < shifts.size()) {
				Shift shift = removeFromProcessing();

				if (shift != null) {
					int id;
					try {
						id = createShift(shift);
						System.out.println("Shift created" + id);
					} catch (DataAccessException e) {
						System.err.println("Error in creation of shift: " + e.getMessage());
					} // Should be a transaction.
					processed++;
				}
				try {
					Thread.sleep(200); // Should simulate a database delay.
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Creation completed");
		});
		producer.start();
		consumer.start();

		try {
			producer.join();
			consumer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Creation and release finished. total shifts: " + getTotalShiftsCreated());
	}
	 public int countEmployeesForShift(int shiftId) throws DataAccessException {
	        return shiftDB.countEmployeesForShift(shiftId);
	    }
	 private boolean isValidHHmm(int t) {
		    int hh = t / 100;
		    int mm = t % 100;
		    return (hh >= 0 && hh <= 23) && (mm >= 0 && mm <= 59);
		}

		private int hhmmToMinutes(int t) {
		    int hh = t / 100;
		    int mm = t % 100;
		    return hh * 60 + mm;
		}

	 
}
