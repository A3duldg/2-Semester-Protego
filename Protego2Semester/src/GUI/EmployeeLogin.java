package GUI;

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
		panel.setToolTipText("Employee id");
		contentPane.add(panel);

		tfEmployeeId = new JTextField();
		tfEmployeeId.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (tfEmployeeId.getText().equals("Employee Id")) {
					tfEmployeeId.setText("");
					tfEmployeeId.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (tfEmployeeId.getText().isEmpty()) {
					tfEmployeeId.setText("Employee Id");
					tfEmployeeId.setForeground(Color.GRAY);

				}
			}
		});
		tfEmployeeId.setToolTipText("");
		tfEmployeeId.setCaretColor(new Color(255, 255, 255));
		tfEmployeeId.setFont(new Font("Tahoma", Font.PLAIN, 11));
		tfEmployeeId.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(tfEmployeeId);
		tfEmployeeId.setColumns(10);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2);

		tfPassword = new JTextField();
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
		tfPassword.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(tfPassword);
		tfPassword.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		
		JButton btnNewButton = new JButton("Login");
		panel_1.add(btnNewButton);

	}

}
