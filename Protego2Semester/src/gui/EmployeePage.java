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
	            JOptionPane.showMessageDialog(null,
	                "Could not start EmployeeLogin.\n" + e.getMessage(),
	                "Error",
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

		ShiftController shiftController = new ShiftController();
		List<Shift> availableShifts = shiftController.findShiftByAvailability(true);
		shiftTableModel.setData(availableShifts);
		setTitle("Employee Dashboard - ID: " + employeeId);

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


		JButton btnBook = new JButton("Book");
		btnBook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelButtons.add(btnBook);

		JButton btnInfo = new JButton("Info");
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelButtons.add(btnInfo);

	}
}
