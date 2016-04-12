package link.jfire.simplerpc;

import link.jfire.simplerpc.data.ComplexOPbject;

public interface Print
{
    public void methodWithoutReturn(String param);
    
    public void par(String[][] ps);
    
    public String methodWithReturn(String param);
    
    public Object[] returnComplexOPbject(ComplexOPbject complexOPbject);
}
