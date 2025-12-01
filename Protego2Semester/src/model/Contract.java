package model;

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
}
