#Jfire-Core���
[TOC]


##�������
**������ȫ��ע�⿪����������**
Jfire-core��һ��IOC&AOP������
IOC���֣�����ע��ʵ������ע�빦�ܡ���������ע�룬���ṩ����ע�룬Mapע������⹦�ܡ�IOC������Ȼ�ṩ����ע��ĵ�����ԭ�Ͷ���ʵ�����ܡ�ͨ����һЩ�ӿڵ�ʵ�֣��������ʵ�ֶ�������ʼ�����̵Ĳ��롣
AOP���֣�������Aspecj��������ʵ��AOPע�롣�ṩ���Ƶ�AOP���ܣ�����ǰ�ã����ã����ƣ��쳣�׳���ǿ�����ö�̬�����ȱ���ʵ�ֶ��Ƿ����������ʧ�������ܡ�
ͨ�������ʼ����ֻ��Ҫָ����Ҫɨ��İ�·�����ɣ������á������Ҫ���ж���Ĺ��ܣ�Ҳ֧��json��ʽ�������ļ�������ӵ�


**����ǿ��**
���е�ע������������ڴ�ƫ����������ɡ����ܽ�ʹ�÷������Լ3����


**�����������С**
�ṩ��jarֻ��60k������������


##��������
�������м����࣬�뿴���´���
```java
package com.jfire.core
@Resource("person")
public class Person
{
    private String name;
    @Resource//ͨ��ע�⣬��home�����ʵ��ע�뵽person�����ʵ����
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
    //����ɨ��İ�·����ע����Resource�Ķ��ᱻʶ��ΪBean
    JfireContext context = new JfireContextImpl("com.jfire.core");
    //�ֶ�����һ��bean���������������ø�bean�����ƣ��Ƿ�����beanʹ�õ���
    context.addBean(Address.class.getName(),false,Address.class);
    //ʹ�ø����Ĳ�������������ʼ��
    context.init();
    //ͨ�����ȡһ�����������е�ʵ�������౻ע��Ϊ����
    Person person = context.getBean(Person.class);
    //Ҳ����ͨ�������趨��bean���ƻ�ȡ����ʵ��
    Person person2 = context.getBean("person");
    Home home = person.getHome();
}
```
##IOC����
###����Bean
Jfire-core��ܽ����Ա���ܹ�������֮Ϊ*bean*�����bean��Ҫ�����ϸ��JavaBean����Ϊ��ܿ��Խ�����������ʵ����Ϊbean��
����һ��bean�����ַ�ʽ
####ע�ⷽʽ����bean
ͨ��ע�ⶨ��һ��bean�Ƿǳ��򵥵ġ�ֻ��Ҫ�����������`Resource`ע�⼴�ɡ�`Resource`������������`name`��`shareable`������`name`��ʾbean�����ơ��������д��Ĭ��Ϊ���ȫ�޶�����`shareable`Ĭ��Ϊtrue����ʾ��bean��һ��������ÿһ�λ�ȡ����ͬһ������ʵ������֮��ÿ�λ�ȡ��ܶ����������ɡ�
####ʹ�������ļ�ָ��һ����Ϊbean
�����������ļ����ƶ�һ�����Ϊbean���ο�����ʾ������
```json
{
    "beans": 
    [
        {
            "beanName": "com.jfire.core.test.function.base.data.House",//bean������
            "prototype": false,//bean�Ƿ���ԭ�͡�ԭ�;��Ƿǵ���
            "className": "com.jfire.core.test.function.base.data.House"//bean�����ȫ�޶���
        }
    ]
}
```
####ͨ��JfireContextֱ�ӽ�һ�������ƶ�Ϊbean���Ҽ���
��ܿ��Խ�һ������ʵ���ƶ�Ϊ����bean���Ҽ�������������ο����´���
```java
    jfireContext.addSingletonEntity("User",new User());//�����������һ������ΪUser��bean�����Ҹ�bean�ǵ����������д洢�����ý���ĵ�����������ʹ��
```
####ͨ�������ļ�ָ��һ�����Ϊbean
ͨ�������ļ�ָ��һ�����Ϊbean�ܼ򵥡���Ҫ���õ���������
```json
 "beans": [
        {
            "beanName": "com.jfire.core.test.function.base.data.House",//����bean������
            "prototype": false,//����bean�Ƿ���ԭ�͡�ԭ�ͱ�ʾ�ǵ���
            "className": "com.jfire.core.test.function.base.data.House"//bean��ȫ�޶���
        }
        ]
```
###��������bean
IOC����Ҫʹ�õĵ�һ������������bean�ķ��ֻ��ơ�����Spring����xml���õ���ʽ���ǳ������������ڣ�������ܾ������Զ����ֻ��ơ�Jfireһ��Ҳ֧���Զ����֡�
####ͨ��ɨ���·�����Զ�����bean
Jfire֧�����ð�ɨ��·������**��Щ·�����Լ���·��**�������ֻ࣬Ҫ����`resource`ע����ɱ��Զ����֡����ð�·���д���������ļ����ַ�ʽ��
1. **���뷽ʽ**��ʹ�����´�������ɨ��·��`jfireContext.addPackageNames("com.jfire.core","com.test.entity")`���÷���֧�ֲ�������String����
2. **�����ļ���ʽ**�������ļ���������`{"packageNames" : ["com.jfire.core.test.function.base","com.test"]}`


