import cn.bluerill.PublicService.ElectricRollingForecast.ElecForecastMng;
import cn.bluerill.PublicService.ElectricRollingForecast.TXTUtils;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class Start {

  //  private static final Logger logger = LoggerFactory.getLogger(Start.class);

    //FTP服务器文件目录路径
    private String pathname="/wangliqiang/";
    //TXT文件名称
    private String filename="tqyb3.txt";
    //TXT文件下载后的文件路径
    private String tqyb3Path;
    //EXCEL存储路径
    private String tqyb3ExcelPath;

    private Date timeBefore = new Date();
    boolean aBoolean=false;

    private java.util.List<String> list=new ArrayList<>();

    private int i=1;


    public Start(){
        ElecForecastMng elecForecastMng=new ElecForecastMng();
        tqyb3Path=elecForecastMng.getTqyb3Path();
        tqyb3ExcelPath=elecForecastMng.getTqyb3ExcelPath();
    }




    Shell shell=null;
    public void start1() {
         ElecForecastMng elecForecastMng =  new ElecForecastMng();

        final Display display = Display.getDefault();
        shell = new Shell(SWT.MIN);
        shell.setText("电力滚动预报");
        shell.setSize(600, 400);
        shell.setLayout(new FillLayout());
        ExpandBar expandBar = new ExpandBar(shell, SWT.V_SCROLL);
        {
            Composite comp = new Composite(expandBar, SWT.NONE);
            comp.setBounds(0, 0, 200, 380);


            ExpandItem expandItem1 = new ExpandItem(expandBar, SWT.NONE);
            expandItem1.setExpanded(true);
            expandItem1.setText("电力滚动预报");

            Composite composite = new Composite(expandBar, SWT.NONE);
            expandItem1.setControl(composite);
            composite.setLayout(new FillLayout(SWT.VERTICAL));

            Button btnTqyb3Excel = new Button(composite, SWT.NONE);
            btnTqyb3Excel.addMouseListener(new MouseAdapter() {
                public void mouseUp(MouseEvent e) {
                    try {
                        elecForecastMng.convert();
                        Program.launch(elecForecastMng.getTqyb3ExcelPath());
                    }catch(Exception e2) {
                        e2.printStackTrace();
                        showErrMsg(e2.getMessage());
                    }
                }
            });
            btnTqyb3Excel.setText("打开Excel文件");

            Button btnTqyb3 = new Button(composite, SWT.NONE);
            btnTqyb3.addMouseListener(new MouseAdapter() {
                public void mouseUp(MouseEvent e) {
                    Program.launch(elecForecastMng.getTqyb3Path());
                }
            });
            btnTqyb3.setText("打开tqyb3");

            Button btnElecSetting = new Button(composite, SWT.NONE);
            btnElecSetting.addMouseListener(new MouseAdapter() {
                public void mouseUp(MouseEvent e) {
                    DlgElecSetting dlg = new DlgElecSetting(shell, elecForecastMng);
                    dlg.open();
                    dlg.close();
                }
            });
            btnElecSetting.setText("设置");



            Button btnHuanghuaMorning = new Button(composite, SWT.NONE);
            btnHuanghuaMorning.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseUp(MouseEvent e) {
                    try {
                        monitorFtp();
                    }catch(Exception e2) {
                        e2.printStackTrace();
                        showErrMsg(e2.getMessage());
                    }
                }
            });
            btnHuanghuaMorning.setText("手动更新");
            expandItem1.setHeight(99);



            new Label(comp, SWT.NONE);
            Group group1 = new Group(comp, SWT.NONE);
            group1.setText("更新时间");
            // group.setBounds(10, 150, 373, 160);


            final TableViewer tableViewer = new TableViewer(group1,
                    SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

            Table table = tableViewer.getTable();
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            table.setBounds(152, 13, 430, 350);
            TableLayout tLayout = new TableLayout();
            table.setLayout(tLayout);

            tLayout.addColumnData(new ColumnWeightData(30));
            new TableColumn(table, SWT.NONE).setText("safgasg");


            tableViewer.setContentProvider(new TableViewerContentProvider());
            tableViewer.setLabelProvider(new TableViewerLabelProvider());
            List<String> list1=new ArrayList<>();
            list1.add("sssssssssssssssssss");
            list1.add("AAAAAAAAAAAAAA");
            tableViewer.setInput(list1);


            ExpandItem item1 = new ExpandItem(expandBar, SWT.NONE);
            item1.setText("asdf");
            item1.setHeight(300);
            item1.setControl(comp);


         /*   timer=new Timer(1000,new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    tableViewer.refresh();
                }
            });
            timer.start();*/

        }



        // shell.layout();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

     void showErrMsg(String msg) {
        MessageBox messageBox =
                new MessageBox(shell,
                        SWT.OK|
                                SWT.CANCEL|
                                SWT.ICON_WARNING);
        messageBox.setMessage(msg);
        messageBox.open();
    }


    /**
     * 监测FTP服务文件任务
     */
    // @Scheduled(cron ="0/10 * * * * ?")
    @Scheduled( fixedRate = 1000*60*10)
    public  void monitorFtp(){
       Start start= new Start();
        if(i==1){

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.YEAR, -1);
            Date y = c.getTime();
            timeBefore=y;
        }

        //链接FTP服务器
        FtpUtils ftpUtils=new FtpUtils();
        ftpUtils.initFtpClient();

        Date timenow=null;
        //获取TXT文件更新时间
        timenow=ftpUtils.fileTime(pathname,filename);
        if(!timeBefore.before(timenow)){
            return ;
        }
        timeBefore=timenow;
        File file = new File(tqyb3Path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        //下载TXT文件到本地
        aBoolean=ftpUtils.downloadFile(pathname,filename,tqyb3Path);
        if(aBoolean==false){
        //    logger.info("下载TXT文件失败"+ Instant.now());
            return ;
        }
        ArrayList<String[]> text=new ArrayList<String[]>();
        TXTUtils txtUtils=new TXTUtils();

        text=txtUtils.readTxtFile(tqyb3Path,"gbk");
        System.out.println(text.get(0)[0]);
        Map<String,String[]> map1=txtUtils.txtFile(text);
        ExcelUtils excelUtils=new ExcelUtils();
        try{
            OutputStream out = new FileOutputStream(tqyb3ExcelPath);
            excelUtils.exportQuestionExcel(map1,out);
         //   logger.info("Excel生成完成"+ Instant.now());
            System.out.println("Excel生成完成"+ Instant.now());
        }catch (IOException e) {
            e.printStackTrace();
         //   logger.info("Excel生成失败"+ Instant.now());
            System.out.println("Excel生成失败"+ Instant.now());
        }
        DateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
        list.add(format.format(new Date()));
        if (i==1){
            start.start1();
            i++;
        }
        return ;

    }


    /**
          * 内容器(写成了一个内部类). 在这里对所有记录集中的记录进行处理
          */
    private static final class MyContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object element) {
            if (element instanceof List)
                return ((List) element).toArray();//将List转化为数组
            else
                return new Object[0];//否则,返回一个空数组
        }

        public void dispose() {}
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }

    /**
          * 标签器(写成了一个内部类).在这里对单条记录进行处理
          */
    private static final class MyLabelProvider implements ITableLabelProvider {


        /**
                  * 这个方法返回的是各列的记录的文字
                  * 参数1:输入的对象
                  * 参数2:列号
                  * 返回值:注意一定要避免Null值,否则出错
                  */
        public String getColumnText(Object element, int col) {
            String  o = (String) element; //转换一下类型
            if (col == 0)
                return o;
            return "";
        }
        /**
          * 返回每条记录前面的图标
          */
        @Override
        public Image getColumnImage(Object o, int i) {
            return null;
        }
        //-------------以下方法用处不大,暂时不管它-----------------
        public void addListener(ILabelProviderListener listener) {}
        public void dispose() {}
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        public void removeListener(ILabelProviderListener listener) {}
    }




    public static void main(String[] args) {
        Start start=new Start();
      //  start.monitorFtp();
        start.start1();
    }
}



