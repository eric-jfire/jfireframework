#Jfire-Core框架
[TOC]


##框架优势
**功能齐全，注解开发，零配置**
Jfire-core是一个IOC&AOP容器。
IOC部分，基于注解实现依赖注入功能。除了依赖注入，还提供参数注入，Map注入等特殊功能。IOC容器天然提供基于注解的单例和原型对象实例功能。通过对一些接口的实现，对象可以实现对容器初始化过程的参与。
AOP部分，基于类Aspecj描述语言实现AOP注入。提供完善的AOP功能，诸如前置，后置，环绕，异常抛出增强。采用动态代码热编译实现而非反射代理，不损失调用性能。
通过代码初始化，只需要指定需要扫描的包路径即可，零配置。如果需要进行额外的功能，也支持json格式的配置文件，方便接单


**性能强大**
所有的注入操作均采用内存偏移量设置完成。性能较使用反射提高约3倍。


**轻量级，体积小**
提供的jar只有60k。代码轻量。


##快速入门
首先先有几个类，请看如下代码
```java
package com.jfire.core
@Resource("person")
public class Person
{
    private String name;
    @Resource//通过注解，将home对象的实例注入到person对象的实例中
    private Home home;
}
@Resource
public class Home
{
    private String name;
    @Resource
    private Address add
}
public class Address
{
    private string location;
    private int num;
}
public static void main(String args[])
{
    //给定扫描的包路径，注解了Resource的都会被识别为Bean
    JfireContext context = new JfireContextImpl("com.jfire.core");
    //手动增加一个bean到容器，并且设置该bean的名称，是否单例，bean使用的类
    context.addBean(Address.class.getName(),false,Address.class);
    //使用给定的参数进行容器初始化
    context.init();
    //通过类获取一个类在容器中的实例。该类被注解为单例
    Person person = context.getBean(Person.class);
    //也可以通过给类设定的bean名称获取到类实例
    Person person2 = context.getBean("person");
    Home home = person.getHome();
}
```
##IOC容器
###定义Bean
Jfire-core框架将可以被框架管理的类称之为*bean*。这个bean不要求是严格的JavaBean。因为框架可以接受外来对象实例成为bean。
定义一个bean有三种方式
####注解方式定义bean
通过注解定义一个bean是非常简单的。只需要在类上面打上`Resource`注解即可。`Resource`中有两个属性`name`和`shareable`。其中`name`表示bean的名称。如果不填写，默认为类的全限定名。`shareable`默认为true，表示该bean是一个单例，每一次获取都是同一个对象实例。反之则每次获取框架都会重新生成。
####使用配置文件指定一个类为bean
可以在配置文件中制定一个类成为bean。参考如下示例代码
```json
{
    "beans": 
    [
        {
            "beanName": "com.jfire.core.test.function.base.data.House",//bean的名称
            "prototype": false,//bean是否是原型。原型就是非单例
            "className": "com.jfire.core.test.function.base.data.House"//bean的类的全限定名
        }
    ]
}
```
####通过JfireContext直接将一个对象制定为bean并且加入
框架可以将一个对象实例制定为单例bean并且加入对象容器。参考如下代码
```java
    jfireContext.addSingletonEntity("User",new User());//往容器中添加一个名称为User的bean，并且该bean是单例，容器中存储着设置进入的单例供其他类使用
```
####通过配置文件指定一个类成为bean
通过配置文件指定一个类成为bean很简单。需要配置的内容如下
```json
 "beans": [
        {
            "beanName": "com.jfire.core.test.function.base.data.House",//定义bean的名称
            "prototype": false,//定义bean是否是原型。原型表示非单例
            "className": "com.jfire.core.test.function.base.data.House"//bean的全限定名
        }
        ]
```
###容器发现bean
IOC容器要使用的第一步就是容器对bean的发现机制。早期Spring采用xml配置的形式，非常繁琐。而现在，主流框架均采用自动发现机制。Jfire一样也支持自动发现。
####通过扫描包路径，自动发现bean
Jfire支持设置包扫描路径，在**这些路径下以及子路径**的所有类，只要打上`resource`注解均可被自动发现。设置包路径有代码和配置文件两种方式。
1. **代码方式**：使用如下代码设置扫描路径`jfireContext.addPackageNames("com.jfire.core","com.test.entity")`。该方法支持不定长的String参数
2. **配置文件方式**：配置文件的内容是`{"packageNames" : ["com.jfire.core.test.function.base","com.test"]}`


