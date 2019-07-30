import java.awt.EventQueue;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

public class ToolHomeScreen {
		
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TabbedPane frmMainWindow = new TabbedPane();
					frmMainWindow.setBounds(0, 0, 650, 600);
					frmMainWindow.setLocationRelativeTo(null);
					frmMainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frmMainWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void readAndWriteToscaFile() throws FileNotFoundException{
		
	}
}
