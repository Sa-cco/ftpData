import cn.bluerill.PublicService.ElectricRollingForecast.ElecForecastMng;
import cn.bluerill.PublicService.ElectricRollingForecast.TXTUtils;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.springframework.scheduling.annotation.Scheduled;

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

public class Expand {

    //  private static final Logger logger = LoggerFactory.getLogger(Start.class);

    FtpUtils ftpUtils = new FtpUtils();
    TXTUtils txtUtils = new TXTUtils();
    ExcelUtils excelUtils = new ExcelUtils();
    ElecForecastMng elecForecastMng = new ElecForecastMng();
    Display display = Display.getDefault();
    Shell shell = new Shell(SWT.MIN);
    TableViewerContentProvider tableViewerContentProvider = new TableViewerContentProvider();
    TableViewerLabelProvider tableViewerLabelProvider = new TableViewerLabelProvider();
    TableViewer tableViewer = new TableViewer(shell,
            SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);



    //FTP服务器文件目录路径
    private String pathname =elecForecastMng.getFtpFilePath();
    //TXT文件名称
    private String filename = "tqyb3.txt";
    //TXT文件下载后的文件路径
    private String tqyb3Path=elecForecastMng.getTqyb3Path();
    //EXCEL存储路径
    private String tqyb3ExcelPath = elecForecastMng.getTqyb3ExcelPath();
    private Date timeBefore = new Date();
    boolean aBoolean = false;
    private java.util.List<String> list = new ArrayList<>();
    private int i = 1;


    public Expand() {

    }


    public void start1() {

        shell.setText("电力滚动预报");
        shell.setSize(400, 400);
        shell.setLayout(new FillLayout());
        ExpandBar expandBar = new ExpandBar(shell, SWT.V_SCROLL);
        {
            //  Composite comp = new Composite(expandBar, SWT.NONE);

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
                    } catch (Exception e2) {
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
                        monitorFtp1();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        showErrMsg(e2.getMessage());
                    }
                }
            });
            btnHuanghuaMorning.setText("手动更新");
            expandItem1.setHeight(99);


            Table table = tableViewer.getTable();
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            table.setBounds(10, 10, 111, 111);
            TableLayout tLayout = new TableLayout();
            table.setLayout(tLayout);

            tLayout.addColumnData(new ColumnWeightData(30));
            new TableColumn(table, SWT.NONE).setText("更新时间");

            tableViewer.setContentProvider(tableViewerContentProvider);
            tableViewer.setLabelProvider(tableViewerLabelProvider);
            tableViewer.setInput(list);


        }

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
                        SWT.OK |
                                SWT.CANCEL |
                                SWT.ICON_WARNING);
        messageBox.setMessage(msg);
        messageBox.open();
    }


    /**
     * 监测FTP服务文件任务
     */
    public  void monitorFtp() {
        //FTP服务器IP
        pathname =elecForecastMng.getFtpPath();
        //TXT文件下载后的文件路径
        tqyb3Path=elecForecastMng.getTqyb3Path();
        //EXCEL存储路径
        tqyb3ExcelPath = elecForecastMng.getTqyb3ExcelPath();

        if (i == 1) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.YEAR, -1);
            Date y = c.getTime();
            timeBefore = y;
        }

        //链接FTP服务器
        ftpUtils.initFtpClient();

        Date timenow = null;
        //获取TXT文件更新时间
        timenow = ftpUtils.fileTime(pathname, filename);
        if (!timeBefore.before(timenow)) {
            return;
        }
        timeBefore = timenow;
        File file = new File(tqyb3Path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        //下载TXT文件到本地
        aBoolean = ftpUtils.downloadFile(pathname, filename, tqyb3Path);
        if (aBoolean == false) {
            //    logger.info("下载TXT文件失败"+ Instant.now());
            return;
        }
        ArrayList<String[]> text = new ArrayList<String[]>();

        text = txtUtils.readTxtFile(tqyb3Path, "GB2312");
        System.out.println(text.get(0)[0]);
        Map<String, String[]> map1 = txtUtils.txtFile(text);

        try {
            OutputStream out = new FileOutputStream(tqyb3ExcelPath);
            excelUtils.exportQuestionExcel(map1, out);
            System.out.println("Excel生成完成" + Instant.now());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Excel生成失败" + Instant.now());
        }
        DateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
        list.add(format.format(new Date()));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                tableViewer.refresh();
            }
        });
        i++;
        return;

    }


    /**
     * 单击事件调用方法，重新下载数据
     */
    public void monitorFtp1() {
        //FTP服务器IP
        pathname =elecForecastMng.getFtpPath();
        //TXT文件下载后的文件路径
        tqyb3Path=elecForecastMng.getTqyb3Path();
        //EXCEL存储路径
        tqyb3ExcelPath = elecForecastMng.getTqyb3ExcelPath();


        //链接FTP服务器
        ftpUtils.initFtpClient();

        Date timenow = null;
        //获取TXT文件更新时间
        timenow = ftpUtils.fileTime(pathname, filename);

        timeBefore = timenow;
        File file = new File(tqyb3Path + filename);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        //下载TXT文件到本地
        aBoolean = ftpUtils.downloadFile(pathname, filename, tqyb3Path);
        if (aBoolean == false) {
            //  logger.info("下载TXT文件失败"+ Instant.now());
            return;
        }
        ArrayList<String[]> text = new ArrayList<String[]>();
        text = txtUtils.readTxtFile(tqyb3Path, "gbk");
        System.out.println(text.get(0)[0]);
        Map<String, String[]> map1 = txtUtils.txtFile(text);
        try {
            OutputStream out = new FileOutputStream(tqyb3ExcelPath);
            excelUtils.exportQuestionExcel(map1, out);
            System.out.println("Excel生成完成" + Instant.now());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Excel生成失败" + Instant.now());
        }
        DateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
        list.add(format.format(new Date()));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                tableViewer.refresh();
            }
        });


        return;
    }

    public static void main(String[] args) {
        Task task=new Task();
        Timer timer = new Timer(true);
        timer.schedule(task,1000,6000*10*10);
        task.getTask().start1();
    }



}


