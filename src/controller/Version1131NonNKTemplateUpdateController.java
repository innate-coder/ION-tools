package controller;

import models.CMgCbamInstantiate;
import models.MainModelClass;
import utils.CommonUtils;
import utils.ZipUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import constants.FilePath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

public class Version1131NonNKTemplateUpdateController {
	
	private static String inputJsonFile, inputTextFile, zone1, zone2, oamPrimaryIP = "", oamSecondaryIP= "";
	
	private static LinkedHashMap<String, ArrayList<String>> ciotValues;
    
    public static void updateCbamInstantiate(String[] args){
    	try {
        	if(args!=null && args.length>0){
        		 inputJsonFile = args[0];
        		 inputTextFile = args[1];
        		 zone1 = args[2];
        		 zone2 = args[3];
        		 oamPrimaryIP = args[4];
        		 oamSecondaryIP = args[5];
        		 System.out.println("inputJsonFile "+inputJsonFile);
        		 System.out.println("inputTextFile "+inputTextFile);
        		 readCiotOutputFile();
        		 mapJSONToObject(inputJsonFile);   		 
         	}         
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
  
    public static void readCiotOutputFile() throws FileNotFoundException {
        ciotValues = new LinkedHashMap<>();
        File file = new File(inputTextFile);
        Scanner sc = new Scanner(file);
        String key = "";
        while (sc.hasNextLine()) {
            String content = sc.nextLine();
            if (!content.contains("Invalid")) {
                if (content.contains("START")) {
                    ArrayList<String> values = new ArrayList<>();
                    content = sc.nextLine();
                    while (!content.contains("END")) {
                        if (!content.contains("Invalid")) {
                            values.add(content);
                        }
                        if (sc.hasNext()) {
                            content = sc.nextLine();
                        }
                    }
                    if (content.contains("END")) {
                        ciotValues.put(key, values);
                    }
                } else {
                    key = content.replace(":", "");
                }
            }
        }
        System.out.println("Values from CIOT : \n" +ciotValues);       
    }
    
    public static void mapJSONToObject(String jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            File file = new File(jsonFilePath);
            String fileName = CommonUtils.removeExtensionFromFileName(file.getName());
            MainModelClass model = CommonUtils.selectSpecificModelClass(fileName);
            if (model != null) {
                if (model instanceof CMgCbamInstantiate) {
                    model = mapper.readValue(file, CMgCbamInstantiate.class);
                    System.out.println("Original value is : " + new Gson().toJson(model));
                    populateUserInputInModel((CMgCbamInstantiate) model);
                    updateCIOTDataInFile((CMgCbamInstantiate) model);                    
                    CommonUtils.writeOutputFile(FilePath.intermediateFolderPath, model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }       
    
    private static void updateCIOTDataInFile(CMgCbamInstantiate model){
        if(ciotValues.containsKey("CPF_REGION_NAME")){
             populateCPFCiotOutput(model, ciotValues);
           }else{
           	populateUPFCiotOutput(model, ciotValues);
        }
    }
     
    public static void populateUserInputInModel(CMgCbamInstantiate model) {
    	model.getZones().get(0).setZoneId(zone1);
    	model.getZones().get(1).setZoneId(zone2);
    	model.getVimConnectionInfo().get(0).getAccessInfo().setUserDomain("Default");
    	model.getVimConnectionInfo().get(0).getAccessInfo().setProjectDomain("Default");
    }

    private static void populateCPFCiotOutput(CMgCbamInstantiate model, LinkedHashMap<String, ArrayList<String>> ciotValues) {
    	String physNet5InitialIP = "", physNet6InitialIP= "", physNet5ResourceID= ""
    			, physNet6ResourceID= "", oamNetworkName= "", oamSubNetworkName= "";   	
    	for (String key : ciotValues.keySet()) {
            if (key.contains("CPF_REGION_NAME") && ciotValues.get(key).size() == 1) {            	
            	model.getVimConnectionInfo().get(0).getAccessInfo().setRegion((String) ciotValues.get(key).get(0));
            } else if (key.contains("CPF_USER_NAME") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getAccessInfo().setUsername((String) ciotValues.get(key).get(0));
            } else if (key.contains("CPF_TENANT_NAME") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getAccessInfo().setTenant((String) ciotValues.get(key).get(0));
            	model.getVimConnectionInfo().get(0).getAccessInfo().setProject((String) ciotValues.get(key).get(0));
            } else if (key.contains("CPF_PASSWORD") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getAccessInfo().setPassword((String) ciotValues.get(key).get(0));
            } else if (key.contains("ENDPOINT_URL") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getInterfaceInfo().setEndpoint((String) ciotValues.get(key).get(0));
            } else if (key.contains("CPF_IMAGE_ID")) {
            	if(ciotValues.get(key).size() > 1){
            		// Show pop-up for selection	 
		    		String value = CommonUtils.showPopUpForMultipleOptions(ciotValues.get("CPF_IMAGE_ID"),"Software Image ID");
		    		model.getSoftwareImages().get(0).setVimSoftwareImageId(value);
            	}else{
            		model.getSoftwareImages().get(0).setVimSoftwareImageId((String) ciotValues.get(key).get(0));
            	}  
            } else if (key.contains("CPF_DFAB1_NW_NAME") && ciotValues.get(key).size() == 1) {
            	model.getExtManagedVirtualLinks().get(0).setResourceId((String) ciotValues.get(key).get(0));                
            }else if (key.contains("CPF_DFAB2_NW_NAME") && ciotValues.get(key).size() == 1) {
            	model.getExtManagedVirtualLinks().get(1).setResourceId((String) ciotValues.get(key).get(0));
            }else if (key.contains("CPF_OAM_FLAVOR_NAME") && ciotValues.get(key).size() > 0) {
            	 for (int index = 0; index <  model.getComputeResourceFlavours().size(); index++) {
            		 if(model.getComputeResourceFlavours().get(index).getVnfdVirtualComputeDescId()
            				 .equalsIgnoreCase("oamCompute")){
            			 model.getComputeResourceFlavours().get(index).setVimFlavourId((String) ciotValues.get(key).get(0)); 
            		 }             
                 }
            }else if (key.contains("CPF_LB_FLAVOR_NAME") && ciotValues.get(key).size() > 0) {
            	 for (int index = 0; index < model.getComputeResourceFlavours().size(); index++) {
            		 if(model.getComputeResourceFlavours().get(index).getVnfdVirtualComputeDescId()
            				 .equalsIgnoreCase("loadBalancerCompute")){
            			 model.getComputeResourceFlavours().get(index).setVimFlavourId((String) ciotValues.get(key).get(0)); 
            		 } 
                 }
            }else if (key.contains("CPF_MG_FLAVOR_NAME") && ciotValues.get(key).size() > 0) {
                for (int index = 0; index < model.getComputeResourceFlavours().size(); index++) {
                	 if(model.getComputeResourceFlavours().get(index).getVnfdVirtualComputeDescId()
            				 .equalsIgnoreCase("mgCompute")){
            			 model.getComputeResourceFlavours().get(index).setVimFlavourId((String) ciotValues.get(key).get(0)); 
            		 } 
                }
            }else if(key.contains("CPF_OAM_NW_NAME")){
            	oamNetworkName = ciotValues.get(key).get(0);
            }else if(key.contains("CPF_OAM_SUB_NW_NAME")){
            	oamSubNetworkName = ciotValues.get(key).get(0);
            }else if(key.contains("CPF_FLAT1_NW_NAME")){
            	physNet5ResourceID = ciotValues.get(key).get(0);
            }else if(key.contains("CPF_FLAT2_NW_NAME")){
            	physNet6ResourceID = ciotValues.get(key).get(0);
            }else if(key.contains("CPF_FLAT1_NW_RANGE")){
            	physNet5InitialIP = ciotValues.get(key).get(0);
            	physNet5InitialIP = physNet5InitialIP.split("/")[0];
            	physNet5InitialIP = CommonUtils.getNextIncrementedIPByN(physNet5InitialIP,2);
            }else if(key.contains("CPF_FLAT2_NW_RANGE")){
            	physNet6InitialIP = ciotValues.get(key).get(0);
            	physNet6InitialIP = physNet6InitialIP.split("/")[0];
            	physNet6InitialIP = CommonUtils.getNextIncrementedIPByN(physNet6InitialIP,2);
            }
        }
    	generateIP(model.getExtVirtualLinks().size(), physNet5InitialIP, physNet6InitialIP, physNet5ResourceID, physNet6ResourceID
    			,oamNetworkName, oamSubNetworkName, model);
        System.out.println("Updated value is : " + new Gson().toJson(model));
    }
    
    private static void populateUPFCiotOutput(CMgCbamInstantiate model, LinkedHashMap<String, ArrayList<String>> ciotValues) {
    	String physNet5InitialIP = "", physNet6InitialIP= "", physNet5ResourceID= ""
    			, physNet6ResourceID= "", oamNetworkName= "", oamSubNetworkName= "";   	
    	for (String key : ciotValues.keySet()) {
            if (key.contains("UPF_REGION_NAME") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getAccessInfo().setRegion((String) ciotValues.get(key).get(0));
            } else if (key.contains("UPF_USER_NAME") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getAccessInfo().setUsername((String) ciotValues.get(key).get(0));
            } else if (key.contains("UPF_TENANT_NAME") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getAccessInfo().setTenant((String) ciotValues.get(key).get(0));
            } else if (key.contains("UPF_PASSWORD") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getAccessInfo().setPassword((String) ciotValues.get(key).get(0));
            } else if (key.contains("ENDPOINT_URL") && ciotValues.get(key).size() == 1) {
            	model.getVimConnectionInfo().get(0).getInterfaceInfo().setEndpoint((String) ciotValues.get(key).get(0));
            } else if (key.contains("UPF_IMAGE_ID")){
            	if(ciotValues.get(key).size() > 1){
            		// Show pop-up for selection	 
		    		String value = CommonUtils.showPopUpForMultipleOptions(ciotValues.get("UPF_IMAGE_ID"),"Software Image ID");
		    		model.getSoftwareImages().get(0).setVimSoftwareImageId(value);
            	}else{
            		model.getSoftwareImages().get(0).setVimSoftwareImageId((String) ciotValues.get(key).get(0));
            	}            	
            } else if (key.contains("UPF_DFAB1_NW_NAME") && ciotValues.get(key).size() == 1) {
            	model.getExtManagedVirtualLinks().get(0).setResourceId((String) ciotValues.get(key).get(0));                
            }else if (key.contains("UPF_DFAB2_NW_NAME") && ciotValues.get(key).size() == 1) {
            	model.getExtManagedVirtualLinks().get(1).setResourceId((String) ciotValues.get(key).get(0));
            }else if (key.contains("UPF_OAM_FLAVOR_NAME") && ciotValues.get(key).size() > 0) {
            	 for (int index = 0; index <  model.getComputeResourceFlavours().size(); index++) {
            		 if(model.getComputeResourceFlavours().get(index).getVnfdVirtualComputeDescId()
            				 .equalsIgnoreCase("oamCompute")){
            			 model.getComputeResourceFlavours().get(index).setVimFlavourId((String) ciotValues.get(key).get(0)); 
            		 }             
                 }
            }else if (key.contains("UPF_LB_FLAVOR_NAME") && ciotValues.get(key).size() > 0) {
            	 for (int index = 0; index < model.getComputeResourceFlavours().size(); index++) {
            		 if(model.getComputeResourceFlavours().get(index).getVnfdVirtualComputeDescId()
            				 .equalsIgnoreCase("loadBalancerCompute")){
            			 model.getComputeResourceFlavours().get(index).setVimFlavourId((String) ciotValues.get(key).get(0)); 
            		 } 
                 }
            }else if (key.contains("UPF_MG_FLAVOR_NAME") && ciotValues.get(key).size() > 0) {
                for (int index = 0; index < model.getComputeResourceFlavours().size(); index++) {
                	 if(model.getComputeResourceFlavours().get(index).getVnfdVirtualComputeDescId()
            				 .equalsIgnoreCase("mgCompute")){
            			 model.getComputeResourceFlavours().get(index).setVimFlavourId((String) ciotValues.get(key).get(0)); 
            		 } 
                }
            }else if(key.contains("UPF_OAM_NW_NAME")){
            	oamNetworkName = ciotValues.get(key).get(0);
            }else if(key.contains("UPF_OAM_SUB_NW_NAME")){
            	oamSubNetworkName = ciotValues.get(key).get(0);
            }else if(key.contains("UPF_FLAT1_NW_NAME")){
            	physNet5ResourceID = ciotValues.get(key).get(0);
            }else if(key.contains("UPF_FLAT2_NW_NAME")){
            	physNet6ResourceID = ciotValues.get(key).get(0);
            }else if(key.contains("UPF_FLAT1_NW_RANGE")){
            	physNet5InitialIP = ciotValues.get(key).get(0);
            	physNet5InitialIP = physNet5InitialIP.split("/")[0];
            	physNet5InitialIP = CommonUtils.getNextIncrementedIPByN(physNet5InitialIP,10);
            }else if(key.contains("UPF_FLAT2_NW_RANGE")){
            	physNet6InitialIP = ciotValues.get(key).get(0);
            	physNet6InitialIP = physNet6InitialIP.split("/")[0];
            	physNet6InitialIP = CommonUtils.getNextIncrementedIPByN(physNet6InitialIP,10);
            }
        }
    	generateIP(model.getExtVirtualLinks().size(), physNet5InitialIP, physNet6InitialIP, physNet5ResourceID, physNet6ResourceID
    			,oamNetworkName, oamSubNetworkName, model);
        System.out.println("Updated value is : " + new Gson().toJson(model));
    }
    
    private static void generateIP(int numberOfNodes, String physNet5InitialIP, 
    	String physNet6InitialIP, String physNet5ResourceID, String physNet6ResourceID
    	, String oamNetworkName, String oamSubNetworkName, CMgCbamInstantiate model) {
        String physNet5Strings = "", physNet6Strings = "";
        int physNet5LastDigit = 0, physNet6LastDigit = 0;
        if (physNet5InitialIP.split("\\.").length == 4) {
            physNet5LastDigit = Integer.parseInt(physNet5InitialIP.split("\\.")[3]);
            physNet5Strings = physNet5InitialIP.substring(0, physNet5InitialIP.length() - String.valueOf(physNet5LastDigit).length());
        }
        if (physNet6InitialIP.split("\\.").length == 4) {
            physNet6LastDigit = Integer.parseInt(physNet6InitialIP.split("\\.")[3]);
            physNet6Strings = physNet6InitialIP.substring(0, physNet6InitialIP.length() - String.valueOf(physNet6LastDigit).length());
        }
        for (int index = 0; index < numberOfNodes; index++) {  
        	model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
			.get(0).getCpProtocolData().get(0).getIpOverEthernet()
			.getIpAddresses().get(0).setType("IPV4");        	
        	if(model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
        			.get(0).getCpProtocolData().get(0).getIpOverEthernet()
        			.getIpAddresses().size()>1){
          		model.getExtVirtualLinks().get(index).setResourceId(oamNetworkName);
          		model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
    			.get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(0)
    			.getFixedAddresses().set(0, oamPrimaryIP);
				model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
    			.get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(1)
    			.getFixedAddresses().set(0, oamSecondaryIP); 
				
				model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
    			.get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(0)
    			.setSubnetId(oamSubNetworkName);
				model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
    			.get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(1)
    			.setSubnetId(oamSubNetworkName);
				
				model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
    			.get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(0)
    			.setType("IPV4");
				model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
    			.get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(1)
    			.setType("IPV4"); 
          	}else{  
          	  if(model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpdId().contains("mg")){
              	if(index % 4 <2){
              	  model.getExtVirtualLinks().get(index).setResourceId(physNet5ResourceID);
                  model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
             	  .get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(0)
             	  .getFixedAddresses().set(0, physNet5Strings + physNet5LastDigit);
                   physNet5LastDigit++;
              	}else{
              	  model.getExtVirtualLinks().get(index).setResourceId(physNet6ResourceID);
                  model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
            	  .get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(0)
            	  .getFixedAddresses().set(0, physNet6Strings + physNet6LastDigit);
                        physNet6LastDigit++;	
              	}          		 
              }else{
        	   if (index % 2 == 0) {
                model.getExtVirtualLinks().get(index).setResourceId(physNet5ResourceID);
                model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
    			.get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(0)
    			.getFixedAddresses().set(0, physNet5Strings + physNet5LastDigit);
                physNet5LastDigit++;
               } else {            	 
                 model.getExtVirtualLinks().get(index).setResourceId(physNet6ResourceID);
                 model.getExtVirtualLinks().get(index).getExtCps().get(0).getCpConfig()
    			 .get(0).getCpProtocolData().get(0).getIpOverEthernet().getIpAddresses().get(0)
    			 .getFixedAddresses().set(0, physNet6Strings + physNet6LastDigit);
                 physNet6LastDigit++;
               }
        	  }
          	}
        }
    }
   
    /*Populate CIOT Output in Tosca File*/
    
    public static void createUpdatedTemplate(String[] toscaValues, String toscaVersion){
    	updateToscaFile(toscaValues, toscaVersion);
        String[] argsList = {FilePath.intermediateFolder, FilePath.outputFolderPath};
        ZipUtils.main(argsList);    	
    }
    
    private static void updateToscaFile(String[] toscaValues, String toscaVersion){
    	System.out.println("Tosca path : "+FilePath.toscaFolder+ toscaVersion);
    	try{
    	    BufferedReader br = new BufferedReader(new FileReader(FilePath.toscaFolder+ toscaVersion));
    	    String str;
    	    System.out.println("Output Tosca path : "+FilePath.toscaFolder+ toscaVersion);
    	    BufferedWriter bw = new BufferedWriter(new FileWriter(FilePath.intermediateFolderPath+
            		FilePath.tosca));
    	    int virtualMemerySizeCounter = 0, numVirtualCPUCounter=0, sizeOfStorageCounter = 0;
    	    while((str = br.readLine()) != null){    	    	
    	    	 if(str.contains("vnfd_version")){   
	    	         String vnfdVersion = str.split(":")[1]; 
	    	         String vnfdVersionValue = StringUtils.substringsBetween(vnfdVersion , "\'","\'")[0];
	    	         int lastDigit = Integer.parseInt(vnfdVersionValue.substring(vnfdVersionValue.length() - 1));
	    	         String updatedVnfdVersionValue = vnfdVersionValue.substring(0, vnfdVersionValue.length() - 1) 
	                 + String.valueOf(++lastDigit);
	    	         System.out.println("After update is : "+updatedVnfdVersionValue);
	    	         str = str.replace(vnfdVersionValue,updatedVnfdVersionValue);
	    	         System.out.println("Final string is : "+str);
	    	         bw.write(str);
	                 bw.newLine();
	             }else if(str.contains("descriptor_version")){   
	            	 String descriptorVersion = str.split(":")[1]; 
	    	         String descriptorVersionValue = StringUtils.substringsBetween(descriptorVersion , "\'","\'")[0];
	    	         int lastDigit = Integer.parseInt(descriptorVersionValue.substring(descriptorVersionValue.length() - 1));
	    	         String updatedDescriptorVersionValue = descriptorVersionValue.substring(0, descriptorVersionValue.length() - 1) 
	    	                 + String.valueOf(++lastDigit);
	    	    	         System.out.println("After update is : "+updatedDescriptorVersionValue);
	    	         str = str.replace(descriptorVersionValue,updatedDescriptorVersionValue);
	    	         bw.write(str);
	                 bw.newLine();  	  		
		         }else if(str.contains("checksum")){   
	    	         String checksumValue = str.split(":")[1];    	  
	  		    	 if(ciotValues.containsKey("CPF_IMAGE_CHECKSUM")){
	  		    		System.out.println("CPF_IMAGE_CHECKSUM "+ciotValues.get("CPF_IMAGE_CHECKSUM"));
	  		    		if(ciotValues.get("CPF_IMAGE_CHECKSUM").size()>1){
	  		    			 // Show pop-up for selection	 
	  		    			String value = CommonUtils.showPopUpForMultipleOptions(ciotValues.get("CPF_IMAGE_CHECKSUM"),"Image Checksum");	  		    			
	  		    			str = str.replace(checksumValue, " "+value);
	  		    		}else{
	  		    			str = str.replace(checksumValue, " "+ciotValues.get("CPF_IMAGE_CHECKSUM").get(0)); 
	  		    		}	  		    		
	  		    		bw.write(str);
   	                    bw.newLine();
	  		    	 }else if(ciotValues.containsKey("UPF_IMAGE_CHECKSUM")){
	  		    	   System.out.println("UPF_IMAGE_CHECKSUM "+ciotValues.get("UPF_IMAGE_CHECKSUM"));
	  		    	   if(ciotValues.get("UPF_IMAGE_CHECKSUM").size()>1){
	  		    			 // Show pop-up for selection	 
	  		    		 String value = CommonUtils.showPopUpForMultipleOptions(ciotValues.get("UPF_IMAGE_CHECKSUM"),"Image Checksum");
	  		    		 str = str.replace(checksumValue, " "+value);
	  		    	   }else{
	  		    		 str = str.replace(checksumValue, " "+ciotValues.get("UPF_IMAGE_CHECKSUM").get(0)); 
	  		    	   }	  		    		 	
	  		    		bw.write(str);
   	                    bw.newLine();
   	  		         }	    	  		
	             }else if(str.contains("virtual_mem_size")){   
	 	    	     String virtualMemerySize = str.split(":")[1];   
	 	    	     str = str.replace(virtualMemerySize, 
	 	    	     " "+getVirtualMemorySize(virtualMemerySizeCounter)+" MB"); 	
			    	 bw.write(str);
	                 bw.newLine();
	                 virtualMemerySizeCounter++;
		         }else if(str.contains("num_virtual_cpu")){   
		    	     String virtualCPUSize = str.split(":")[1];   
		    	     str = str.replace(virtualCPUSize, 
		    	    		 " "+getVirtualCPUSize(numVirtualCPUCounter)); 	
			    	 bw.write(str);
	                 bw.newLine(); 		 	
	                 numVirtualCPUCounter++;
		        }else if(str.contains("size_of_storage")){   
		    	     String sizeOfStorage = str.split(":")[1];   
		    	     str = str.replace(sizeOfStorage, 
		    	    		 " "+getSizeOfStorage(sizeOfStorageCounter)+" GB"); 	
			    	 bw.write(str);
	                 bw.newLine(); 		 	
	                 sizeOfStorageCounter++;
		        }else {
	               bw.write(str);
	               bw.newLine();
	               if(str.contains("vnfHostname")) {   
	  		    		   String vnfHostName = br.readLine();
	  		    		   if(StringUtils.substringsBetween(vnfHostName , "\"", "\"").length>0){
	  		    			 String hostName = StringUtils.substringsBetween(vnfHostName , "\"","\"")[0];
	  		    			 if(toscaValues!=null && toscaValues.length>0){
	  		    			  vnfHostName = vnfHostName.replace(hostName, toscaValues[0]); 	
	  		    			  bw.write(vnfHostName);
 	                	      bw.newLine();
	  		    			 }
	  		    		   }	    		 
	  		      }else if(str.contains("systemIpAddr")){
	  		    		 String systemIpAddr = br.readLine();
 		    		     if(StringUtils.substringsBetween(systemIpAddr , "\"", "\"").length>0){
 		    			   String systemIpAddrValue = StringUtils.substringsBetween(systemIpAddr , "\"","\"")[0];
 		    			   if(toscaValues!=null && toscaValues.length>0){
 		    				systemIpAddr = systemIpAddr.replace(systemIpAddrValue, toscaValues[1]); 	
 		    			    bw.write(systemIpAddr);
	                	    bw.newLine();
 		    			 }
 		    		   }	
	  		     }else if(str.contains("staticRoute")){
	  		    		String staticRoute = br.readLine();
		    		     if(StringUtils.substringsBetween(staticRoute , "\"", "\"").length>0){
		    			   String staticRouteValue = StringUtils.substringsBetween(staticRoute , "\"","\"")[0];
		    			   if(toscaValues!=null && toscaValues.length>0){
		    				staticRoute = staticRoute.replace(staticRouteValue, toscaValues[2]); 	
		    			    bw.write(staticRoute);
	                	    bw.newLine();
		    			   }
	  		    	    }                   
	             }else if(str.contains("hyperThreadingSupportValue")){
	  		    		 String hyperThreadingSupport = br.readLine();
		    		     if(StringUtils.substringsBetween(hyperThreadingSupport , "\"", "\"").length>0){
		    			   String hyperThreadingSupportValue = StringUtils.substringsBetween(hyperThreadingSupport , "\"","\"")[0];
		    			   if(toscaValues!=null && toscaValues.length>0){
		    				   hyperThreadingSupport = hyperThreadingSupport.replace(hyperThreadingSupportValue, "1"); 	
		    				 bw.write(hyperThreadingSupport);
	                	     bw.newLine();
		    			   }
		    		     }
  		    	 }else if(str.contains("controlPlaneCores")){
  		    		 String controlPlaneCores = br.readLine();
	    		     if(StringUtils.substringsBetween(controlPlaneCores , "\"", "\"").length>0){
	    			   String controlPlaneCoresValue = StringUtils.substringsBetween(controlPlaneCores , "\"","\"")[0];
	    			   if(toscaValues!=null && toscaValues.length>0){
	    				 controlPlaneCores = controlPlaneCores.replace(controlPlaneCoresValue, toscaValues[3]); 	
	    				 bw.write(controlPlaneCores);
                	     bw.newLine();
	    			   }
	    		     }
		    	 }else if(str.contains("controlSwitchFabricCidr")){
	  		    		 String controlSwitchFabricCidr = br.readLine();
		    		     if(StringUtils.substringsBetween(controlSwitchFabricCidr , "\"", "\"").length>0){
		    			   String controlSwitchFabricCidrValue = StringUtils.substringsBetween(controlSwitchFabricCidr , "\"","\"")[0];		    			  
		    			   if(toscaValues!=null && toscaValues.length>0){
		    				 controlSwitchFabricCidr = controlSwitchFabricCidr.replace(controlSwitchFabricCidrValue, toscaValues[4]); 	
		    				 bw.write(controlSwitchFabricCidr);
                	         bw.newLine();
		    			   }
 		    	         }                                      
                 }else if(str.contains("controlSwitchFabricGatewayIp")){
  		    		 String controlSwitchFabricGatewayIp = br.readLine();
  		    		 String gateWayIP = "";
	    		     if(StringUtils.substringsBetween(controlSwitchFabricGatewayIp , "\"", "\"").length>0){
	    		       String controlSwitchFabricGatewayIpValue = StringUtils.substringsBetween(controlSwitchFabricGatewayIp , "\"","\"")[0];
	    		       gateWayIP =  CommonUtils.getNextIncrementedIP(toscaValues[4]);
	    			   if(toscaValues!=null && toscaValues.length>0){
	    				 controlSwitchFabricGatewayIp = controlSwitchFabricGatewayIp.replace(controlSwitchFabricGatewayIpValue, gateWayIP); 	
	    				 bw.write(controlSwitchFabricGatewayIp);
            	         bw.newLine();
	    			   }
		    	         }                                      
               }else if(str.contains("mgLbManagementCidr")){
  		    		String mgLbManagementCidr = br.readLine();
	    		     if(StringUtils.substringsBetween(mgLbManagementCidr , "\"", "\"").length>0){
	    			   String mgLbManagementCidrValue = StringUtils.substringsBetween(mgLbManagementCidr , "\"","\"")[0];
	    			   if(toscaValues!=null && toscaValues.length>0){
	    			     mgLbManagementCidr = mgLbManagementCidr.replace(mgLbManagementCidrValue, toscaValues[5]); 	
	    				 bw.write(mgLbManagementCidr);
             	         bw.newLine();
	    			   }
		    	     }                                      
              }else if(str.contains("mgLbManagementGatewayIp")){
		    		 String mgLbManagementGatewayIp = br.readLine();
		    		 String gateWayIP = "";
	    		     if(StringUtils.substringsBetween(mgLbManagementGatewayIp , "\"", "\"").length>0){
	    		       String mgLbManagementGatewayIpValue = StringUtils.substringsBetween(mgLbManagementGatewayIp , "\"","\"")[0];
	    		       gateWayIP =  CommonUtils.getNextIncrementedIP(toscaValues[5]);
	    			   if(toscaValues!=null && toscaValues.length>0){
	    				 mgLbManagementGatewayIp = mgLbManagementGatewayIp.replace(mgLbManagementGatewayIpValue, gateWayIP); 	
	    				 bw.write(mgLbManagementGatewayIp);
         	             bw.newLine();
	    			   }
		    	     }                                      
             }else if(str.contains("primaryConfigFile")){
	  		    	String primaryConfigFile = br.readLine();
		    		if(StringUtils.substringsBetween(primaryConfigFile , "\"", "\"").length>0){
		    		   String primaryConfigFileValue = StringUtils.substringsBetween(primaryConfigFile , "\"","\"")[0];
		    		   if(toscaValues!=null && toscaValues.length>0){
		    			 primaryConfigFile = primaryConfigFile.replace(primaryConfigFileValue, toscaValues[6]); 	
		    		     bw.write(primaryConfigFile);
	                     bw.newLine();
		    		   }
	  		        }                                      
	             }else if(str.contains("licenseFile")){
	   	  		    String licenseFile = br.readLine();
	 		    	if(StringUtils.substringsBetween(licenseFile , "\"", "\"").length>0){
	 		    	  String licenseFileValue = StringUtils.substringsBetween(licenseFile , "\"","\"")[0];
	 		    	  if(toscaValues!=null && toscaValues.length>0){
	 		    		licenseFile = licenseFile.replace(licenseFileValue, toscaValues[7]); 	
	 		    		bw.write(licenseFile);
		                bw.newLine();
	 		          }
		  		    }                                      
		        }else if(str.contains("csfFailureDetectionTimer")){
	   	  		    String csfFailureDetectionTimer = br.readLine();
	 		    	if(StringUtils.substringsBetween(csfFailureDetectionTimer , "\"", "\"").length>0){
	 		    	  String csfFailureDetectionTimerValue = StringUtils.substringsBetween(csfFailureDetectionTimer , "\"","\"")[0];
	 		    	  if(toscaValues!=null && toscaValues.length>0){
	 		    		csfFailureDetectionTimer = csfFailureDetectionTimer.replace(csfFailureDetectionTimerValue, toscaValues[8]); 	
	 		    		bw.write(csfFailureDetectionTimer);
		                bw.newLine();
	 		          }
		  		    }                                      
		        }else if(str.contains("oamActiveVirtualIp")){
	   	  		    String oamActiveVirtualIp = br.readLine();
	 		    	if(StringUtils.substringsBetween(oamActiveVirtualIp , "\"", "\"").length>0){
	 		    	  String oamActiveVirtualIpValue = StringUtils.substringsBetween(oamActiveVirtualIp , "\"","\"")[0];
	 		    	  if(toscaValues!=null && toscaValues.length>0){
	 		    		oamActiveVirtualIp = oamActiveVirtualIp.replace(oamActiveVirtualIpValue, toscaValues[9]); 	
	 		    		bw.write(oamActiveVirtualIp);
		                bw.newLine();
	 		          }
		  		    }                                      
		        }else if(str.contains("oamStandbyVirtualIp")){
	   	  		    String oamStandbyVirtualIp = br.readLine();
	 		    	if(StringUtils.substringsBetween(oamStandbyVirtualIp , "\"", "\"").length>0){
	 		    	  String oamStandbyVirtualIpValue = StringUtils.substringsBetween(oamStandbyVirtualIp , "\"","\"")[0];
	 		    	  if(toscaValues!=null && toscaValues.length>0){
	 		    		oamStandbyVirtualIp = oamStandbyVirtualIp.replace(oamStandbyVirtualIpValue, toscaValues[10]); 	
	 		    		bw.write(oamStandbyVirtualIp);
		                bw.newLine();
	 		          }
		  		    }                                      
		        }else if(str.contains("securityGroup")){   
 	  		      String securityGroup = br.readLine();
 	  		      if(StringUtils.substringsBetween(securityGroup , "\"", "\"").length>0){
 	  		         String securityGroupName = StringUtils.substringsBetween(securityGroup , "\"","\"")[0];
 	  		    	 if(ciotValues.containsKey("CPF_SECURITY_GROUP_NAME")){
 	  		    		securityGroup = securityGroup.replace(securityGroupName, ciotValues.get("CPF_SECURITY_GROUP_NAME").get(0)); 	
 	  		    		bw.write(securityGroup);
     	                bw.newLine();
 	  		    	 }else if(ciotValues.containsKey("UPF_SECURITY_GROUP_NAME")){
	    	  		   	securityGroup = securityGroup.replace(securityGroupName, ciotValues.get("UPF_SECURITY_GROUP_NAME").get(0)); 	
	    	  		   	bw.write(securityGroup);
	        	        bw.newLine();
	    	  		 }
 	  		      }	    		 		    	   
 	          }else if(str.contains("oamManagementNetmask")){   
 	  		    String oamManagementNetmask = br.readLine();
 	  		      if(StringUtils.substringsBetween(oamManagementNetmask , "\"", "\"").length>0){
 	  		         String oamManagementNetmaskValue = StringUtils.substringsBetween(oamManagementNetmask , "\"","\"")[0];
 	  		    	 if(ciotValues.containsKey("CPF_OAM_NET_MASK")){
 	  		    		oamManagementNetmask = oamManagementNetmask.replace(oamManagementNetmaskValue, ciotValues.get("CPF_OAM_NET_MASK").get(0)); 	
 	  		    		bw.write(oamManagementNetmask);
     	                bw.newLine();
 	  		    	 }else if(ciotValues.containsKey("UPF_OAM_NET_MASK")){
	    	  		    oamManagementNetmask = oamManagementNetmask.replace(oamManagementNetmaskValue, ciotValues.get("UPF_OAM_NET_MASK").get(0)); 	
	    	  		    bw.write(oamManagementNetmask);
	        	        bw.newLine();
	    	  		 }
 	  		      }	    		 		    	   
 	        }else if(str.contains("oamCompactFlash1Size")){   
 	  		     String oamCompactFlash1Size = br.readLine();
 	  		      if(StringUtils.substringsBetween(oamCompactFlash1Size , "\"", "\"").length>0){
 	  		         String oamCompactFlash1SizeValue = StringUtils.substringsBetween(oamCompactFlash1Size , "\"","\"")[0];
 	  		    	 if(ciotValues.containsKey("CPF_FLASH1_SIZE")){
 	  		    		oamCompactFlash1Size = oamCompactFlash1Size.replace(oamCompactFlash1SizeValue, ciotValues.get("CPF_FLASH1_SIZE").get(0)); 	
 	  		    		bw.write(oamCompactFlash1Size);
     	                bw.newLine();
 	  		    	 }else if(ciotValues.containsKey("UPF_FLASH1_SIZE")){
	    	  		    oamCompactFlash1Size = oamCompactFlash1Size.replace(oamCompactFlash1SizeValue, ciotValues.get("UPF_FLASH1_SIZE").get(0)); 	
	    	  		    bw.write(oamCompactFlash1Size);
	        	        bw.newLine();
	    	  		 }
 	  		      }	    		 		    	   
 	        }else if(str.contains("oamCompactFlash2Size")){   
 	  		      String oamCompactFlash2Size = br.readLine();
 	  		      if(StringUtils.substringsBetween(oamCompactFlash2Size , "\"", "\"").length>0){
 	  		         String oamCompactFlash2SizeValue = StringUtils.substringsBetween(oamCompactFlash2Size , "\"","\"")[0];
 	  		    	 if(ciotValues.containsKey("CPF_FLASH2_SIZE")){
 	  		    		oamCompactFlash2Size = oamCompactFlash2Size.replace(oamCompactFlash2SizeValue, ciotValues.get("CPF_FLASH2_SIZE").get(0)); 	
 	  		    		bw.write(oamCompactFlash2Size);
     	                bw.newLine();
 	  		    	 }else if(ciotValues.containsKey("UPF_FLASH2_SIZE")){
	    	  		    oamCompactFlash2Size = oamCompactFlash2Size.replace(oamCompactFlash2SizeValue, ciotValues.get("UPF_FLASH2_SIZE").get(0)); 	
	    	  		    bw.write(oamCompactFlash2Size);
	        	        bw.newLine();
	    	  		 }
 	  		      }	    		 		    	   
 	       }else if(str.contains("oamACompactFlash1Id")){ 	 
 	    	    String value = getCompactFlashValue(str.replace("Id:", ""));
 	    	    String oamCompactFlashID = br.readLine();
	  		    if(StringUtils.substringsBetween(oamCompactFlashID , "\"", "\"").length>0){
	  		       String oamCompactFlashIDValue = StringUtils.substringsBetween(oamCompactFlashID , "\"","\"")[0];
	  		       oamCompactFlashID = oamCompactFlashID.replace(oamCompactFlashIDValue, value ); 	
	  		       bw.write(oamCompactFlashID);
	               bw.newLine();
	  		    }	  
 	       }else if(str.contains("oamACompactFlash2Id")){
 	    	    String value = getCompactFlashValue(str.replace("Id:", ""));
	    	    String oamCompactFlashID = br.readLine();
	  		    if(StringUtils.substringsBetween(oamCompactFlashID , "\"", "\"").length>0){
	  		       String oamCompactFlashIDValue = StringUtils.substringsBetween(oamCompactFlashID , "\"","\"")[0];
	  		       oamCompactFlashID=oamCompactFlashID.replace(oamCompactFlashIDValue, value); 	
	  		       bw.write(oamCompactFlashID);
	               bw.newLine();
	  		    }	  
	       }else if(str.contains("oamBCompactFlash1Id")){
	    	    String value = getCompactFlashValue(str.replace("Id:", ""));
	    	    String oamCompactFlashID = br.readLine();
	  		    if(StringUtils.substringsBetween(oamCompactFlashID , "\"", "\"").length>0){
	  		       String oamCompactFlashIDValue = StringUtils.substringsBetween(oamCompactFlashID , "\"","\"")[0];
	  		       oamCompactFlashID = oamCompactFlashID.replace(oamCompactFlashIDValue, value); 	
	  		       bw.write(oamCompactFlashID);
	               bw.newLine();
	  		    }	  
	       }else if(str.contains("oamBCompactFlash2Id")){
	    	    String value = getCompactFlashValue(str.replace("Id:", ""));
	    	    String oamCompactFlashID = br.readLine();
	  		    if(StringUtils.substringsBetween(oamCompactFlashID , "\"", "\"").length>0){
	  		       String oamCompactFlashIDValue = StringUtils.substringsBetween(oamCompactFlashID , "\"","\"")[0];
	  		       oamCompactFlashID = oamCompactFlashID.replace(oamCompactFlashIDValue, value); 	
	  		       bw.write(oamCompactFlashID);
	               bw.newLine();
	  		    }	  
	       }else if(str.contains("dataSwitchFabric1Cidr")){   
	  		    String oamCompactFlash2Size = br.readLine();
	  		      if(StringUtils.substringsBetween(oamCompactFlash2Size , "\"", "\"").length>0){
	  		         String oamCompactFlash2SizeValue = StringUtils.substringsBetween(oamCompactFlash2Size , "\"","\"")[0];
	  		    	 if(ciotValues.containsKey("CPF_DFAB1_NW_RANGE")){
	  		    		oamCompactFlash2Size = oamCompactFlash2Size.replace(oamCompactFlash2SizeValue, ciotValues.get("CPF_DFAB1_NW_RANGE").get(0)); 	
	  		    		bw.write(oamCompactFlash2Size);
   	                    bw.newLine();
	  		    	 }else if(ciotValues.containsKey("UPF_DFAB1_NW_RANGE")){
	    	  		    oamCompactFlash2Size = oamCompactFlash2Size.replace(oamCompactFlash2SizeValue, ciotValues.get("UPF_DFAB1_NW_RANGE").get(0)); 	
	    	  		    bw.write(oamCompactFlash2Size);
	        	        bw.newLine();
	    	  		 }
	  		      }	    		 		    	   
	       }else if(str.contains("dataSwitchFabric2Cidr")){   
	  		    String oamCompactFlash2Size = br.readLine();
	  		    if(StringUtils.substringsBetween(oamCompactFlash2Size , "\"", "\"").length>0){
	  		       String oamCompactFlash2SizeValue = StringUtils.substringsBetween(oamCompactFlash2Size , "\"","\"")[0];
	  		       if(ciotValues.containsKey("CPF_DFAB2_NW_RANGE")){
	  		    	 oamCompactFlash2Size = oamCompactFlash2Size.replace(oamCompactFlash2SizeValue, ciotValues.get("CPF_DFAB2_NW_RANGE").get(0)); 	
	  		    	 bw.write(oamCompactFlash2Size);
   	                 bw.newLine();
	  		       }else if(ciotValues.containsKey("UPF_DFAB2_NW_RANGE")){
	    	  		 oamCompactFlash2Size = oamCompactFlash2Size.replace(oamCompactFlash2SizeValue, ciotValues.get("UPF_DFAB2_NW_RANGE").get(0)); 	
	    	  		 bw.write(oamCompactFlash2Size);
	        	     bw.newLine();
	    	  	   }
	  		    }	    		 		    	   
	        }
	     } 	    	
    	}    
        br.close();
    	bw.close();
    }catch(Exception ex){
      ex.printStackTrace();    		
   }        	
  }

    
  private static String getCompactFlashValue(String flashIDKey){
	 String flashIDValue = ""; 
	 if(ciotValues.containsKey("CPF_CINDER_LIST")){
		 for(int index = 0; index<ciotValues.get("CPF_CINDER_LIST").size(); index++){
            if(ciotValues.get("CPF_CINDER_LIST").get(index).contains(flashIDKey.trim())){
            	flashIDValue = ciotValues.get("CPF_CINDER_LIST").get(index);
            	return CommonUtils.getSecondIndexValue(flashIDValue);
            }			 
		 }
	 }else if(ciotValues.containsKey("UPF_CINDER_LIST")){
		 for(int index = 0; index<ciotValues.get("UPF_CINDER_LIST").size(); index++){
			 if(ciotValues.get("UPF_CINDER_LIST").get(index).contains(flashIDKey.trim())){
	            flashIDValue = ciotValues.get("UPF_CINDER_LIST").get(index);
	            return CommonUtils.getSecondIndexValue(flashIDValue);
	         } 
		 }
	}
	return flashIDValue;
  }
  
  private static String getVirtualMemorySize(int counter){
	  String virtualMemorySize = "";
	   if(counter == 0){
		   if(ciotValues.containsKey("CPF_OAM_VRAM")){
			   virtualMemorySize =  ciotValues.get("CPF_OAM_VRAM").get(0);	    		
	       }else if(ciotValues.containsKey("UPF_OAM_VRAM")){
	    	   virtualMemorySize =  ciotValues.get("UPF_OAM_VRAM").get(0);	    		
		   } 
	   }else if(counter == 1){
		   if(ciotValues.containsKey("CPF_LB_VRAM")){
			   virtualMemorySize =  ciotValues.get("CPF_LB_VRAM").get(0);	    		
	       }else if(ciotValues.containsKey("UPF_LB_VRAM")){
	    	   virtualMemorySize =  ciotValues.get("UPF_LB_VRAM").get(0);	    		
		   }
	   }else if(counter == 2){
		   if(ciotValues.containsKey("CPF_MG_VRAM")){
			   virtualMemorySize =  ciotValues.get("CPF_MG_VRAM").get(0);	    		
	       }else if(ciotValues.containsKey("UPF_MG_VRAM")){
	    	   virtualMemorySize =  ciotValues.get("UPF_MG_VRAM").get(0);	    		
		   }
	   }	   
	   return virtualMemorySize;
  }
   
  private static String getVirtualCPUSize(int counter){
	  String virtualCPUSize = "";
	   if(counter == 0){
		   if(ciotValues.containsKey("CPF_OAM_VCPU")){
			   virtualCPUSize =  ciotValues.get("CPF_OAM_VCPU").get(0);	    		
	       }else if(ciotValues.containsKey("UPF_OAM_VCPU")){
	    	   virtualCPUSize =  ciotValues.get("UPF_OAM_VCPU").get(0);	    		
		   } 
	   }else if(counter == 1){
		   if(ciotValues.containsKey("CPF_LB_VCPU")){
			   virtualCPUSize =  ciotValues.get("CPF_LB_VCPU").get(0);	    		
	       }else if(ciotValues.containsKey("UPF_LB_VCPU")){
	    	   virtualCPUSize =  ciotValues.get("UPF_LB_VCPU").get(0);	    		
		   }
	   }else if(counter == 2){
		   if(ciotValues.containsKey("CPF_MG_VCPU")){
			   virtualCPUSize =  ciotValues.get("CPF_MG_VCPU").get(0);	    		
	       }else if(ciotValues.containsKey("UPF_MG_VCPU")){
	    	   virtualCPUSize =  ciotValues.get("UPF_MG_VCPU").get(0);	    		
		   }
	   }	   
	   return virtualCPUSize;
  }
  
  private static String getSizeOfStorage(int count){
	  String sizeOfStorage = "";
	  if(count==0){
		 if(ciotValues.containsKey("CPF_FLASH1_SIZE")){
		   sizeOfStorage =  ciotValues.get("CPF_FLASH1_SIZE").get(0);	    		
	     }else if(ciotValues.containsKey("UPF_FLASH1_SIZE")){
	       sizeOfStorage =  ciotValues.get("UPF_FLASH1_SIZE").get(0);	    		
		 }
	  }else{
		if(ciotValues.containsKey("CPF_FLASH2_SIZE")){
		   sizeOfStorage =  ciotValues.get("CPF_FLASH2_SIZE").get(0);	    		
		}else if(ciotValues.containsKey("UPF_FLASH2_SIZE")){
		   sizeOfStorage =  ciotValues.get("UPF_FLASH2_SIZE").get(0);	    		
	    }  
	  }	  
	  return sizeOfStorage;
  }
    
}
