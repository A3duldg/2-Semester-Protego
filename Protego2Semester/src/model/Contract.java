package model;

import java.time.LocalDate;
import java.util.Objects;

public class Contract {
	private int contractId;
	private int employeeId;
	private LocalDate startDate;
	private LocalDate endDate;
	private int guardAmount;
	private boolean active;
	private boolean confirmed;
	private String type;
	public Contract(int contractId) {
		
	
		this.contractId = contractId;
		this.active = true;
		this.confirmed = true;
	}

	public Contract(int contractId, int employeeId, LocalDate startDate, LocalDate endDate, boolean active,
			boolean confirmed, int guardAmount) {
		//Validation
		if (contractId <= 0) {
			throw new IllegalArgumentException("Contract ID must be positive");
		}
		if (employeeId <= 0) {
			throw new IllegalArgumentException("Employee ID must be positive");
		}
		if (startDate == null) {
			throw new IllegalArgumentException("Start date cannot be null");
		}
		if (endDate == null) {
			throw new IllegalArgumentException("End date cannot be null");
		}
		if (endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date must be after start date");
		}
		if (guardAmount <= 0) {
			throw new IllegalArgumentException("There needs to be at least 1 or more guards needed");
		}

		this.contractId = contractId;
		this.employeeId = employeeId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.active = active;
		this.confirmed = confirmed;
		this.guardAmount = guardAmount;
	}
	public String getType() {
		return type;
	}
	
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getGuardAmount() {
		return guardAmount;
	}

	public int getContract() {
		return contractId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isConfirmed() {
		return confirmed;
	}
	
	public void setGuardAmount(int guardAmount) {
		if (guardAmount <= 0) {
			throw new IllegalArgumentException("There needs to be at least 1 or more guards needed");
		}
		this.guardAmount = guardAmount;
	}
	
	public void setEmployeeId(int employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be bigger than 0");
        }
        this.employeeId = employeeId;
    }

	public void setEndDate(LocalDate endDate) {
		if (endDate == null) {
			throw new IllegalArgumentException("End date cannot be null");
		}
		if (startDate != null && endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date must be after start date");
		}
		this.endDate = endDate;
	}

	public void setStartDate(LocalDate startDate) {
		if (startDate == null) {
			throw new IllegalArgumentException("Start date cannot be null");
		}
		if (endDate != null && startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date must be before end date");
		}
		this.startDate = startDate;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	public Contract confirmContract() {
		this.confirmed = true;
		return this;
	}
	//Deaktiver contract
	public void deactivate() {
		this.active = false;
	}
	//Tjekker om contract er gyldig på en bestemt dato
	public boolean isValidOn(LocalDate date) {
		if (date == null || !active) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
	}
	//Tjekker om Contact er udløbet
	 public boolean isExpired() {
	        if (endDate == null) {
	            return false;
	        }
	        return LocalDate.now().isAfter(endDate);
	    }
	 @Override
	    public boolean equals(Object obj) {
	        if (this == obj) return true;
	        if (obj == null || getClass() != obj.getClass()) return false;
	        Contract contract = (Contract) obj;
	        return contractId == contract.contractId;
	    }
	    
	    @Override
	    public int hashCode() {
	        return Objects.hash(contractId);
	    }
	    
	    @Override
	    public String toString() {
	        return String.format("Contract[id=%d, employeeId=%d, start=%s, end=%s, active=%b, confirmed=%b]",
	            contractId, employeeId, startDate, endDate, active, confirmed);
	    }
}
