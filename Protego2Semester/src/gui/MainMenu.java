package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;

import database.DataAccessException;

import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Dimension;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu frame = new MainMenu();
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
	public MainMenu() {
		setTitle("Protego Security Aps - System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 513, 369);

		setIconImage(new ImageIcon(getClass().getResource("/Resources/Images/Protego_Aps.jpg")).getImage());

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mnOptions.add(mntmHelp);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener((e) -> System.exit(0));
		mnOptions.add(mntmExit);

		contentPane = new GradientPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JPanel panelIntroductionText = new JPanel();
		panelIntroductionText.setOpaque(false);
		contentPane.add(panelIntroductionText);

		JLabel lblWelcome = new JLabel("Welcome, please select a login type");
		lblWelcome.setOpaque(false);
		lblWelcome.setForeground(Color.WHITE);
		lblWelcome.setInheritsPopupMenu(false);
		panelIntroductionText.add(lblWelcome);

		JPanel panelManagerLogin = new JPanel();
		panelManagerLogin.setOpaque(false);
		contentPane.add(panelManagerLogin);

		JButton btnManagerLogin = new JButton("Manager Login");
		btnManagerLogin.setFocusPainted(false);
		btnManagerLogin.setForeground(new Color(255, 255, 255));
		btnManagerLogin.setBackground(new Color(0, 0, 0, 0));
		btnManagerLogin.setOpaque(false);
		btnManagerLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ManagerLogin ml = new ManagerLogin();
				ml.setVisible(true);
				
			}
		});
		panelManagerLogin.add(btnManagerLogin);

		JPanel panelEmployeeLogin = new JPanel();
		panelEmployeeLogin.setOpaque(false);
		contentPane.add(panelEmployeeLogin);

		JButton btnEmployeeLogin = new JButton("Employee Login");
		btnEmployeeLogin.setOpaque(false);
		btnEmployeeLogin.setFocusPainted(false);
		btnEmployeeLogin.setForeground(new Color(255, 255, 255));
		btnEmployeeLogin.setBackground(new Color(0, 0, 0, 0));

		btnEmployeeLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EmployeeLogin el = new EmployeeLogin();
				el.setVisible(true);
			
			}
		});
		panelEmployeeLogin.add(btnEmployeeLogin);

	}

}
