package cn.bluerill.PublicService.ElectricRollingForecast;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TXTUtils {
	public TXTUtils() {
		
	}
	
    /** Java读取txt文件的内容,--开头的自动忽略,返回一个每行为一个String[]的ArrayList集合
     * @param filePath 文件路径
     * @param encoding 编码格式
     * @return 每行为一个String[]的ArrayList集合
     */
    public  ArrayList<String[]> readTxtFile(String filePath, String encoding) {
        ArrayList<String[]> res = new ArrayList<String[]>();
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 编码格式必须和文件的一致
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (!lineTxt.startsWith("--")) {
                        res.add(lineTxt.split("\t"));
                    }
                }
                read.close();
            } else {
                System.out.println("指定的文件不存在");
            }

        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return res;
    }

    /** 将读取到的TXT文件转换成EXCEL需要的格式
     * @param txtfile TXT文件
     * @return 一行EXCEL所需的数据
     */
    public   Map<String,String[]> txtFile(ArrayList<String[]> txtfile) {
        Map<String,String[]> map=new HashMap<>();
        LocalDate today=LocalDate.now();
        int year=today.getYear();
        if (txtfile.isEmpty()){
            return map;
        }

        for (int i=0;txtfile.size()>i;i++){
            if(txtfile.get(i)[0].equals("天 津 市 区")){
                for (int j=1;j<txtfile.size();j++){
                    if (txtfile.get(i+j).length<2){
                        return map;
                    }
                    String t1=txtfile.get(i+j)[0];
                    String t2=txtfile.get(i+j)[1];
                    Map<String,String> map1=getWeather(t2);
                    if(t1.indexOf("-")>=0){
                        String q1=t1.substring(t1.indexOf("-")+1);
                        if(q1.indexOf("月")>=0&&j!=1){
                            String e1=t1.substring(0,t1.indexOf("日")+1);
                            String e2=t1.substring(t1.indexOf("-")+1,t1.lastIndexOf("日")+1);
                            e2=year+"年"+e2;
                            if(!map.containsKey(e1)){
                                map.put(e1,new String[]{"市区",getTime(e1),map1.get("weather"),map1.get("windDirection"),map1.get("windSpeed"),map1.get("lowestTemperature"),map1.get("highestTemperature"),map1.get("precipitation")});
                            }else {
                                String[]strings=getMap(getTime(e1),map.get(e1),map1);
                                map.put(e1,strings);
                            }
                            if(!map.containsKey(e2)){
                                map.put(e2,new String[]{"市区",getTime(e2),map1.get("weather"),map1.get("windDirection"),map1.get("windSpeed"),map1.get("lowestTemperature"),map1.get("highestTemperature"),map1.get("precipitation")});
                            }else {
                                String[]strings=getMap(getTime(e2),map.get(e2),map1);
                                map.put(e2,strings);
                            }
                        }else if(j==1) {
                            String e3=t1.substring(t1.indexOf("-")+1,t1.lastIndexOf("日")+1);
                            e3=year+"年"+e3;
                            if(!map.containsKey(e3)){
                                map.put(e3,new String[]{"市区",getTime(e3),map1.get("weather"),map1.get("windDirection"),map1.get("windSpeed"),map1.get("lowestTemperature"),map1.get("highestTemperature"),map1.get("precipitation")});
                            }else {
                                String[]strings=getMap(getTime(e3),map.get(e3),map1);
                                map.put(e3,strings);
                            }

                        }else {
                            String e4=t1.substring(0,t1.indexOf("日")+1);
                            if(!map.containsKey(e4)){
                                map.put(e4,new String[]{"市区",getTime(e4),map1.get("weather"),map1.get("windDirection"),map1.get("windSpeed"),map1.get("lowestTemperature"),map1.get("highestTemperature"),map1.get("precipitation")});
                            }else {
                                String[]strings=getMap(getTime(e4),map.get(e4),map1);
                                map.put(e4,strings);
                            }
                        }
                    }else {
                        if(!map.containsKey(t1)){
                            map.put(t1,new String[]{"市区",getTime(t1),map1.get("weather"),map1.get("windDirection"),map1.get("windSpeed"),map1.get("lowestTemperature"),map1.get("highestTemperature"),map1.get("precipitation")});
                        }else {
                            String[]strings=getMap(getTime(t1),map.get(t1),map1);
                            map.put(t1,strings);
                        }
                    }
                }

            }
        }
        return map;
    }

    /**
     * 组合没有存储到MAP中的数据
     * @param time
     * @param strings
     * @param map2
     * @return
     */
    public  String[] getMap(String time,String[] strings,Map<String,String> map2){
        strings[0]="市区";
        strings[1]=time;
        strings[2]=map2.get("weather");
        strings[3]=map2.get("windDirection");
        strings[4]=map2.get("windSpeed");
        if(map2.get("lowestTemperature")!=null){
            strings[5]=map2.get("lowestTemperature");
        }
        if(map2.get("highestTemperature")!=null){
            strings[6]=map2.get("highestTemperature");
        }
        if(map2.get("precipitation")!=null){
            strings[7]=map2.get("precipitation");
        }
        return strings;
    }

    /**
     * 将TXT文件第二段数据转化成所需数据
     * @param weather
     * @return
     */
    public  Map<String,String> getWeather(String weather){
        Map<String,String> map=new HashMap<>();
        String[] weather1=weather.split(",");
        if(weather1.length>=1){
            map.put("weather",weather1[0]);
        }
        if(weather1.length>=2){
            map.put("precipitation",getPrecipitation(weather1[1]));
        }
        if(weather1.length>=3){
            map.put("windDirection",weather1[2].substring(0,weather1[2].indexOf("-")-1));
            map.put("windSpeed",weather1[2].substring(weather1[2].indexOf("-")-1));
        }
        if(weather1.length>=4){
            if(weather1[3].indexOf("低")>=0){
                map.put("lowestTemperature",getTemperature(weather1[3]));
            }
            if(weather1[3].indexOf("高")>=0){
                map.put("highestTemperature",getTemperature(weather1[3]));
            }
        }
        if (weather1.length>=5){
            if(weather1[4].indexOf("低")>=0){
                map.put("lowestTemperature",getTemperature(weather1[4]));
            }
            if(weather1[4].indexOf("高")>=0){
                map.put("highestTemperature",getTemperature(weather1[4]));
            }
        }
        return map;
    }

    public String getTemperature(String temperature){
        return temperature.substring(2);
    }
    public String getPrecipitation(String precipitation){
        return precipitation.substring(4);
    }
    public String getTime(String time){
        String a1=time.substring(0,4);
        String a2=time.substring(5,7);
        String a3=time.substring(8,10);
        return a1+"/"+a2+"/"+a3;
    }
    

    /**
     * 时间的插入排序
     *
     */
    public String[] getDate(Map<String,String[]> data){
        try{
            //时间排序  插入排序
            DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            String[] date =  data.keySet().stream().toArray(String[]::new);
            for (int i = 1; i <date.length; ++i) {
                String value = date[i];
                int j = i - 1;
                // 查找插入的位置
                for (; j >= 0; --j) {
                    Date date1=format.parse(value);
                    Date date2=format.parse(date[j]);
                    if (date2.after(date1)) {
                        date[j+1] = date[j];  // 数据移动
                    } else {
                        break;
                    }
                }
                date[j+1] = value; // 插入数据
            }
            return date;
        }catch (ParseException e){
            e.getErrorOffset();
        }
        return null;
    }
}




