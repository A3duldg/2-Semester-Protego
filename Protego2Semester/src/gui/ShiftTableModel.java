package gui;

import java.util.List;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import model.Shift;

public class ShiftTableModel extends AbstractTableModel {
	private List<Shift> data;
	private static final String[] COL_NAMES = { "ID", "Start", "End", "Guards", "Location", "Type", "Available" };

	public ShiftTableModel() {
		setData(null);
	}

	public void setData(List<Shift> shifts) {
		if (shifts != null) {
			this.data = shifts;
		} else {
			this.data = new ArrayList<>(0);
		}
		super.fireTableDataChanged();

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
        	return s.getStartTime();
        case 2: 
        	return s.getEndTime();
        case 3: 
        	return s.getGuardAmount();
        case 4: 
        	return s.getShiftLocation();
        case 5: 
        	return s.getType();
        case 6: 
        	return s.isAvailable() ? "Yes" : "No";
		default:
			return "UNKNOLWN COL NAME";
		}
	}
	
}