####�ֶ����һ���ൽ����
����ʹ�ô��뷽ʽ�ֶ����һ���ൽ�����������ֲ�ͬ�����
1. **�౾����`resource`ע��**�����಻��ɨ��·����Χ�ڡ�����ʹ�ô����ֶ�����`jfireContext.addBean(User.class)`.���������ȡ���ϵ�`resource`��Ϣ��Ȼ����װ��bean��������
2. **��û��`resource`ע��**��ͨ�����뽫bean���ƣ��Ƿ��������ȫ�޶������뵽�����С�������`jfireContext.addBean(House.class.getName(), false, House.class)`


####����һ���ⲿ����ʵ��������
�е�ʱ����Ҫ�������������ⲿ����ʵ������Щ�ⲿʾ��������û�а취�Ӵ���Դ������߲������Լ����Ƴ�ʼ���ġ���Щ�ⲿʵ�������Ե�������ʽ��ӵ������С�������`jfireContext.addSingletonEntity("userBean",new User())`


###����ע��
####��ͨ����ע��
����ע���У�����ľ��������Ե�ע�롣Ҳ���ǽ�һ�����ʵ��ע�뵽����һ����ʵ���������С�
���֮��ʹ������ע��ǳ����㡣ֻ��Ҫ�����������ʹ��`resource`ע�⼴�ɡ������´���
```java
public class Person
{
    @Resource //���������ע���ʾ�Ὣbean������"com.test.Home"��beanʵ��ע�뵽��������С�
    private com.test.Home home;
    @Resource("home1")//������ʾ�Ὣbean������"home1"��beanע�뵽���������
    private Home home;
}
```
��ܲ�������ע���������ע��ķ�ʽ��������Ϊ�˱�������ʹ�á��ڿ����ÿһ��bean����һ��bean���ơ�����������ϴ�`Resource`ע��ͱ�ʾ�Ὣָ�����Ƶ�beanע�뵽��Ӧ�������С����`Resource`ע��û�м����ƣ���ʾ�����Ե����ȫ�޶�����beanע�뵽���ԣ���������Զ�������Ƶ�beanע�뵽���ԡ�
####ͨ�������ļ���������ע��
ͨ�������ļ���������ע��ܼ򵥣���Ҫ���õ���Ϣ����
�����Ǵ���
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
Ȼ���������ļ�
```json
{
    "beanConfigs": 
    [
        {
            "beanName": "p2",
            "dependencies"://��ʾhouse���bean����ʲô������Ҫ��������ע��
            {
                "person":"Per"//ÿһ����ֵ�Զ���һ������ע�롣key����ע������Ե����ƣ�value��ʾע���bean������
            }
        }
    ],
    "beans":
    [
        {
            "beanName": "com.jfire.core.test.function.base.data.House",//����bean������    
            "prototype": false,//����bean�Ƿ���ԭ�͡�ԭ�ͱ�ʾ�ǵ���
            "className":"com.jfire.core.test.function.base.data.House",
            "dependencies"://��ʾhouse���bean����ʲô������Ҫ��������ע��
            {
                "person":"Per"//ÿһ����ֵ�Զ���һ������ע�롣key����ע������Ե����ƣ�value��ʾע���bean������
            }
        }
    ]
}


```
####List����ע��
���֧��List���͵�ע�롣ʹ�ó����Ƕ��Bean���඼ʵ����ĳһ���ӿڡ�����һ��bean������Ϊ`List<T>`�����͡������м̳л���ʵ����T��bean���ᱻע�뵽��������С�ͬʱ�������Բ���Ϊnull��Ҳ����list�������������ֵ�ġ��ο�����
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
    @Resource//�����Ļ���ʵ���˽ӿ�Person��bean���ᱻע�뵽���list��,�������list����Ҫʵ�ִ��ڵġ�
    private List<Person> pers = new ArrayList();;
}
```
####ͨ�����ý���Listע��
Listע������ú�����ע���ࡣ����
```json
"beans": 
[
    {
        "beanName": "com.jfire.core.test.function.base.data.House",//����bean������                       "prototype": false,//����bean�Ƿ���ԭ�͡�ԭ�ͱ�ʾ�ǵ���
        "className": "com.jfire.core.test.function.base.data.House"//bean��ȫ�޶���
        "dependencies"://��ʾhouse���bean����ʲô������Ҫ��������ע��
        {
            "person":"Per1;Per2;Per3"//ÿһ����ֵ�Զ���һ������ע�롣key����ע������Ե����ƣ�value��ʾע���bean�����Ʋ��Ҳ���;�ŷָ�ÿһ����ע���bean����
        }
    }
]
```
####Mapע��
���֧��Map���͵�ע�롣�ȿ�ʾ������
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
    @Resource//ʵ���˽ӿ�Order��bean���ᱻע��
    @Mapkey("keyName")//���б�ע���bean����һ���޲ε�keyName������ʹ�ø÷�������ֵ��Ϊ��bean��key
    private Map<Integer,Order> map = new Hashmap();
    
    @Resource//ʵ����Order�ӿڵ�bean���ᱻע�룬����û��MapKeyע�⣬���и�mapע��ʱ��key���Ǳ�ע���bean��beanName�����Ը�Map��key���ͱ�����String
    private Map<String,Order> map2 = new Hashmap();
}
```
��ʾ��������Կ�������Map���Խ���ע�룬�Ƿ�ʹ��`@MapKey`ע��,Ч���ǲ�ͬ��
+ ʹ��`@Mapkey`ע�⡣Mapע���ԭ����Map���Ե�V�����ͣ����м̳и����ͻ���ʵ���˸ýӿڵ�bean������ע�룬�����Ƕ�Ӧ��key����bean�е�ĳһ���޲η����ķ���ֵ�������������������ע��`MapKey`ָ����ͬʱ��Map���Բ���Ϊnull��
+ ��ʹ��`@Mapkey`ע�⡣mapע���ԭ����Map���Ե�V�����ͣ����м̳и����ͻ���ʵ���˸ýӿڵ�bean������ע�롣�����Ƕ�Ӧ��key����bean�����ơ�
####ͨ�������ļ�����Mapע��
�������ļ��У���map���͵��ֶε�д�������֣���ͬ��д����Ӧ��ͬ��ʶ��ʽ��
1. **version1!getName:bean1;bean2;bean3**:����д����,`:`֮�������������Ҫע���bean�����ơ�`:`֮ǰ����������Щbean�ж���Ҫ�߱���һ���޲����з���ֵ�ķ��������֡��ڶ�map�ֶν���ע���ʱ�򣬻�������bean�ĸ÷������õ�����ֵ��Ϊkey����bean��Ϊvalue����map�С�
2. **version2!keyName1:bean1|keyName2:bean2**:������д���У�ʹ��`|`���в�ͬ���ݵ�������ÿһ�����ݵ��У�`:`ǰ��������key��֮���������bean������
����
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
3. **version3!beanName1,beanName2**:����д���У�beanName1��Щ����Ҫע���bean�����ơ�����Ӧ��key���Ǹ�bean�����ơ�
###�趨bean�ĳ�ʼ������
��һЩӦ�ó����д���һЩ�����ٽ�bean��ʵ���ṩ֮ǰ����Ҫ����һ���޲εĳ�ʼ�������������һ����ʹ��java����Ƕע��`@PostConstruct`����ע����ڷ����ϣ���bean�ڱ��ṩǰ�����������������������ϲű��ṩ����ע��һ����ֻ����һ��
```java
@Resource
public class Person
{
    @PostConstruct
    public void say(){
        System.out.println("���");
    }
    public static void main(String args[])
    {
        JfireContext context = new JfireContextImpl("com.test");
        context.getBean(Person.class);//��ȡ��bean��ʱ��ͻ���ñ�InitMethodע���˵ķ����������ǵ���say������
    }
}
```
###ͨ�������ļ��趨bean�ĳ�ʼ������
���˴�����ʹ��ע���⣬Ҳ����ʹ�������ļ��ķ�ʽ����bean��ʼ�����������á�ʾ����������
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
###������ʼ�������ӿ�
bean����ʵ��������ʼ���ӿڡ��ýӿڴ�������
```java
public interface ContextInitFinish extends Order
{
    /**
     * ��������ʼ����ɺ󣬸ýӿڻᱻ��������
     * 
     * @author �ֱ�(eric@jfire.cn)
     */
    public void afterContextInit();
}
```
�ӿڰ�������������һ��`order`����������������ж��beanʵ��������ӿڣ���������ִ�С������Ȼ����һ��`afterContextInit`������ʾ������ʼ����Ϻ󣬻�����������


