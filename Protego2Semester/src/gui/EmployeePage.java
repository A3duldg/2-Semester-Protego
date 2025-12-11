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
				// Hent shift-objektet fra modellen
				Shift selectedShift = shiftTableModel.getShiftOfRow(selectedRow);
				if (selectedShift == null) {
					JOptionPane.showMessageDialog(this, "Selected shift not found.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Hent employee og controllers
				EmployeeController employeeController = new EmployeeController(new EmployeeDB());
				ShiftController shiftController = new ShiftController();
				ContractController contractController = new ContractController();

				Employee loggedInEmployee = employeeController.getEmployeeId(employeeId);
				if (loggedInEmployee == null) {
					JOptionPane.showMessageDialog(this, "Logged-in employee not found.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Count booked **for this shift** (IKKE for contract)
				int bookedForThisShift = shiftController.countEmployeesForShift(selectedShift.getShiftId());

				// Hvis shift har en contract: hent contract.guardAmount og brug den som max
				int contractId = selectedShift.getContract();
				if (contractId > 0) {
					Contract contract = contractController.findContractById(contractId);
					if (contract == null) {
						JOptionPane.showMessageDialog(this, "Referenced contract not found.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					Integer contractGuardAmount = contract.getGuardAmount();
					if (contractGuardAmount == null) {
						JOptionPane.showMessageDialog(this, "Contract has no guardAmount set.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Compare bookedForThisShift vs contract.guardAmount
					if (bookedForThisShift >= contractGuardAmount) {
						JOptionPane.showMessageDialog(this,
								"This contract is fully staffed. No more bookings allowed.");
						return;
					}
					// ellers: ok at booke
				} else {
					// Ingen contract på shift — fallback: brug shift.guardAmount som kapacitet
					int shiftCapacity = selectedShift.getGuardAmount();
					if (bookedForThisShift >= shiftCapacity) {
						JOptionPane.showMessageDialog(this, "This shift is fully staffed. No more bookings allowed.");
						return;
					}
				}

				// Hvis vi når hertil: opret booking
				employeeController.connectShiftToEmployee(loggedInEmployee, selectedShift);
				JOptionPane.showMessageDialog(this, "Shift booked successfully!");
				refreshShiftTable();

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Please enter valid input.", "Validation",
						JOptionPane.WARNING_MESSAGE);
			} catch (DataAccessException ex) {
				JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage(), "DB Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error",
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
