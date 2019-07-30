package constants;

import java.util.HashMap;

public class FilePath {

	public static String intermediateFolderPath = "C:\\cMG-Spinner\\shaldixi\\workspace\\GuiProject\\src\\files\\intermediatefiles\\";
	public static String intermediateFolder = "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\intermediatefiles";
	public static String inputFolderPath = "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\inputfiles\\";
	public static String outputFolderPath = "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\outputfiles\\";
	public static String outputFolder = "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\outputfiles";
	public static String toscaFolder = "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\toscafiles\\";

	public static String grantlessFileName = "cMgCbamGrantless.json";
	public static String ciotOutputFileName = "C_IOT_CPF_updated_output.txt";
	public static String templatePath = "cMg_Cbam_Sriov_Package_v10.7.4.zip";
	public static String outputTemplateName = "";
	
	public static String outputTextFile = "output.txt";
	public static String tosca = "cMG.vnfd.scale.tosca.yaml";
	
	public static String v1 = "V1_cMG.vnfd.scale.tosca.yaml";
	public static String v2 = "V2_cMG.vnfd.scale.tosca.yaml";
	
	/*Record of Template version in Repository*/
	
	private static HashMap<String, String> template;
	public static String templateV1 = "";
	public static String templateV2 = "";
	
	/*Template Version List*/
	public static String V1 = "v10.6.4";
	public static String V2 = "v10.7.4";
	
	
	public static void setCiotOutputFilePath(String solutionType){
		if(solutionType.equalsIgnoreCase("LB_Mode")){
			ciotOutputFileName = "C_IOT_CPF_updated_output.txt";
		}else{
			ciotOutputFileName = "C_IOT_UPF_updated_output.txt";
		}
		System.out.println("Selected CIOT File is: "+templatePath);
	}	
	
	public static String getTemplatePathFromRepo(String templateVersion, String solutionType){
		setTemplatePathInMap();
		String templatePath = "";
		if(solutionType.equalsIgnoreCase("LB_Mode")){
			if(templateVersion.equalsIgnoreCase(V1)){
				templatePath = template.get("v10.6.4_SRIOV");
			}else{
				templatePath = template.get("v10.7.4_SRIOV");
			}			
		}else{
			if(templateVersion.equalsIgnoreCase("V1")){
				templatePath = template.get("v10.6.4_MG_REDIRECT");
			}else{
				templatePath = template.get("v10.7.4_MG_REDIRECT");
			}
		}
		outputTemplateName = templatePath.substring(templatePath.lastIndexOf('\\') + 1);
		System.out.println("Output folder name is : "+outputTemplateName);
		return templatePath;
	}
	
	private static void setTemplatePathInMap(){
		template = new HashMap<String, String>();
		template.put("v10.6.4_SRIOV", "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\TemplateRepo\\LB_Mode\\cMg_Cbam_Sriov_Package_v10.6.4.zip");
		template.put("v10.6.4_MG_REDIRECT", "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\TemplateRepo\\LB_Less\\cMg_Cbam_Sriov_MgRedirect_Package_v10.6.4.zip");
		template.put("v10.7.4_SRIOV", "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\TemplateRepo\\LB_Mode\\cMg_Cbam_Sriov_Package_v10.7.4.zip");
		template.put("v10.7.4_MG_REDIRECT", "C:\\Users\\shaldixi\\workspace\\GuiProject\\src\\files\\TemplateRepo\\LB_Less\\cMg_Cbam_Sriov_MgRedirect_Package_v10.7.4.zip");
    }

}
