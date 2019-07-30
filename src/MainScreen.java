import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;
import constants.FilePath;
import utils.CommonUtils;
import utils.UnZipUtility;

import java.io.File;
import java.io.IOException;

public class MainScreen {

	private JFrame frmMainWindow;
	private JTextField txtTemplatePath, txtCiotPath, txtPhysnet5InitialIP, 
	txtPhysnet6InitialIP, txtOAMPrimaryIP, txtOAMSecondaryIP, txtZone1, txtZone2 ;
	private JLabel lblChooseTemplate, lblChooseCiot, lblInitialIpForPhysnet5, 
	lblInitialIpForPhysnet6, lblOAMPrimaryIP, lblOAMSecondaryIP, lblZone1, lblZone2,lblToscaYamlVersion;
	private JButton btnBrowseTemplate, btnBrowseCiot, btnSubmit;
	private JComboBox<String[]> comboBox;
	private String[] toscaYamlVersions = {"Tosca.yaml V1", "Tosca.yaml V2", "Tosca.yaml V3", "Tosca.yaml V4"};


	/**
	 * 
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainScreen window = new MainScreen();
					window.frmMainWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainScreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMainWindow = new JFrame();
		frmMainWindow.setTitle("Main window");
		frmMainWindow.setBounds(50, 50, 650, 500);
		frmMainWindow.setLocationRelativeTo(null);
		frmMainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMainWindow.getContentPane().setLayout(null);
		
		lblChooseTemplate = new JLabel("Browse template ");
		lblChooseTemplate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblChooseTemplate.setBounds(15, 20, 178, 20);
		frmMainWindow.getContentPane().add(lblChooseTemplate);
		
		txtTemplatePath = new JTextField();
		txtTemplatePath.setBounds(224, 17, 244, 30);
		frmMainWindow.getContentPane().add(txtTemplatePath);
		txtTemplatePath.setColumns(10);
		
		btnBrowseTemplate = new JButton("Browse");
		btnBrowseTemplate.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnBrowseTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selectedFilepath = showOpenDialog("zip");		
				if(selectedFilepath!=null){
					txtTemplatePath.setText(selectedFilepath);
					copyFile(selectedFilepath);
				}
			}
		});
		btnBrowseTemplate.setBounds(498, 16, 115, 29);
		frmMainWindow.getContentPane().add(btnBrowseTemplate);
		
		lblChooseCiot = new JLabel("Browse CIOT output");
		lblChooseCiot.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblChooseCiot.setBounds(15, 58, 178, 20);
		frmMainWindow.getContentPane().add(lblChooseCiot);
		
		txtCiotPath = new JTextField();
		txtCiotPath.setColumns(10);
		txtCiotPath.setBounds(224, 55, 244, 30);
		frmMainWindow.getContentPane().add(txtCiotPath);
		
		btnBrowseCiot = new JButton("Browse");
		btnBrowseCiot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selectedFilepath = showOpenDialog("txt");		
				if(selectedFilepath!=null){
					txtCiotPath.setText(selectedFilepath);
					copyFile(selectedFilepath);
				}
			}
		});
		btnBrowseCiot.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnBrowseCiot.setBounds(498, 53, 115, 29);
		frmMainWindow.getContentPane().add(btnBrowseCiot);
		
		lblInitialIpForPhysnet5 = new JLabel("Initial IP for Physnet5");
		lblInitialIpForPhysnet5.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblInitialIpForPhysnet5.setBounds(15, 97, 178, 20);
		frmMainWindow.getContentPane().add(lblInitialIpForPhysnet5);
		
		txtPhysnet5InitialIP = new JTextField();
		txtPhysnet5InitialIP.setColumns(10);
		txtPhysnet5InitialIP.setBounds(224, 91, 244, 30);
		frmMainWindow.getContentPane().add(txtPhysnet5InitialIP);
		
		lblInitialIpForPhysnet6 = new JLabel("Initial IP for Physnet6");
		lblInitialIpForPhysnet6.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblInitialIpForPhysnet6.setBounds(15, 133, 178, 20);
		frmMainWindow.getContentPane().add(lblInitialIpForPhysnet6);
		
		txtPhysnet6InitialIP = new JTextField();
		txtPhysnet6InitialIP.setColumns(10);
		txtPhysnet6InitialIP.setBounds(224, 128, 244, 30);
		frmMainWindow.getContentPane().add(txtPhysnet6InitialIP);
		
		lblZone1 = new JLabel("Zone1 resource ID");
		lblZone1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblZone1.setBounds(15, 244, 178, 20);
		frmMainWindow.getContentPane().add(lblZone1);
		
		txtZone1 = new JTextField();
		txtZone1.setColumns(10);
		txtZone1.setBounds(224, 243, 244, 30);
		frmMainWindow.getContentPane().add(txtZone1);
		
		lblZone2 = new JLabel("Zone2 Resource ID");
		lblZone2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblZone2.setBounds(15, 286, 178, 20);
		frmMainWindow.getContentPane().add(lblZone2);
		
		txtZone2 = new JTextField();
		txtZone2.setColumns(10);
		txtZone2.setBounds(224, 283, 244, 30);
		frmMainWindow.getContentPane().add(txtZone2);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(allFieldsAreFilled()){
					showAlertMsg("Submitted successfully");
				}
			}
		});
		btnSubmit.setBounds(163, 391, 115, 37);
		frmMainWindow.getContentPane().add(btnSubmit);
		
		lblToscaYamlVersion = new JLabel("Tosca.yaml Version");
		lblToscaYamlVersion.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblToscaYamlVersion.setBounds(15, 335, 178, 20);
		frmMainWindow.getContentPane().add(lblToscaYamlVersion);
		
		comboBox = new JComboBox<String[]>();
		comboBox.setModel(new DefaultComboBoxModel(toscaYamlVersions));
		comboBox.setBounds(224, 329, 244, 30);
		frmMainWindow.getContentPane().add(comboBox);
		
		lblOAMSecondaryIP = new JLabel("OAM Secondary IP");
		lblOAMSecondaryIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblOAMSecondaryIP.setBounds(15, 211, 178, 20);
		frmMainWindow.getContentPane().add(lblOAMSecondaryIP);
		
		txtOAMSecondaryIP = new JTextField();
		txtOAMSecondaryIP.setColumns(10);
		txtOAMSecondaryIP.setBounds(224, 205, 244, 30);
		frmMainWindow.getContentPane().add(txtOAMSecondaryIP);
		
		txtOAMPrimaryIP = new JTextField();
		txtOAMPrimaryIP.setColumns(10);
		txtOAMPrimaryIP.setBounds(224, 165, 244, 30);
		frmMainWindow.getContentPane().add(txtOAMPrimaryIP);
		
		lblOAMPrimaryIP = new JLabel("OAM Primary IP");
		lblOAMPrimaryIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblOAMPrimaryIP.setBounds(15, 171, 178, 20);
		frmMainWindow.getContentPane().add(lblOAMPrimaryIP);
	}
	

    public static String showOpenDialog(String extension) {
        // Create a filter so that we only see files with extension
    	FileNameExtensionFilter filter = null;
    	if(extension.equalsIgnoreCase("txt")){
    		filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");	
    	}else{
    		filter = new FileNameExtensionFilter(null, extension);
    	}
        // Create and show the file filter
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        int response = fc.showOpenDialog(null);
        // Check the user pressed OK, and not Cancel.
        if (response == JFileChooser.APPROVE_OPTION) {
            File yourZip = fc.getSelectedFile();
            System.out.println("File name is: "+yourZip.getAbsolutePath());
            return yourZip.getAbsolutePath();
            // Do whatever you want with the file
        }
        return null;
   }
         
   private void showAlertMsg(String message){
	   String inputJsonFile = FilePath.intermediateFolderPath+FilePath.grantlessFileName;
       String inputTextFile = FilePath.inputFolderPath+FilePath.ciotOutputFileName;
       String[] args = {inputJsonFile,inputTextFile,txtZone1.getText(),txtZone2.getText(),
    		   txtPhysnet5InitialIP.getText(),txtPhysnet6InitialIP.getText(), txtOAMPrimaryIP.getText(),
    		   txtOAMSecondaryIP.getText()};
//       Tools.main(args);
      JOptionPane.showMessageDialog(null, message);
   }
    
   private void copyFile(String sourceFile){
    	File source = new File(sourceFile);
    	File dest = new File(FilePath.inputFolderPath + source.getName());
    	try {
    	    FileUtils.copyFile(source, dest);
    	    extractZip(dest.getPath(),FilePath.intermediateFolderPath);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }
    
    private void extractZip(String sourceZipPath, String destZipPath){
    	UnZipUtility unZipper = new UnZipUtility();
        try {
            unZipper.unzip(sourceZipPath, destZipPath);         
        }catch(Exception ex){
        	
        }
    }
    
    private boolean allFieldsAreFilled(){
    	if(txtTemplatePath.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please select template with .zip file format");
    		return false;
    	}else if(txtCiotPath.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please select CIOT with .txt file format");
    		return false;
    	}else if(txtPhysnet5InitialIP.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please enter Physnet5 Initial IP");
    		return false;
    	}else if(txtPhysnet6InitialIP.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please enter Physnet6 Initial IP");
    		return false;
    	}else if(txtZone1.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please enter Zone1 resource ID");
    		return false;
    	}else if(txtZone2.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please enter Zone2 resource ID");
    		return false;
    	}else{
    	  return true;
    	}
    }
}
