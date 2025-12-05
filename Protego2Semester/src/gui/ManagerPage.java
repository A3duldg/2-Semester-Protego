package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ManagerPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblShiftList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ManagerPage frame = new ManagerPage();
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
	public ManagerPage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new GradientPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panelShiftList = new JPanel();
		panelShiftList.setBackground(new Color(70, 70, 70));
		contentPane.add(panelShiftList);
		
		JScrollPane scrollPane = new JScrollPane();
		panelShiftList.add(scrollPane);
		
		tblShiftList = new JTable();
		panelShiftList.add(tblShiftList);
		
		JPanel panelButtons = new JPanel();
		panelButtons.setOpaque(false);
		contentPane.add(panelButtons);
		
		JButton btnNewShift = new JButton("New Shift");
		btnNewShift.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
