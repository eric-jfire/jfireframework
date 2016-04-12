package link.jfire.simplerpc.data;

public class ComplexOPbject
{
    private int     age;
    private Integer sex;
    private String  name;
    private String  data2   = "dasda8sdaindaisdy a8dsadai edqw3e";
   
    
    public int getAge()
    {
        return age;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public Integer getSex()
    {
        return sex;
    }
    
    public void setSex(Integer sex)
    {
        this.sex = sex;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public boolean equals(Object target)
    {
        if (target instanceof ComplexOPbject)
        {
            ComplexOPbject tmp = (ComplexOPbject) target;
            if (tmp.getAge() == age && tmp.getSex().equals(sex) && tmp.getName().equals(name))
            {
                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }
}
