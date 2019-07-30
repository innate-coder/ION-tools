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
import org.apache.commons.lang.StringUtils;

import constants.FilePath;
import utils.CommonUtils;
import utils.UnZipUtility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HomeScreen {

	private JFrame frmMainWindow;
	private JTextField txtVnfHostName, txtTemplatePath, txtCiotPath, txtOAMPrimaryIP, txtOAMSecondaryIP, txtZone1, txtZone2 ;
	private JLabel lblSelectNodeType, lblSelectSolutionType,lblVnfHostName, lblVnfTemplate, lblChooseCiot, lblOAMPrimaryIP, lblOAMSecondaryIP, lblZone1, lblZone2,lblToscaYamlVersion;
	private JButton btnBrowseTemplate, btnBrowseCiot, btnSubmit;
	private JComboBox<String[]> comboBoxNodeType, comboBoxSolutionType, comboBoxYamlversion;
	private String[] nodeTypes = {"CPF", "UPF", "Non-Cups"};
	private String[] solutionType = {"LB_Mode", "LB_Less"};
	private String[] templateVersion = {"V_10.7.4", "V_10.6.4"};
	private String[] toscaYamlVersions = {"Tosca.yaml V1", "Tosca.yaml V2"};
	private String toscaFilePath = FilePath.toscaFolder+FilePath.v1;
	

	/**
	 * 
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeScreen window = new HomeScreen();
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
	public HomeScreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMainWindow = new JFrame();
		frmMainWindow.setTitle("Main window");
		frmMainWindow.setBounds(0, 0, 650, 600);
		frmMainWindow.setLocationRelativeTo(null);
		frmMainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMainWindow.getContentPane().setLayout(null);
		
		lblVnfTemplate = new JLabel("VNF template ");
		lblVnfTemplate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblVnfTemplate.setBounds(15, 132, 178, 20);
		frmMainWindow.getContentPane().add(lblVnfTemplate);
		
		txtTemplatePath = new JTextField();
		txtTemplatePath.setBounds(224, 128, 244, 30);
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
		btnBrowseTemplate.setBounds(498, 128, 115, 29);
		frmMainWindow.getContentPane().add(btnBrowseTemplate);
		
		lblChooseCiot = new JLabel("CIOT output");
		lblChooseCiot.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblChooseCiot.setBounds(15, 180, 178, 20);
		frmMainWindow.getContentPane().add(lblChooseCiot);
		
		txtCiotPath = new JTextField();
		txtCiotPath.setColumns(10);
		txtCiotPath.setBounds(224, 177, 244, 30);
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
		btnBrowseCiot.setBounds(498, 176, 115, 29);
		frmMainWindow.getContentPane().add(btnBrowseCiot);
		
		lblZone1 = new JLabel("Availability Zone1");
		lblZone1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblZone1.setBounds(15, 303, 178, 28);
		frmMainWindow.getContentPane().add(lblZone1);
		
		txtZone1 = new JTextField();
		txtZone1.setColumns(10);
		txtZone1.setBounds(224, 303, 244, 30);
		frmMainWindow.getContentPane().add(txtZone1);
		
		lblZone2 = new JLabel("Availability Zone2");
		lblZone2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblZone2.setBounds(15, 347, 178, 26);
		frmMainWindow.getContentPane().add(lblZone2);
		
		txtZone2 = new JTextField();
		txtZone2.setColumns(10);
		txtZone2.setBounds(224, 343, 244, 30);
		frmMainWindow.getContentPane().add(txtZone2);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(allFieldsAreFilled()){
					showAlertMsg("Template updated successfully. Do you want to save it?");
				}
			}
		});
		btnSubmit.setBounds(236, 466, 157, 37);
		frmMainWindow.getContentPane().add(btnSubmit);
		
		lblToscaYamlVersion = new JLabel("Tosca.yaml Version");
		lblToscaYamlVersion.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblToscaYamlVersion.setBounds(15, 381, 178, 30);
		frmMainWindow.getContentPane().add(lblToscaYamlVersion);
		
		comboBoxYamlversion = new JComboBox<String[]>();
		comboBoxYamlversion.setModel(new DefaultComboBoxModel(toscaYamlVersions));
		comboBoxYamlversion.setBounds(224, 387, 244, 30);
		frmMainWindow.getContentPane().add(comboBoxYamlversion);
		
		lblOAMSecondaryIP = new JLabel("OAM Standby IP");
		lblOAMSecondaryIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblOAMSecondaryIP.setBounds(15, 263, 178, 26);
		frmMainWindow.getContentPane().add(lblOAMSecondaryIP);
		
		txtOAMSecondaryIP = new JTextField();
		txtOAMSecondaryIP.setColumns(10);
		txtOAMSecondaryIP.setBounds(224, 263, 244, 30);
		frmMainWindow.getContentPane().add(txtOAMSecondaryIP);
		
		txtOAMPrimaryIP = new JTextField();
		txtOAMPrimaryIP.setColumns(10);
		txtOAMPrimaryIP.setBounds(224, 217, 244, 30);
		frmMainWindow.getContentPane().add(txtOAMPrimaryIP);
		
		lblOAMPrimaryIP = new JLabel("OAM Active IP");
		lblOAMPrimaryIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblOAMPrimaryIP.setBounds(15, 217, 178, 30);
		frmMainWindow.getContentPane().add(lblOAMPrimaryIP);
		
		lblSelectNodeType = new JLabel("Node type");
		lblSelectNodeType.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblSelectNodeType.setBounds(15, 15, 178, 20);
		frmMainWindow.getContentPane().add(lblSelectNodeType);
		
		lblSelectSolutionType = new JLabel("Solution type");
		lblSelectSolutionType.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblSelectSolutionType.setBounds(15, 51, 178, 20);
		frmMainWindow.getContentPane().add(lblSelectSolutionType);
		
		comboBoxNodeType = new JComboBox<String[]>();
		comboBoxNodeType.setModel(new DefaultComboBoxModel(nodeTypes));
		comboBoxNodeType.setBounds(224, 10, 244, 30);
		frmMainWindow.getContentPane().add(comboBoxNodeType);
		
		comboBoxSolutionType = new JComboBox<String[]>();
		comboBoxSolutionType.setModel(new DefaultComboBoxModel(solutionType));
		comboBoxSolutionType.setBounds(224, 49, 244, 30);
		frmMainWindow.getContentPane().add(comboBoxSolutionType);
		
		lblVnfHostName = new JLabel("VNF Host Name ");
		lblVnfHostName.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblVnfHostName.setBounds(15, 91, 178, 20);
		frmMainWindow.getContentPane().add(lblVnfHostName);
		
		txtVnfHostName = new JTextField();
		txtVnfHostName.setColumns(10);
		txtVnfHostName.setBounds(224, 87, 244, 30);
		frmMainWindow.getContentPane().add(txtVnfHostName);
		manageSelection();
		selectToscaFile();
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
       String[] args = {inputJsonFile,inputTextFile,txtZone1.getText(),txtZone2.getText(), txtOAMPrimaryIP.getText(),
    		   txtOAMSecondaryIP.getText()};
       Tools.updateGrantLess(args);
       showDownloadMessage(message,"Save template");
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
	   fileChooser.setSelectedFile(new File(FilePath.templatePath));
	   if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	     File destFile = fileChooser.getSelectedFile();
	     CommonUtils.copyFile(FilePath.outputFolderPath +FilePath.templatePath,destFile);
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
    	if(txtVnfHostName.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please enter host name");
    		return false;
    	}if(txtTemplatePath.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please select template with .zip file format");
    		return false;
    	}else if(txtCiotPath.getText().toString().isEmpty() ){
    		CommonUtils.showAlertMsg("Please select CIOT with .txt file format");
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
//    	FilePath.setTemplatePath(comboBoxSolutionType.getSelectedItem().toString());   	
    	comboBoxSolutionType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JComboBox<String[]> comboBox = (JComboBox<String[]>) event.getSource();
//                FilePath.setTemplatePath(comboBox.getSelectedItem().toString());
            }
        });  
    }
        
    private void selectToscaFile(){    	
    	comboBoxSolutionType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JComboBox<String[]> comboBox = (JComboBox<String[]>) event.getSource();
                if(comboBox.getSelectedIndex()==0){
                	toscaFilePath = FilePath.toscaFolder+FilePath.v1;
            	}else if(comboBox.getSelectedIndex()==1){
            		toscaFilePath = FilePath.toscaFolder+FilePath.v2;
            	}    
            }            
        });    		
    	System.out.println("TOSCA Version is : "+toscaFilePath);
    }
       
    public static void readAndWriteToscaFile() throws FileNotFoundException{
    	try{
    	    BufferedReader br = new BufferedReader(new FileReader(FilePath.toscaFolder+FilePath.v1));
    	    String str;
    	    try(BufferedWriter bw = new BufferedWriter(new FileWriter(FilePath.inputFolderPath+
            		FilePath.outputTextFile))){
    	    while((str = br.readLine()) != null){
    	    		        System.out.println("str is : "+str);
    	                	bw.write(str);
    	                	bw.newLine();
    	                	if(str.contains("vnfHostname")) {   
    	  		    		   String vnfHostName = br.readLine();
    	  		    		   if(StringUtils.substringsBetween(vnfHostName , "\"", "\"").length>0){
    	  		    			 String hostName = StringUtils.substringsBetween(vnfHostName , "\"","\"")[0];
    	  		    			 vnfHostName = vnfHostName.replace(hostName, "HostName"); 	
    	  		    			 bw.write(vnfHostName);
        	                	 bw.newLine();
    	  		    		   }	    		 
    	  		    	   }else if(str.contains("vnfHostname")){
    	  		    		   
    	  		    	   }                   
    	                }
    	            }
//    	    }       
    	   
    	}catch(Exception ex){
    		
    	}    
    }

    
}
