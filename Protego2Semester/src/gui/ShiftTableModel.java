package gui;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import controller.ContractController;
import controller.ShiftController;
import database.DataAccessException;
import model.Contract;
import model.Shift;

public class ShiftTableModel extends AbstractTableModel {

	private final Object lock = new Object();
	private final HashMap<Integer, Integer> bookedCache = new HashMap<>();
	private final HashMap<Integer, Integer> guardCache = new HashMap<>();

	private ArrayList<Shift> data = new ArrayList<>();
	private static final String[] COL_NAMES = { "ID", "Date", "Start", "End", "Guards", "Location", "Type", "Available",
			"Staffing" };

	private final ShiftController shiftController;
	private final ContractController contractController;

	public ShiftTableModel() {
		this.shiftController = new ShiftController();
		try {
			this.contractController = new ContractController();
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void setData(ArrayList<Shift> shifts) {
		this.data = (shifts != null) ? new ArrayList<>(shifts) : new ArrayList<>();

		synchronized (lock) {
			bookedCache.clear();
			guardCache.clear();

			for (Shift s : data) {
				try {
					int booked = shiftController.countEmployeesForShift(s.getShiftId());
					bookedCache.put(s.getShiftId(), booked);

					if (s.getContract() > 0) {
						Contract c = contractController.findContractById(s.getContract());
						if (c != null) {
							guardCache.put(s.getShiftId(), c.getGuardAmount());
						}
					}
				} catch (DataAccessException e) {
					bookedCache.put(s.getShiftId(), -1);
				}
			}
		}

		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}
	
	public Shift getShiftOfRow(int index) {
	    if (index < 0 || index >= data.size()) {
	        return null;
	    }
	    return data.get(index);
	}


	@Override
	public int getColumnCount() {
		return COL_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return COL_NAMES[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		Shift s = data.get(row);

		switch (column) {
		case 0:
			return s.getShiftId();
		case 1:
			return s.getShiftDate();
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
		case 8:
			synchronized (lock) {
				Integer booked = bookedCache.get(s.getShiftId());
				Integer needed = guardCache.get(s.getShiftId());

				if (booked == null)
					return "Loading...";
				if (booked < 0)
					return "Error";

				int max = (needed != null && needed > 0) ? needed : s.getGuardAmount();

				return booked + " / " + max;
			}
		}

		return "";
	}
}
