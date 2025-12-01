package interfaceDB;
import model.Contract;

public interface ContractDBIF {
	
		Contract findActiveContract(int contractId);
	    Contract confirmContract();	
	    
}