####手动添加一个类到容器
可以使用代码方式手动添加一个类到容器。有两种不同的情况
1. **类本身有`resource`注解**：该类不在扫描路径范围内。可以使用代码手动加入`jfireContext.addBean(User.class)`.这个代码会读取类上的`resource`信息。然后组装成bean加入容器
2. **类没有`resource`注解**：通过代码将bean名称，是否单例，类的全限定名加入到容器中。代码如`jfireContext.addBean(House.class.getName(), false, House.class)`


####增加一个外部对象实例到容器
有的时候需要往容器中增加外部对象实例。这些外部示例往往是没有办法接触到源代码或者不是由自己控制初始化的。这些外部实例可以以单例的形式添加到容器中。代码是`jfireContext.addSingletonEntity("userBean",new User())`


###依赖注入
####普通类型注入
依赖注入中，最常见的就是类属性的注入。也就是将一个类的实例注入到另外一个类实例的属性中。
框架之中使用依赖注入非常方便。只需要在类的属性上使用`resource`注解即可。看如下代码
```java
public class Person
{
    @Resource //在这里打上注解表示会将bean名称是"com.test.Home"的bean实例注入到这个属性中。
    private com.test.Home home;
    @Resource("home1")//这样表示会将bean名称是"home1"的bean注入到这个属性中
    private Home home;
}
```
框架采用名称注入而非类型注入的方式，这样是为了避免错误的使用。在框架中每一个bean都有一个bean名称。在类的属性上打`Resource`注解就表示会将指定名称的bean注入到对应的属性中。如果`Resource`注解没有加名称，表示将属性的类的全限定名的bean注入到属性，否则就以自定义的名称的bean注入到属性。
####通过配置文件进行依赖注入
通过配置文件进行依赖注入很简单，需要配置的信息如下
首先是代码
```java
@Resource("Per")
public class Person
{
}
public class House
{
    private Person person;
}
```
然后是配置文件
```json
{
    "beanConfigs": 
    [
        {
            "beanName": "p2",
            "dependencies"://表示house这个bean中有什么属性需要进行依赖注入
            {
                "person":"Per"//每一个键值对都是一个依赖注入。key代表被注入的属性的名称，value表示注入的bean的名称
            }
        }
    ],
    "beans":
    [
        {
            "beanName": "com.jfire.core.test.function.base.data.House",//定义bean的名称    
            "prototype": false,//定义bean是否是原型。原型表示非单例
            "className":"com.jfire.core.test.function.base.data.House",
            "dependencies"://表示house这个bean中有什么属性需要进行依赖注入
            {
                "person":"Per"//每一个键值对都是一个依赖注入。key代表被注入的属性的名称，value表示注入的bean的名称
            }
        }
    ]
}


```
####List类型注入
框架支持List类型的注入。使用场景是多个Bean的类都实现了某一个接口。而有一个bean的属性为`List<T>`的类型。则所有继承或者实现了T的bean都会被注入到这个属性中。同时，该属性不能为null，也就是list的这个属性是有值的。参考代码
```java
public interface Person
{
    public String name();
    public int age();
}
@Resource
public class Teacher implements Person
{
}
@Resource
public class Student implements Person
{
}
public class Home
{
    @Resource//这样的话，实现了接口Person的bean都会被注入到这个list中,但是这个list是需要实现存在的。
    private List<Person> pers = new ArrayList();;
}
```
####通过配置进行List注入
List注入的配置和依赖注入差不多。如下
```json
"beans": 
[
    {
        "beanName": "com.jfire.core.test.function.base.data.House",//定义bean的名称                       "prototype": false,//定义bean是否是原型。原型表示非单例
        "className": "com.jfire.core.test.function.base.data.House"//bean的全限定名
        "dependencies"://表示house这个bean中有什么属性需要进行依赖注入
        {
            "person":"Per1;Per2;Per3"//每一个键值对都是一个依赖注入。key代表被注入的属性的名称，value表示注入的bean的名称并且采用;号分割每一个被注入的bean名称
        }
    }
]
```
####Map注入
框架支持Map类型的注入。先看示例代码
```java
@Resource
public interface Order{}


public class Order1 implements Order
{
    public Integer keyName();
}
@Resource
public class Order2 implements Order
{
    public Integer keyName();
}
@Resource
public class MapEntity
{
    @Resource//实现了接口Order的bean都会被注入
    @Mapkey("keyName")//所有被注入的bean都有一个无参的keyName方法，使用该方法返回值作为该bean的key
    private Map<Integer,Order> map = new Hashmap();
    
    @Resource//实现了Order接口的bean都会被注入，由于没有MapKey注解，所有该map注入时的key就是被注入的bean的beanName。所以该Map的key类型必须是String
    private Map<String,Order> map2 = new Hashmap();
}
```
从示例代码可以看出。对Map属性进行注入，是否使用`@MapKey`注解,效果是不同的
+ 使用`@Mapkey`注解。Map注入的原则是Map属性的V的类型，所有继承该类型或是实现了该接口的bean都可以注入，而他们对应的key就是bean中的某一个无参方法的返回值。而这个方法的名称由注解`MapKey`指定。同时该Map属性不能为null。
+ 不使用`@Mapkey`注解。map注入的原则是Map属性的V的类型，所有继承该类型或者实现了该接口的bean都可以注入。而他们对应的key就是bean的名称。
####通过配置文件进行Map注入
在配置文件中，对map类型的字段的写法有两种，不同的写法对应不同的识别方式。
1. **version1!getName:bean1;bean2;bean3**:这种写法中,`:`之后的内容则是需要注入的bean的名称。`:`之前的内容是这些bean中都需要具备的一个无参且有返回值的方法的名字。在对map字段进行注入的时候，会调用这个bean的该方法，得到返回值作为key，该bean作为value放入map中。
2. **version2!keyName1:bean1|keyName2:bean2**:在这种写法中，使用`|`进行不同内容的区隔。每一个内容当中，`:`前的内容是key，之后的内容是bean的名称
例子
```json
"beanConfigs": 
[
        {
            "beanName": "house",
            "dependencies": 
            {
                "map": "version2!p1:person1|p2:person2"
            }
        }
]
```
3. **version3!beanName1,beanName2**:这种写法中，beanName1这些是需要注入的bean的名称。而对应的key就是该bean的名称。
###设定bean的初始化方法
在一些应用场景中存在一些需求，再将bean的实例提供之前，需要运行一个无参的初始化方法。针对这一需求，使用java的内嵌注解`@PostConstruct`。该注解打在方法上，则bean在被提供前都会运行这个方法，运行完毕才被提供。该注解一个类只能有一个
```java
@Resource
public class Person
{
    @PostConstruct
    public void say(){
        System.out.println("你好");
    }
    public static void main(String args[])
    {
        JfireContext context = new JfireContextImpl("com.test");
        context.getBean(Person.class);//获取到bean的时候就会调用被InitMethod注解了的方法。这里是调用say方法。
    }
}
```
###通过配置文件设定bean的初始化方法
除了代码中使用注解外，也可以使用配置文件的方式进行bean初始化方法的设置。示例代码如下
```json
{
    "beanConfigs": [
        {
            "beanName": "p2",
            "postConstructMethod": "initage"
        }
    ]
}
```
###容器初始化结束接口
bean可以实现容器初始化接口。该接口代码如下
```java
public interface ContextInitFinish extends Order
{
    /**
     * 当容器初始化完成后，该接口会被容器调用
     * 
     * @author 林斌(eric@jfire.cn)
     */
    public void afterContextInit();
}
```
接口包含两个方法。一个`order`方法用来排序。如果有多个bean实现了这个接口，则根据数字大小进行自然排序。一个`afterContextInit`方法表示容器初始化完毕后，会调用这个方法


