package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.ShiftController;
import database.DataAccessException;
import model.Shift;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class ManagerPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblShiftList;
	private ShiftTableModel shiftTableModel;
	private int managerId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    EventQueue.invokeLater(() -> {
	        try {
	            ManagerLogin loginFrame = new ManagerLogin();
	            loginFrame.setVisible(true);
	        } catch (Exception e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(null,
	                "Could not start ManagerLogin.\n" + e.getMessage(),
	                "Error",
	                JOptionPane.ERROR_MESSAGE);
	        }
	    });
	}


	/**
	 * Create the frame.
	 */
	public ManagerPage(int managerId) {
		this.managerId = managerId;
		shiftTableModel = new ShiftTableModel();

		ShiftController shiftController = new ShiftController();
		List<Shift> availableShifts = shiftController.findShiftByAvailability(true);
		shiftTableModel.setData(availableShifts);
		setTitle("Manager Dashboard - ID: " + managerId);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 300);
		contentPane = new GradientPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));

		// Panel for the table
		JPanel panelShiftList = new JPanel();
		panelShiftList.setBackground(new Color(70, 70, 70));
		contentPane.add(panelShiftList);

		// creating model and tabel

		tblShiftList = new JTable(shiftTableModel);

		// Inserting the table into a scrollpane
		JScrollPane scrollPane = new JScrollPane(tblShiftList);
		panelShiftList.add(scrollPane);

		// Panel for buttons
		JPanel panelButtons = new JPanel();
		panelButtons.setOpaque(false);
		contentPane.add(panelButtons);

		JButton btnNewShift = new JButton("New Shift");
		btnNewShift.addActionListener(e -> {
			JTextField txtStartTime = new JTextField();
			JTextField txtEndTime = new JTextField();
			JTextField txtGuardAmount = new JTextField();
			JTextField txtLocation = new JTextField();
			JTextField txtType = new JTextField();
			JCheckBox chkAvailable = new JCheckBox("Available", true);

			JPanel panel = new JPanel(new GridLayout(0, 2));
			panel.add(new JLabel("Start Time:"));
			panel.add(txtStartTime);
			panel.add(new JLabel("End Time:"));
			panel.add(txtEndTime);
			panel.add(new JLabel("Guard Amount:"));
			panel.add(txtGuardAmount);
			panel.add(new JLabel("Location:"));
			panel.add(txtLocation);
			panel.add(new JLabel("Type:"));
			panel.add(txtType);
			panel.add(new JLabel(""));
			panel.add(chkAvailable); // Maybe shouldnt be here

			// Show dialog and capture result
			int result = JOptionPane.showConfirmDialog(this, panel, "Create New Shift", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				try {
					Shift newShift = new Shift(Integer.parseInt(txtStartTime.getText()),
							Integer.parseInt(txtEndTime.getText()), Integer.parseInt(txtGuardAmount.getText()),
							txtLocation.getText(), chkAvailable.isSelected(), -1 // Placeholder Id until database
																					// generates one (we dont have one
																					// when we create the shift)
					);
					newShift.setShiftType(txtType.getText()); // here we set the type for the shift

					// Creates shift controller and calls createShift to save it to DB
					ShiftController controller = new ShiftController();
					controller.createShift(newShift);

					// Refresh table
					List<Shift> updatedList = controller.findShiftByAvailability(true);
					shiftTableModel.setData(updatedList);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "Please enter valid numbers for start, end, and guards.");
				}

			}
		});
		panelButtons.add(btnNewShift);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelButtons.add(btnUpdate);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelButtons.add(btnDelete);

	}
}
