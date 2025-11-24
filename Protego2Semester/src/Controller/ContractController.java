package Controller;
import interfaceDB.ContractDBIF;
import Database.ContractDB;
import Model.Contract;

public class ContractController {
	private ContractDBIF contractDB;

	public ContractController() {
		contractDB = new ContractDB();
	}
	public Contract findActiveContract(int contractId) {
		return contractDB.findActiveContract(contractId);
	}
	public Contract confirmContract() {
		return contractDB.confirmContract();
	}
}
