package controller;
import interfaceDB.ContractDBIF;
import model.Contract;
import database.ContractDB;

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
