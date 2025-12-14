package controller;
import interfaceDB.ContractDBIF;
import model.Contract;

import java.util.ArrayList;

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
	public ArrayList<Contract> getAllContracts() throws DataAccessException {
	    try {
	        return new ContractDB().findAllContracts();
	    } catch (DataAccessException dae) {
	        throw dae;
	    }
	}
	public ArrayList<Contract> getAllActiveContracts() throws DataAccessException {
	    return new ContractDB().findAllActiveContracts();
	}

}
