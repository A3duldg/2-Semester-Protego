package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.ContractController;
import controller.EmployeeController;
import controller.ShiftController;
import database.DataAccessException;
import database.EmployeeDB;
import model.Contract;
import model.Employee;
import model.Shift;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class EmployeePage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblShiftList;
	private ShiftTableModel shiftTableModel;
	private int employeeId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				EmployeeLogin loginFrame = new EmployeeLogin();
				loginFrame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Could not start EmployeeLogin.\n" + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EmployeePage(int employeeId) {
		this.employeeId = employeeId;
		shiftTableModel = new ShiftTableModel();

		try {
			ShiftController shiftController = new ShiftController();
			List<Shift> availableShifts = shiftController.findShiftByAvailability(true);
			shiftTableModel.setData(availableShifts);
		} catch (DataAccessException e) {
			JOptionPane.showMessageDialog(this, "Error loading shifts: " + e.getMessage());
		}

		setTitle("Employee Dashboard - ID: " + employeeId);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 300);
		contentPane = new GradientPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));

		// Panel for the table
		JPanel panelShiftList = new JPanel(new BorderLayout());
		panelShiftList.setBackground(new Color(70, 70, 70));
		contentPane.add(panelShiftList);

		// creating model and tabel

		tblShiftList = new JTable(shiftTableModel);

		// Inserting the table into a scrollpane
		JScrollPane scrollPane = new JScrollPane(tblShiftList);
		panelShiftList.add(scrollPane, BorderLayout.CENTER);

		// Panel for buttons
		JPanel panelButtons = new JPanel();
		panelButtons.setOpaque(false);
		contentPane.add(panelButtons);

		JButton btnBook = new JButton("Book");
		btnBook.addActionListener(e -> {

		    int selectedRow = tblShiftList.getSelectedRow();
		    if (selectedRow < 0) {
		        JOptionPane.showMessageDialog(this, "Please select a shift to book.");
		        return;
		    }

		    try {
		        // 1) Get selected shift from table model
		        Shift selectedShift = shiftTableModel.getShiftOfRow(selectedRow);
		        if (selectedShift == null) {
		            JOptionPane.showMessageDialog(this, "Selected shift not found.", "Error",
		                    JOptionPane.ERROR_MESSAGE);
		            return;
		        }

		        // 2) Get logged-in employee
		        EmployeeController employeeController = new EmployeeController(new EmployeeDB());
		        Employee loggedInEmployee = employeeController.getEmployeeId(employeeId);

		        if (loggedInEmployee == null) {
		            JOptionPane.showMessageDialog(this, "Logged-in employee not found.", "Error",
		                    JOptionPane.ERROR_MESSAGE);
		            return;
		        }

		        // 3) Attempt booking
		        employeeController.connectShiftToEmployee(loggedInEmployee, selectedShift);

		        // 4) Success feedback
		        JOptionPane.showMessageDialog(this, "Shift booked successfully!");
		        refreshShiftTable();

		    } catch (DataAccessException ex) {
		        // Booking failed (capacity reached, already booked, not available, etc.)
		        JOptionPane.showMessageDialog(this,
		                ex.getMessage(),
		                "Booking failed",
		                JOptionPane.ERROR_MESSAGE);

		    } catch (Exception ex) {
		        JOptionPane.showMessageDialog(this,
		                "Unexpected error: " + ex.getMessage(),
		                "Error",
		                JOptionPane.ERROR_MESSAGE);
		    }
		});


		panelButtons.add(btnBook);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshShiftTable();
			}
		});
		panelButtons.add(btnRefresh);

		JButton btnInfo = new JButton("Info");
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelButtons.add(btnInfo);

	}

	private void refreshShiftTable() {
		try {
			ShiftController shiftController = new ShiftController();
			List<Shift> updatedShifts = shiftController.findShiftByAvailability(true);
			shiftTableModel.setData(updatedShifts);
			shiftTableModel.fireTableDataChanged();
		} catch (DataAccessException e) {
			JOptionPane.showMessageDialog(this, "Error refreshing shift table: " + e.getMessage());
		}
	}
}
