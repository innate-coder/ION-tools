import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Font;

public class DownloadFinalZipScreen extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					/*DownloadFinalZipScreen frame = new DownloadFinalZipScreen();
					frame.setBounds(50, 50, 650, 500);
					frame.setLocationRelativeTo(null);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.getContentPane().setLayout(null);*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DownloadFinalZipScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblSuccessMsg = new JLabel("Your template has been updated successfully.");
		lblSuccessMsg.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblSuccessMsg.setBounds(51, 16, 362, 20);
		contentPane.add(lblSuccessMsg);
		
		JButton btnDownloadTemplate = new JButton("Download Template");
		btnDownloadTemplate.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnDownloadTemplate.setBounds(89, 180, 235, 29);
		contentPane.add(btnDownloadTemplate);
		
		JLabel lblDownloadMsg = new JLabel("Kindly click on below button to save the updated template");
		lblDownloadMsg.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblDownloadMsg.setBounds(43, 52, 385, 20);
		contentPane.add(lblDownloadMsg);
	}

}