###����ע��
����ע�빦�ܿ�������ʵ������ʱ�������ע�������ƶ��õ�ֵ��֧�ֵ����ʹӻ������͵���װ�����Լ�String����Щ���͵����顣�������ļ��У���ʹ��param�ֶ�����ʾ�ġ������������
```json
"beanConfigs": //��bean��������
[
        {
            "beanName": "com.entity.Person", //ָ��Ҫ���õ�bean������
            "params": //��bean���в���ע�롣
            { 
                "name": "test",
                "age": "19",
                "hasHome": "false"
            }
        }
    ]
```
`params`�ֶ��м�ֵ�Զ����ַ������ͣ�����ע���ʱ�����������ԵĶ��������Զ�ת�������������Ļ����������ļ���ʹ�ö��Ž�������������
```json
"attribute": 
{
    "params": 
    {
        "name": "�ֱ�",
        "age": "25",
        "boy": "true",
        "arrays":"12,123,1234"
    }
}
```
�Զ��������������������ʱ����Զ����ö��ŷָ���ע�����ݲ����������ת�������������ʽע��
###���������ļ�
����Ľ����н����в��ֵ������ļ���˵�����ˣ����ڸ���һ�������������ļ���������һ��ֱ�۵���ʶ��������Ҫ˵�����ǣ���һ��bean�������ã������������ط����С�
��һ���ط�����`beanConfigs`�ڵ��С�����ָ����Ҫ���õ�bean������Ȼ��������á�������������bean���Ѿ������������еġ�
�ڶ����ط�����bean�ڲ���`attributes`�ڵ��Ͻ������á�����ʱ��������Ϣ�����ڲ���Ϣ�����bean�������õ�ʱ�򱻼��뵽�����е�
```json
{
    "packageNames": //ָ��Ҫɨ��İ�·�����������·������·���µ�������
    [
        "com.jfire.core.test.function.base",
        "cn.starnet.entity"
    ],
    "beanConfigs": //���Ѿ��������е�bean���ж�����Ϣ����
    [
        {
            "beanName": "com.entity.Person", //ָ��Ҫ���õ�bean�����ƣ������bean���Ѿ��������д��ڵ�
            "params": //���ø�bean�Ĳ���ע��
            { 
                "name": "test",
                "age": "19",
                "hasHome": "false"
            },
            "dependencies": //���ø�bean������ע��
            {
                "home": "com.test.Home",
                "address": "com.test.Address"
            },
            //���ø�bean�ĳ�ʼ������
            "postConstrutcMethod": "say"
        }
    ],
    "beans": [//����Ҫ���뵽������bean����Ϣ�������İ���bean���ƣ�bean�Ƿ�����bean�����ȫ�޶���
        {
            "beanName": "com.jfire.core.test.function.base.data.House",//bean������
            "prototype": false,//bean�Ƿ�ԭ�͡�ԭ�;��Ƿǵ���
            "className": "com.jfire.core.test.function.base.data.House"//beanʹ�õ����ȫ�޶���
        },
        {
            "beanName": "com.jfire.core.test.function.base.data.ImmutablePerson",
            "prototype": false,
            "className": "com.jfire.core.test.function.base.data.ImmutablePerson",
            "params": //���ø�bean�Ĳ���ע��
            {
                "name": "test",
                "age": "25",
                "boy": "true"
            },
            "dependencies": //���ø�bean������ע��
            {
                "home": "com.test.Home",
                "address": "com.test.Address"
            },
            //���ø�bean�ĳ�ʼ������
            "postConstructMethod": "say"
        }
    ]
}
```
##AOP����
��ܴ���ǿ���AOP���ܣ�ͨ�����ʽ����·��ƥ�䣬���Ժܷ���Ķ������AOP��̡������AOPƥ�������Ϊ�������֡�������Ҫ����AOP�ࡣ���巽ʽ����
```java


//���ע��������bean��һ��AOP��ǿ�ࡣ����ֵ�Ǳ�֯�루����ǿ��Ŀ�� ��ȶ���һ����˼�����ƣ������ȫ�޶�����������ʽ֧��ͨ���`*`�� * �÷��ű�ʾ���ⳤ�ȵ������ַ�
@EnhanceClass("com.test.*.Action.*")
@Resource//���ע���ÿ�ܿ��Է������AOP��
public class Aop
{
    @BeofreEnhance(value="before()",order=1)//�����һ��ǰ�����أ���ʾҪ���صķ�����������before��û����Σ�order����������������ж�����ط���ͬʱ��һ�������������ء�ʹ����Ȼ˳���������
    public void before(ProceedPoint point){}
}
```
AOP��ǿ���������衣
1. ͨ����ǿ���ϵ�`EnhanceClass`ע���`value`ֵ�����������ƥ�䣬ȷ����Ҫ��ǿ��Ŀ����.
2. ͨ����ǿ���з����ϵ���ǿע�⣬ƥ��Ŀ��������Ҫ��ǿ��Ŀ�귽����Ȼ��ʹ����ǿ������Ŀ�귽��������ǿ��
**Ŀ����ȷ��**
AOP�����У������Ƕ�Ŀ�����ȷ����EnhanceClas�ϵ�ֵ�����Ƕ���ȫ�޶�������ƥ�䡣����`*`����ƥ�����ⳤ�ȵ������ַ���
>����`com.test.*Action`�Ϳ���ƥ����`com.test.UserAction`


