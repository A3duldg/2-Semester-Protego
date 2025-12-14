package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.ShiftController;
import database.ContractDB;
import database.DataAccessException;
import model.*;

class JunitTest {

	private Shift shift;
	private ShiftController controller;
	private ContractDB contractDB;

	@BeforeEach
	void setUp() throws Exception {
		controller = new ShiftController();
		contractDB = new ContractDB();
		shift = null;
	}

	@AfterEach
	void tearDown() throws Exception {
		shift = null;
		System.out.println("Shift object cleaned up");
	}

	// TC1-TC3 Første tabel
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

	// TC1-TC4 i anden tabel
	@Test
	void TC4() throws DataAccessException {

		shift = new Shift(8, 16, 2, "Tivoli", true, 1);
		shift.setShiftDate(LocalDate.of(2025, 12, 14));
		controller.createShift(shift);

		boolean result = controller.bookShift(shift);

		assertTrue(result);
		assertFalse(shift.isAvailable());
	}

	@Test
	void TC5() throws DataAccessException {
		shift = new Shift(8, 16, 2, "tivoli", false, 1);
		shift.setShiftDate(LocalDate.of(2025, 12, 14));
		controller.createShift(shift);

		boolean result = controller.bookShift(shift);

		assertTrue(result, "Booking has to fail when a shift is full");
		assertFalse(shift.isAvailable(), "Shift has to be unavailable");
	}



	@Test
	void TC7() throws DataAccessException {
		shift = new Shift(8, 16, 2, "tivoli", true, -2);
		shift.setShiftDate(LocalDate.of(2025, 12, 14));

		boolean result = controller.bookShift(shift);

		assertFalse(result, "booking has to fail when shift doesnt exist in database");
	}
}