###参数注入
参数注入功能可以在类实例化的时候对属性注入事先制定好的值。支持的类型从基本类型到包装类型以及String和这些类型的数组。在配置文件中，是使用param字段来表示的。看下面的例子
```json
"beanConfigs": //对bean进行配置
[
        {
            "beanName": "com.entity.Person", //指定要配置的bean的名称
            "params": //对bean进行参数注入。
            { 
                "name": "test",
                "age": "19",
                "hasHome": "false"
            }
        }
    ]
```
`params`字段中键值对都是字符串类型，属性注入的时候会根据类属性的对象类型自动转换。如果是数组的话，则配置文件中使用逗号进行区隔。如下
```json
"attribute": 
{
    "params": 
    {
        "name": "林斌",
        "age": "25",
        "boy": "true",
        "arrays":"12,123,1234"
    }
}
```
以逗号区隔，在属性输入的时候会自动的用逗号分隔开注入内容并且完成类型转换后以数组的形式注入
###完整配置文件
上面的讲述中将所有部分的配置文件都说明到了，现在给出一份完整的配置文件，这样有一个直观的认识。其中需要说明的是，对一个bean进行配置，可以在两个地方进行。
第一个地方是在`beanConfigs`节点中。可以指明需要配置的bean的名称然后进行配置。这种情况，这个bean是已经存在在容器中的。
第二个地方是在bean内部的`attributes`节点上进行配置。这种时候，配置信息属于内部信息，这个bean是在配置的时候被加入到容器中的
```json
{
    "packageNames": //指定要扫描的包路径。会检索该路径和子路径下的所有类
    [
        "com.jfire.core.test.function.base",
        "cn.starnet.entity"
    ],
    "beanConfigs": //对已经在容器中的bean进行额外信息配置
    [
        {
            "beanName": "com.entity.Person", //指定要配置的bean的名称，这里的bean是已经在容器中存在的
            "params": //配置该bean的参数注入
            { 
                "name": "test",
                "age": "19",
                "hasHome": "false"
            },
            "dependencies": //配置该bean的依赖注入
            {
                "home": "com.test.Home",
                "address": "com.test.Address"
            },
            //配置该bean的初始化方法
            "postConstrutcMethod": "say"
        }
    ],
    "beans": [//配置要加入到容器的bean的信息，基础的包括bean名称，bean是否单例，bean的类的全限定名
        {
            "beanName": "com.jfire.core.test.function.base.data.House",//bean的名称
            "prototype": false,//bean是否原型。原型就是非单例
            "className": "com.jfire.core.test.function.base.data.House"//bean使用的类的全限定名
        },
        {
            "beanName": "com.jfire.core.test.function.base.data.ImmutablePerson",
            "prototype": false,
            "className": "com.jfire.core.test.function.base.data.ImmutablePerson",
            "params": //配置该bean的参数注入
            {
                "name": "test",
                "age": "25",
                "boy": "true"
            },
            "dependencies": //配置该bean的依赖注入
            {
                "home": "com.test.Home",
                "address": "com.test.Address"
            },
            //配置该bean的初始化方法
            "postConstructMethod": "say"
        }
    ]
}
```
##AOP功能
框架带有强大的AOP功能，通过表达式进行路径匹配，可以很方便的对类进行AOP编程。框架中AOP匹配操作分为两个部分。首先需要定义AOP类。定义方式如下
```java


//这个注解表明这个bean是一个AOP增强类。它的值是被织入（被增强，目标 类等都是一个意思的名称）类的类全限定名。这个表达式支持通配符`*`。 * 该符号表示任意长度的任意字符
@EnhanceClass("com.test.*.Action.*")
@Resource//这个注解让框架可以发现这个AOP类
public class Aop
{
    @BeofreEnhance(value="before()",order=1)//这个是一个前置拦截，表示要拦截的方法的名称是before，没有入参，order属性用于排序，如果有多个拦截方法同时对一个方法进行拦截。使用自然顺序进行排序
    public void before(ProceedPoint point){}
}
```
AOP增强是两个步骤。
1. 通过增强类上的`EnhanceClass`注解的`value`值对类进行类名匹配，确定需要增强的目标类.
2. 通过增强类中方法上的增强注解，匹配目标类中需要增强的目标方法，然后使用增强方法对目标方法进行增强。
**目标类确定**
AOP拦截中，首先是对目标类的确定。EnhanceClas上的值，就是对类全限定名进行匹配。其中`*`可以匹配任意长度的任意字符。
>比如`com.test.*Action`就可以匹配上`com.test.UserAction`