**����ȷ��**
����ȷ����ͨ����ǿע���е�·��ֵ�����еģ�ֻ�Է������Ͳ�������ƥ�䣬���Է���ֵ��Ҫ�󡣱���·��ֵ��`getName()`��ʾƥ��һ��������getName�������޲εķ�����·��ֵҪ��������`()`�š����Ҫ�Բ������ͽ���ƥ�䣬����д��`()`�У�����ֱ��д�������͵ļ����ƣ�Ҳ����д���͵�ȫ�޶�����������֮�����һ���ո����������`setName(String int)`��`setName(java.util.String int)`�ǵȼ۵�ƥ����������ƥ���ʱ��Ҫ���Բ������ͣ�ʹ��`(*)`���ɡ�����`getName(*)`�ͱ�ʾ����������ͺ͸�����ֻҪ����������getName���ɡ�
����������������飬�����д��`int[][]`��������ʽ��
**��ǿ��������**
���һ��Ŀ�귽�����ж����ǿ����������ǿ��������ע���ϵ�order�ֶε�ֵ��������
**��ǿ���������**
��ǿ������ֻ��һ����Σ�����`com.jfire.core.aop.ProceedPoint`.������ṩ�˹���Ŀ�귽���ĸ�����Ϣ������������ִ��ʵ���ȡ��������ڸ�����ǿ�������𵽲�һ�������á�������4������������
1. `ProceedPoint.getHost()`:��ȡִ��ԭ�����Ķ���ʵ��
2. `ProceedPoint.getParam()`�����ԭ�������������
3. `ProceedPoint.getResult()`:�����ǿ�������ú�ķ���ֵ
4. `ProceedPoint.setResult(Object invokedResult)`:������ǿ�������ú����յõ��ķ���ֵ


