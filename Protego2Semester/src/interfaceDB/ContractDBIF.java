package interfaceDB;
import database.DataAccessException;
import model.Contract;

public interface ContractDBIF {
	
		Contract findActiveContract(int contractId) throws DataAccessException;
	    Contract confirmContract() throws DataAccessException;	
	    
}



