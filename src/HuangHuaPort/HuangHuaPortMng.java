package HuangHuaPort;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.meteoinfo.data.meteodata.DataInfo;
import org.meteoinfo.data.meteodata.MeteoDataInfo;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class HuangHuaPortMng {
	String configPath = "huanghu.properties";
	Properties prop = null;
	String ecthinDirPath = null;
	String morningXls = null;
	String afternoonXls = null;

	public enum ForecastType { Morning , Afternoon };
	
	//时序列表
	List<LocalDateTime> timeList = new ArrayList<LocalDateTime>(24);
	List<String> windDirList = new ArrayList<String>(24);
	List<Double> windVelList = new ArrayList<Double>(24);
	List<Double> gustVelList = new ArrayList<Double>(24);
	
	//风电场海域（118°45.1′～118°51.3′E，38°55.2′～39°3.9′N）'与micaps看到的信息不相符 117.75,38.5
//	double minLon = 118.75166666666667;
//	double maxLon = 118.855;
//	double minLat = 38.92;
//	double maxLat = 39.065;
	double minLon = 117.75;
	double maxLon = 117.755;
	double minLat = 38.5;
	double maxLat = 38.55;
	
	double windDir ;
	double windVel ; 
	double windVelOffset = 4.0;	//风速增加偏移，预报的风速总是偏小
	double gustVel;	//阵风风速，默认1.2倍
	
	int[] titleExcelIdxArr ;
	int[] morningExcelIdxArr = {1,6,14,21};
	int[] afternoonExcelIdxArr = {1,3,11,19};
	
	public HuangHuaPortMng() {
		prop = loadProp();
		this.ecthinDirPath = prop.getProperty("ecthin");
		this.morningXls = prop.getProperty("morningXls");
		this.afternoonXls = prop.getProperty("afternoonXls");
	}
	
	
	
	public void convert(ForecastType ft) throws Exception {
		LocalDateTime nowTime = LocalDateTime.now();
		nowTime = LocalDateTime.of(nowTime.getYear(), nowTime.getMonth(), nowTime.getDayOfMonth(), 0, 0, 0);
		LocalDateTime startForeTime = nowTime;
		String excelfilePath = "";
		switch (ft) {
			case Morning:
				nowTime = nowTime.plusHours(11);
				startForeTime = nowTime.minusHours(12+3);
				excelfilePath = this.morningXls;
				titleExcelIdxArr = morningExcelIdxArr;
				break;
			case Afternoon:
				nowTime = nowTime.plusHours(20);
				startForeTime = nowTime.minusHours(12);
				excelfilePath = this.afternoonXls;
				titleExcelIdxArr = afternoonExcelIdxArr;
				break;
			default:
				return;
		}
		
		timeList.clear();
		windDirList.clear();
		windVelList.clear();
		gustVelList.clear();
		for(int i = 0 ; i < 24 ; i++) {
			LocalDateTime dt = nowTime.plusHours(i*3);
			timeList.add(dt);
			String filename = getForecastFileName(dt, startForeTime);
			if (filename != null) {
				probeWind(filename);
				windDirList.add(getWirdDirStr(windDir));
				windVelList.add(windVel);
				gustVelList.add(gustVel);
			}
			else {
				windDirList.add("/");
				windVelList.add(0.0);
				gustVelList.add(0.0);
			}
		}
		
		exportExcel(excelfilePath);//更新excel文件内容
	}
	
	/**
	 * 输出到excel
	 * @param filepath
	 * @throws Exception 
	 */
	private void exportExcel(String filepath) throws Exception {
		FileInputStream filein = new FileInputStream(filepath);
        //POIFSFileSystem fs = new POIFSFileSystem(filein);
        XSSFWorkbook workbook = new XSSFWorkbook(filein);
        XSSFSheet sheet = workbook.getSheetAt(0);
        
         for(int col = 1; col <= 24 ; col++){
        	 XSSFRow row3 = sheet.getRow(3);
        	 XSSFRow row4 = sheet.getRow(4);
        	 XSSFRow row5 = sheet.getRow(5);
        	 row3.getCell(col).setCellValue(windDirList.get(col-1));
        	 row4.getCell(col).setCellValue(Math.round(windVelList.get(col-1)) );
        	 row5.getCell(col).setCellValue(Math.round(gustVelList.get(col-1)) );
        }
         
         workbook.setForceFormulaRecalculation(true);
        
        //复制数据
        FormulaEvaluator eval=new XSSFFormulaEvaluator(workbook);;
        for(int row=21 ; row <= 22 ; row++) {
        	XSSFRow row_1 = sheet.getRow(row);
        	XSSFRow row_2 = sheet.getRow(row+15);
        	for(int col = 1; col <= 24 ; col++){
        		try {
        			double val  = row_1.getCell(col).getNumericCellValue();
        			row_2.getCell(col).setCellValue(val);
        		}
        		catch (Exception e) {
        			row_2.getCell(col).setCellValue("/");
				}
        	}
        }
        
        //设置日期显示
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM月dd日");
        String dt1 = timeList.get(0).format(formatter);
        sheet.getRow(1).getCell(titleExcelIdxArr[0]).setCellValue(dt1);
        sheet.getRow(1).getCell(titleExcelIdxArr[0]).getCellStyle().setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        sheet.getRow(18).getCell(titleExcelIdxArr[0]).setCellValue(dt1);
        sheet.getRow(18).getCell(titleExcelIdxArr[0]).getCellStyle().setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        String dt2 = timeList.get(5).format(formatter);
        sheet.getRow(1).getCell(titleExcelIdxArr[1]).setCellValue(dt2);
        sheet.getRow(1).getCell(titleExcelIdxArr[1]).getCellStyle().setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        sheet.getRow(18).getCell(titleExcelIdxArr[1]).setCellValue(dt2);
        sheet.getRow(18).getCell(titleExcelIdxArr[1]).getCellStyle().setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        String dt3 = timeList.get(13).format(formatter);
        sheet.getRow(1).getCell(titleExcelIdxArr[2]).setCellValue(dt3);
        sheet.getRow(1).getCell(titleExcelIdxArr[2]).getCellStyle().setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        sheet.getRow(18).getCell(titleExcelIdxArr[2]).setCellValue(dt3);
        sheet.getRow(18).getCell(titleExcelIdxArr[2]).getCellStyle().setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        String dt4 = timeList.get(21).format(formatter);
        sheet.getRow(1).getCell(titleExcelIdxArr[3]).setCellValue(dt4);
        sheet.getRow(1).getCell(titleExcelIdxArr[3]).getCellStyle().setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        sheet.getRow(18).getCell(titleExcelIdxArr[3]).setCellValue(dt4);
        sheet.getRow(18).getCell(titleExcelIdxArr[3]).getCellStyle().setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
      
        FileOutputStream fileout = new FileOutputStream(filepath);
        workbook.write(fileout);
        filein.close();
        fileout.close();
        workbook.close();
	}
	
	private String getForecastFileName(LocalDateTime dt, LocalDateTime startDt) {
		Duration duration = Duration.between( startDt , dt);
		Long hours = duration.toHours();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHH");
		String ttStr = "";
		String startStr = "";
		if(hours < 0)
			return null;
		else if(hours <= 72 ) {
			ttStr = String.format("%03d", hours);
			startStr = startDt.format(formatter);
		}else if(hours <= 240) {
			startStr = startDt.format(formatter);
			if(hours % 6 == 0)
				ttStr = String.format("%03d", hours);
			else
				ttStr = String.format("%03d", hours + 3);
		}else
			return null;
			
		String filename = startStr + "." + ttStr;
		File fileU = new File(this.ecthinDirPath +"/U/1000/"+filename);
		File fileV = new File(this.ecthinDirPath +"/V/1000/"+filename);
		if(!fileU.exists() || !fileV.exists())
			return getForecastFileName(dt, startDt.minusHours(12));
		else
			return filename;
	}
	
	/**
	 * 探测预报文件中对应的风力
	 * @param filepath
	 */
	private void probeWind(String filename) {
		double u,v;
		int xIdx=0,yIdx=0;
		String filepath = this.ecthinDirPath +"/U/1000/"+filename;
		MeteoDataInfo meteoDataInfo = new MeteoDataInfo();
		meteoDataInfo.openMICAPSData(filepath);
		meteoDataInfo.setDimensionSet(org.meteoinfo.data.meteodata.PlotDimension.Lat_Lon);
		DataInfo dataInfo = meteoDataInfo.getDataInfo();
		int timeNum = dataInfo.getTimeNum();
		double[] xLon = dataInfo.getXDimension().getValues();
		double[] yLat = dataInfo.getYDimension().getValues();
		for(int x = 0 ; x < xLon.length ; x++) {
			for(int y = 0 ; y < yLat.length ; y++) {
				if( xLon[x] >= minLon && xLon[x] <= maxLon 
						&& yLat[y] >= minLat && yLat[y] <= maxLat) {
					//System.out.println(x+","+y + "--" + xLon[x] + "," + yLat[y]);
					xIdx = x;
					yIdx = y;
					break;
				}
			}
		}
		
		u = meteoDataInfo.getGridData().data[yIdx][xIdx];
		
		filepath = this.ecthinDirPath +"/V/1000/"+filename;
		meteoDataInfo = new MeteoDataInfo();
		meteoDataInfo.openMICAPSData(filepath);
		meteoDataInfo.setDimensionSet(org.meteoinfo.data.meteodata.PlotDimension.Lat_Lon);
		
		v = meteoDataInfo.getGridData().data[yIdx][xIdx];
		
		windVel = Math.sqrt(u*u + v*v);
		windVel = windVel + windVelOffset;
		gustVel = windVel * 1.2;
		
		windDir = 180+Math.atan2(u,v)*180/Math.PI;
		//System.out.println(u+","+v + "--" + windDir );
	}
	
	private String getWirdDirStr(double dir) {
		if(dir < 0)
			dir = dir + 360;
		dir = dir % 360;
		
		if(dir <= 22.5)
			return "N";
		else if(dir <= 67.5)
			return "NE";
		else if(dir <= 112.5)
			return "E";
		else if(dir <= 157.5)
			return "SE";
		else if(dir <= 202.5)
			return "S";
		else if(dir <= 247.5)
			return "SW";
		else if(dir <= 292.5)
			return "W";
		else if(dir <= 337.5)
			return "NW";
		else
			return "N";
	}
	
	public void setEcthinDirPath(String path) {
		this.ecthinDirPath = path;
		prop.setProperty("ecthin", this.ecthinDirPath);
	}
	
	public String getEcthinDirPath() {
		return this.ecthinDirPath;
	}
	
	public void setMorningXls(String path) {
		this.morningXls = path;
		prop.setProperty("morningXls", this.morningXls);
	}
	
	public String getMorningXls() {
		return this.morningXls;
	}
	
	public void setAfternoonXls(String path) {
		this.afternoonXls = path;
		prop.setProperty("afternoonXls", this.afternoonXls);
	}
	
	public String getAfternoonXls() {
		return this.afternoonXls;
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
