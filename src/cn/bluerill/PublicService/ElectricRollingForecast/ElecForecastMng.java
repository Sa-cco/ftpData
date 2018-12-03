package cn.bluerill.PublicService.ElectricRollingForecast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * 电力预报管理
 * @author zhanglei
 *
 */
public class ElecForecastMng {
	String configPath = "dianliyubao.properties";
	Properties prop = null;
	String tqyb3Path = null;
	String tqyb3ExcelPath = null;
	String ftpPath=null;
	String ftpUsername=null;
	String ftpPassword=null;
	String ftpFilePath=null;
	public ElecForecastMng() {
		prop = loadProp();
		this.tqyb3Path = prop.getProperty("tqyb3");
		this.tqyb3ExcelPath = prop.getProperty("tqyb3Excel");
		this.ftpPath = prop.getProperty("ftpPath");
		this.ftpUsername = prop.getProperty("ftpUsername");
		this.ftpPassword = prop.getProperty("ftpPassword");
		this.ftpFilePath = prop.getProperty("ftpFilePath");
	}
	
	/**
	 * 转换 tqyb3.txt到excel文件
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void convert() throws FileNotFoundException, IOException {
		TXTUtils txtUtils = new TXTUtils();
		ArrayList<String[]> text=new ArrayList<String[]>();
        text=txtUtils.readTxtFile(this.tqyb3Path,"GB2312");
        //System.out.println(text.get(0)[0]);
        Map<String,String[]> map1=txtUtils.txtFile(text);
        String[] datestrmsg = txtUtils.getDate(map1);
        for (String key : datestrmsg) { 
        	  System.out.println("Key = " + key); 
        	  String[] arr = map1.get(key);
        	  for(int i = 0 ; i < arr.length ; i++) {
        		  System.out.print("\t"+arr[i]);
        	  }
        	  System.out.println();
        } 
        
        FileInputStream filein = new FileInputStream(this.tqyb3ExcelPath);
        //使用POIFSFileSystem构造HSSFWorkbook
        POIFSFileSystem fs = new POIFSFileSystem(filein);
        HSSFWorkbook workbook = new HSSFWorkbook(fs);
        HSSFSheet sheet = workbook.getSheetAt(0);
        
        int startRow = 4;
        int startCol = 2;
        for(int i = 0 ; i < datestrmsg.length ; i++) {
        	HSSFRow rowBody = sheet.getRow(i+startRow);
        	String[] arr = map1.get(datestrmsg[i]);
	      	for(int c = 1 ; c < arr.length ; c++) {
	      		rowBody.getCell(startCol+c-1).setCellValue(arr[c]);
	      	}
        }
             
      	HSSFCell cell = sheet.getRow(2).getCell(0);
      	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	cell.setCellValue("发布日期："+sdf.format(new Date()));
    	      
        FileOutputStream fileout = new FileOutputStream(this.tqyb3ExcelPath);
        workbook.write(fileout);
        filein.close();
        fileout.close();
        workbook.close();
	}
	
	public void setTqyb3Path(String path) {
		this.tqyb3Path = path;
		prop.setProperty("tqyb3", this.tqyb3Path);
	}
	
	public void setTqyb3ExcelPath(String path) {
		this.tqyb3ExcelPath = path;
		prop.setProperty("tqyb3Excel", this.tqyb3ExcelPath);
	}
	
	public String getTqyb3Path() {
		if(tqyb3Path == null)
			return "";
		return tqyb3Path;
	}

	public String getFtpPath() {
		if(ftpPath == null)
			return "";
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
		prop.setProperty("ftpPath", this.ftpPath);
	}

	public String getFtpUsername() {
		if(ftpUsername == null)
			return "";
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
		prop.setProperty("ftpUsername", this.ftpUsername);
	}

	public String getFtpPassword() {
		if(ftpPassword == null)
			return "";
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
		prop.setProperty("ftpPassword", this.ftpPassword);
	}

	public String getFtpFilePath() {
		if(ftpFilePath == null)
			return "";
		return ftpFilePath;
	}

	public void setFtpFilePath(String ftpFilePath) {
		this.ftpFilePath = ftpFilePath;
		prop.setProperty("ftpFilePath", this.ftpFilePath);
	}

	public String getTqyb3ExcelPath() {
		if(tqyb3ExcelPath == null)
			return "";
		return tqyb3ExcelPath;
	}
	
	private Properties loadProp() {	
		Properties prop = new Properties();   
		InputStream in = null;
		try{
            //读取属性文件a.properties
			in = new BufferedInputStream(new FileInputStream(configPath));
            prop.load(in);     ///加载属性列表
            in.close();
        }
        catch(Exception e){
            System.out.println(e);
        }finally {
        	
				try {
					if(in != null)
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
		return prop;
	}
	
	public void saveProp() {
		///保存属性到b.properties文件
        FileOutputStream oFile = null;
		try {
			oFile = new FileOutputStream(configPath);
			if( prop != null)
				prop.store(oFile, (new Date()).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
        	
			try {
				if(oFile != null)
					oFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
        
	}
}
