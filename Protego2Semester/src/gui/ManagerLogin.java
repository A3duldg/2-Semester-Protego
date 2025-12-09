package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.border.LineBorder;

import controller.LoginValidator;
import database.DataAccessException;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class ManagerLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tfManagerId;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ManagerLogin frame = new ManagerLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ManagerLogin() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mnOptions.add(mntmHelp);
		contentPane = new GradientPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_ID = new JPanel();
		panel_ID.setOpaque(false);
		panel_ID.setToolTipText("Manager ID");
		contentPane.add(panel_ID);

		tfManagerId = new JTextField();
		tfManagerId.setText("Manager ID");
		tfManagerId.setForeground(Color.GRAY);
		tfManagerId.setHorizontalAlignment(SwingConstants.CENTER);
		tfManagerId.setColumns(10);
		panel_ID.add(tfManagerId);
		tfManagerId.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (tfManagerId.getText().equals("Manager ID")) {
					tfManagerId.setText("");
					tfManagerId.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (tfManagerId.getText().isEmpty()) {
					tfManagerId.setText("Manager ID");
					tfManagerId.setForeground(Color.GRAY);

				}
			}
		});

		JPanel panel_Password = new JPanel();
		panel_Password.setOpaque(false);
		contentPane.add(panel_Password);

		passwordField = new JPasswordField();
		passwordField.setText("Password");
		passwordField.setEchoChar((char) 0);
		passwordField.setForeground(Color.GRAY);
		passwordField.setHorizontalAlignment(SwingConstants.CENTER);
		passwordField.setColumns(10);
		panel_Password.add(passwordField);

		passwordField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) { // checks for the box in focus
				passwordField.setText("");
				passwordField.setEchoChar('*'); // We put the masking back on
				passwordField.setForeground(Color.BLACK);
			}

			@Override
			public void focusLost(FocusEvent e) { // checks for when the box is out of focus

				if (passwordField.getPassword().length == 0) { // We have the if statement check that the box is empty
					passwordField.setText("Password"); // Here we set the placeholder text to say password
					passwordField.setEchoChar((char) 0); // Here we turn the masking off so the user can see the text
					passwordField.setForeground(Color.BLACK);
				}
			}
		});

		JPanel panel_Button = new JPanel();
		panel_Button.setOpaque(false);
		contentPane.add(panel_Button);

		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(e -> {
			String id = tfManagerId.getText(); // get text from id field
			String password = new String(passwordField.getPassword()); // get password from field

			try {
				LoginValidator validator = new LoginValidator();
				LoginValidator.Role role = validator.validate(id);

				if (role == LoginValidator.Role.MANAGER) {
					int managerId = Integer.parseInt(id);
					ManagerPage managerPage = new ManagerPage(managerId);
					managerPage.setVisible(true);

					dispose();
				} else {
					JOptionPane.showMessageDialog(ManagerLogin.this, "Invalid credentials");

				}
				// JOptionPane.showMessageDialog(panel_ID, "Login success!");
			} catch (DataAccessException dae) {
				JOptionPane.showMessageDialog(ManagerLogin.this, "Access denied.");
			}

		});

		panel_Button.add(btnNewButton);
		SwingUtilities.invokeLater(() -> btnNewButton.requestFocusInWindow());

	}

}
