import java.awt.EventQueue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import constants.FilePath;
import utils.CommonUtils;
import utils.UnZipUtility;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class TabbedPane extends JFrame {
	
	/*GrantLess Panel fields*/
	private JTextField  txtCiotPath, txtOAMActiveIP, txtOAMStandByIP,
	txtOAMActiveVirtualIP, txtOAMStandByVirtualIP, txtZone1, txtZone2 ;
	private JLabel lblTemplateVersion, lblSelectNodeType, lblSelectSolutionType,lblVnfTemplate, lblChooseCiot,
	lblOAMActiveIP, lblOAMStandByIP,lblOAMActiveVirtualIP, lblOAMStandByVirtualIP, lblZone1, lblZone2;
	private JButton btnBrowseCiot, btnNext;
	
	/*TOSCA Panel fields*/
	private JTextField txtVnfHostName, txtSystemIPAddress, txtStaticRoute, txtControlPlaneCores, txtControlSwitchFabricCidr,
	txtMgLbManagementCidr, txtPrimaryConfigFile, txtLicenseFile, txtCsfFailureDetectionTimer;
	private JLabel lblToscaYamlVersion, lblVnfHostName, lblSystemIPAddress, lblStaticRoute, lblControlPlaneCores, lblControlSwitchFabricCidr,
	lblMgLbManagementCidr,lblPrimaryConfigFile,lblLicenseFile, lblCsfFailureDetectionTimer;
	private JButton btnUpdateTemplate;	
	
	private JPanel jPanelGrantless, jPanelTosca;
	private JTabbedPane jTabbedPane;
	private JComboBox<String[]> comboBoxTemplateVersion, comboBoxNodeType, comboBoxSolutionType, comboBoxYamlversion;
	private String[] nodeTypes = {"CPF", "UPF", "Non-Cups"};
	private String[] solutionType = {"LB_Mode", "LB_Less"};
	private String[] templateVersion = {FilePath.V1, FilePath.V2};
	private String[] toscaYamlVersions = {"Tosca.yaml V1", "Tosca.yaml V2"};
		
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TabbedPane tp = new TabbedPane();
					tp.setTitle("Tabbed window");
					tp.setBounds(0, 0, 650, 600);
					tp.setLocationRelativeTo(null);
			        tp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			        tp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	 public TabbedPane() {         
	        setTitle("Tabbed Pane");
	        jTabbedPane = new JTabbedPane();
	        jTabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        getContentPane().add(jTabbedPane);
	        jPanelGrantless = new JPanel();
	        jPanelGrantless.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelGrantless.setLayout(null);
	        
	        jPanelTosca = new JPanel();	    
	        jPanelTosca.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jTabbedPane.addTab("Step One", jPanelGrantless);
	        jTabbedPane.addTab("Step Two", jPanelTosca);	        	        
	        
	        jPanelTosca.setLayout(null);
	                
	        setGrantlessPanelView();
	        setToscaPanelView();
	 }
	 
	 private void setGrantlessPanelView(){					
			lblChooseCiot = new JLabel("CIOT output");
			lblChooseCiot.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblChooseCiot);
			lblChooseCiot.setBounds(15, 127, 115, 20);
			
			txtCiotPath = new JTextField();
			txtCiotPath.setColumns(10);
			txtCiotPath.setBounds(224, 123, 244, 30);
			jPanelGrantless.add(txtCiotPath);
			
			btnBrowseCiot = new JButton("Browse");
			jPanelGrantless.add(btnBrowseCiot);
			btnBrowseCiot.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String selectedFilepath = showOpenDialog("txt");		
					if(selectedFilepath!=null){
						txtCiotPath.setText(selectedFilepath);
						String templatePath = FilePath.getTemplatePathFromRepo(comboBoxTemplateVersion.getSelectedItem().toString(),
								comboBoxSolutionType.getSelectedItem().toString());
						System.out.println("Template Path : "+templatePath);
						copyFile(templatePath);
						copyFile(selectedFilepath);
					}
				}
			});
			btnBrowseCiot.setFont(new Font("Tahoma", Font.PLAIN, 17));
			btnBrowseCiot.setBounds(498, 123, 115, 29);
			
			lblZone1 = new JLabel("Availability Zone1");
			lblZone1.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblZone1);
			lblZone1.setBounds(15, 304, 178, 28);
			
			txtZone1 = new JTextField();
			txtZone1.setColumns(10);
			jPanelGrantless.add(txtZone1);
			txtZone1.setBounds(224, 303, 244, 30);
			
			lblZone2 = new JLabel("Availability Zone2");
			lblZone2.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblZone2);
			lblZone2.setBounds(15, 342, 178, 26);
			
			txtZone2 = new JTextField();
			txtZone2.setColumns(10);
			jPanelGrantless.add(txtZone2);
			txtZone2.setBounds(224, 339, 244, 30);
			
			btnNext = new JButton("Next");
			jPanelGrantless.add(btnNext);
			btnNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
				 if(allFieldsAreFilled()){					 
					 String inputJsonFile = FilePath.intermediateFolderPath+FilePath.grantlessFileName;
				     String inputTextFile = FilePath.inputFolderPath+FilePath.ciotOutputFileName;
				     String[] args = {inputJsonFile,inputTextFile,txtZone1.getText(),txtZone2.getText(), txtOAMActiveIP.getText(),
				    		   txtOAMStandByIP.getText()};
				     Tools.updateGrantLess(args);
				     jTabbedPane.setSelectedIndex(1);
				 }					
				}
			});
			btnNext.setBounds(236, 448, 157, 37);
						
			lblOAMStandByIP = new JLabel("OAM Standby IP");
			lblOAMStandByIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblOAMStandByIP);
			lblOAMStandByIP.setBounds(15, 196, 178, 26);
			
			txtOAMStandByIP = new JTextField();
			txtOAMStandByIP.setColumns(10);
			jPanelGrantless.add(txtOAMStandByIP);
			txtOAMStandByIP.setBounds(224, 195, 244, 30);
			
			txtOAMActiveIP = new JTextField();
			txtOAMActiveIP.setColumns(10);
			jPanelGrantless.add(txtOAMActiveIP);
			txtOAMActiveIP.setBounds(224, 159, 244, 30);
			
			lblOAMActiveIP = new JLabel("OAM Active IP");
			lblOAMActiveIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblOAMActiveIP);
			lblOAMActiveIP.setBounds(15, 159, 178, 30);
			
			lblSelectNodeType = new JLabel("Node type");
			lblSelectNodeType.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblSelectNodeType.setBounds(15, 50, 178, 20);
			jPanelGrantless.add(lblSelectNodeType);
			
			lblSelectSolutionType = new JLabel("Solution type");
			lblSelectSolutionType.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblSelectSolutionType.setBounds(15, 88, 178, 20);
			jPanelGrantless.add(lblSelectSolutionType);
			
			comboBoxNodeType = new JComboBox<String[]>();
			comboBoxNodeType.setModel(new DefaultComboBoxModel(nodeTypes));
			comboBoxNodeType.setBounds(224, 47, 244, 30);
			jPanelGrantless.add(comboBoxNodeType);
			
			comboBoxSolutionType = new JComboBox<String[]>();
			comboBoxSolutionType.setModel(new DefaultComboBoxModel(solutionType));
			comboBoxSolutionType.setBounds(223, 85, 244, 30);
			jPanelGrantless.add(comboBoxSolutionType);
			
			lblOAMActiveVirtualIP = new JLabel("OAM Active Virtual IP");
			lblOAMActiveVirtualIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblOAMActiveVirtualIP.setBounds(15, 231, 178, 30);
			jPanelGrantless.add(lblOAMActiveVirtualIP);
			
			txtOAMActiveVirtualIP = new JTextField();
			txtOAMActiveVirtualIP.setColumns(10);
			txtOAMActiveVirtualIP.setBounds(224, 231, 244, 30);
			jPanelGrantless.add(txtOAMActiveVirtualIP);
			
			lblOAMStandByVirtualIP = new JLabel("OAM StandBy Virtual IP");
			lblOAMStandByVirtualIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblOAMStandByVirtualIP.setBounds(15, 269, 178, 30);
			jPanelGrantless.add(lblOAMStandByVirtualIP);
			
			txtOAMStandByVirtualIP = new JTextField();
			txtOAMStandByVirtualIP.setColumns(10);
			txtOAMStandByVirtualIP.setBounds(224, 268, 244, 30);
			jPanelGrantless.add(txtOAMStandByVirtualIP);
			
			lblTemplateVersion = new JLabel("Template Version");
			lblTemplateVersion.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblTemplateVersion.setBounds(15, 13, 178, 20);
			jPanelGrantless.add(lblTemplateVersion);
			
			comboBoxTemplateVersion = new JComboBox<String[]>();
			comboBoxTemplateVersion.setModel(new DefaultComboBoxModel(templateVersion));
			comboBoxTemplateVersion.setBounds(224, 11, 244, 30);
			jPanelGrantless.add(comboBoxTemplateVersion);
			manageSelection();
	 }
	 
	 private void setToscaPanelView(){
		    lblToscaYamlVersion = new JLabel("Tosca.yaml Version");
			lblToscaYamlVersion.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblToscaYamlVersion.setBounds(15, 15, 178, 30);
			jPanelTosca.add(lblToscaYamlVersion);
						
			comboBoxYamlversion = new JComboBox<String[]>();
			comboBoxYamlversion.setModel(new DefaultComboBoxModel(toscaYamlVersions));
			jPanelTosca.add(comboBoxYamlversion);
			comboBoxYamlversion.setBounds(246, 16, 244, 30);
			
			lblVnfHostName = new JLabel("VNF Host Name ");
			lblVnfHostName.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblVnfHostName.setBounds(15, 55, 178, 20);
			jPanelTosca.add(lblVnfHostName);
			
		    txtVnfHostName = new JTextField();
			txtVnfHostName.setColumns(10);
			jPanelTosca.add(txtVnfHostName);
			txtVnfHostName.setBounds(246, 53, 244, 25);
			
		    lblSystemIPAddress = new JLabel("systemIpAddr");
	        lblSystemIPAddress.setBounds(14, 88, 190, 20);
	        lblSystemIPAddress.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblSystemIPAddress);
	        
	        txtSystemIPAddress = new JTextField();
	        txtSystemIPAddress.setBounds(246, 88, 244, 25);
	        jPanelTosca.add(txtSystemIPAddress);
	        
	        lblStaticRoute = new JLabel("staticRoute");
	        lblStaticRoute.setBounds(15, 121, 152, 20);
	        lblStaticRoute.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblStaticRoute);
	        
	        txtStaticRoute = new JTextField();
	        txtStaticRoute.setBounds(247, 118, 244, 23);
	        jPanelTosca.add(txtStaticRoute);
	        
	        lblControlPlaneCores = new JLabel("controlPlaneCores");
	        lblControlPlaneCores.setBounds(15, 155, 152, 20);
	        lblControlPlaneCores.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblControlPlaneCores);
	        
	        txtControlPlaneCores = new JTextField();
	        txtControlPlaneCores.setBounds(247, 152, 243, 23);
	        jPanelTosca.add(txtControlPlaneCores);
	        
	        lblControlSwitchFabricCidr = new JLabel("controlSwitchFabricCidr");
	        lblControlSwitchFabricCidr.setBounds(15, 187, 190, 20);
	        lblControlSwitchFabricCidr.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblControlSwitchFabricCidr);
	        
	        txtControlSwitchFabricCidr = new JTextField();
	        txtControlSwitchFabricCidr.setBounds(247, 184, 244, 23);
	        jPanelTosca.add(txtControlSwitchFabricCidr);
	        
	        lblMgLbManagementCidr = new JLabel("mgLbManagementCidr");
	        lblMgLbManagementCidr.setBounds(15, 216, 190, 20);
	        lblMgLbManagementCidr.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblMgLbManagementCidr);
	        
	        txtMgLbManagementCidr = new JTextField();
	        txtMgLbManagementCidr.setBounds(247, 213, 243, 23);
	        jPanelTosca.add(txtMgLbManagementCidr);
	        
	        lblPrimaryConfigFile = new JLabel("primaryConfigFile");
	        lblPrimaryConfigFile.setBounds(15, 249, 152, 20);
	        lblPrimaryConfigFile.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblPrimaryConfigFile);
	        
	        txtPrimaryConfigFile = new JTextField();
	        txtPrimaryConfigFile.setBounds(247, 246, 243, 23);
	        jPanelTosca.add(txtPrimaryConfigFile);
	        
	        lblLicenseFile = new JLabel("licenseFile");
	        lblLicenseFile.setBounds(15, 280, 152, 20);
	        lblLicenseFile.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblLicenseFile);
	        
	        txtLicenseFile = new JTextField();
	        txtLicenseFile.setBounds(247, 277, 243, 23);
	        jPanelTosca.add(txtLicenseFile);
	        
	        lblCsfFailureDetectionTimer = new JLabel("csfFailureDetectionTimer");
	        lblCsfFailureDetectionTimer.setBounds(15, 317, 217, 20);
	        lblCsfFailureDetectionTimer.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblCsfFailureDetectionTimer);
	        
	        txtCsfFailureDetectionTimer = new JTextField();
	        txtCsfFailureDetectionTimer.setBounds(247, 313, 243, 24);
	        jPanelTosca.add(txtCsfFailureDetectionTimer); 
	        
	        btnUpdateTemplate = new JButton("UpdateTemplate");
	        btnUpdateTemplate.setBounds(163, 391, 238, 29);
	        jPanelTosca.add(btnUpdateTemplate);
	        
	        btnUpdateTemplate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(allFieldsAreFilledForTosca()){
						updateToscaFile();
				   }
				}
			});      	        
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
	         
	 private void updateToscaFile(){
	       String[] args = {txtVnfHostName.getText().toString(),txtSystemIPAddress.getText().toString(),
				   txtStaticRoute.getText().toString(),txtControlPlaneCores.getText().toString(),
				   txtControlSwitchFabricCidr.getText().toString(),txtMgLbManagementCidr.getText().toString(),
				   txtPrimaryConfigFile.getText().toString(),txtLicenseFile.getText().toString()
				   ,txtCsfFailureDetectionTimer.getText().toString()
				   ,txtOAMActiveVirtualIP.getText().toString()
				   ,txtOAMStandByVirtualIP.getText().toString()};
	       String selectedToscaVersion = "";
	       if(comboBoxYamlversion.getSelectedIndex()==0){
	    	   selectedToscaVersion = FilePath.v1;
	       }else if(comboBoxYamlversion.getSelectedIndex()==0){
	    	   selectedToscaVersion = FilePath.v2;
	       }
	       Tools.createUpdatedTemplate(args, selectedToscaVersion);
	       showDownloadMessage("Template updated successfully. Do you want to save it?","Save template");
	  }	    
	   
	  private void showDownloadMessage(String message, String title){
		   int result = JOptionPane.showConfirmDialog(null, message, title,
	               JOptionPane.YES_NO_OPTION,
	               JOptionPane.QUESTION_MESSAGE);
	            if(result == JOptionPane.YES_OPTION){
	            	openSaveDialog();
	            }else if (result == JOptionPane.NO_OPTION){
	            }else {
	            }   
	       System.out.println("Result is: "+ result);
	  }
	   
	  private void openSaveDialog(){
		   JFileChooser fileChooser = new JFileChooser();
		   FileNameExtensionFilter filter  = new FileNameExtensionFilter(null, ".zip");
		   fileChooser.setFileFilter(filter);
		   fileChooser.setSelectedFile(new File(FilePath.outputTemplateName));
		   if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
		     File destFile = fileChooser.getSelectedFile();
		     CommonUtils.copyFile(FilePath.outputFolderPath +FilePath.outputTemplateName,destFile);
		   }
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
	    	if(txtCiotPath.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please select CIOT with .txt file format");
	    		return false;
	    	}else if(txtOAMActiveIP.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter OAM Active IP");
	    		return false;
	    	}else if(txtOAMStandByIP.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter OAM StandBy IP");
	    		return false;
	    	}else if(txtOAMActiveVirtualIP.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter OAM Active Virtual IP");
	    		return false;
	    	}else if(txtOAMStandByVirtualIP.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter OAM StandBy Virtual IP");
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
	    
	    private boolean allFieldsAreFilledForTosca(){
	    	if(txtVnfHostName.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter host name");
	    		return false;
	    	}else if(txtSystemIPAddress.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter systemIpAddr");
	    		return false;
	    	}else if(txtStaticRoute.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter staticRoute");
	    		return false;
	    	}else if(txtControlPlaneCores.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter controlPlaneCores");
	    		return false;
	    	}else if(txtControlSwitchFabricCidr.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter controlSwitchFabricCidr");
	    		return false;
	    	}else if(txtMgLbManagementCidr.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter mgLbManagementCidr");
	    		return false;
	    	}else if(txtPrimaryConfigFile.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter primaryConfigFile");
	    		return false;
	    	}else if(txtLicenseFile.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter licenseFile");
	    		return false;
	    	}else if(txtCsfFailureDetectionTimer.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter csfFailureDetectionTimer");
	    		return false;
	    	}else{
	    	  return true;
	    	}
	    }
	   
	 private void manageSelection(){
	    	if(comboBoxNodeType.getSelectedIndex()==0){
	     		comboBoxSolutionType.setEnabled(false);
	     	}else{
	     		comboBoxSolutionType.setEnabled(true);
	     	}
	    	comboBoxNodeType.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent event) {
	                JComboBox<String[]> comboBox = (JComboBox<String[]>) event.getSource();
	                if(comboBox.getSelectedIndex()==0){
	                	comboBoxSolutionType.setSelectedIndex(0);
	            		comboBoxSolutionType.setEnabled(false);
	            	}else{
	            		comboBoxSolutionType.setEnabled(true);
	            	}
	            }
	        });  
	    	FilePath.setCiotOutputFilePath(comboBoxSolutionType.getSelectedItem().toString());   	
	    	comboBoxSolutionType.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent event) {
	                JComboBox<String[]> comboBox = (JComboBox<String[]>) event.getSource();
	                FilePath.setCiotOutputFilePath(comboBox.getSelectedItem().toString());
	            }
	        });  
	}
}
