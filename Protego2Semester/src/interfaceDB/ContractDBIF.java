package interfaceDB;
import Model.Contract;

public interface ContractDBIF {
	
		Contract findActiveContract(int contractId);
	    Contract confirmContract();	
	    
}



