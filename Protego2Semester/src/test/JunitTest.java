package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.ShiftController;
import model.*;

class JunitTest {

    private Shift shift;
    private ShiftController controller;

    @BeforeEach
    void setUp() throws Exception {
        // Hvis du vil kan du initialisere her
        shift = null;
    }

    @AfterEach
    void tearDown() throws Exception {
        // Rydder op efter testen
        shift = null; 
        // Hvis Shift har andre ressourcer som filer eller database, luk/ryd op her
        System.out.println("Shift objekt ryddet op");
    }

    @Test
    void TC1() {
        int startTime = 8;
        int endTime = 16;
        int guardAmount = 2;
        String location = "Tivoli";

        shift = new Shift(startTime, endTime, guardAmount, location, true, 1);
        boolean typeSet = shift.setShiftType("Dørmand");
        shift.setContractId(1);

        assertEquals(startTime, shift.getStartTime());
        assertEquals(endTime, shift.getEndTime());
        assertEquals(guardAmount, shift.getGuardAmount());
        assertEquals(location, shift.getShiftLocation());
        assertEquals("Dørmand", shift.getType());
        assertTrue(typeSet, "Type should be set successfully");
        assertEquals(1, shift.getContract());
        assertTrue(shift.isAvailable());
    }

    @Test
    void TC2() {
        int startTime = 8;
        int endTime = 16;
        int guardAmount = 2;
        String location = "Tivoli";
        String type = "Dørmand";

        shift = new Shift(startTime, endTime, guardAmount, location, true, -1);
        shift.setContractId(-1);

        assertNotEquals(1, shift.getContract(), "Contract ID 1 should not be set for invalid input");

    }

    @Test
    void TC3() {
 
    	int startTime = 19;
    	int endTime = 16;
    	int guardAmount = 2;
    	String location = "Tivoli";
    	String type = "Dørmand";
    
    	shift = new Shift(startTime, endTime, guardAmount, location, true, 1);
    	
        boolean isValid = shift.getEndTime() > shift.getStartTime();

        assertFalse(isValid, "Shift with endTime > startTime should be valid");
    }
       



}