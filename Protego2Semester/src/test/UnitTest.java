package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import controller.ContractController;
import controller.ShiftController;
import database.DBConnection;
import database.DataAccessException;
import model.Shift;

class UnitTest {

    
    private final ArrayList<Integer> createdEmployeeShiftIds = new ArrayList<>();
    private final ArrayList<Integer> createdShiftIds = new ArrayList<>();
    private final ArrayList<Integer> createdContractIds = new ArrayList<>();
    private final ArrayList<Integer> createdEmployeeIds = new ArrayList<>();
    private final ArrayList<Integer> createdPersonIds = new ArrayList<>();

 
    private Connection getConn() throws DataAccessException {
        return DBConnection.getInstance().getConnection();
    }

    private int insertPerson(String fn, String ln) throws Exception {
        String sql = "INSERT INTO Person (firstName, lastName, phone, email, personType, addressId) " +
                     "VALUES (?, ?, NULL, NULL, 'employee', NULL)";
        Connection con = null;
        try {
            con = getConn();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, fn);
                ps.setString(2, ln);
                int rows = ps.executeUpdate();
                assertTrue(rows > 0);

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    int id = rs.getInt(1);
                    createdPersonIds.add(id);
                    return id;
                }
            }
        } finally {
            if (con != null) DBConnection.getInstance().releaseConnection(con);
        }
    }

    private int insertEmployee(int personId) throws Exception {
        String sql = "INSERT INTO Employee (employeeId) VALUES (?)";
        Connection con = null;
        try {
            con = getConn();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, personId);
                int rows = ps.executeUpdate();
                assertTrue(rows > 0);
                createdEmployeeIds.add(personId);
                return personId;
            }
        } finally {
            if (con != null) DBConnection.getInstance().releaseConnection(con);
        }
    }

    private int insertContract(int guardAmount) throws Exception {
        
        String sql = "INSERT INTO Contract (startDate, endDate, guardAmount, estimatedPrice, active, customerId) " +
                     "VALUES (?, ?, ?, 0, 1, NULL)";
        Connection con = null;
        try {
            con = getConn();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, Date.valueOf(LocalDate.now().minusDays(1)));
                ps.setDate(2, Date.valueOf(LocalDate.now().plusDays(30)));
                ps.setInt(3, guardAmount);

                int rows = ps.executeUpdate();
                assertTrue(rows > 0);

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    int id = rs.getInt(1);
                    createdContractIds.add(id);
                    return id;
                }
            }
        } finally {
            if (con != null) DBConnection.getInstance().releaseConnection(con);
        }
    }

    private int insertShift(int contractId) throws Exception {
        String sql = "INSERT INTO Shift (shiftDate, startTime, endTime, guardAmount, availability, shiftLocation, type, contractId, managerId, certifiedId) " +
                     "VALUES (?, ?, ?, ?, 1, 'UT-Loc', 'Brandvagt', ?, NULL, NULL)";
        Connection con = null;
        try {
            con = getConn();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, Date.valueOf(LocalDate.now().plusDays(1)));
                ps.setInt(2, 800);
                ps.setInt(3, 1600);
                ps.setInt(4, 1);
                ps.setInt(5, contractId);

                int rows = ps.executeUpdate();
                assertTrue(rows > 0);

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    int id = rs.getInt(1);
                    createdShiftIds.add(id);
                    return id;
                }
            }
        } finally {
            if (con != null) DBConnection.getInstance().releaseConnection(con);
        }
    }

    private int insertEmployeeShift(int empId, int shiftId) throws Exception {
        String sql = "INSERT INTO EmployeeShift (employeeId, shiftId) VALUES (?, ?)";
        Connection con = null;
        try {
            con = getConn();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, empId);
                ps.setInt(2, shiftId);

                int rows = ps.executeUpdate();
                assertTrue(rows > 0);

                // EmployeeShift har Id IDENTITY, så vi kan tracke den
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    int id = rs.getInt(1);
                    createdEmployeeShiftIds.add(id);
                    return id;
                }
            }
        } finally {
            if (con != null) DBConnection.getInstance().releaseConnection(con);
        }
    }

    @AfterEach
    void cleanupDbRows() throws Exception {
        Connection con = null;
        try {
            con = getConn();
            con.setAutoCommit(false);

            // Delete i korrekt rækkefølge pga FK
            try (PreparedStatement ps1 = con.prepareStatement("DELETE FROM EmployeeShift WHERE Id = ?")) {
                for (int id : createdEmployeeShiftIds) {
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                }
            }

            try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM Shift WHERE shiftId = ?")) {
                for (int id : createdShiftIds) {
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                }
            }

            try (PreparedStatement ps3 = con.prepareStatement("DELETE FROM Contract WHERE contractId = ?")) {
                for (int id : createdContractIds) {
                    ps3.setInt(1, id);
                    ps3.executeUpdate();
                }
            }

            try (PreparedStatement ps4 = con.prepareStatement("DELETE FROM Employee WHERE employeeId = ?")) {
                for (int id : createdEmployeeIds) {
                    ps4.setInt(1, id);
                    ps4.executeUpdate();
                }
            }

            try (PreparedStatement ps5 = con.prepareStatement("DELETE FROM Person WHERE personId = ?")) {
                for (int id : createdPersonIds) {
                    ps5.setInt(1, id);
                    ps5.executeUpdate();
                }
            }

            con.commit();
        } catch (Exception e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            createdEmployeeShiftIds.clear();
            createdShiftIds.clear();
            createdContractIds.clear();
            createdEmployeeIds.clear();
            createdPersonIds.clear();

            if (con != null) {
                con.setAutoCommit(true);
                DBConnection.getInstance().releaseConnection(con);
            }
        }
    }

    // =======================================================
    // UT1–UT5: ContractController.isFullyStaffed(int contractId)
    // =======================================================

    @Test
    void UT1_notFullyStaffed_returnsFalse() throws Exception {
        // guardAmount=3, bookings=1 -> false
        int contractId = insertContract(3);
        int shiftId = insertShift(contractId);

        int p1 = insertPerson("UT", "E1");
        int e1 = insertEmployee(p1);
        insertEmployeeShift(e1, shiftId); // bookings=1

        ContractController cc = new ContractController();
        assertFalse(cc.isFullyStaffed(contractId));
    }

    @Test
    void UT2_fullyStaffed_returnsTrue() throws Exception {
        // guardAmount=3, bookings=3 -> true
        int contractId = insertContract(3);
        int shiftId = insertShift(contractId);

        int e1 = insertEmployee(insertPerson("UT", "E1"));
        int e2 = insertEmployee(insertPerson("UT", "E2"));
        int e3 = insertEmployee(insertPerson("UT", "E3"));

        insertEmployeeShift(e1, shiftId);
        insertEmployeeShift(e2, shiftId);
        insertEmployeeShift(e3, shiftId);

        ContractController cc = new ContractController();
        assertTrue(cc.isFullyStaffed(contractId));
    }

    @Test
    void UT3_overStaffed_returnsTrue() throws Exception {
        // guardAmount=3, bookings=4 -> true
        int contractId = insertContract(3);
        int shiftId = insertShift(contractId);

        int e1 = insertEmployee(insertPerson("UT", "E1"));
        int e2 = insertEmployee(insertPerson("UT", "E2"));
        int e3 = insertEmployee(insertPerson("UT", "E3"));
        int e4 = insertEmployee(insertPerson("UT", "E4"));

        insertEmployeeShift(e1, shiftId);
        insertEmployeeShift(e2, shiftId);
        insertEmployeeShift(e3, shiftId);
        insertEmployeeShift(e4, shiftId);

        ContractController cc = new ContractController();
        assertTrue(cc.isFullyStaffed(contractId));
    }

    @Test
    void UT4_noBookings_returnsFalse() throws Exception {
        // guardAmount=3, bookings=0 -> false
        int contractId = insertContract(3);
        insertShift(contractId); // ingen bookings

        ContractController cc = new ContractController();
        assertFalse(cc.isFullyStaffed(contractId));
    }

    @Test
    void UT5_invalidContract_throwsException() throws Exception {
        ContractController cc = new ContractController();
        assertThrows(Exception.class, () -> cc.isFullyStaffed(-3));
    }

    // =======================================================
    // UT6–UT10: ShiftController.findAvailableShifts(List<Shift>)
    // =======================================================

    @Test
    void UT6_allAvailable_returnsAll() {
        ShiftController sc = new ShiftController();

        ArrayList<Shift> input = new ArrayList<>();
        input.add(new Shift(800, 1600, 1, "A", true, 1));
        input.add(new Shift(900, 1700, 1, "B", true, 2));
        input.add(new Shift(1000, 1800, 1, "C", true, 3));
        input.add(new Shift(1100, 1900, 1, "D", true, 4));

        ArrayList<Shift> out = sc.findAvailableShifts(input);
        assertEquals(4, out.size());
    }

    @Test
    void UT7_someAvailable_returnsOnlyAvailable() {
        ShiftController sc = new ShiftController();

        ArrayList<Shift> input = new ArrayList<>();
        input.add(new Shift(800, 1600, 1, "A", true, 1));
        input.add(new Shift(900, 1700, 1, "B", false, 2));
        input.add(new Shift(1000, 1800, 1, "C", true, 3));
        input.add(new Shift(1100, 1900, 1, "D", false, 4));
        input.add(new Shift(1200, 2000, 1, "E", true, 5));
        input.add(new Shift(1300, 2100, 1, "F", false, 6));

        ArrayList<Shift> out = sc.findAvailableShifts(input);
        assertEquals(3, out.size());
        assertTrue(out.stream().allMatch(Shift::isAvailable));
    }

    @Test
    void UT8_noneAvailable_returnsEmpty() {
        ShiftController sc = new ShiftController();

        ArrayList<Shift> input = new ArrayList<>();
        input.add(new Shift(800, 1600, 1, "A", false, 1));
        input.add(new Shift(900, 1700, 1, "B", false, 2));
        input.add(new Shift(1000, 1800, 1, "C", false, 3));
        input.add(new Shift(1100, 1900, 1, "D", false, 4));
        input.add(new Shift(1200, 2000, 1, "E", false, 5));

        ArrayList<Shift> out = sc.findAvailableShifts(input);
        assertEquals(0, out.size());
    }

    @Test
    void UT9_emptyInput_returnsEmpty() {
        ShiftController sc = new ShiftController();

        ArrayList<Shift> input = new ArrayList<>();
        ArrayList<Shift> out = sc.findAvailableShifts(input);

        assertNotNull(out);
        assertEquals(0, out.size());
    }

    @Test
    void UT10_nullInput_throwsOrEmpty() {
        ShiftController sc = new ShiftController();

        //allShifts.forEach(...) -> giver NullPointerException på null
        assertThrows(NullPointerException.class, () -> sc.findAvailableShifts(null));
    }
 // UT11–UT14: ShiftController.calculateTotalHours(List<Shift> shifts)

    @Test
    void UT11_oneShift_8Hours_returns8() throws Exception {
        ShiftController sc = new ShiftController();

        ArrayList<Shift> shifts = new ArrayList<>();
        shifts.add(new Shift(800, 1600, 1, "Aarhus", true, -1)); // 08:00 -> 16:00 = 8 timer

        int total = sc.calculateTotalHours(shifts);

        assertEquals(8, total);
    }

    @Test
    void UT12_moreShifts_returns12() throws Exception {
        ShiftController sc = new ShiftController();

        ArrayList<Shift> shifts = new ArrayList<>();
        shifts.add(new Shift(800, 1600, 1, "Aarhus", true, -1));   // 8 timer
        shifts.add(new Shift(1600, 2000, 1, "Aarhus", true, -1));  // 4 timer

        int total = sc.calculateTotalHours(shifts);

        assertEquals(12, total);
    }

    @Test
    void UT13_emptyList_returns0() throws Exception {
        ShiftController sc = new ShiftController();

        ArrayList<Shift> shifts = new ArrayList<>();

        int total = sc.calculateTotalHours(shifts);

        assertEquals(0, total);
    }

    @Test
    void UT14_invalidTimes_throwsException() throws Exception {
        ShiftController sc = new ShiftController();

        ArrayList<Shift> shifts = new ArrayList<>();
        shifts.add(new Shift(1200, 1100, 1, "Test", true, -1)); // end før start = invalid

        assertThrows(IllegalArgumentException.class, () -> sc.calculateTotalHours(shifts));
    }

}

