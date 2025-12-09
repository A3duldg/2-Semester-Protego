package model;

import java.time.LocalDate;

public class Contract {
	private int contractId;

	public Contract(int contractId) {
		this.contractId = contractId;
	}

	public int getContract() {
		return contractId;
	}

	public Contract confirmContract() {
		return this;
	}

	public void setEndDate(LocalDate localDate) {
		// TODO Auto-generated method stub

	}

	public void setStartDate(LocalDate localDate) {
		// TODO Auto-generated method stub

	}

	public void setEmployeeId(int int1) {
		// TODO Auto-generated method stub

	}
}
