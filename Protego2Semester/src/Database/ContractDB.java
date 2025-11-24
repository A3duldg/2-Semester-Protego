package Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import interfaceDB.ContractDBIF;
import Model.Contract;
import Database.DBConnection;

public class ContractDB implements ContractDBIF {
	
	private static final String FIND_ACTIVE_CONTRACT_Q = 
			"SELECT contractId " +
	        "FROM Contract " +
		    "WHERE contractId = ? AND active = 1";
	private static final String CONFIRM_CONTRACT_Q =
			"UPDATE Contract SET confirmed = 1 WHERE active = 1";
	
    private PreparedStatement findActiveContract;
    private PreparedStatement confirmContract;

public ContractDB() {	
	try { Connection con = DBConnection.getInstance().getConnection();
	
	findActiveContract = con.prepareStatement(FIND_ACTIVE_CONTRACT_Q);
	confirmContract = con.prepareStatement(CONFIRM_CONTRACT_Q);
	} 
	 catch (Exception e) {
		 e.printStackTrace();
	 }
}
public Contract findActiveContract(int contractId) {
	return null;
}
public Contract confirmContract() {
	return null;
}
private Contract buildObject(ResultSet rs) {
	
}

}
