import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import cn.bluerill.PublicService.ElectricRollingForecast.ElecForecastMng;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DlgElecSetting extends Dialog {
	private Text txtTqyb3;
	private Text txtExcel;
	private Text ftpPath;
	private Text ftpUsername;
	private Text ftpPassword;
	private Text ftpFilePath;
	
	private ElecForecastMng mng;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DlgElecSetting(Shell parentShell, ElecForecastMng mng ) {
		super(parentShell);
		this.mng = mng;
		
	}
	

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		container.getShell().setText("电力滚动预报设置");
		
		Label lblTqyb = new Label(container, SWT.NONE);
		lblTqyb.setBounds(10, 13, 118, 20);
		lblTqyb.setText("tqyb3\u6587\u4EF6\u8DEF\u5F84\uFF1A");
		
		txtTqyb3 = new Text(container, SWT.BORDER);
		txtTqyb3.setBounds(134, 10, 239, 26);
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				FileDialog fd=new FileDialog( getParentShell(),SWT.OPEN);
				fd.setFilterPath(System.getProperty("JAVA.HOME")); 
				fd.setFilterExtensions(new String[]{"*.txt","*.*"}); 
				fd.setFilterNames(new String[]{"Text Files(*.txt)","All Files(*.*)"}); 
				String file=fd.open(); 
				if( file != null ) { 
					txtTqyb3.setText(file);
				}
 
			}
		});
		btnNewButton.setBounds(379, 8, 98, 30);
		btnNewButton.setText("选择");
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u7535\u529B\u6587\u4EF6\u8DEF\u5F84\uFF1A");
		label.setBounds(23, 56, 98, 20);
		
		txtExcel = new Text(container, SWT.BORDER);
		txtExcel.setBounds(134, 53, 239, 26);
		
		Button button = new Button(container, SWT.NONE);
		button.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				FileDialog fd=new FileDialog( getParentShell(),SWT.OPEN);
				fd.setFilterPath(System.getProperty("JAVA.HOME")); 
				fd.setFilterExtensions(new String[]{"*.xls","*.*"}); 
				fd.setFilterNames(new String[]{"Excel Files(*.xls)","All Files(*.*)"}); 
				String file=fd.open(); 
				if( file != null ) { 
					txtExcel.setText(file);
				}
			}
		});
		button.setText("选择");
		button.setBounds(379, 51, 98, 30);


		Label labe2 = new Label(container, SWT.NONE);
		labe2.setText("FTP的IP:");
		labe2.setBounds(23, 102, 98, 20);
		ftpPath = new Text(container, SWT.BORDER);
		ftpPath.setBounds(134, 96, 239, 26);

		Label labe3 = new Label(container, SWT.NONE);
		labe3.setText("FTP账号:");
		labe3.setBounds(23, 148, 98, 20);
		ftpUsername = new Text(container, SWT.BORDER);
		ftpUsername.setBounds(134, 139, 239, 26);

		Label labe4 = new Label(container, SWT.NONE);
		labe4.setText("FTP密码:");
		labe4.setBounds(23, 188, 98, 20);
		ftpPassword = new Text(container, SWT.BORDER);
		ftpPassword.setBounds(134, 182, 239, 26);

		Label labe5 = new Label(container, SWT.NONE);
		labe5.setText("FTP文件路径:");
		labe5.setBounds(23, 225, 98, 20);
		ftpFilePath = new Text(container, SWT.BORDER);
		ftpFilePath.setBounds(134, 225, 239, 26);

	
		initVal();
		
		return container;
	}
	
	private void initVal() {
		txtTqyb3.setText(mng.getTqyb3Path());
		txtExcel.setText(mng.getTqyb3ExcelPath());
		ftpPath.setText(mng.getFtpPath());
		ftpUsername.setText(mng.getFtpUsername());
		ftpPassword.setText(mng.getFtpPassword());
		ftpFilePath.setText(mng.getFtpFilePath());
	}
	
	private void saveVal() {
		mng.setTqyb3Path(txtTqyb3.getText());
		mng.setTqyb3ExcelPath(txtExcel.getText());
		mng.setFtpPath(ftpPath.getText());
		mng.setFtpUsername(ftpUsername.getText());
		mng.setFtpPassword(ftpPassword.getText());
		mng.setFtpFilePath(ftpFilePath.getText());
		mng.saveProp();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				saveVal();
			}
		});
		button.setText("\u4FDD\u5B58");
		Button button_1 = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		button_1.setText("\u53D6\u6D88");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	protected Point getInitialSize() {
		return new Point(493, 400);
	}
}
