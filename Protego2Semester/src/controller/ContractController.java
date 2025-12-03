package controller;
import interfaceDB.ContractDBIF;
import model.Contract;
import database.ContractDB;
import database.DataAccessException;

public class ContractController {
	private ContractDBIF contractDB;

	public ContractController() throws DataAccessException{
		contractDB = new ContractDB();
	}
	public Contract findActiveContract(int contractId) throws DataAccessException {
		return contractDB.findActiveContract(contractId);
	}
	public Contract confirmContract() throws DataAccessException {
		return contractDB.confirmContract();
	}
}
