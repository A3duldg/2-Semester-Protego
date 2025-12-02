package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
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
	private JTextField tfPassword;

	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the frame.
	 */
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

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setToolTipText("Employee id");
		contentPane.add(panel);

		tfEmployeeId = new JTextField();
		tfEmployeeId.setText("Employee ID");
		tfEmployeeId.setForeground(Color.GRAY);
		tfEmployeeId.setHorizontalAlignment(SwingConstants.CENTER);
		tfEmployeeId.setColumns(10);
		panel.add(tfEmployeeId);
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

		JPanel panel_2 = new JPanel();
		panel_2.setOpaque(false);
		contentPane.add(panel_2);

		tfPassword = new JTextField();
		tfPassword.setText("Password");
		tfPassword.setForeground(Color.GRAY);
		tfPassword.setHorizontalAlignment(SwingConstants.CENTER);
		tfPassword.setColumns(10);
		panel_2.add(tfPassword);
		tfPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (tfPassword.getText().equals("Password")) {
					tfPassword.setText("");
					tfPassword.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				
				if (tfPassword.getText().isEmpty()) {
					tfPassword.setText("Password");
					tfPassword.setForeground(Color.GRAY);
				}
			}
		});
	
		
		JPanel panel_1 = new JPanel();
		panel_1.setOpaque(false);
		contentPane.add(panel_1);
		
		JButton btnNewButton = new JButton("Login");
		panel_1.add(btnNewButton);
		SwingUtilities.invokeLater(() -> btnNewButton.requestFocusInWindow());

	


	}

}
