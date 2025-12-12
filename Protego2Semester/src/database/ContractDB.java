package database;

import java.sql.*;
import java.util.ArrayList;

import interfaceDB.ContractDBIF;
import model.Contract;

public class ContractDB implements ContractDBIF {
	private final DBConnection db;

	public ContractDB() throws DataAccessException {

		db = DBConnection.getInstance();
	}

	public Contract findContractById(int contractId) throws DataAccessException {
		Contract contract = null;
		String sql =
			    "SELECT contractId, guardAmount, startDate, endDate FROM Contract, WHERE active = 1, AND (StartDate IS NULL OR StartDate <= CAST(GETDATE() AS date)), AND (EndDate IS NULL OR EndDate >= CAST(GETDATE() AS date))";

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, contractId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Integer cid = rs.getObject("contractId", Integer.class);
					if (cid == null) {
						// Usædvanligt: fundet en række uden contractId
						System.err.println("findContractById: contractId var NULL for søgning på id=" + contractId);
						return null;
					}
					contract = new Contract(cid);
					
					Integer guardAmt = rs.getObject("guardAmount", Integer.class);
	                if (guardAmt != null) {
	                    contract.setGuardAmount(guardAmt);
	                }

					Date sd = rs.getDate("startDate");
					if (sd != null)
						contract.setStartDate(sd.toLocalDate());

					Date ed = rs.getDate("endDate");
					if (ed != null)
						contract.setEndDate(ed.toLocalDate());
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

	@Override
	public Contract confirmContract() throws DataAccessException {
		Contract contract = null;

		String findActiveSql = "SELECT contractId FROM Contract WHERE active = 1";
		String confirmSql = "UPDATE Contract SET confirmed = 1 WHERE contractId = ?";

		 try (Connection con = DBConnection.getInstance().getConnection()) {
	            boolean oldAutoCommit = con.getAutoCommit();
	            con.setAutoCommit(false);
	            try (PreparedStatement findStmt = con.prepareStatement(findActiveSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	                 ResultSet rs = findStmt.executeQuery()) {

	                if (rs.next()) {
	                    Integer id = rs.getObject("contractId", Integer.class);
	                    if (id != null) {
	                        try (PreparedStatement confirmStmt = con.prepareStatement(confirmSql)) {
	                            confirmStmt.setInt(1, id);
	                            int updated = confirmStmt.executeUpdate();
	                            if (updated > 0) {
	                                contract = new Contract(id);
	                            } else {
	                                con.rollback();
	                                System.err.println("confirmContract: UPDATE påvirkede 0 rækker for contractId=" + id);
	                            }
	                        }
	                    } else {
	                        System.err.println("confirmContract: fundet aktiv kontrakt-række uden contractId");
	                    }
	                }
	                con.commit();
	            } catch (SQLException e) {
	                con.rollback();
	                throw e;
	            } finally {
	                con.setAutoCommit(oldAutoCommit);
	            }
	        } catch (SQLException e) {
	            throw new DataAccessException("Error confirming contract", e);
	        }

	        return contract;
	}

	public int countBookedGuardsForContract(int contractId) throws DataAccessException {
		String sql = "SELECT COUNT(*) FROM EmployeeShift es JOIN Shift s ON es.shiftId = s.shiftId WHERE s.contractId = ?";
		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {
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
		   String sql = "SELECT contractId, employeeId, startDate, endDate FROM Contract WHERE employeeId = ? AND active = 1";

		try (Connection con = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

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
	    String sql = "SELECT contractId, guardAmount, StartDate, EndDate FROM Contract";

	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql);
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

	    String sql =
	        "SELECT contractId, guardAmount, StartDate, EndDate " +
	        "FROM Contract " +
	        "WHERE active = 1 " +
	        "AND (StartDate IS NULL OR StartDate <= CAST(GETDATE() AS date)) " +
	        "AND (EndDate IS NULL OR EndDate >= CAST(GETDATE() AS date))";

	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql);
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
