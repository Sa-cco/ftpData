import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import java.awt.*;

/**
 * 这个方法主要是作为"标签器"的作用来用的.
 * "标签器"将一个个实体对象的字段变量分别取出然后系那是在TableViewer的各个列中.
 * 实现对应的接口
 * @author kongxiaohan
 */
public class TableViewerLabelProvider implements ITableLabelProvider {


    @Override
    public Image getColumnImage(Object o, int i) {
        return null;//这个和getColumnText()不同的是,这个方法可以返回一个null值.
    }

    /**
     * 由此方法决定数据记录在表格的每一列是显示什么文字
     * @param element 实体类对象
     * @param columnIndex 列号，0是第一列
     * @return 返回值一定要避免NULL值,否则出错
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        String people = (String) element;
        if(columnIndex == 0){
            return people;
        }
        //当都不符合的时候返回一个空字符串
        return "";
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>书上说以下这几个方法用处不大,空实现就可以了>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @Override
    public void addListener(ILabelProviderListener listener) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {

    }
}