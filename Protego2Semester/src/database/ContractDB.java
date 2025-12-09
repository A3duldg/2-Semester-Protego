package database;

import java.sql.*;
import interfaceDB.ContractDBIF;
import model.Contract;

public class ContractDB implements ContractDBIF {
	private final DBConnection db;

	public ContractDB() throws DataAccessException {

		db = DBConnection.getInstance();
	}


	
	public Contract findContractById(int contractId) throws DataAccessException {
	    Contract contract = null;
	    String sql = "SELECT contractId, employeeId, startDate, endDate FROM Contract WHERE contractId = ?";

	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql)) {

	        stmt.setInt(1, contractId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                // Use the one-argument constructor
	                contract = new Contract(rs.getInt("contractId"));

	                // Set other fields via setters
	                contract.setEmployeeId(rs.getInt("employeeId"));
	                contract.setStartDate(rs.getDate("startDate").toLocalDate());
	                contract.setEndDate(rs.getDate("endDate").toLocalDate());
	            }
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error finding contract", e);
	    }

	    return contract;
	}



	@Override
	public Contract confirmContract() {
	    Contract contract = null;

	    String findActiveSql = "SELECT contractId FROM Contract WHERE active = 1"; 
	    String confirmSql = "UPDATE Contract SET confirmed = 1 WHERE contractId = ?";

	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement findStmt = con.prepareStatement(findActiveSql);
	         ResultSet rs = findStmt.executeQuery()) {

	        if (rs.next()) {
	            int id = rs.getInt("contractId");

	            try (PreparedStatement confirmStmt = con.prepareStatement(confirmSql)) {
	                confirmStmt.setInt(1, id);
	                int updated = confirmStmt.executeUpdate();

	                if (updated > 0) {
	                    contract = new Contract(id);
	                }
	            }
	        }
	    } catch (SQLException | DataAccessException e) {
	        e.printStackTrace();
	    }

	    return contract;
	}
	
	// Added this method
	@Override
	public Contract findActiveContract(int employeeId) throws DataAccessException {
	    Contract contract = null;
	    String sql = "SELECT contractId, employeeId, startDate, endDate " +
	                 "FROM Contract WHERE employeeId = ? AND active = 1";

	    try (Connection con = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql)) {

	        stmt.setInt(1, employeeId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                contract = new Contract(rs.getInt("contractId"));
	                contract.setEmployeeId(rs.getInt("employeeId"));
	                contract.setStartDate(rs.getDate("startDate").toLocalDate());
	                contract.setEndDate(rs.getDate("endDate").toLocalDate());
	            }
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error finding active contract", e);
	    }

	    return contract;
	}



}
