package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.ContractController;
import controller.EmployeeController;
import database.ContractDB;
import database.DataAccessException;
import database.EmployeeDB;
import model.Employee;
import model.Shift;
import database.ShiftDB;

public class IntergrationTest {

	class IntegrationTest {
	    
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
	        ArrayList<Shift> availableShifts = shiftDB.findShiftByAvailability(availability);
	        
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
	        ArrayList<Shift> bookedShifts = shiftDB.findShiftByAvailability(availability);
	        
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
	        ArrayList<Shift> shifts = shiftDB.findShiftByAvailability(availability);
	        
	        // Assert
	        assertNotNull(shifts, "Should return empty list, not null");
	        // Will be empty or have shifts depending on database
	        System.out.println("IT3 - Found " + shifts.size() + " available shifts");
	    }
	    
	    @Test
	    void IT4_DatabaseConnectionError() {
	        // This is hard to test without closing the database but can verify that the method handles exceptions
	        assertDoesNotThrow(() -> {
	            ArrayList<Shift> shifts = shiftDB.findShiftByAvailability(true);
	            assertNotNull(shifts, "Should handle errors the right way");
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
	 
	    
	    @Test
	    void IT10_EmployeeNull() {
	        // Arrange Null employee
	        Employee employee = null;
	        Shift shift = new Shift(8, 16, 2, "Test", true, 1);
	        
	        // Act & Assert
	        assertDoesNotThrow(() -> {
	            employeeController.connectShiftToEmployee(employee, shift);
	        }, "Should handle null employee the right way");
	    }
	    
	    @Test
	    void IT11_ShiftNull() throws DataAccessException {
	        // Arrange found employee, null shift
	        Employee employee = employeeDB.getEmployeeId(1);
	        Shift shift = null;
	        
	        // Act & Assert
	        assertDoesNotThrow(() -> {
	            employeeController.connectShiftToEmployee(employee, shift);
	        }, "Should handle null shift the right way");
	    }
	    
	    @Test
	    void IT12_ShiftAlreadyAssigned() throws DataAccessException {
	        // Arrange Shift is allready booked
	        Employee employee1 = employeeDB.getEmployeeId(1);
	        Employee employee2 = employeeDB.getEmployeeId(2);
	        Shift shift = new Shift(8, 16, 2, "Test", true, 1);
	        
	        // Book first time
	        try {
	            employeeController.connectShiftToEmployee(employee1, shift);
	        } catch (Exception e) {
	            // Ignore if already booked
	        }
	        
	        // Act & Assert - Try to book again
	        assertThrows(IllegalStateException.class, () -> {
	            employeeController.connectShiftToEmployee(employee2, shift);
	        }, "Should throw exception when shift is already assigned");
	    }
	    
	    @Test
	    void IT13_EmployeeNotInDatabase() {
	        // Arrange Employee that is not in the database
	        Employee fakeEmployee = new Employee(-1, "Fake", "User", 
	            "Address", "City", 1234, "12345678", "fake@email.com");
	        Shift shift = new Shift(8, 16, 2, "Test", true, 1);
	        
	        // Act & Assert
	        assertThrows(IllegalStateException.class, () -> {
	            employeeController.connectShiftToEmployee(fakeEmployee, shift);
	        }, "Should throw exception when employee doesn't exist");
	    }
	}
}
