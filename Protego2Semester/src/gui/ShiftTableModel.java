package gui;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.table.AbstractTableModel;

import controller.ContractController;
import controller.ShiftController;
import database.DataAccessException;
import model.Contract;
import model.Shift;

public class ShiftTableModel extends AbstractTableModel {
	private List<Shift> data;
	private static final String[] COL_NAMES = { "ID", "Date", "Start", "End", "Guards", "Location", "Type", "Available",
			"Staffing" };

	private final ContractController contractController;
	private final ShiftController shiftController; 
	
	private final Map<Integer, Contract> contractById = new HashMap<>();
	private final Map<Integer, Integer> bookedByShiftId = new HashMap<>();


	
	public ShiftTableModel() {
		 this.shiftController = new ShiftController();
		try {
			this.contractController = new ContractController();
		} catch (DataAccessException e) {
			throw new RuntimeException("Failed to initialize ContractController", e);
		}
		setData(null);
	}

	public void setData(List<Shift> shifts) {
	    if (shifts != null) {
	        this.data = shifts;
	    } else {
	        this.data = new ArrayList<>(0);
	    }

	    // Clear previous caches immediately so UI ikke viser gamle værdier
	    contractById.clear();
	    bookedByShiftId.clear();

	    // Fire so table at least renders immediately (with "Loading..." in staffing if needed)
	    fireTableDataChanged();

	    // Collect unique shiftIds and contractIds to fetch
	    final List<Shift> snapshot = new ArrayList<>(this.data);
	    final Set<Integer> shiftIds = new HashSet<>();
	    final Set<Integer> contractIds = new HashSet<>();
	    for (Shift s : snapshot) {
	        shiftIds.add(s.getShiftId());
	        int cid = s.getContract();
	        if (cid > 0) contractIds.add(cid);
	    }

	    // Use SwingWorker to fetch counts/contracts off the EDT
	    javax.swing.SwingWorker<Void, Void> worker = new javax.swing.SwingWorker<>() {
	        @Override
	        protected Void doInBackground() {
	            // 1) Prefetch counts per shift
	            for (Integer sid : shiftIds) {
	                try {
	                    int booked = shiftController.countEmployeesForShift(sid);
	                    synchronized (bookedByShiftId) {
	                        bookedByShiftId.put(sid, booked);
	                    }
	                } catch (DataAccessException dae) {
	                    System.err.println("ShiftTableModel: error counting for shiftId=" + sid + ": " + dae.getMessage());
	                    synchronized (bookedByShiftId) {
	                        bookedByShiftId.put(sid, -1); // mark as error
	                    }
	                }
	            }

	            // 2) Prefetch contracts
	            for (Integer cid : contractIds) {
	                try {
	                    Contract c = contractController.findContractById(cid);
	                    synchronized (contractById) {
	                        contractById.put(cid, c);
	                    }
	                } catch (DataAccessException dae) {
	                    System.err.println("ShiftTableModel: error loading contractId=" + cid + ": " + dae.getMessage());
	                    synchronized (contractById) {
	                        contractById.put(cid, null);
	                    }
	                }
	            }
	            return null;
	        }

	        @Override
	        protected void done() {
	            // Når færdig -> opdater GUI (EDT)
	            fireTableDataChanged();
	        }
	    };

	    worker.execute();
	}


	public Shift getShiftOfRow(int index) {
		if (index < 0 || index >= data.size()) {
			return null;
		}
		return this.data.get(index);
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public String getColumnName(int column) {
		return COL_NAMES[column];
	}

	@Override
	public int getColumnCount() {
		return COL_NAMES.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
	    Shift s = data.get(row);

	    switch (column) {
	        case 0:
	            return s.getShiftId();

	        case 1:
	            return s.getShiftDate(); // 👈 DEN NYE

	        case 2:
	            return s.getStartTime();

	        case 3:
	            return s.getEndTime();

	        case 4:
	            return s.getGuardAmount();

	        case 5:
	            return s.getShiftLocation();

	        case 6:
	            return s.getType();

	        case 7:
	            return s.isAvailable() ? "Yes" : "No";

	        case 8: { // 👈 STAFFING ER NU CASE 8
	            int shiftId = s.getShiftId();
	            int contractId = s.getContract();

	            Integer booked;
	            synchronized (bookedByShiftId) {
	                booked = bookedByShiftId.get(shiftId);
	            }
	            if (booked == null) return "Loading...";
	            if (booked < 0) return "Error";

	            Integer needed = null;
	            if (contractId > 0) {
	                synchronized (contractById) {
	                    Contract c = contractById.get(contractId);
	                    if (c != null) needed = c.getGuardAmount();
	                }
	            }
	            int neededInt = (needed != null && needed > 0) ? needed : s.getGuardAmount();
	            return booked + " / " + neededInt;
	        }

	        default:
	            return "UNKNOWN COL";
	    }
	}
}
