package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.ContractController;
import controller.ShiftController;
import database.DataAccessException;
import model.Contract;
import model.Shift;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import java.awt.GridLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import java.awt.Color;
import java.awt.Component;
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
	private ContractController contractController;

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
		
		try {
		    this.contractController = new ContractController();
		} catch (DataAccessException e) {
		    // Hvis vi ikke kan forbinde til DB, gem null og håndter det senere i loadContractsIntoCombo.
		    this.contractController = null;
		    System.err.println("ManagerPage: Could not initialize ContractController: " + e.getMessage());
		}

		try {

		ShiftController shiftController = new ShiftController();
		List<Shift> availableShifts = shiftController.findShiftByAvailability(true);
		shiftTableModel.setData(availableShifts);
		} catch (DataAccessException e) {
		    JOptionPane.showMessageDialog(this,
		        "Error loading shifts: " + e.getMessage(),
		        "Database Error",
		        JOptionPane.ERROR_MESSAGE);
		}

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
        btnNewShift.addActionListener(e -> openNewShiftDialog());
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
	
	private void openNewShiftDialog() {
        JTextField txtStartTime = new JTextField();
        JTextField txtEndTime = new JTextField();
        JTextField txtGuardAmount = new JTextField();
        JTextField txtLocation = new JTextField();
        JTextField txtType = new JTextField();
        JCheckBox chkAvailable = new JCheckBox("Available", true);

        // Contract combo
        JComboBox<Contract> contractCombo = new JComboBox<>();
        contractCombo.setRenderer(new ListCellRenderer<Contract>() {
            private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
            @Override
            public Component getListCellRendererComponent(JList<? extends Contract> list, Contract value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    renderer.setText("— No contract —");
                } else {
                    String start = (value.getStartDate() != null) ? value.getStartDate().toString() : "n/a";
                    String end = (value.getEndDate() != null) ? value.getEndDate().toString() : "n/a";
                    renderer.setText("ID:" + value.getContract() + " — " + start + " -> " + end + " (guards: " + value.getGuardAmount() + ")");
                }
                return renderer;
            }
        });

        // Load contracts into combo
        loadContractsIntoCombo(contractCombo);

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
        panel.add(new JLabel("Contract:"));
        panel.add(contractCombo);
        panel.add(new JLabel(""));
        panel.add(chkAvailable);

        // Show dialog and capture result
        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Shift", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int start = Integer.parseInt(txtStartTime.getText().trim());
                int end = Integer.parseInt(txtEndTime.getText().trim());
                int guards = Integer.parseInt(txtGuardAmount.getText().trim());
                String location = txtLocation.getText().trim();
                String type = txtType.getText().trim();
                boolean available = chkAvailable.isSelected();

                // Validate contract selection
                Contract selectedContract = (Contract) contractCombo.getSelectedItem();
                if (selectedContract == null) {
                    JOptionPane.showMessageDialog(this, "Please select a contract from the list.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int contractIdForInsert = selectedContract.getContract();

                // Optionally validate contract still exists in DB
                try {
                    Contract verified = contractController != null ? contractController.findContractById(contractIdForInsert) : null;
                    if (verified == null) {
                        JOptionPane.showMessageDialog(this, "The selected contract was not found in the database.", "Validation", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (DataAccessException dae) {
                    JOptionPane.showMessageDialog(this, "Database error while validating contract: " + dae.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Build and persist shift
                Shift newShift = new Shift(start, end, guards, location, available, -1);
                newShift.setShiftType(type);
                newShift.setContractId(contractIdForInsert);

                ShiftController controller = new ShiftController();
                int newId = controller.createShift(newShift); // ensure this returns id or >0 on success

                if (newId > 0) {
                    JOptionPane.showMessageDialog(this, "Shift created with id: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Refresh table
                    try {
                        List<Shift> updatedList = controller.findShiftByAvailability(true);
                        shiftTableModel.setData(updatedList);
                    } catch (DataAccessException dae) {
                        JOptionPane.showMessageDialog(this, "Error refreshing shifts: " + dae.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create shift.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for start, end, and guards.", "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, "Database error while creating shift: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Load contracts from DB and populate the combo box. If no contracts, combo will contain a single null entry.
     */
    private void loadContractsIntoCombo(JComboBox<Contract> combo) {
        combo.removeAllItems();
        if (contractController == null) {
            combo.addItem(null);
            combo.setEnabled(false);
            return;
        }
        try {
            List<Contract> contracts = contractController.getAllContracts();
            if (contracts == null || contracts.isEmpty()) {
                combo.addItem(null);
                combo.setEnabled(false);
            } else {
                for (Contract c : contracts) combo.addItem(c);
                combo.setEnabled(true);
            }
        } catch (DataAccessException dae) {
            combo.addItem(null);
            combo.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Error loading contracts: " + dae.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

