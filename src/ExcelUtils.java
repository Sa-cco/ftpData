import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class ExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    /**
     * 将TXT文件数据导入excel文件中去
     *
     * @param data 数据
     * @param out  输出流
     */
    public  void exportQuestionExcel(Map<String, String[]> data,OutputStream out ) {
        try {
            //得到有序的排序时间
            String[]date=getDate(data);
            //1.创建工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();
            //创建工作表
            HSSFSheet sheet = workbook.createSheet("电力营销七天滚动预报");
            //导入图片及表格样式设置
            infoImage(workbook,sheet,out);
            //组织sheet的title
            getSheetTitleByType(workbook,sheet, true);
            //

            //组织sheet的body
            for (int i=0;i<date.length;i++){

                HSSFRow rowBody = sheet.createRow(i + 4);
                //
                HSSFCellStyle colStyle = createCellStyle(workbook, (short) 12,1,sheet);
                HSSFCell cell2 =rowBody.createCell(2);
                HSSFCell cell3 =rowBody.createCell(3);
                HSSFCell cell4 =rowBody.createCell(4);
                HSSFCell cell5 =rowBody.createCell(5);
                HSSFCell cell6 =rowBody.createCell(6);
                HSSFCell cell7 =rowBody.createCell(7);
                HSSFCell cell8 =rowBody.createCell(8);

                cell2.setCellStyle(colStyle);
                cell3.setCellStyle(colStyle);
                cell4.setCellStyle(colStyle);
                cell5.setCellStyle(colStyle);
                cell6.setCellStyle(colStyle);
                cell7.setCellStyle(colStyle);
                cell8.setCellStyle(colStyle);
                //组织sheet的body
                cell2.setCellValue(data.get(date[i])[1]);
                cell3.setCellValue(data.get(date[i])[2]);
                cell4.setCellValue(data.get(date[i])[3]);
                cell5.setCellValue(data.get(date[i])[4]);
                cell6.setCellValue(data.get(date[i])[5]);
                cell7.setCellValue(data.get(date[i])[6]);
                cell8.setCellValue(data.get(date[i])[7]);
                if(i==6){
                    //在sheet里增加“市区”所需的合并单元格
                    CellRangeAddress cra3=new CellRangeAddress(3, 10, 1, 1);
                    sheet.addMergedRegion(cra3);
                    // 使用RegionUtil类为合并后的单元格添加边框
                    RegionUtil.setBorderBottom(BorderStyle.THIN, cra3, sheet); // 下边框
                    RegionUtil.setBorderLeft(BorderStyle.THIN, cra3, sheet); // 左边框
                    RegionUtil.setBorderRight(BorderStyle.THIN, cra3, sheet); // 右边框
                    RegionUtil.setBorderTop(BorderStyle.THIN, cra3, sheet); // 上边框
                }
            }
            //输出
            workbook.write(out);
            workbook.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("数据写入EXCEL失败"+ Instant.now());
        }finally{
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public  void infoImage(HSSFWorkbook workbook, HSSFSheet sheet, OutputStream out) {
        FileOutputStream fileOut = null;
        BufferedImage bufferImg = null;
        //在sheet里增加图片所需的合并单元格
        CellRangeAddress cra=new CellRangeAddress(0, 0, 0, 8);
        sheet.addMergedRegion(cra);
        // 设置图片所在行的高度
        Row row = sheet.createRow(0);
        row.setHeightInPoints(228);

        //设置列宽
        sheet.setColumnWidth(0,(int)((5 + 0.72) * 256));
        sheet.setColumnWidth(1,(int)((10 + 0.72) * 256));
        sheet.setColumnWidth(2,(int)((12 + 0.72) * 256));
        sheet.setColumnWidth(3,(int)((24 + 0.72) * 256));
        sheet.setColumnWidth(4,(int)((18 + 0.72) * 256));
        sheet.setColumnWidth(5,(int)((19 + 0.72) * 256));
        sheet.setColumnWidth(6,(int)((12 + 0.72) * 256));
        sheet.setColumnWidth(7,(int)((11 + 0.72) * 256));
        sheet.setColumnWidth(8,(int)((11 + 0.72) * 256));

        //先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
        try {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

       //     InputStream path = this.getClass().getResourceAsStream("image/1.jpg");

          // String path = Thread.currentThread().getContextClassLoader().getResource("image/1.jpg").getPath();
          //  String path =getDefaultClassLoader().getResource("image/1.jpg").getPath();

          //  File file = ResourceUtils.getFile("classpath:image/1.jpg");
          /*  ClassPathResource classPathResource=new ClassPathResource("image/1.jpg");
            File file= classPathResource.getFile();
            file.getPath();*/

            InputStream stream=getClass().getClassLoader().getResourceAsStream("image/1.jpg");


          //  System.out.println("======================================"+file.getPath());
            bufferImg = ImageIO.read(stream);
            ImageIO.write(bufferImg, "jpg", byteArrayOut);

            //画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
            //anchor主要用于设置图片的属性
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 228, 255,(short) 0, 0,  (short) 8, 0);
            // anchor.setAnchorType();
            //插入图片
            patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
            // 写入excel文件
           // workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            logger.info("数据时间排序失败"+ Instant.now());
        }
        return null;
    }

    /**
     * 导入Excel标题
     *
     * @param workbook
     * @return
     */
    public  void getSheetTitleByType(HSSFWorkbook workbook, HSSFSheet sheet, Boolean isPaper) {
        //设置一级表头
        setExcelTitle1(sheet,workbook);
        //设置二级表头
        setExcelTitle2(sheet,workbook);
        //设置三级表头
        setExcelTitle3(sheet,workbook);
    }

    public  void setExcelTitle1(HSSFSheet sheet, HSSFWorkbook workbook) {
        HSSFRow row = sheet.createRow(1);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("电力营销七天滚动预报：");
        cell.setCellStyle(createCellStyle(workbook,(short) 20, 0, sheet));
        //在sheet里增加一级表头所需的合并单元格
        CellRangeAddress cra1=new CellRangeAddress(1, 1, 0, 8);
        sheet.addMergedRegion(cra1);
        // 设置一级表头所在行的高度
        row.setHeightInPoints(28);

    }
    public  void setExcelTitle2(HSSFSheet sheet, HSSFWorkbook workbook) {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        HSSFRow rowBody = sheet.createRow(2);
        HSSFCell cell = rowBody.createCell(0);
        cell.setCellValue("发布日期："+format.format(new Date()));
        cell.setCellStyle(createCellStyle(workbook, (short) 12,2,sheet));
        //在sheet里增加二级表头所需的合并单元格
        CellRangeAddress cra2=new CellRangeAddress(2, 2, 0, 8);
        sheet.addMergedRegion(cra2);
        // 设置级表头所在行的高度
        rowBody.setHeightInPoints(43);
    }

    public  void setExcelTitle3(HSSFSheet sheet, HSSFWorkbook workbook) {
        HSSFRow rowTitle=sheet.createRow(3);
        HSSFCell cell = rowTitle.createCell(1);
        cell.setCellValue("市区");
        cell.setCellStyle(createCellStyle(workbook, (short) 16,3,sheet));
        String[] titles = null;
        String date = "日期";
        String weather = "天气";
        String windDirection = "风向";
        String windSpeed = "风速";
        String lowestTemperature = "最低气温";
        String highestTemperature = "最高气温";
        String precipitation = "降水概率";
        titles = new String[]{ date, weather, windDirection, windSpeed, lowestTemperature, highestTemperature, precipitation};
        HSSFCellStyle colStyle = createCellStyle(workbook,(short) 12, 1, sheet);
        HSSFCell cell1 = rowTitle.createCell(2);
        HSSFCell cell2 = rowTitle.createCell(3);
        HSSFCell cell3 = rowTitle.createCell(4);
        HSSFCell cell4 = rowTitle.createCell(5);
        HSSFCell cell5 = rowTitle.createCell(6);
        HSSFCell cell6 = rowTitle.createCell(7);
        HSSFCell cell7 = rowTitle.createCell(8);
        //加入数据
        cell1.setCellValue(titles[0]);
        cell2.setCellValue(titles[1]);
        cell3.setCellValue(titles[2]);
        cell4.setCellValue(titles[3]);
        cell5.setCellValue(titles[4]);
        cell6.setCellValue(titles[5]);
        cell7.setCellValue(titles[6]);
        //加载单元格样式
        cell1.setCellStyle(colStyle);
        cell2.setCellStyle(colStyle);
        cell3.setCellStyle(colStyle);
        cell4.setCellStyle(colStyle);
        cell5.setCellStyle(colStyle);
        cell6.setCellStyle(colStyle);
        cell7.setCellStyle(colStyle);
    }

    /**
     * @param workbook
     * @param fontsize
     * @return 单元格样式
     */
    public HSSFCellStyle createCellStyle(HSSFWorkbook workbook, short fontsize, int t, HSSFSheet sheet) {
        HSSFCellStyle style = workbook.createCellStyle();
        //边框
        if(t==1){
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
        }
        if (t==3){
            style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        }

        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        //创建字体
        HSSFFont font = workbook.createFont();
        if(t!=1){
            font.setBold(true);
        }
        font.setFontHeightInPoints(fontsize);
        //加载字体
        style.setFont(font);
        return style;
    }




















}
