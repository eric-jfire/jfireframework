package link.jfire.simplerpc;

import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import link.jfire.simplerpc.data.ComplexOPbject;

public class PrintImpl implements Print
{
    private Logger logger = ConsoleLogFactory.getLogger();
    
    @Override
    public void methodWithoutReturn(String param)
    {
        logger.info("方法被调用");
        logger.info("收到的参数是{}", param);
    }
    
    @Override
    public String methodWithReturn(String param)
    {
        logger.info("收到的参数是{}", param);
        param = param + "追加的末尾信息";
        logger.info("返回的结果是{}", param);
        return param;
    }
    
    @Override
    public Object[] returnComplexOPbject(ComplexOPbject complexOPbject)
    {
        Object[] result = new Object[] { 10, new ComplexOPbject[] { new ComplexOPbject(), new ComplexOPbject() } };
        return result;
    }
    
    @Override
    public void par(String[][] ps)
    {
        // TODO Auto-generated method stub
        
    }
    
}
