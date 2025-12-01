package database;
import java.sql.*;
import interfaceDB.ContractDBIF;
import model.Contract;


public class ContractDB implements ContractDBIF {
	
	private static final String FIND_ACTIVE_CONTRACT_Q = 
			"SELECT contractId " +
	        "FROM Contract " +
		    "WHERE contractId = ? AND active = 1";
	private static final String CONFIRM_CONTRACT_Q =
			"UPDATE Contract " +
			"SET confirmed = 1 " +
			"WHERE contractId = ? AND active = 1";
	
    private PreparedStatement findActiveContract;
    private PreparedStatement confirmContract;

public ContractDB() {	
	try { Connection con = DBConnection.getInstance().getConnection();
	
	findActiveContract = con.prepareStatement(FIND_ACTIVE_CONTRACT_Q);
	confirmContract = con.prepareStatement(CONFIRM_CONTRACT_Q);
	} 
	 catch (Exception e) {
		 e.printStackTrace();
		 throw new RuntimeException("Could not intialize ContractDB", e);
	 }
}
public Contract findActiveContract(int contractId) {
	try {
		findActiveContract.setInt(1, contractId);
		
		ResultSet rs = findActiveContract.executeQuery();
		
		if (rs.next()) {
			return buildObject(rs);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	
	return null;
}
@Override
public Contract confirmContract() {
	try {
		ResultSet rs = findActiveContract.executeQuery();
		
		if (rs.next()) {
			int id = rs.getInt("contractId");
			
			confirmContract.setInt(1, id);
			
			int updated = confirmContract.executeUpdate();
			
			if (updated > 0) {
				return new Contract(id);
			}
		}
} 
	catch (SQLException e) {
		e.printStackTrace();
	}
	
	return null;
	
}
private Contract buildObject(ResultSet rs) throws SQLException {
	int id = rs.getInt("contractId");
	return new Contract(id);
	
}

}