**方法确定**
方法确定是通过增强注解中的路径值来进行的，只对方法名和参数进行匹配，不对返回值做要求。比如路径值是`getName()`表示匹配一个名称是getName，并且无参的方法。路径值要求必须带有`()`号。如果要对参数类型进行匹配，可以写在`()`中，可以直接写参数类型的简单名称，也可以写类型的全限定名。类型名之间采用一个空格隔开。比如`setName(String int)`和`setName(java.util.String int)`是等价的匹配规则。如果在匹配的时候要忽略参数类型，使用`(*)`即可。比如`getName(*)`就表示忽略入参类型和个数，只要方法名称是getName即可。
如果参数类型是数组，则可以写成`int[][]`这样的形式。
**增强方法排序**
如果一个目标方法上有多个增强方法，则增强方法按照注解上的order字段的值进行排序
**增强方法的入参**
增强方法都只有一个入参，就是`com.jfire.core.aop.ProceedPoint`.这个类提供了关于目标方法的各种信息，包含参数，执行实例等。这个入参在各种增强方法中起到不一样的作用。以下是4个公共方法。
1. `ProceedPoint.getHost()`:获取执行原方法的对象实例
2. `ProceedPoint.getParam()`：获得原方法的入参数组
3. `ProceedPoint.getResult()`:获得增强方法调用后的返回值
4. `ProceedPoint.setResult(Object invokedResult)`:设置增强方法调用后最终得到的返回值