����һЩ�������ض�����ǿ�в������壬������һ���⡣
###ǰ����ǿ
ǰ����ǿʹ��ע��`@BeforeEnhance`����ʾ��ǿ��������Ŀ�귽��֮ǰִ�С�ͬʱ����ͨ������`ProceedPoint.setPermission(boolean)`�������Ƿ��������Ŀ�귽����ִ�С��������Ϊfalse�ͱ�ʾ����Ŀ�귽��ִ�С�Ҳ����ͨ������`ProceedPoint.setResult(Object)`���趨�ڲ���Ŀ�귽��ִ�е�����µķ���ֵ��
###������ǿ
������ǿʹ��ע��`AfterEnhance`����ʾ��ǿ��������Ŀ�귽��ִ��֮��ִ�С�����ͨ������`ProceedPoint.getResult()`���Ŀ�귽�����ú�Ľ��ֵ������ͨ������`ProceedPoint.setResult(Object invokedResult)`�޸����յķ��ؽ����
###�쳣��ǿ
�쳣��ǿʹ��ע��`ThrowEnhance`����ʾ��ǿ��������Ŀ�귽���׳��쳣ʱִ�С���ע��߱�һ������`type`,��һ��Class���顣������ʾĿ�귽���׳�ָ�����͵��쳣ʱ��ǿ���������á�Ĭ�ϵ�ֵ��`Throwable`.
###������ǿ
������ǿʹ��ע��`AroundEnhance`.������ǿ�У�Ŀ�귽���Ƿ�ִ�о�ȡ������ǿ����������ͨ������`ProceedPoint.invoke()`��ִ��ԭ���ķ��������ԭ�������з���ֵ����ô�ڵ���`ProceedPoint.invoke()`ִ��ԭ�����󣬸÷����᷵��ԭ�����ķ���ֵ����ʱͨ��`ProceedPoint.setResult(Object invokedResult)`������������ǿ�����ķ���ֵ��