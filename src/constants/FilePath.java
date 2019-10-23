package constants;


import java.util.ArrayList;
import java.util.HashMap;

public class FilePath {

	public static String intermediateFolderPath = "files\\intermediatefiles\\";
	public static String intermediateFolder = "files\\intermediatefiles";
	public static String inputFolderPath = "files\\inputfiles\\";
	public static String inputFolder = "files\\inputfiles";
	public static String outputFolderPath = "files\\outputfiles\\";
	public static String outputFolder = "files\\outputfiles";
	public static String toscaFolder = "files\\toscafiles\\";
	public static String defaultvalues = "files\\defaultvalues\\";
	public static String nuageHotFolder = "files\\nuagehotfiles\\";
	public static String nonNuageHotFolder = "files\\non-nuagehotfiles\\";
	
	public static String lbMode = "LB_Mode";
	public static String lbLess = "LB_Less";
	public static String pathDivider = "\\";

	public static String defaultVILFile = "VIL.json";
	public static String defaultOneWebFile = "OneWeb.json";
	
	public static String grantlessFileName = "cMgCbamGrantless.json";
	public static String instantiateFileName = "cMgCbamInstantiate.json";
	public static String ciotOutputFileName = "C_IOT_CPF_updated_output.txt";
	public static String templatePath = "cMg_Cbam_Sriov_Package_v10.7.4.zip";
	public static String outputTemplateName = "";
	
	public static String outputTextFile = "output.txt";
	public static String tosca = "cMG.vnfd.scale.tosca.yaml";
	
	public static String LB_Less_v1 = "LB_Less/V1_cMG.vnfd.scale.tosca.yaml";
	public static String LB_Less_v2 = "LB_Less/V2_cMG.vnfd.scale.tosca.yaml";
	public static String LB_Less_v3 = "LB_Less/V3_cMG.vnfd.scale.tosca.yaml";
	
	public static String LB_Mode_v1 = "LB_Mode/V1_cMG.vnfd.scale.tosca.yaml";
	public static String LB_Mode_v2 = "LB_Mode/V2_cMG.vnfd.scale.tosca.yaml";
	public static String LB_Mode_v3 = "LB_Mode/V3_cMG.vnfd.scale.tosca.yaml";
	
	/*Record of Template version in Repository*/
	private static HashMap<String, String> template;
		
	/*Template Version List*/
	public static String V1 = "v10.7.4";
	public static String V2 = "v11.1.2";
	public static String V3 = "v11.3.1";
	
	public static ArrayList<String> templateVersionList = new ArrayList<String>();
	public static ArrayList<String> lbLessToscaVersionList = new ArrayList<String>();
	public static ArrayList<String> lbModeToscaVersionList = new ArrayList<String>();
	
	public static void setCiotOutputFilePath(String solutionType){
		if(solutionType.equalsIgnoreCase("LB_Mode")){
			ciotOutputFileName = "C_IOT_CPF_updated_output.txt";
		}else{
			ciotOutputFileName = "C_IOT_UPF_updated_output.txt";
		}
	}	

    public static void main(String[] args){
		/*Add templates in the list*/
		templateVersionList.add("v10.7.4");
		templateVersionList.add("v11.1.2");
		templateVersionList.add("v11.3.1");
		
       /*Add default LB_Less TOSCA files in the list*/
	   lbLessToscaVersionList.add("LB_Less/V1_cMG.vnfd.scale.tosca.yaml");
	   lbLessToscaVersionList.add("LB_Less/V2_cMG.vnfd.scale.tosca.yaml");
		
	  /*Add default LB_Mode TOSCA files in the list*/
	   lbModeToscaVersionList.add("LB_Mode/V1_cMG.vnfd.scale.tosca.yaml");
	   lbModeToscaVersionList.add("LB_Mode/V2_cMG.vnfd.scale.tosca.yaml");
	   
	   System.out.println("Sizes is : "+templateVersionList.size()+" & "+
			   lbLessToscaVersionList.size()+" & "+lbModeToscaVersionList.size());
	}
	
	public static String getTemplatePathFromRepo(boolean isNKRedundant, String templateVersion, String solutionType){
		setTemplatePathInMap(isNKRedundant);
		String templatePath = "";
		if(solutionType.equalsIgnoreCase("LB_Mode")){
			if(templateVersion.equalsIgnoreCase(V1)){
				templatePath = template.get("v10.7.4_SRIOV");
			}else if(templateVersion.equalsIgnoreCase(V2)){
				templatePath = template.get("v11.1.2_SRIOV");
			}else if(templateVersion.equalsIgnoreCase(V3)){
				if(isNKRedundant){
					templatePath = template.get("v11.3.1_SRIOV_NK");	
				}else{
					templatePath = template.get("v11.3.1_SRIOV");
				}
			}									
		}else{		
			if(templateVersion.equalsIgnoreCase(V1)){
				templatePath = template.get("v10.7.4_MG_REDIRECT");
			}else if(templateVersion.equalsIgnoreCase(V2)){
				templatePath = template.get("v11.1.2_MG_REDIRECT");
			}else if(templateVersion.equalsIgnoreCase(V3)){
     			if(isNKRedundant){
					templatePath = template.get("v11.3.1_MG_REDIRECT_NK");	
				}else{
					templatePath = template.get("v11.3.1_MG_REDIRECT");
				}
			}				
		}
		outputTemplateName = templatePath.substring(templatePath.lastIndexOf('\\') + 1);
		System.out.println("Output folder name is : "+outputTemplateName);
		return templatePath;
	}
	
	private static void setTemplatePathInMap(boolean isNKRedundant){
		template = new HashMap<String, String>();		
		template.put("v10.7.4_SRIOV", "files\\TemplateRepo\\LB_Mode\\cMg_Cbam_Sriov_Package_v10.7.4.zip");
		template.put("v10.7.4_MG_REDIRECT", "files\\TemplateRepo\\LB_Less\\cMg_Cbam_Sriov_MgRedirect_Package_v10.7.4.zip");
		template.put("v11.1.2_SRIOV", "files\\TemplateRepo\\LB_Mode\\cMg_Cbam_Sriov_Package_v11.1.2.zip");
		template.put("v11.1.2_MG_REDIRECT", "files\\TemplateRepo\\LB_Less\\cMg_Cbam_Sriov_MgRedirect_Package_v11.1.2.zip");
	    if(isNKRedundant){
		  template.put("v11.3.1_SRIOV_NK", "files\\TemplateRepo\\LB_Mode\\NK\\cMg_Cbam_Sriov_Package_v11.3.1.zip");
		  template.put("v11.3.1_MG_REDIRECT_NK", "files\\TemplateRepo\\LB_Less\\NK\\cMg_Cbam_Sriov_MgRedirect_Package_v11.3.1.zip");		
	    }else{
		  template.put("v11.3.1_SRIOV", "files\\TemplateRepo\\LB_Mode\\Non_NK\\cMg_Cbam_Sriov_Package_v11.3.1.zip");
		  template.put("v11.3.1_MG_REDIRECT", "files\\TemplateRepo\\LB_Less\\Non_NK\\cMg_Cbam_Sriov_MgRedirect_Package_v11.3.1.zip");
	    }	
    }

}
