import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import constants.FilePath;

public class ReadAndWriteFile {
	
	public static List<String> readFile() throws IOException {
        try(
        	BufferedReader br = new BufferedReader(new FileReader(FilePath.toscaFolder+FilePath.v1))){
            List<String> listOfData = new ArrayList<>();
            String d;
            while((d = br.readLine()) != null){
                listOfData.add(d);
            }
            return listOfData;
        }
    }

    public static void writeFile(List<String> listOfData) throws IOException{
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FilePath.inputFolderPath+
        		FilePath.outputTextFile))){
            for(String str: listOfData){
                bw.write(str);
                bw.newLine();
            }
        }
    }
    
    public static void readAndWrite() throws FileNotFoundException{
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

    public static void main(String[] args) throws IOException {
//        List<String> data = readFile();
//        writeFile(data);
//    	updateTosca();
    	readAndWrite();
    }
   
}
