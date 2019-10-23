package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import constants.FilePath;
import utils.CommonUtils;

public class ZipUtils {

    private List <String> fileList;
    private static String OUTPUT_ZIP_FILE ;
    private static String SOURCE_FOLDER ;
 
    public ZipUtils() {
        fileList = new ArrayList < String > ();
    }

    public static void main(String[] args) {
  	
    	if(args!=null && args.length>0){
    		SOURCE_FOLDER = args[0];
    		OUTPUT_ZIP_FILE = args[1];
            CommonUtils.cleanDirectory(new File(FilePath.outputFolder));
    		ZipUtils appZip = new ZipUtils();
    	    appZip.generateFileList(new File(SOURCE_FOLDER));
    	    appZip.zipIt(OUTPUT_ZIP_FILE);	
    	}
    }

      
    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    public void zipIt(String outputFolderPath){
     byte[] buffer = new byte[1024];
     try{
    	File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()){ 
        	outputFolder.mkdirs();
    	}
        File zipFilePath = new File(outputFolder, FilePath.outputTemplateName);    
    	FileOutputStream fos = new FileOutputStream(zipFilePath);
    	ZipOutputStream zos = new ZipOutputStream(fos);
    	System.out.println("Output to Zip : " + zipFilePath);
    	for(String file : this.fileList){
    		System.out.println("File Added : " + file);
    		ZipEntry ze= new ZipEntry(file);
        	zos.putNextEntry(ze);
        	FileInputStream in =
                       new FileInputStream(SOURCE_FOLDER + File.separator + file);
        	int len;
        	while ((len = in.read(buffer)) > 0) {
        		zos.write(buffer, 0, len);
        	}
        	in.close();
    	}
    	zos.closeEntry();
    	//remember close it
    	zos.close();
    	System.out.println("Done");
    }catch(IOException ex){
       ex.printStackTrace();
    }
   }

    public void generateFileList(File node) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    private String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }
    
}
