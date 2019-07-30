package utils;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import constants.ModelClassConstants;
import models.CMgCbamGrantless;
import models.CMgCbamModifyVnfd;
import models.MainModelClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CommonUtils {
	
	 public static String removeExtensionFromFileName(String fileName) {
	        if (fileName.indexOf(".") > 0)
	            fileName = fileName.substring(0, fileName.lastIndexOf("."));
	        return fileName;
	 }
	 
	 public static MainModelClass selectSpecificModelClass(String fileName) {
	        switch (fileName) {
	            case ModelClassConstants.CMgCbamModifyVnfd:
	                return new CMgCbamModifyVnfd();
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
	
  public static void copyFile(String sourceFile, File destFile){
	    	File source = new File(sourceFile);
	    	try {
	    	    FileUtils.copyFile(source, destFile);	    	    
	 	   } catch (IOException e) {
	 	    e.printStackTrace();
	      }
  }
	
}

