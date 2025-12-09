package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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

public class EmployeeLogin extends JFrame {

	 private static final long serialVersionUID = 1L;
	    private JPanel contentPane;
	    private JTextField tfEmployeeId;
	    private JPasswordField passwordField;


	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EmployeeLogin frame = new EmployeeLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public EmployeeLogin() {
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
		panel_ID.setToolTipText("Employee ID");
		contentPane.add(panel_ID);

		tfEmployeeId = new JTextField();
		tfEmployeeId.setText("Employee ID");
		tfEmployeeId.setForeground(Color.GRAY);
		tfEmployeeId.setHorizontalAlignment(SwingConstants.CENTER);
		tfEmployeeId.setColumns(10);
		panel_ID.add(tfEmployeeId);
		
		tfEmployeeId.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (tfEmployeeId.getText().equals("Employee ID")) {
					tfEmployeeId.setText("");
					tfEmployeeId.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (tfEmployeeId.getText().isEmpty()) {
					tfEmployeeId.setText("Employee ID");
					tfEmployeeId.setForeground(Color.GRAY);

				}
			}
		});

		JPanel panel_Password = new JPanel();
        panel_Password.setOpaque(false);
        contentPane.add(panel_Password);

        passwordField = new JPasswordField("Password");
        passwordField.setEchoChar((char) 0);
        passwordField.setForeground(Color.GRAY);
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setColumns(10);
        panel_Password.add(passwordField);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setText("");
                passwordField.setEchoChar('*');
                passwordField.setForeground(Color.BLACK);
            }



			@Override
			 public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText("Password");
                    passwordField.setEchoChar((char) 0);
                    passwordField.setForeground(Color.GRAY);
                }

			}
		});
	
		 JPanel panel_Button = new JPanel();
	        panel_Button.setOpaque(false);
	        contentPane.add(panel_Button);

	        JButton btnLogin = new JButton("Login");
	        btnLogin.addActionListener(e -> {
	            String id = tfEmployeeId.getText();
	            String password = new String(passwordField.getPassword());

	            try {
	                LoginValidator validator = new LoginValidator();
	               LoginValidator.Role role = validator.validate(id);

	                if (role == LoginValidator.Role.EMPLOYEE) {
	                    int employeeId = Integer.parseInt(id);
	                    EmployeePage employeePage = new EmployeePage(employeeId);
	                    employeePage.setVisible(true);
	                    dispose();
	                    
	                } else {
	                    JOptionPane.showMessageDialog(EmployeeLogin.this, "Invalid credentials");
	                }
	            } catch (DataAccessException dae) {
	                JOptionPane.showMessageDialog(EmployeeLogin.this, "Access denied.");
	            }
	        });

	        panel_Button.add(btnLogin);
	        SwingUtilities.invokeLater(() -> btnLogin.requestFocusInWindow());


	}

}
