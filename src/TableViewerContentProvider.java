import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;

/**
 * 创建这个类,主要是对List集合中的数据记录进行筛选和转化.
 * 是"内容器"的作用.
 * 内容器中主要是对setInput()输入的数据集集合(本例子中指的是在PeopleFactory中封装好的List集合)做处理.
 * 并且转换化成一个数组返回.
 *
 * 实现对应的接口IStructuredContentProvider,然后实现其中的方法.
 * @author kongxiaohan
 */
public class TableViewerContentProvider implements IStructuredContentProvider {

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    //实现IStructuredContentProvider接口之后,主要复写的就是这个getElements()方法.
    @Override
    public Object[] getElements(Object inputElement) {//setInput传过来的是一个集合
        if(inputElement instanceof List){//对这个参数进行类型的判断.
            return ((List)inputElement).toArray();//将setInput传过来的List变成一个数组输出
        }else{
            return new Object[0];
        }
    }
}
