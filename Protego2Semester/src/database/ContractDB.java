package database;

import java.sql.*;
import java.util.ArrayList;

import interfaceDB.ContractDBIF;
import model.Contract;

public class ContractDB implements ContractDBIF {
	//private final DBConnection db;

	private static final String FIND_CONTRACT_BY_ID_Q = "SELECT contractId, guardAmount, startDate, endDate, active FROM Contract WHERE contractId = ?";
	
	private static final String COUNT_BOOKED_GUARDS_FOR_CONTRACT_Q = "SELECT COUNT(*) FROM EmployeeShift es JOIN Shift s ON es.shiftId = s.shiftId WHERE s.contractId = ?";
	
	private static final String FIND_ACTIVE_CONTRACT_BY_EMPLOYEE_ID_Q = "SELECT contractId, employeeId, startDate, endDate FROM Contract WHERE employeeId = ? AND active = 1";
	
	private static final String FIND_ALL_CONTRACTS_Q = "SELECT contractId, guardAmount, StartDate, EndDate FROM Contract";
	
	private static final String FIND_ALL_ACTIVE_CONTRACTS_Q = "SELECT contractId, guardAmount, startDate, endDate FROM Contract WHERE active = 1 AND (endDate IS NULL OR endDate >= CAST(GETDATE() AS date))";
	
	public ContractDB() throws DataAccessException {

		//db = DBConnection.getInstance();
	}

	public Contract findContractById(int contractId) throws DataAccessException {
	    Contract contract = null;

	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(FIND_CONTRACT_BY_ID_Q)) {

	        stmt.setInt(1, contractId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                contract = new Contract(rs.getInt("contractId"));
	                contract.setGuardAmount(rs.getInt("guardAmount"));

	                Date sd = rs.getDate("startDate");
	                if (sd != null) contract.setStartDate(sd.toLocalDate());

	                Date ed = rs.getDate("endDate");
	                if (ed != null) contract.setEndDate(ed.toLocalDate());

	                contract.setActive(rs.getBoolean("active"));
	            }
	        }
	    } catch (SQLException e) {
	        // Print fuld JDBC-fejl til konsol for at finde root-cause, så vi kan fixe den præcist.
		    System.err.println("ContractDB.findContractById: SQLException: " + e.getMessage());
		    e.printStackTrace(System.err);
	        throw new DataAccessException("Error finding contract", e);
	    }
	    return contract;
	
	}


	public int countBookedGuardsForContract(int contractId) throws DataAccessException {
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(COUNT_BOOKED_GUARDS_FOR_CONTRACT_Q)) {
			stmt.setInt(1, contractId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error counting booked guards for contract", e);
		}
		return 0;
	}

	// Added this method
	@Override
	public Contract findActiveContract(int employeeId) throws DataAccessException {
		Contract contract = null;

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(FIND_ACTIVE_CONTRACT_BY_EMPLOYEE_ID_Q)) {

			stmt.setInt(1, employeeId);

			 try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    Integer cid = rs.getObject("contractId", Integer.class);
	                    if (cid == null) return null;
	                    contract = new Contract(cid);

	                    Integer empId = rs.getObject("employeeId", Integer.class);
	                    if (empId != null) contract.setEmployeeId(empId);

	                    Date sd = rs.getDate("startDate");
	                    if (sd != null) contract.setStartDate(sd.toLocalDate());

	                    Date ed = rs.getDate("endDate");
	                    if (ed != null) contract.setEndDate(ed.toLocalDate());
	                }
	            }
	        } catch (SQLException e) {
	            throw new DataAccessException("Error finding active contract", e);
	        }

	        return contract;
	    }
	
	public ArrayList<Contract> findAllContracts() throws DataAccessException {
	    ArrayList<Contract> list = new ArrayList<>();

	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(FIND_ALL_CONTRACTS_Q);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            Integer cid = rs.getObject("contractId", Integer.class);
	            if (cid == null) continue;

	            Contract c = new Contract(cid);

	            Integer guardAmt = null;
	            try { guardAmt = rs.getObject("guardAmount", Integer.class); } catch (SQLException ignore) {}
	            if (guardAmt != null) c.setGuardAmount(guardAmt);

	            java.sql.Date sd = null;
	            try { sd = rs.getDate("StartDate"); } catch (SQLException ignore) {}
	            if (sd != null) c.setStartDate(sd.toLocalDate());

	            java.sql.Date ed = null;
	            try { ed = rs.getDate("EndDate"); } catch (SQLException ignore) {}
	            if (ed != null) c.setEndDate(ed.toLocalDate());

	            list.add(c);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error reading contracts", e);
	    }
	    return list;
	}
	//nyt
	public ArrayList<Contract> findAllActiveContracts() throws DataAccessException {
	    ArrayList<Contract> list = new ArrayList<>();

	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(FIND_ALL_ACTIVE_CONTRACTS_Q);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            Integer cid = rs.getObject("contractId", Integer.class);
	            if (cid == null) continue;

	            Contract c = new Contract(cid);

	            Integer guardAmt = rs.getObject("guardAmount", Integer.class);
	            if (guardAmt != null) c.setGuardAmount(guardAmt);

	            Date sd = rs.getDate("StartDate");
	            if (sd != null) c.setStartDate(sd.toLocalDate());

	            Date ed = rs.getDate("EndDate");
	            if (ed != null) c.setEndDate(ed.toLocalDate());

	            list.add(c);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error reading active contracts", e);
	    }

	    return list;
	}
	}
