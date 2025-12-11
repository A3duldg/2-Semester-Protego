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
	
	public Contract findContractById(int contractId) throws DataAccessException {
		return contractDB.findContractById(contractId);
	}
	
	public int countBookedGuardsForContract(int contractId) throws DataAccessException {
		return contractDB.countBookedGuardsForContract(contractId);
	}
	
	public boolean isFullyStaffed(int contractId) throws DataAccessException {
		Contract contract = contractDB.findContractById(contractId);
		int booked = contractDB.countBookedGuardsForContract(contractId);
		return booked >= contract.getGuardAmount();

	}
}
