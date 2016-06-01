#Jfire-Sql框架
[TOC]


##框架说明##
Jfire-sql是一个sql映射框架,通过将一个接口方法和sql语句绑定，来达到调用方法就是发出sql语句的目的。框架提供了透明自动的结果到对象，对象集合的转换，自动sql功能，动态表名，字段名的支持。彻底将程序从jdbc编程中解放出来。让程序员可以更加专注在业务上。

##框架优势
###快速的单表CURD操作
框架使用类对数据库表进行映射。通过执行类似代码`session.save(entity),session.delete(entity),session.get(entityClass,id)`就可以快速完成单行数据的CURD操作

###便捷的sql占位表达
可以书写类似
```java
@Query(sql="select * from User where code=$code,name=$user.name",paramNames="code","user")
public List<User> find(String code,User user);
```
这样的sql，字符串占位方式提高代码可读性.传参位置和信息一目了然。

###透明的结果对象转换
使用框架查询后，可以透明自动的将一行的数据转换为一个对象的实例，如果是多行的数据则以`List<T>`的方式返回。无需任何人工干预。

###动态sql支持
jdbc编程中，遇到经常会遇到多条件查询，需要根据条件判断结果手动拼接sql。Jfire-sql给出的动态sql方案解决了这个问题。可以使用类似`select * from user where 1=1 [name] and name=$name # [id] and id=$id # `这样的sql。当条件不存在或者为假时忽略从`[]`到`#`的内容从而自动完成sql的拼接

###动态表名，字段名支持
在jdbc编程中，有时候需要一些动态表名的支持。以往都是通过手动拼接sql字符串完成。现在框架可以自动的帮我们完成。可以使用`{}`做包围符将内容自动的拼接到sql中。
```java
@Query(sql="select * from user{userno}",paramNames="userno")
public List<User> find(int userno);
```
上面这个例子里，就可以使用方法的参数来自动完成动态表名的支持了。


##快速入门
下面来看一段入门的代码
```java
//首先需要一个实体类
@tableEntity(name="db_user")
package com.jfire.test;
public class User
{
    private Integer id;
    private String name;
    private int age;
    private Date birthday;
}
//接着是一个接口类
public interface UserOp
{
    @Query(sql = "select * from User",paramNames={})
    public List<User> list();
    @Query(sql = "select name from User where id=$id",paramNames="id")
    public String getName(int id);
    @Query(sql = "select * from User where name=$user.name and age=$user.age",paramNames="user")
    public User get(User user);
    @Update(sql="update User set name=name where id=$id",paramNames="id,name")
    public void update(int id,String name);


}
//有了这些就可以开始使用了
public static void main(String args[])
{
    DataSource ds = new MysqlDataSource("jdbcurl","username","password");
    SessionFactory sf = new SessionFactoryImpl(ds);
    sf.setScanPackage("com.jfire.orm");
    sf.init();
    //好了下面开始数据的CURD操作
    //新增一行数据
    User user = new User();
    user.setName("test");
    user.setAge(20);
    user.setBirthday(new Date());
    Sqlsession session = sf.openSession();
    session.save(User);//保存一个对象到数据库，相当于插入一行的数据
    session.close();
    //获得一行数据
    Sqlsession session = sf.openSession();
    User user = session.get(User.class,1);
    session.close();
    //使用接口进行查询
    Sqlsession session = sf.openSession();
    UserOp op = session.getMapper(UserOp.Class);
    List<User> list = op.list();
    op.update("test2",1);
    session.close();
}
```


##基础设置
在Jfire-Orm框架中,使用SqlSession来代表一个数据库连接,而SqlSession是由SessionFactory产生的.所以框架使用的开始就是初始化SessionFactory.


###SessionFactory
SessionFactory的实现类是SessionFactoryImpl.初始话的时候需要提供一个连接池对象.代码如下.
```java
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("root");
        dataSource.setPassword("centerm");
        sessionFactory = new SessionFactoryImpl(dataSource);
        sessionFactory.setScanPackage("com.jfire.sql");//设置需要扫描的路径，在这个路径下使用注解的类会被自动发现。路径之间可以使用;号区隔
        sessionFactory.init();使用给定的参数初始化
```


###SqlSession
SqlSession表示的一个数据库连接实例.其中包含了大部分的sql功能.这点观看代码很容易理解.
在使用SqlSession提供的查询接口中，框架具备将查询结果自动转换为对应的映射类的实例的能力。这样通过查询结果到类实例就变得自动化和透明。
SqlSession提供了类似HIbernate的类的CURD操作。对类的CURD操作就如果直接对表进行对象操作一样。比如插入一行数据就是调用`session.save(entity)`来完成。


####两种获取session的方式
1. `sessionFactory.openSession()`会创建一个新的session。
2. `sessionFactory.getCurrentSession()`则会获取当前存储在ThreadLocal中的session对象。如果当前线程中不存在已经打开了session对象，则返回null


