package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import constants.ModelClassConstants;
import models.CMgCbamGrantless;
import models.CMgCbamInstantiate;
import models.MainModelClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CommonUtils {
	
	private static final String IPv4_REGEX =
			"^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
			"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
			"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
			"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	
	private static final Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);

	
	 public static String removeExtensionFromFileName(String fileName) {
	        if (fileName.indexOf(".") > 0)
	            fileName = fileName.substring(0, fileName.lastIndexOf("."));
	        return fileName;
	 }
	 
	 public static MainModelClass selectSpecificModelClass(String fileName) {
	        switch (fileName) {
	            case ModelClassConstants.CMgCbamInstantiate:
	                return new CMgCbamInstantiate();
	            case ModelClassConstants.CMgCbamGrantless:
	                return new CMgCbamGrantless();
	            default:
	                return null;
	        }
	}
	 
	public static void showAlertMsg(String message){
	   	JOptionPane.showMessageDialog(null, message);
	}
	
	public static void writeOutputFile(String outputFilePath, MainModelClass modelClass) {
		if(outputFilePath!=null & !outputFilePath.isEmpty()){
	        ObjectMapper mapper = new ObjectMapper();
	        try {
	        	mapper.enable(SerializationFeature.INDENT_OUTPUT);
	            mapper.writeValue(new File(outputFilePath+modelClass.getClass().getSimpleName()+".json"), modelClass);
	        } catch (IOException e) {
	        	System.out.println("Got exception");
	            e.printStackTrace();
	        }
		}
	}
	
  public static boolean isValidInet4Address(String ip) {
		if (ip == null) {
			return false;
		}

		Matcher matcher = IPv4_PATTERN.matcher(ip);
     	System.out.println("Value is : "+ip +" and it's "+ matcher.matches());
		return matcher.matches();
  }
    
  public static boolean isSpaceAvailable(String value){
	  String regex = "^[a-zA-Z0-9]+$";	  
	  Pattern pattern = Pattern.compile(regex);
	  Matcher matcher = pattern.matcher(value);
	  System.out.println(matcher.matches());
	  return matcher.matches();
  }

  public static String getSecondIndexValue(String value){
	  return value.split(" ")[1];
  }
  
  public static String getNextIncrementedIP(String cidr){
	  String value = "";
	  int lastDigit = 0;
	  String ip = cidr.split("/")[0];
	  System.out.println("Initial IP is : "+ip);
	  if (ip.split("\\.").length == 4) {
		  lastDigit = Integer.parseInt(ip.split("\\.")[3]);
          value = ip.substring(0, ip.length() - String.valueOf(lastDigit).length());
      }
	  return (value+(++lastDigit));
  }
  
  public static String getNextIncrementedIPByN(String initialIP, int number){
	  String value = "";
	  int lastDigit = 0;
	  String ip = initialIP.split("/")[0];
	  System.out.println("Initial IP is : "+ip);
	  if (ip.split("\\.").length == 4) {
		  lastDigit = Integer.parseInt(ip.split("\\.")[3]);
          value = ip.substring(0, ip.length() - String.valueOf(lastDigit).length());
      }
	  for(int index = 0; index<number; index++){
		  lastDigit++;
	  }
	  System.out.println("Last digit of IP is: "+lastDigit);
	  return (value+(lastDigit));
  }
  
  public static void cleanDirectory(File directory){
	try {
		FileUtils.cleanDirectory(directory);
	} catch (IOException e) {
		e.printStackTrace();
	} 
  }
  
  public static void skipLines(Scanner s, int lineNum) {
      for (int i = 0; i < lineNum; i++) {
          if (s.hasNextLine()) s.nextLine();
      }
  }
  
 public static void copyFile(String sourceFile, File destFile){
     File source = new File(sourceFile);
     try {
    	FileUtils.copyFile(source, destFile);	    	    
 	 } catch (IOException e) {
 	    e.printStackTrace();
     }
 }  

 public static void copyFile(String sourceFile, String destinationFilePath){
 	File source = new File(sourceFile);	    	
 	File destinationFile = new File(destinationFilePath);
 	System.out.println("Source File: "+source.getPath());
 	System.out.println("Destination File: "+destinationFile.getPath());
 	try {
 	    FileUtils.copyDirectory(source, destinationFile);
 	} catch (IOException e) {
 	    e.printStackTrace();
 	}
 }
 
 public static void copyFileInDirectory(String sourceFile, String destinationFilePath){
	 	File source = new File(sourceFile);	    	
	 	File destinationFile = new File(destinationFilePath);
	 	System.out.println("Source File: "+source.getPath());
	 	System.out.println("Destination File: "+destinationFile.getPath());
	 	try {
	 	    FileUtils.copyDirectory(source, destinationFile);
	 	} catch (IOException e) {
	 	    e.printStackTrace();
	 	}
 }
 
 public static void copyFileWithNewName(String sourceFile, String destinationFilePath){
  	File source = new File(sourceFile);	    	
  	File destinationFile = new File(destinationFilePath);
  	System.out.println("Source File: "+source.getPath());
  	System.out.println("Destination File: "+destinationFile.getPath());
  	try {
  	    FileUtils.copyFile(source, destinationFile);
  	} catch (IOException e) {
  	    e.printStackTrace();
  	}
 }
  
 public static String showPopUpForMultipleOptions(ArrayList<String> options, String message){
	  String[] items = options.toArray(new String[options.size()]); 
	  String s = (String) JOptionPane.showInputDialog(
	          null,
	          "Please select "+message,
	          "Select One",
	          JOptionPane.PLAIN_MESSAGE,
	          null,
	          items,
	          options.get(0));
	  return s;
  }
}

