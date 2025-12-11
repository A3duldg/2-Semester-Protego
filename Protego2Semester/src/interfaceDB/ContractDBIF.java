package interfaceDB;
import database.DataAccessException;
import model.Contract;

public interface ContractDBIF {
	
		Contract findActiveContract(int contractId) throws DataAccessException;
	    Contract confirmContract() throws DataAccessException;
	    int countBookedGuardsForContract(int contractId) throws DataAccessException;
	    Contract findContractById(int contractId) throws DataAccessException;
}