##将数据库表映射到类
将一个类与数据库表建立映射关系后，即可执行CURD操作或者是在查询语句中自动将数据库查询结果转换为对象实例。例子如下
```java
@TableEntity(name = "user")//通过注解映射来表明该对象所映射的表.
public class User
{
    @Id//表明该字段所对应的数据库字段为主键,在curd操作中,该注解是必须的
    @Column(name = "userid")//表明该字段对应的数据库字段的名称,如果属性名和数据库字段名一致,则不需要该注解.
    private Integer id;
    @Column(name = "username")
    private String  name;
    @Column(saveIgnore=true)//saveIgnore为true的时候表示在执行session.save()操作时该属性的值会被忽略，但是查询的时候仍然是生效的
    private String  password;
    private int age;
    @SqlIgnore//使用该注解表明该字段不是数据库的映射字段
    private String  birthday;


}
```
如果一个类中有一个字段使用了Id注解表示主键，则该类可以通过SqlSession接口进行CURD操作。比如需要保存一行记录到数据库。可以构建一个User对象的实例，然后使用代码`session.save(user)`来完成。其中save会通过Id字段是否为空来判断是插入数据还是更新数据。


##CURD操作
对于一个数据库表的CURD操作，等同于对类的执行相关操作。


###增加一行数据
增加一行数据到数据库，可以构建一个与该表映射的类的实例。如果数据库主键是自增，则可以使用`session.save(entity)`来完成插入的动作。否则就需要在类实例的id字段设置一个值，并且使用`session.insert(entity)`来将数据插入到数据库


###更新一行数据
更新一行数据，可以构建一个与该表映射的类的实例。并且注解了Id的字段要有值，该值与该行数据的主键相等。使用代码`session.save(entity)`来完成数据的更新。除了Id字段外，所有类中的字段的值都会被更新到数据库中


###删除一行数据
删除一行数据，只需要构建一个与该表映射的类的实例，并且将Id字段赋值。使用代码`session.delete(entity)`即可删除主键为id字段值的数据行


###得到一行数据
得到一行数据，只需要确定主键的值。通过代码`session.get(EntityClass,id)`就会返回entityClass的一个实例对象。


##接口和sql语句绑定
在Jfire-Orm框架中,最为常见和实际的使用是接口方法和sql语句的绑定.也就是通过定义一些接口方法，并且将接口方法和sql语句绑定，这样，程序中调用对应的方法就相当于发出sql语句。请见看下面的简单示例
```java
public interface UserOp{
    @query(sql="select * from user where id=$id",paramNames="id")
    public User query(integer id);
}
public UserService{
    public User find(integer id){
        SqlSession session = sessionFactory.getCurrentSession();
        //获取接口的实例，这个实例是框架自动生成的，每次获取都是一个新对象，同时该对象内使用了当前的session。所以如果     session关闭，该实例不可继续进行操作
        Userop userop = session.getMapper(Userop.Class);
        return userop.query(id);
     }
}
```
通过上面的代码可以很简单的看明白这个sql语句和方法的Orm是怎么使用的。简单的说步骤如下。


1. 定义一个接口，将接口方法上打上orm注解，主要有`@Query，@Update,@BatchUpdate`
2. 在程序中获得一个session，使用session的getMapper()方法传入接口的类对象，获取接口实例。
3. 使用接口方法，当调用接口方法的时候就相当于是发出了sql语句
4. 如果是查询语句，返回的数据结果会自动映射为方法的返回对象类型


###类名和属性名的映射
在编写sql语句的时候。如果遇到表名或者字段名变化的情况，则代码需要修改。但是如果在sql语句中，可以使用类名替换表名，属性名替换字段名采用映射机制，则可以屏蔽这些变化。
比如有一个类如下
```java
@tableentity(name="tb_user")
Class User{
    @id
    @column(name="userid")
    private int id;
    private string age;
}
```
框架会将sql`select User.id from User`转换为`select userid from tb_user`。
sql语句中，如果出现大写开头的单词，则框架会识别为类的简单名称，并且寻找对应的映射.同时如果出现类似`User.id`这样的字段，框架也会到对应的类中寻找对应的属性。如果属性存在Column注解，那么使用注解中的值替换sql中的字段。否则使用属性名作为查询字段。

###查询操作
通过框架完成对数据库的查询非常简单。先看示例代码
```java
    @Query(sql="select name from User where id = $id",paramNames="id")
    public String getName(int id);
    @Query(sql="select name from User where id = user.id",paramNames="user")
    public String getName(User user);
    @Query(sql = "select * from User",paramNames="")
    public List<User> list();
    @Query(sql="select * from User where id=$id",paramNames="id")
    public User find(int id);
```
从上面的代码可以看出。进行查询操作首先需要定义一个接口和查询的方法。然后打上`Query`注解。并且设置注解的两个属性值。注解有两个属性。
1. sql:表示调用该方法会发出的sql语句。
2. paramNames：是个String,内部可以用逗号区隔。表示该方法入参的名称。**注意，该参数中名称的顺序需要和方法参数的顺序完全一致**

