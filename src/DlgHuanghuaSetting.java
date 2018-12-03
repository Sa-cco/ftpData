import HuangHuaPort.HuangHuaPortMng;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;


import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class DlgHuanghuaSetting extends Dialog {
	private Text text;
	private HuangHuaPortMng mng ;
	private Text text_afternoon;
	private Text text_morning;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DlgHuanghuaSetting(Shell parentShell,HuangHuaPortMng mng) {
		super(parentShell);
		this.mng = mng;
	}
	
	void initVal(){
		text.setText(mng.getEcthinDirPath());
		text_afternoon.setText(mng.getAfternoonXls());
		text_morning.setText(mng.getMorningXls());
	}
	
	void saveVal() {
		mng.setEcthinDirPath(text.getText());
		mng.setAfternoonXls(text_afternoon.getText());
		mng.setMorningXls(text_morning.getText());
		mng.saveProp();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 3;
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("早晨预报文件");
		
		text_morning = new Text(container, SWT.BORDER);
		text_morning.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button = new Button(container, SWT.NONE);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fd=new FileDialog( getParentShell(),SWT.OPEN);
				fd.setFilterPath(System.getProperty("JAVA.HOME")); 
				fd.setFilterExtensions(new String[]{"*.xlsx","*.*"}); 
				fd.setFilterNames(new String[]{"Excel Files(*.xlsx)","All Files(*.*)"}); 
				String file=fd.open(); 
				if( file != null ) { 
					text_morning.setText(file);
				}
			}
		});
		button.setText("选择");
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("下午预报文件");
		
		text_afternoon = new Text(container, SWT.BORDER);
		text_afternoon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton_1 = new Button(container, SWT.NONE);
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fd=new FileDialog( getParentShell(),SWT.OPEN);
				fd.setFilterPath(System.getProperty("JAVA.HOME")); 
				fd.setFilterExtensions(new String[]{"*.xlsx","*.*"}); 
				fd.setFilterNames(new String[]{"Excel Files(*.xlsx)","All Files(*.*)"}); 
				String file=fd.open(); 
				if( file != null ) { 
					text_afternoon.setText(file);
				}
			}
		});
		btnNewButton_1.setText("选择");
		
		Label lblEcthin = new Label(container, SWT.NONE);
		lblEcthin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEcthin.setText("ec_thin文件夹");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				DirectoryDialog	 fd=new DirectoryDialog( getParentShell(),SWT.OPEN);
				fd.setFilterPath(System.getProperty("JAVA.HOME")); 
				String file=fd.open(); 
				if( file != null ) { 
					text.setText(file);
				}
			}
		});
		btnNewButton.setText("选择");

		initVal();
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.setText("保存");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				saveVal();
			}
		});
		Button button_1 = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		button_1.setText("取消");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 212);
	}

}
