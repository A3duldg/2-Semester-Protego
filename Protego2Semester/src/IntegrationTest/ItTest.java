package IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.ContractController;
import controller.EmployeeController;
import database.ContractDB;
import database.DataAccessException;
import database.EmployeeDB;
import model.Shift;
import database.ShiftDB;

public class ItTest {

	class IntegrationTests {
	    
	    private ShiftDB shiftDB;
	    private ContractDB contractDB;
	    private EmployeeDB employeeDB;
	    private ContractController contractController;
	    private EmployeeController employeeController;
	    
	    @BeforeEach
	    void setUp() throws DataAccessException {
	        shiftDB = new ShiftDB();
	        contractDB = new ContractDB();
	        employeeDB = new EmployeeDB();
	        contractController = new ContractController();
	        employeeController = new EmployeeController(employeeDB);
	    }
	    
	    
	    @Test
	    void IT1_FindAvailableShifts() throws DataAccessException {
	        // Arrange database has available shifts
	        boolean availability = true;
	        
	        // Act
	        List<Shift> availableShifts = shiftDB.findShiftByAvailability(availability);
	        
	        // Assert
	        assertNotNull(availableShifts, "Should return a list, not null");
	        assertTrue(availableShifts.stream().allMatch(s -> s.isAvailable()), 
	            "All returned shifts should be available");
	        
	        System.out.println("IT1 - Found " + availableShifts.size() + " available shifts");
	    }
	    
	    @Test
	    void IT2_FindBookedShifts() throws DataAccessException {
	        // Arrange database has booked shifts
	        boolean availability = false;
	        
	        // Act
	        List<Shift> bookedShifts = shiftDB.findShiftByAvailability(availability);
	        
	        // Assert
	        assertNotNull(bookedShifts, "Should return a list, not null");
	        assertTrue(bookedShifts.stream().noneMatch(s -> s.isAvailable()), 
	            "All returned shifts should be booked");
	        
	        System.out.println("IT2 - Found " + bookedShifts.size() + " booked shifts");
	    }
	    
	    @Test
	    void IT3_NoAvailableShifts() throws DataAccessException {
	        // Arrange if all shifts is booked
	        boolean availability = true;
	        
	        // Act
	        List<Shift> shifts = shiftDB.findShiftByAvailability(availability);
	        
	        // Assert
	        assertNotNull(shifts, "Should return empty list, not null");
	        // Will be empty or have shifts depending on database
	        System.out.println("IT3 - Found " + shifts.size() + " available shifts");
	    }
	    
	    @Test
	    void IT4_DatabaseConnectionError() {
	        // This is hard to test without closing the database but can verify that the method handles exceptions
	        assertDoesNotThrow(() -> {
	            List<Shift> shifts = shiftDB.findShiftByAvailability(true);
	            assertNotNull(shifts, "Should handle errors");
	        });
	    }
	    
	    @Test
	    void IT5_ContractWithBookings() throws DataAccessException {
	        // Arrange Contract ID 1 has to have some bookings in DB
	        int contractId = 1;
	        
	        // Act
	        int bookedCount = contractDB.countBookedGuardsForContract(contractId);
	        
	        // Assert
	        assertTrue(bookedCount >= 0, "Should return posetive count");
	        System.out.println("IT5 - Contract 1 has " + bookedCount + " bookings");
	    }
	    
	    @Test
	    void IT6_ContractNoBookings() throws DataAccessException {
	        // Arrange Contract without bookings has to be created in db
	        int contractId = 999; // Random number shouldnt have bookings
	        
	        // Act
	        int bookedCount = contractDB.countBookedGuardsForContract(contractId);
	        
	        // Assert
	        assertEquals(0, bookedCount, "Contract without bookings should return 0");
	    }
	    
	    @Test
	    void IT7_InvalidContractId() throws DataAccessException {
	        // Arrange false Contract ID
	        int contractId = -1;
	        
	        // Act
	        int bookedCount = contractDB.countBookedGuardsForContract(contractId);
	        
	        // Assert
	        assertEquals(0, bookedCount, "Invalid contract should return 0");
	    }
	    
	    @Test
	    void IT8_NullContractId() {
	        // This is supposed to require that the method accepts an Integer thats nullable
	        // Here we test that 0 works
	        assertDoesNotThrow(() -> {
	            int count = contractDB.countBookedGuardsForContract(0);
	            assertEquals(0, count, "Contract 0 should return 0");
	        });
	    }
	}
}