####sql语句写法
sql语句中可以使用`$`来表示占位符，功能和jdbc编程中的`?`相同。比如如果是`$id`就会在方法入参中寻找名称为`id`的属性，在确切发出sql的时候进行注入。而如果是`$user.id`则会方法入参中寻找名称为`user`的实例，取出其`id`属性的值，在发出sql的时候注入。也就是说`$`支持获取直接的方法入参，也支持获取类中的属性。

####返回结果自动转换
sql查询的结果，框架可以自动的进行转换。比如示例代码中第三个方法。框架将每一行的数据都转换成了一个User对象实例，并且将最终结果放入到一个List中返回。框架会解析查询方法的返回值类型，并且将查询的数据自动转换成该对象的实例。如果确定返回的数据只有单行，也可以将返回值类型修改为类的形式。比如示例的第四个方法。
当然如果返回值就是一个单行单列的数据也是支持的。比如直接返回一个String或者一个int。

###更新操作
通过框架完成更新操作也很简单。先看示例代码
```java
    @Update(sql="update User set name=$name where id=$id",paramNames="name,id")
    public void updateName(String name,int id);
    @Update(sql="update User set name=$name where id=$id",paramNames="name,id")
    public int updateName2(String name,int id);
```
更新操作的注解中属性的含义和`Query`是相同的。更新操作的方法返回值类型可以是`void`也可以是`int`。如果是`int`表示这个更新语句对多少条数据库记录产生影响


###动态sql
在jdbc编程中，很多时候会需要编写动态sql的情况。多半是由于查询条件的不固定。最常见的就是在查询中存在多个查询条件，但是查询条件可能任意为空，此时就需要根据查询条件的是否为空来编写sql语句。比较麻烦。为了解决这些时常会碰到的动态sql的需求。Jfire-sql框架支持了大多数的动态sql需求场景。
所谓的动态sql就是指支持在sql语句中写入框架支持的条件判断语句，由框架执行对应的条件判断。并且自动的完成的sql语句的拼接。避免了程序员手动进行if判断后对sql进行拼接。Jfire-sql框架支持由`[]`包围起来的条件判断语句，并且以`#`作为一个条件的结束。如果条件判断的结果为真，则`[]`到`#`之间的内容就会自动的被拼接到sql之中。
####自定义条件判断
先来看下示例代码
```java
    @Query(sql="select * from User [$name] where name=$name #",paramNames="name")
    public List<User> find(String name);
```
在sql语句中，以`[]`包围起来的内容表示条件判断。如果`[]`内的条件结果为假，则`[]`到`#`之间的内容不会出现在sql语句中。在上面的例子中，如果name为null，则最后发出的sql是`select * from User`否则就是`select * from User where name = $name`.
在`[]`中，以`$`来表示变量名的开始，其中值的寻找解析原则和sql中占位符`{}`的一致。如果`[]`只有一个变量并且没有指定条件，则进行非空判断，否则就执行自定义的条件判断。
请看下面的代码
```java
    @Query(sql="select * from User [$age >20 && $age <25] where age=$age #",paramNames="age")
    public List<User> find(int age);
```
在上面的代码中，会执行自定义的条件判断。如果`age>20 && age<25`为真则sql语句会是`select * from User where age =$age`
在`[]`中除了`$`代表变量名的开始，其余写法与java代码大致相同。同时`$`不仅支持直接的方法入参，也支持当入参是个类的时候获取入参属性值比如`$user.age`就是获取了入参User对象的age属性。
####不定个数参数支持
在范围内查询时会碰到类似这样的sql`select * from User where id in (?,?)`。此时往往需要根据前台传递的参数来确定`?`的个数。Jfire-orm提供对不定长参数的内置支持。在占位符前增加`~`符号表示需要对占位符内的内容做特殊处理。

#####String支持
可以使用sql`select * from User where ^id in $~ids`其中ids是一个字符串，其中内容以逗号区隔。框架会自动使用逗号将ids的内容分割，并且将sql转换成`select * from User where id in (?,?,?)`。其中？的个数和ids用逗号分割出来的数组个数相同。然后将分割出来的元素依次填入

#####数组支持
可以使用sql`select * from User where id in $~ids`其中ids是一个数组。框架会将sql转换成`select * from User where id in (?,?,?)`其中？的个数和数组的长度相同。然后数组的每一个元素依次填入。

#####List支持
可以使用sql`select * from User where id in $~ids`其中ids是一个List。框架会将sql转换成`select * from User where id in (?,?,?)`其中？的个数和List的长度相同。然后List的每一个元素依次填入。

####动态表名和字段支持
由于分库分表的情况存在，有些时候，查询的表名是要在运行期才能被确定的，这个时候也就涉及到对表名和字段名的动态支持。实质上就是字符串的动态添加。对于这种情况，jfire-sql也是提供了支持。如下所示
```java
@Query(sql="select * from user{userno}",paramNames="userno")
public List<User> find(int userno);
```
如果userno是1，那么最后的sql语句会变成`select * from user1`。可以看到，对于动态表名和字段名，只要理解为普通的string拼接即可。