还有一些方法在特定的增强中才有意义，下面逐一讲解。
###前置增强
前置增强使用注解`@BeforeEnhance`。表示增强方法会在目标方法之前执行。同时可以通过方法`ProceedPoint.setPermission(boolean)`来设置是否可以允许目标方法的执行。如果设置为false就表示不让目标方法执行。也可以通过方法`ProceedPoint.setResult(Object)`来设定在不让目标方法执行的情况下的返回值。
###后置增强
后置增强使用注解`AfterEnhance`。表示增强方法会在目标方法执行之后执行。可以通过方法`ProceedPoint.getResult()`获得目标方法调用后的结果值，或者通过方法`ProceedPoint.setResult(Object invokedResult)`修改最终的返回结果。
###异常增强
异常增强使用注解`ThrowEnhance`。表示增强方法会在目标方法抛出异常时执行。该注解具备一个属性`type`,是一个Class数组。用来表示目标方法抛出指定类型的异常时增强方法起作用。默认的值是`Throwable`.
###环绕增强
环绕增强使用注解`AroundEnhance`.环绕增强中，目标方法是否执行就取决于增强方法。可以通过方法`ProceedPoint.invoke()`来执行原来的方法。如果原方法具有返回值，那么在调用`ProceedPoint.invoke()`执行原方法后，该方法会返回原方法的返回值，此时通过`ProceedPoint.setResult(Object invokedResult)`来设置最终增强方法的返回值。