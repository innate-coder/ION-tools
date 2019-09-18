package constants;

import java.util.HashMap;

public class FilePath {

	public static String intermediateFolderPath = "files\\intermediatefiles\\";
	public static String intermediateFolder = "files\\intermediatefiles";
	public static String inputFolderPath = "files\\inputfiles\\";
	public static String outputFolderPath = "files\\outputfiles\\";
	public static String outputFolder = "files\\outputfiles";
	public static String toscaFolder = "files\\toscafiles\\";
	public static String defaultvalues = "files\\defaultvalues\\";
	
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
	
	public static String LB_Mode_v1 = "LB_Mode/V1_cMG.vnfd.scale.tosca.yaml";
	public static String LB_Mode_v2 = "LB_Mode/V2_cMG.vnfd.scale.tosca.yaml";
	
	/*Record of Template version in Repository*/
	
	private static HashMap<String, String> template;
		
	/*Template Version List*/
	public static String V1 = "v10.7.4";
	public static String V2 = "v11.1.2";

	
	
	public static void setCiotOutputFilePath(String solutionType){
		if(solutionType.equalsIgnoreCase("LB_Mode")){
			ciotOutputFileName = "C_IOT_CPF_updated_output.txt";
		}else{
			ciotOutputFileName = "C_IOT_UPF_updated_output.txt";
		}
	}	
	
	public static String getTemplatePathFromRepo(String templateVersion, String solutionType){
		setTemplatePathInMap();
		String templatePath = "";
		if(solutionType.equalsIgnoreCase("LB_Mode")){
			if(templateVersion.equalsIgnoreCase(V1)){
				templatePath = template.get("v10.7.4_SRIOV");
			}else{
				templatePath = template.get("v11.1.2_SRIOV");
			}			
		}else{
			if(templateVersion.equalsIgnoreCase(V1)){
				templatePath = template.get("v10.7.4_MG_REDIRECT");
			}else{
				templatePath = template.get("v11.1.2_MG_REDIRECT");
			}
		}
		outputTemplateName = templatePath.substring(templatePath.lastIndexOf('\\') + 1);
		System.out.println("Output folder name is : "+outputTemplateName);
		return templatePath;
	}
	
	private static void setTemplatePathInMap(){
		template = new HashMap<String, String>();		
		template.put("v10.7.4_SRIOV", "files\\TemplateRepo\\LB_Mode\\cMg_Cbam_Sriov_Package_v10.7.4.zip");
		template.put("v10.7.4_MG_REDIRECT", "files\\TemplateRepo\\LB_Less\\cMg_Cbam_Sriov_MgRedirect_Package_v10.7.4.zip");
		template.put("v11.1.2_SRIOV", "files\\TemplateRepo\\LB_Mode\\cMg_Cbam_Sriov_Package_v11.1.2.zip");
		template.put("v11.1.2_MG_REDIRECT", "files\\TemplateRepo\\LB_Less\\cMg_Cbam_Sriov_MgRedirect_Package_v11.1.2.zip");
    }

}
