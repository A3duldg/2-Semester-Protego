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

	// Tråd sikker liste til vagter der er under behandling
	private final ArrayList<Shift> processingShifts;
	private final Object shiftLock = new Object();

	// Tråd sikker tæller
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


	 // observer har ændre småt nogle ting, for at få den til at notificere observer.
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

		// Implementering Lambda for hver vagt, tjek om den er ledig
		allShifts.forEach(shift -> {
			if (shift.isAvailable()) {
				available.add(shift);
			}
		});

		return available;
	}

	public ArrayList<Shift> sortShiftsByStartTime(ArrayList<Shift> shifts) {
		ArrayList<Shift> sorted = new ArrayList<>(shifts);

		// Implementing af en funktion som skal sortere vagter udfra starttider
		sorted.sort((shift1, shift2) -> Integer.compare(shift1.getStartTime(), shift2.getStartTime()));

		return sorted;
	}

	public int calculateTotalHours(ArrayList<Shift> shifts) {
		int totalHours = 0;

		// Implementering af en Lambda der beregner timerne for hver vagt
		for (Shift shift : shifts) {
			totalHours += (shift.getEndTime() - shift.getStartTime());
		}

		return totalHours;
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

	// Producer consumer der opretter flere vagter og ligger dem ud
	public void bulkShiftCreation(ArrayList<Shift> shifts) {
		System.out.println("Starter oprettelse af " + shifts.size() + "vagter");

		// Producer som tilføjer flere vagter til en liste
		Thread producer = new Thread(() -> {
			for (Shift shift : shifts) {
				addToProcessing(shift);

				try {
					Thread.sleep(100);// Sættes til 100 millisekunder de den skal arbejde hurtigere end
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Oprettelse færdig.");
		});

		// Consumer tager vagterne og opretter dem
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
					} // Skulle være en transaction
					processed++;
				}
				try {
					Thread.sleep(200); // Skal ligne database forsinkelse
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Oprettelse færdig");
		});
		producer.start();
		consumer.start();

		try {
			producer.join();
			consumer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Oprettelse og udgivelse færdig. Totale vagter: " + getTotalShiftsCreated());
	}
	 public int countEmployeesForShift(int shiftId) throws DataAccessException {
	        return shiftDB.countEmployeesForShift(shiftId);
	    }
	 
}
