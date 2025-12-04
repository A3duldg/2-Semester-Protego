package controller;

import interfaceDB.ShiftDBIF;
import model.Contract;
import model.Manager;
import model.Shift;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DataAccessException;
import database.ShiftDB;

public class ShiftController {
	private ManagerController managerCtr;
	private ContractController contractCtr;
	private ShiftDBIF shiftDB;
	
	//Tråd sikker liste til vagter der er under behandling
	private final List<Shift> processingShifts;
	private final Object shiftLock = new Object();
	
	//Tråd sikker tæller
	private int totalShiftsCreated = 0;
	private final Object counterLock = new Object();
	

	public ShiftController() throws DataAccessException {
		shiftDB = new ShiftDB();
		managerCtr = new ManagerController();
		contractCtr = new ContractController();
		processingShifts = new ArrayList<>();
	}

	public Contract findActiveContract() throws DataAccessException {
		return contractCtr.findActiveContract(0);
	}

	public Manager findActiveManager() throws DataAccessException {
		return managerCtr.findActiveManager(0);
	}

	public int createShift(Shift shift) throws DataAccessException {

		return shiftDB.createShift(shift);

	}

	public boolean setShiftType(Shift shift) throws DataAccessException {
		return shiftDB.setShiftType(shift);
	}

	public Contract confirmContract() throws DataAccessException {
		return contractCtr.confirmContract();
	}

	boolean bookShift(Shift shift) throws DataAccessException {
		return shiftDB.bookShift(shift);
	}

	List<Shift> findShiftByAvailability(boolean Availability) throws DataAccessException {
		return shiftDB.findShiftByAvailability(Availability);
	}

	public List<Shift> findAvailableShifts(List<Shift> allShifts) {
		List<Shift> available = new ArrayList<>();

		// Implementering Lambda for hver vagt, tjek om den er ledig
		allShifts.forEach(shift -> {
			if (shift.isAvailable()) {
				available.add(shift);
			}
		});

		return available;
	}

	public List<Shift> sortShiftsByStartTime(List<Shift> shifts) {
		List<Shift> sorted = new ArrayList<>(shifts);

		// Implementing af en funktion som skal sortere vagter udfra starttider
		sorted.sort((shift1, shift2) -> Integer.compare(shift1.getStartTime(), shift2.getStartTime()));

		return sorted;
	}

	public int calculateTotalHours(List<Shift> shifts) {
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
			System.out.println("Vagt tilføjet til processering. Total: " + 
				processingShifts.size() + " (Thread: " + 
				Thread.currentThread().getName() + ")");
		}
	}
	
	public Shift removeFromProcessing() {
		synchronized (shiftLock) {
			if (!processingShifts.isEmpty()) {
				Shift shift = processingShifts.remove(0);
				System.out.println("Vagt fjernet fra processering. Tilbage: " + 
					processingShifts.size());
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
	public void bulkShiftCreation (List<Shift> shifts) {
		System.out.println("Starter oprettelse af " + shifts.size() + "vagter");
		
		//Producer som tilføjer flere vagter til en liste
		Thread producer = new Thread(() -> {
			for (Shift shift : shifts) {
				addToProcessing(shift);
				
				try {
					Thread.sleep(100);//Sættes til 100 millisekunder de den skal arbejde hurtigere end
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Oprettelse færdig.");
		});
		
		//Consumer tager vagterne og opretter dem
		Thread consumer = new Thread(() -> {
			int processed = 0;
			while (processed < shifts.size()) {
				Shift shift = removeFromProcessing();
			
				if (shift != null) {
					int id = createShift(shift); //Skulle være en transaction
					System.out.println("Vagt oprettet" + id);
					processed++;
				}
				try {
					Thread.sleep(200); //Skal ligne database forsinkelse
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
}
