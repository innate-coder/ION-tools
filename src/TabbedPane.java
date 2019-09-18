import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import constants.FilePath;
import models.CMgCbamInstantiate;
import models.DefaultValuesModel;
import models.MainModelClass;
import utils.CommonUtils;
import utils.UnZipUtility;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.UIManager;
import javax.swing.JRadioButton;

public class TabbedPane extends JFrame {
	
	/*GrantLess Panel fields*/
	private JTextField  txtCiotPath, txtOAMActiveIP, txtOAMStandByIP,
	txtOAMActiveVirtualIP, txtOAMStandByVirtualIP, txtZone1, txtZone2 ;
	private JLabel lblCmgVersion, lblTemplateVersion, lblSelectNodeType, lblSelectSolutionType,lblVnfTemplate, lblChooseCiot,
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
	private JComboBox<String[]> comboBoxCMGVersion, comboBoxTemplateVersion, comboBoxNodeType, comboBoxSolutionType, comboBoxYamlversion;
	private String[] nodeTypes = {"CPF", "UPF", "Non-Cups"};
	private String[] solutionType = {"LB_Mode", "LB_Less"};
	private String[] templateVersion = {FilePath.V1, FilePath.V2};
	private String[] toscaYamlVersions = {"Tosca.yaml V1", "Tosca.yaml V2"};
	private String[] cMGVersion = {"cMG10.0R11", "cMG11.0R1"};
	private JLabel lblNetworkType;
	private JRadioButton rdbtnNuage, rdbtnNonnuage;
	private ButtonGroup btnGroup;
		
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String currentDirectory = System.getProperty("user.dir");
				    System.out.println("The current working directory is " + currentDirectory);
					TabbedPane tp = new TabbedPane();
					tp.setTitle("Tabbed window");
					tp.setBounds(0, 0, 700, 660);
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
		    setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("earphones.png")));         
	        setTitle("Tabbed Pane");
	        jTabbedPane = new JTabbedPane();
	        jTabbedPane.setBackground(new Color(255, 255, 255));
	        jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	        jTabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        getContentPane().add(jTabbedPane);
	        jPanelGrantless = new JPanel();
	        jPanelGrantless.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelGrantless.setForeground(new Color(0, 51, 153));
	        jPanelGrantless.setLayout(null);
	        
	        jPanelTosca = new JPanel();	    
	        jPanelTosca.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.setForeground(new Color(0, 51, 153));

	        jTabbedPane.addTab("Step One", jPanelGrantless);
	        jTabbedPane.addTab("Step Two", jPanelTosca);	        	        
	        
	        jPanelTosca.setLayout(null);
	                
	        setGrantlessPanelView();
	        setToscaPanelView();
	        mapDefaultJson();
	 }
	 
	 /*Create view of GrantLess Panel*/
	 private void setGrantlessPanelView(){					
			lblChooseCiot = new JLabel("CIOT output");
			lblChooseCiot.setForeground(new Color(0, 0, 0));
			lblChooseCiot.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblChooseCiot);
			lblChooseCiot.setBounds(15, 198, 115, 22);
			
			txtCiotPath = new JTextField();
			txtCiotPath.setFont(new Font("Tahoma", Font.PLAIN, 17));
			txtCiotPath.setBackground(Color.WHITE);
			txtCiotPath.setColumns(10);
			txtCiotPath.setBounds(251, 196, 244, 30);
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
			btnBrowseCiot.setBounds(535, 197, 98, 28);
			
			lblZone1 = new JLabel("Availability Zone1");
			lblZone1.setForeground(new Color(0, 0, 0));
			lblZone1.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblZone1);
			lblZone1.setBounds(15, 388, 178, 30);
			
			txtZone1 = new JTextField();
			txtZone1.setFont(new Font("Tahoma", Font.PLAIN, 17));
			txtZone1.setBackground(Color.WHITE);
			txtZone1.setColumns(10);
			jPanelGrantless.add(txtZone1);
			txtZone1.setBounds(251, 388, 244, 30);
			
			lblZone2 = new JLabel("Availability Zone2");
			lblZone2.setForeground(new Color(0, 0, 0));
			lblZone2.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblZone2);
			lblZone2.setBounds(15, 427, 178, 30);
			
			txtZone2 = new JTextField();
			txtZone2.setFont(new Font("Tahoma", Font.PLAIN, 17));
			txtZone2.setBackground(Color.WHITE);
			txtZone2.setColumns(10);
			jPanelGrantless.add(txtZone2);
			txtZone2.setBounds(251, 428, 244, 30);
			
			btnNext = new JButton("Next");
			jPanelGrantless.add(btnNext);
			btnNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
				 if(allFieldsAreFilled()){	
					 String inputJsonFile = "";
					 if(comboBoxTemplateVersion.getSelectedIndex()==0){
						 inputJsonFile = FilePath.intermediateFolderPath+FilePath.grantlessFileName;
				     }else{
				    	  inputJsonFile = FilePath.intermediateFolderPath+FilePath.instantiateFileName;
				     }
					 String inputTextFile = FilePath.inputFolderPath+FilePath.ciotOutputFileName;
				     String[] args = {inputJsonFile,inputTextFile,txtZone1.getText(),txtZone2.getText(), txtOAMActiveIP.getText(),
				    		   txtOAMStandByIP.getText()};
				     if(comboBoxTemplateVersion.getSelectedIndex()==0){
				    	 Version10TemplateUpdateController.updateGrantLess(args); 
				     }else{
				    	 Version11TemplateUpdateController.updateCbamInstantiate(args);
				     }				     
				     jTabbedPane.setSelectedIndex(1);
				 }					
				}
			});
			btnNext.setBounds(236, 491, 157, 30);
						
			lblOAMStandByIP = new JLabel("OAM Standby IP");
			lblOAMStandByIP.setForeground(new Color(0, 0, 0));
			lblOAMStandByIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblOAMStandByIP);
			lblOAMStandByIP.setBounds(15, 269, 178, 30);
			
			txtOAMStandByIP = new JTextField();
			txtOAMStandByIP.setFont(new Font("Tahoma", Font.PLAIN, 17));
			txtOAMStandByIP.setBackground(Color.WHITE);
			txtOAMStandByIP.setColumns(10);
			jPanelGrantless.add(txtOAMStandByIP);
			txtOAMStandByIP.setBounds(250, 270, 244, 30);
			
			txtOAMActiveIP = new JTextField();
			txtOAMActiveIP.setFont(new Font("Tahoma", Font.PLAIN, 17));
			txtOAMActiveIP.setBackground(Color.WHITE);
			txtOAMActiveIP.setToolTipText("Please enter text in 192.168.0.9 format");
			txtOAMActiveIP.setColumns(10);
			jPanelGrantless.add(txtOAMActiveIP);
			txtOAMActiveIP.setBounds(250, 232, 244, 30);
			
			lblOAMActiveIP = new JLabel("OAM Active IP");
			lblOAMActiveIP.setForeground(new Color(0, 0, 0));
			lblOAMActiveIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
			jPanelGrantless.add(lblOAMActiveIP);
			lblOAMActiveIP.setBounds(15, 228, 178, 30);
			
			lblSelectNodeType = new JLabel("Node type");
			lblSelectNodeType.setForeground(new Color(0, 0, 0));
			lblSelectNodeType.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblSelectNodeType.setBounds(15, 90, 178, 30);
			jPanelGrantless.add(lblSelectNodeType);
			
			lblSelectSolutionType = new JLabel("Solution type");
			lblSelectSolutionType.setForeground(new Color(0, 0, 0));
			lblSelectSolutionType.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblSelectSolutionType.setBounds(15, 123, 178, 30);
			jPanelGrantless.add(lblSelectSolutionType);
			
			comboBoxNodeType = new JComboBox<String[]>();
			comboBoxNodeType.setFont(new Font("Tahoma", Font.PLAIN, 17));
			comboBoxNodeType.setModel(new DefaultComboBoxModel(nodeTypes));
			comboBoxNodeType.setBounds(253, 91, 244, 30);
			jPanelGrantless.add(comboBoxNodeType);
			
			comboBoxSolutionType = new JComboBox<String[]>();
			comboBoxSolutionType.setFont(new Font("Tahoma", Font.PLAIN, 17));
			comboBoxSolutionType.setModel(new DefaultComboBoxModel(solutionType));
			comboBoxSolutionType.setBounds(252, 127, 244, 30);
			jPanelGrantless.add(comboBoxSolutionType);
			
			lblOAMActiveVirtualIP = new JLabel("OAM Active Virtual IP");
			lblOAMActiveVirtualIP.setForeground(new Color(0, 0, 0));
			lblOAMActiveVirtualIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblOAMActiveVirtualIP.setBounds(15, 309, 178, 30);
			jPanelGrantless.add(lblOAMActiveVirtualIP);
			
			txtOAMActiveVirtualIP = new JTextField();
			txtOAMActiveVirtualIP.setFont(new Font("Tahoma", Font.PLAIN, 17));
			txtOAMActiveVirtualIP.setBackground(Color.WHITE);
			txtOAMActiveVirtualIP.setColumns(10);
			txtOAMActiveVirtualIP.setBounds(251, 308, 244, 30);
			jPanelGrantless.add(txtOAMActiveVirtualIP);
			
			lblOAMStandByVirtualIP = new JLabel("OAM StandBy Virtual IP");
			lblOAMStandByVirtualIP.setForeground(new Color(0, 0, 0));
			lblOAMStandByVirtualIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblOAMStandByVirtualIP.setBounds(15, 350, 221, 30);
			jPanelGrantless.add(lblOAMStandByVirtualIP);
			
			txtOAMStandByVirtualIP = new JTextField();
			txtOAMStandByVirtualIP.setFont(new Font("Tahoma", Font.PLAIN, 17));
			txtOAMStandByVirtualIP.setBackground(Color.WHITE);
			txtOAMStandByVirtualIP.setColumns(10);
			txtOAMStandByVirtualIP.setBounds(251, 348, 244, 30);
			jPanelGrantless.add(txtOAMStandByVirtualIP);
			
			lblTemplateVersion = new JLabel("Template Version");
			lblTemplateVersion.setForeground(new Color(0, 0, 0));
			lblTemplateVersion.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblTemplateVersion.setBounds(15, 55, 178, 30);
			jPanelGrantless.add(lblTemplateVersion);
			
			comboBoxTemplateVersion = new JComboBox<String[]>();
			comboBoxTemplateVersion.setFont(new Font("Tahoma", Font.PLAIN, 17));
			comboBoxTemplateVersion.setModel(new DefaultComboBoxModel(templateVersion));
			comboBoxTemplateVersion.setBounds(252, 53, 244, 30);
			jPanelGrantless.add(comboBoxTemplateVersion);
			
			lblNetworkType = new JLabel("Network Type");
			lblNetworkType.setForeground(Color.BLACK);
			lblNetworkType.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblNetworkType.setBounds(13, 160, 178, 30);
			jPanelGrantless.add(lblNetworkType);
			
			rdbtnNuage = new JRadioButton("Nuage");
			rdbtnNuage.setFont(new Font("Tahoma", Font.PLAIN, 17));
			rdbtnNuage.setBounds(250, 162, 98, 29);
			jPanelGrantless.add(rdbtnNuage);
			
			rdbtnNonnuage = new JRadioButton("Non-Nuage");
			rdbtnNonnuage.setFont(new Font("Tahoma", Font.PLAIN, 17));
			rdbtnNonnuage.setBounds(363, 162, 155, 29);
			jPanelGrantless.add(rdbtnNonnuage);
			
			btnGroup = new ButtonGroup();
			btnGroup.add(rdbtnNuage);
			btnGroup.add(rdbtnNonnuage);
			rdbtnNuage.setSelected(true);
			
			lblCmgVersion = new JLabel("cMG Version");
			lblCmgVersion.setForeground(Color.BLACK);
			lblCmgVersion.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblCmgVersion.setBounds(14, 17, 178, 30);
			jPanelGrantless.add(lblCmgVersion);
			
			comboBoxCMGVersion = new JComboBox<String[]>();
			comboBoxCMGVersion.setFont(new Font("Tahoma", Font.PLAIN, 17));
			comboBoxCMGVersion.setBounds(251, 15, 244, 30);
			comboBoxCMGVersion.setModel(new DefaultComboBoxModel(cMGVersion));
			jPanelGrantless.add(comboBoxCMGVersion);
			manageSelection();
	 }
	 /*Create view of TOSCA Panel*/
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
			lblVnfHostName.setBounds(15, 55, 178, 30);
			jPanelTosca.add(lblVnfHostName);
			
		    txtVnfHostName = new JTextField();
			txtVnfHostName.setColumns(10);
			jPanelTosca.add(txtVnfHostName);
			txtVnfHostName.setBounds(246, 53, 244, 30);
			
		    lblSystemIPAddress = new JLabel("systemIpAddr");
	        lblSystemIPAddress.setBounds(14, 90, 190, 30);
	        lblSystemIPAddress.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblSystemIPAddress);
	        
	        txtSystemIPAddress = new JTextField();
	        txtSystemIPAddress.setBounds(246, 90, 244, 30);
	        jPanelTosca.add(txtSystemIPAddress);
	        
	        lblStaticRoute = new JLabel("staticRoute");
	        lblStaticRoute.setBounds(15, 133, 152, 30);
	        lblStaticRoute.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblStaticRoute);
	        
	        txtStaticRoute = new JTextField();
	        txtStaticRoute.setBounds(247, 130, 244, 30);
	        jPanelTosca.add(txtStaticRoute);
	        
	        lblControlPlaneCores = new JLabel("controlPlaneCores");
	        lblControlPlaneCores.setBounds(15, 169, 152, 30);
	        lblControlPlaneCores.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblControlPlaneCores);
	        
	        txtControlPlaneCores = new JTextField();
	        txtControlPlaneCores.setBounds(247, 166, 243, 30);
	        jPanelTosca.add(txtControlPlaneCores);
	        
	        lblControlSwitchFabricCidr = new JLabel("controlSwitchFabricCidr");
	        lblControlSwitchFabricCidr.setBounds(15, 205, 190, 30);
	        lblControlSwitchFabricCidr.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblControlSwitchFabricCidr);
	        
	        txtControlSwitchFabricCidr = new JTextField();
	        txtControlSwitchFabricCidr.setBounds(247, 204, 244, 30);
	        jPanelTosca.add(txtControlSwitchFabricCidr);
	        
	        lblMgLbManagementCidr = new JLabel("mgLbManagementCidr");
	        lblMgLbManagementCidr.setBounds(15, 244, 190, 30);
	        lblMgLbManagementCidr.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblMgLbManagementCidr);
	        
	        txtMgLbManagementCidr = new JTextField();
	        txtMgLbManagementCidr.setBounds(247, 241, 243, 30);
	        jPanelTosca.add(txtMgLbManagementCidr);
	        
	        lblPrimaryConfigFile = new JLabel("primaryConfigFile");
	        lblPrimaryConfigFile.setBounds(15, 281, 152, 30);
	        lblPrimaryConfigFile.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblPrimaryConfigFile);
	        
	        txtPrimaryConfigFile = new JTextField();
	        txtPrimaryConfigFile.setBounds(247, 278, 243, 30);
	        jPanelTosca.add(txtPrimaryConfigFile);
	        
	        lblLicenseFile = new JLabel("licenseFile");
	        lblLicenseFile.setBounds(15, 318, 152, 30);
	        lblLicenseFile.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblLicenseFile);
	        
	        txtLicenseFile = new JTextField();
	        txtLicenseFile.setBounds(247, 315, 243, 30);
	        jPanelTosca.add(txtLicenseFile);
	        
	        lblCsfFailureDetectionTimer = new JLabel("csfFailureDetectionTimer");
	        lblCsfFailureDetectionTimer.setBounds(15, 355, 217, 30);
	        lblCsfFailureDetectionTimer.setFont(new Font("Tahoma", Font.PLAIN, 18));
	        jPanelTosca.add(lblCsfFailureDetectionTimer);
	        
	        txtCsfFailureDetectionTimer = new JTextField();
	        txtCsfFailureDetectionTimer.setBounds(247, 351, 243, 30);
	        jPanelTosca.add(txtCsfFailureDetectionTimer); 
	        
	        btnUpdateTemplate = new JButton("UpdateTemplate");
	        btnUpdateTemplate.setBounds(163, 417, 238, 30);
	        jPanelTosca.add(btnUpdateTemplate);
	        
	        btnUpdateTemplate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(allFieldsAreFilledForTosca()){
						updateToscaFile();
				   }
				}
			});      	        
	 } 
		 
	 /*Open dialog for selecting Files*/
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
	         
	 /**/
	 private void updateToscaFile(){
	       String[] args = {txtVnfHostName.getText().toString(),txtSystemIPAddress.getText().toString(),
				   txtStaticRoute.getText().toString(),txtControlPlaneCores.getText().toString(),
				   txtControlSwitchFabricCidr.getText().toString(),txtMgLbManagementCidr.getText().toString(),
				   txtPrimaryConfigFile.getText().toString(),txtLicenseFile.getText().toString()
				   ,txtCsfFailureDetectionTimer.getText().toString()
				   ,txtOAMActiveVirtualIP.getText().toString()
				   ,txtOAMStandByVirtualIP.getText().toString()};
	       String selectedToscaVersion = "";
	       if(comboBoxSolutionType.getSelectedIndex()==0){
	    	   if(comboBoxYamlversion.getSelectedIndex()==0){
		    	   selectedToscaVersion = FilePath.LB_Mode_v1;
		       }else if(comboBoxYamlversion.getSelectedIndex()==1){
		    	   selectedToscaVersion = FilePath.LB_Mode_v2;
		       }
	       }else{
	    	   if(comboBoxYamlversion.getSelectedIndex()==0){
		    	   selectedToscaVersion = FilePath.LB_Less_v1;
		       }else if(comboBoxYamlversion.getSelectedIndex()==1){
		    	   selectedToscaVersion = FilePath.LB_Less_v2;
		       }
	       }	
	       if(comboBoxTemplateVersion.getSelectedIndex()==0){
	    	   Version10TemplateUpdateController.createUpdatedTemplate(args, selectedToscaVersion);
		     }else{
		       Version11TemplateUpdateController.createUpdatedTemplate(args, selectedToscaVersion);
		     }
	       
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
	    	System.out.println("Source File: "+source.getPath());
	    	System.out.println("Destination File: "+dest.getPath());
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
	    	}else if(!CommonUtils.isValidInet4Address(txtOAMActiveIP.getText().toString())){
	    		CommonUtils.showAlertMsg("Please enter Valid OAM Active IP");
	    		return false;
	    	}else if(txtOAMStandByIP.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter OAM StandBy IP");
	    		return false;
	    	}else if(!CommonUtils.isValidInet4Address(txtOAMStandByIP.getText().toString())){
	    		CommonUtils.showAlertMsg("Please enter Valid OAM StandBy IP");
	    		return false;
	    	}else if(txtOAMActiveVirtualIP.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter OAM Active Virtual IP");
	    		return false;
	    	}else if(!CommonUtils.isValidInet4Address(txtOAMActiveVirtualIP.getText().toString())){
	    		CommonUtils.showAlertMsg("Please enter Valid OAM Active Virtual IP");
	    		return false;
	    	}else if(txtOAMStandByVirtualIP.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter OAM StandBy Virtual IP");
	    		return false;
	    	}else if(!CommonUtils.isValidInet4Address(txtOAMStandByVirtualIP.getText().toString())){
	    		CommonUtils.showAlertMsg("Please enter Valid OAM StandBy Virtual IP");
	    		return false;
	    	}else if(txtZone1.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter Zone1 resource ID");
	    		return false;
	    	}/*else if(!CommonUtils.isSpaceAvailable(txtZone1.getText().toString())){
	    		CommonUtils.showAlertMsg("Please enter valid Zone1 resource ID");
	    		return false;
	    	}*/else if(txtZone2.getText().toString().isEmpty() ){
	    		CommonUtils.showAlertMsg("Please enter Zone2 resource ID");
	    		return false;
	    	}/*else if(!CommonUtils.isSpaceAvailable(txtZone2.getText().toString())){
	    		CommonUtils.showAlertMsg("Please enter valid Zone2 resource ID");
	    		return false;
	    	}*/else{
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
	    	}else if(!CommonUtils.isValidInet4Address(txtSystemIPAddress.getText().toString())){
	    		CommonUtils.showAlertMsg("Please enter Valid systemIpAddr");
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
	                mapDefaultJson();
	            }
	        });  
	}
	 
	private void setDefaultValues(DefaultValuesModel model){
		txtZone1.setText(model.getZoneId1());
		txtZone2.setText(model.getZoneId2());
		txtControlPlaneCores.setText(model.getPlanecores());
		txtCsfFailureDetectionTimer.setText(model.getCsfFailureDetectionTimer());
		txtPrimaryConfigFile.setText(model.getPrimaryConfigFile());
		txtLicenseFile.setText(model.getLicenseFile());
		txtControlSwitchFabricCidr.setText(model.getControlSwitchFabricCidr());
		txtMgLbManagementCidr.setText(model.getMgLbManagementCidr());
	}
	
	private void mapDefaultJson(){
		  String filePath;
		  if(comboBoxSolutionType.getSelectedIndex()==0/* && comboBoxSolutionType.getSelectedIndex()==1*/){
			 filePath = FilePath.defaultvalues+FilePath.lbMode+FilePath.pathDivider+FilePath.defaultVILFile;
		  }else{
			 filePath = FilePath.defaultvalues+FilePath.lbLess+FilePath.pathDivider+FilePath.defaultVILFile;
		  }
		  ObjectMapper mapper = new ObjectMapper();
	      try{
	            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	            File file = new File(filePath);
	            DefaultValuesModel model = mapper.readValue(file, DefaultValuesModel.class);	  
	            setDefaultValues(model);
	        } catch (Exception e) {
	            e.printStackTrace();
	      }
	}
}
