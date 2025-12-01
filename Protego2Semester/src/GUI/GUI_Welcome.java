package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI_Welcome extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_Welcome frame = new GUI_Welcome();
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
	public GUI_Welcome() {
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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panelIntroductionText = new JPanel();
		contentPane.add(panelIntroductionText);
		
		JLabel lblWelcome = new JLabel("Welcome, please select a login type");
		panelIntroductionText.add(lblWelcome);
		
		JPanel panelManagerLogin = new JPanel();
		contentPane.add(panelManagerLogin);
		
		JButton btnManagerLogin = new JButton("Manager Login");
		btnManagerLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ManagerLogin ml = new ManagerLogin();
				ml.setVisible(true);
			}
		});
		panelManagerLogin.add(btnManagerLogin);
		
		JPanel panelEmployeeLogin = new JPanel();
		contentPane.add(panelEmployeeLogin);
		
		JButton btnNewButton = new JButton("Employee Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EmployeeLogin el = new EmployeeLogin();
				el.setVisible(true);
			}
		});
		panelEmployeeLogin.add(btnNewButton);

	}

}
