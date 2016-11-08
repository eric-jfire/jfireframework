package com.jfireframework.sql.test;

import java.util.List;
import com.jfireframework.sql.annotation.Sql;
import com.jfireframework.sql.jfirecontext.MapperOp;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.test.entity.User;
import com.jfireframework.sql.test.entity.User.Enumint;
import com.jfireframework.sql.test.entity.User.Enumstring;

@MapperOp
public interface UserDAO
{
    
    @Sql(sql = "select * from User where id=$id and enumint=$enumint and enumstring=$enumstring", paramNames = "id,enumint,enumstring")
    public User selectUser(int id, Enumint enumint, Enumstring enumstring);
    
    @Sql(sql = "select enumint from User where id=$id", paramNames = "id")
    public Enumint selectEnumInt(int id);
    
    @Sql(sql = "select * from User where enumint=$enumint", paramNames = "enumint")
    public User selectUserByEnumint(Enumint enumint);
    
    @Sql(sql = "select * from User where enumint=Enumint.b", paramNames = "")
    public User selectUserEnumintA();
    
    @Sql(sql = "select * from User [ $name.length()>2] where name like $%name% #", paramNames = "name")
    public List<User> functionUse(String name);
    
    @Sql(sql = "select * from User [$name && $name1] where age>20 #", paramNames = "name,name1")
    public List<User> functionUse2(String name, String name2);
    
    @Sql(sql = "select u.name as name from User as u order by u.id", paramNames = "")
    public List<String> getUsernames();
    
    @Sql(sql = "select username from user order by userid", paramNames = "")
    public List<String> getUsernames(Page page);
    
    @Sql(sql = "select username from user where userid=$id", paramNames = "id")
    public List<String> getUsernames2(int id);
    
    @Sql(sql = "select name from User where id=$userid ", paramNames = "userid")
    public String getUserName(int userid);
    
    @Sql(sql = "select age from User  where id=$userid ", paramNames = "userid")
    public long getUserAge(int userid);
    
    @Sql(sql = "select User.id,name,age,enumint,enumstring from User where userid=$id ", paramNames = "id")
    public User getUserByid(int id);
    
    @Sql(sql = "select * from user where userid=$id ", paramNames = "id")
    public List<User> getUserByidWithName(int id);
    
    @Sql(sql = "insert into User (name,age,password,birthday,id) values ($user.name,$user.age,$user.password,$user.birthday,$user.id)", paramNames = "user")
    public int insertUser(User user);
    
    @Sql(sql = "delete  from User where userid=$id", paramNames = "id")
    public int deleteUser(int id);
    
    @Sql(sql = "select * from User where userid=User.staticId", paramNames = "")
    public User getUserByStaticValue();
    
    @Sql(sql = "select * from User where userid=staticId", paramNames = "")
    public User getUserByStaticValue2();
    
    @Sql(sql = "select * from User as u where userid=u.staticId", paramNames = "")
    public User getUserByStaticValue3();
    
    @Sql(sql = "select * from User where 1=1 [$user.age] and age=$user.age# [$user.name] and username like $%user.name%# [$user.id] and userid=$user.id#", paramNames = "user")
    public List<User> dynamicQuery(User user);
    
    @Sql(sql = "select * from User where 1=1 [$user.age] and age=$user.age# [$user.name] and username like $%user.name%# [$user.id] and userid=$user.id#", paramNames = "user")
    public List<User> dynamicQuery2(User user, Page page);
    
    @Sql(sql = "select * from User where id in $~ids and name = '林斌'", paramNames = "ids")
    public List<User> listinquestion(String ids);
    
    @Sql(sql = "select * from User where id in $~ids and name = '林斌'", paramNames = "ids")
    public List<User> listinquestion(int[] ids);
    
    @Sql(sql = "select * from User where id in $~ids and name = '林斌'", paramNames = "ids")
    public List<User> listinquestion(Integer[] ids);
    
    @Sql(sql = "update User set name=$name where id in $~ids", paramNames = "name,ids")
    public Long updatename(String name, String ids);
    
    @Sql(sql = "select name from User [$age > 15 && $age < 20] where id =1 # [ $age <=15] where id=3 #", paramNames = "age")
    public String name2(Integer age);
    
    @Sql(sql = "select username from {tableName} where userid=1", paramNames = "tableName")
    public String name3(String tableName);
    
    @Sql(sql = "select * from user where userid=$id for update", paramNames = "id")
    public User selectForUpdate(int id);
    
    @Sql(sql = "select * from User where userid=$id", paramNames = "id")
    public User select(int id);
    
    @Sql(sql = "update user set age=$newAge where age=$oldAge", paramNames = "newAge,oldAge")
    public int updateAge(int newAge, int oldAge);
    
    @Sql(sql = "select * from User [$ids.size()>0] where id in $~ids #", paramNames = "ids")
    public List<User> querySize(List<Integer> ids);
    
}
