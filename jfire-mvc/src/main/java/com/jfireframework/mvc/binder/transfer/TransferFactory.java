package com.jfireframework.mvc.binder.transfer;

public class TransferFactory
{
    private static Transfer<String>  _string  = new Transfer<String>() {
                                                  
                                                  @Override
                                                  public String trans(String value)
                                                  {
                                                      return value;
                                                  }
                                              };
    private static Transfer<Integer> _int     = new Transfer<Integer>() {
                                                  
                                                  @Override
                                                  public Integer trans(String value)
                                                  {
                                                      return Integer.valueOf(value);
                                                  }
                                              };
    private static Transfer<Short>   _short   = new Transfer<Short>() {
                                                  
                                                  @Override
                                                  public Short trans(String value)
                                                  {
                                                      return Short.valueOf(value);
                                                  }
                                              };
    private static Transfer<Long>    _long    = new Transfer<Long>() {
                                                  
                                                  @Override
                                                  public Long trans(String value)
                                                  {
                                                      return Long.valueOf(value);
                                                  }
                                              };
    private static Transfer<Float>   _float   = new Transfer<Float>() {
                                                  
                                                  @Override
                                                  public Float trans(String value)
                                                  {
                                                      return Float.valueOf(value);
                                                  }
                                              };
    private static Transfer<Double>  _double  = new Transfer<Double>() {
                                                  
                                                  @Override
                                                  public Double trans(String value)
                                                  {
                                                      return Double.valueOf(value);
                                                  }
                                              };
    private static Transfer<Boolean> _boolean = new Transfer<Boolean>() {
                                                  
                                                  @Override
                                                  public Boolean trans(String value)
                                                  {
                                                      return Boolean.valueOf(value);
                                                  }
                                              };
    
    @SuppressWarnings("unchecked")
    public static <T> Transfer<T> build(Class<T> ckass)
    {
        if (ckass == String.class)
        {
            return (Transfer<T>) _string;
        }
        else if (ckass == Integer.class || ckass == int.class)
        {
            return (Transfer<T>) _int;
        }
        else if (ckass == Long.class || ckass == long.class)
        {
            return (Transfer<T>) _long;
        }
        else if (ckass == Short.class || ckass == short.class)
        {
            return (Transfer<T>) _short;
        }
        else if (ckass == Float.class || ckass == float.class)
        {
            return (Transfer<T>) _float;
        }
        else if (ckass == Double.class || ckass == double.class)
        {
            return (Transfer<T>) _double;
        }
        else if (ckass == Boolean.class || ckass == boolean.class)
        {
            return (Transfer<T>) _boolean;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
    
}
