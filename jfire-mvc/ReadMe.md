#Jfire-mvc�����ĵ�
[TOC]
##Jfire-mvc��ʲô
Jfire-mvc��һ���Чǿ���MVC��ܡ�Լ���������õ�ԭ��ʹ��ͨ���ÿ�ܿ���web���������ȫ����ע�⡣��ôJfire-mvc������Щǿ��Ĺ����أ�

+ �򵥵�urlƴд����
+ ��Ч�����ݰ󶨹��ܣ���ҳ�����Զ�ת��Ϊ����
+ ͸���򵥵��ļ��ϴ�
+ �ṩRest������֧��
+ ��Ƕ��ҳ�滺�湦�ܣ����50%io����
+ ���������ع��ܡ��޸�Դ�ļ���������������������Ч
+ ����ģʽ�ṩǿ�������

������һ��Demoʾ�����ô��������ʹ��Jfire-mvc��ô�ļ򵥡�

1. ������Ҫ�����web.xml����web������ͬѧ�϶��������İ������Jfire-mvc��ȫ����Ҫ��web.xml�ж����κ����ݡ�ֻ��Ҫ��һ��Ĭ�ϵ��ļ����ɡ�**��Ȼ����Ҫ��֧��servlet3.0���ϵ�**��������һ������
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
    http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">
    <display-name>demo</display-name>
</web-app>
```
���ű�дAction��
```java
@Resource//���ע�������ᱻ����ʶ��
@ActionClass("admin")//���ע�����������url���࣬���Ұ󶨵�url��ǰ׺��admin
public class LoginAction
{
     @ActionMethod(resultType = ResultType.Beetl, url = "login", methods = { RequestMethod.GET })//���ע��������������url����һ�������ķ���url��/admin/login.����http����Ϊget���󡣸����󷵻صĽ����һ��htmlҳ����ͼ����beetl������freemarker�����Ǹ�Ϊǿ�����ã�������Ⱦ
    public ViewAndModel loginview(HttpSession session)
    {
	    //����һ����ͼ���󣬸���ͼ����˵����Ҫ��Ⱦ���ļ���web��Ŀ¼�µ�/admin/login.html�ļ�
        return new ViewAndModel("/admin/login.html");
    }
}
```
������Ŀ�����������blog����ô���������еķ����󶨵�url����xx.xx.xx.xx:80/blog/admin/login��ͨ��������ӿ��Կ�����ʹ��Jfire-mvc����web������һ���ǳ��򵥵����顣������ȫע�⿪�������������������ϸ���˽����ʹ��Jfire-mvc����
###��Spring-Mvc���
�������ڴ󲿷ֿ�����Ӧ�ö�ѡ����Spring-mvc����Struts2�ˡ�Spring��mvc�ڸ������ȷ�Ƿǳ����õġ�MVC��ܷ�չ��������ʵ�Ѿ�û�ж������ظкͼ���������Ӧ��˵Spring-mvc��java�����mvc����һ���ܸߵķᱮ��Jfire-mvc�����ģ�Ҳֻ���������ԺͿ�������������Ŭ��������**�����˴󲿷ֲ���Ҫ�����ã�Ĭ���������Կ��伴�ã������������ػ��ƣ��������Ĳ���Ϳ��������޸�Դ���뼴����Ч���������ķ���**
##ʹ��Jfire-mvc����web����
###����һ��Action��/ʹ��@ActionClassע��
���Ƚ�һ���ඨ��ΪAction�࣬���������е�public����������url�󶨡�Ϊ�˶��壬��Ҫ����ע�⡣

1. @Resource������������ܵ������Ĺ���Jfire-mvc��������Jfire-core���������е����������ϵ��
2. @ActionClass�������������Action�࣬���е�public�������Ժ�url�󶨡����ע��߱�һ��String�Ĳ���ֵ��

 + @ActionClass("login")��ֵ�������������а󶨵�url��ǰ׺��login
 + @ActionClass()�ձ�ʾʹ��Ĭ��ֵ��Ϊע����ļ����ƣ���������LoginAction������ζ��������������а󶨵�urlǰ׺����LoginAction
 + @ActionClass("/")��ֵ�����������ǰ׺��Ҳ����˵�����°󶨵�url����Ҫ���ǰ׺��Ϣ��

###����һ��Action����/ʹ��@ActionMethodע��
*�ٶ���Ŀ����������blog��actionclassע���������admin*
����һ��Action������Ҫʹ��ע��@ActionMethod��ʹ���˸�ע���public��������һ��url���а󶨡���ע��߱����������

1. url:��������������󶨵�url�ĺ�׺��ʲô�����ֵ�����������
 + @ActionMethod(url="ctrl/login")��ζ�Ÿ�url��ctrl/login��β����������urlӦ����*��������*/*@ActionClassע���ֵ*/��url����ֵ��ʾ����/blog/admin/ctrl/login
 + @ActionMethod()Ϊ����ζ��urlΪĬ�����ȡֵҲ���ǵ��ڷ���������
 + @ActionMethod(url="/")��ζ�Ÿ�url���ر��׺��Ҳ����˵������urlֻ�����������ƺ�@ActionClassע���urlǰ׺��ʾ����/blog/admin
2. resultType:����һ��enum���͵�ֵ��ȡֵ���ݰ������¼���
 + Json:��ʾ���صĽ��Ӧ����һ�����󣬲��Ҹö���ᱻ���л���json�ַ���
 + Beetl:��ʾ���صĽ����һ��ViewAndModel����ʵ������ʵ������������ģ���ļ�·��������beetl��Ⱦ����htmlҳ�����ʽ���͸������
 + Html:��ʾ���ص�һ��������ҳ���ļ�·����ViewAndModel����ʵ������ʵ����htmlҳ�����ʽ���͸������
 + Redirect:��ʾ��Ҫ����һ��ҳ���ض��򡣷���ֵ����һ������ҳ���ļ���·����Stringֵ��
 + None:��ʾ�����ķ���ֵ��void��Ҳ���ǿ�ܲ�������κδ���
 + String:��ʾ�������ص����ַ��������ֱ�ӽ��ַ������
 + Bytes:��ʾ�������ص�byte���飬���ֱ�ӽ�byte�������
 
3. contentType:��ʾ����Ӧ��contentType����һ��enum��ֵ���ж��ع̶�ֵ����д�롣Ĭ��ֵΪ
4. methods:��һ�����飬ΪRequestMethod�����顣��ʾ��url�󶨵�http������֧��put��post��delete��get��Ĭ��ֵΪget��post
5. readStream:һ������ֵ����ʾ�÷����Ƿ�ϣ��ֱ�Ӵ���HttpServletRequest�е���������Ĭ��Ϊfalse�����Ϊtrue�����ܲ�����в����󶨺��ļ��ϴ�����ȹ��̡�

###���
����������������裬һ��Url���ʵ�ַ�ͱ��󶨵��˷����ϡ����������ķ���urlΪ@ActionClass��ע��ֵ��@ActionMethod�е�urlע��ֵ��ͬ��ɡ�������Ŀ����·��Ϊblog�����µĴ��룬�����url:*xx.xx.xx.xx:80/blog/admin/login*���󶨵������login������
```java
@Resource
@ActionClass("admin")
public class LoginAction
{
    @ActionMethod(resultType = ResultType.Beetl)
    public ViewAndModel login()
    {
        return new ViewAndModel("/admin/login.html");
    }
}
```
###Action��ʼ����ɽӿ�
����û�����Action������ʼ����ɵ�ʱ�����һЩ��������ֻ��Ҫ��һ����ʵ���˽ӿ�ActionInitListener���ɡ�
```java
@Resource//���ע�ⲻ���٣�����Ͳ�����Ч��
public class ActionInit implements ActionInitListener
{

    @Override
    public void init(Action action)//ÿһ��action��������ʼ����ɺ󶼻���øýӿڵ��������
    {
        System.out.println(action.getRequestUrl());
    }

}
```
##ʹ��Jfire-mvc��ɲ�����
�κ�һ��mvc��ܶ��������ṩǿ��Ĳ����󶨹��ܡ�Jfire-mvcͬ�������⡣Jfire-mvc�ṩ��͸���Ĳ����󶨹��ܡ��û�ֻ��Ҫ��Action��������д��������http��������������ͬ�Ĳ����ͻ��Զ����󶨵�����������ϡ�����������У���������Ҫ�����ע������������ǲ�ͬ��ʾ����
###�󶨻������ͣ���װ�࣬�ַ����İ�
ֻҪ�ڷ����������ʹ�û������ͣ���װ���ͣ�String���;Ϳ����Զ���http������������ͬ�Ĳ����󶨵�������ϡ�����
```java
@ActionMethod()
//��hhtp�����е�user������password�����ͻᱻ�Զ��󶨵��������������ϡ�������ڲ����ڣ���Ϊnull
public void login(String user,String password)
```
###���Զ�������
��ܻ��Զ���http�����а���"**��������.����������**"�Ĳ����󶨵�һ���ɿ�����ɵ��Զ������͵Ķ���ʵ���С�����
```java
public class User
{
    String name;
    String password;
}
@ActionMethod()
//http�������������user.name��user.password���ƵĲ��������ܻ�����һ��User��ʵ�����ҽ������󶨵������С����ҽ����ʵ���󶨵�����ϡ�
public void login(User user)
```
���ϣ��ǰ׺���ǲ������ƣ�����ָ����ֵ������ʹ��@RequestParamע��������á�����
```java
public class User
{
    String name;
    String password;
}
@ActionMethod()
//http�������������u.name��u.password���ƵĲ��������ܻ�����һ��User��ʵ�����ҽ������󶨵������С����ҽ����ʵ���󶨵�����ϡ�
public void login(@RequestParam("u")User user)
```
���@RequestParam("")�����Բ������ơ�Ҳ����˵
```java
@ActionMethod()
//http�������������name��password���ƵĲ��������ܻ�����һ��User��ʵ�����ҽ������󶨵������С����ҽ����ʵ���󶨵�����ϡ�
public void login(@RequestParam("")User user)
```
###����������
����������������ͣ���Ĭ������¸�ʽ��Ҫ����"yyyy-MM-dd HH:mm:ss"��Ҫ�������Ҫ�Լ������ʽ��ʹ��@RequestParam(dateFormat="yyyy-MM-dd")���޸ġ�����
```java
@ActionMethod()//���http�������в���date��ʹ��yyyy-MM-dd���и�ʽ��ΪDate���󣬲��Ұ󶨵��ò�����
public void login(@RequestParam(dateFormat="yyyy-MM-dd") Date date);
```
###��Map����
����������ʵûʲô��˵�ģ�ʵ���Ͼ������е�http�������ƺͲ���ֵ��map����
###��HttpServletRequest,HttpServletResponse,HttpServletSession,ServletContext
��������ֻ��Ҫ������ζ�������ӿڣ����۲�����������ʲô�����Ա���
###��UploadItem����/͸�����ļ��ϴ�
����������ʵ��Ϊ�˽����ļ��ϴ�ʹ�õġ�͸�����ļ��ϴ���һ���ϸ��MVC������ر��ġ���Jfire-mvc����һ�����ر����ס������һ��http������ֻ��һ���ļ���Ҫ�ϴ����򷽷�����а���UploadItem���ɡ��������������ϴ��ļ���������Ϣ�����ļ����ƣ��ϴ������ƣ��ļ��������������ṩ��һЩ��ݵķ��������һ��Http�����а����˶���ϴ��ļ����򷽷������ֻҪ����java.lang.List<UploadItem>���ɡ�ÿһ���ϴ��ļ�����һ��UploadItem����ʵ����
ʾ��
```java
@ActionMethod()//��http�����а���һ���ϴ��ļ����ᱻ�󶨵�������UploadItem�����ϡ�
public void login(UploadItem item)
```
##���ض���
һ��url��������������󶨵ķ���ִ�к󣬱�Ȼ��Ҫ����һЩ��Ϣ������������׼���˶��ֵķ���ֵ��ʹ����ѡ��
###ViewAndModel/��ͼģ�Ͷ���
���������£�һ��url��������Ҫ����һ��ҳ��ġ���ҳ����Ҳ��Ȼ�����һЩ��Ҫ������Ⱦ�Ĳ�����Ϣ����ǰһ��ʹ��jsp����FreeMarker��Jfire-mvc�Ƽ�ʹ��Beetl����������������������ǿ�󣬶���ʹ�ü򵥡�������һ���򵥵�����
```java
@Resource
@ActionClass("/")
public class IndexAction
{
    @ActionMethod(resultType=Beetl,contentType="text/html")
    public ViewAndModel index()
    {
        ViewAndModel vm = new ViewAndModel("/admin/index.bt");
        vm.addData("count",1);//vm���Կ�����һ��Map���������Է��������ֵ������key������String����
        vm.addData("name","eric");
        return vm;//�ڷ��������������󣬷��ض��󡣿�ܸ���ע��ʹ��Beetl��ģ���ļ�������Ⱦ�����ҽ���Ⱦ��Ľ�����͸���������
    }
}
```
��Ȼ���ֻ�Ƿ���һ��htmlҳ���û����������Ⱦ������������÷�������ΪHtml������
```java
@Resource
@ActionClass("/")
public class IndexAction
{
    @ActionMethod(resultType=Html,contentType="text/html")
    public ViewAndModel index()
    {
       //����һ��htmlҳ��
       return new ViewAndModel("/admin/helloworld.html");
    }
}
```
###����json����
����һ��json�ַ����������ڵ�http������˵��ʮ�ֳ����ġ���Jfire-mvc�з���һ��json�ַ����ǳ��򵥡�ֻ��Ҫ�����ؽ���趨Ϊjson�����ܻ��Զ������ؽ���Ķ���ʵ�����л�Ϊjson�ַ����������������Ӧ�з���������
```java
public class User
{
    private String name;
    private String password;
}
public class IndexAction
{
@ActionMethod(resultType = ResultType.Json, url = "{id}", methods = { RequestMethod.GET })
    public User get(int id)
    {
        //��ܻὫ������ض������л�Ϊjson�ַ�����������Ӧ
        return new User();
    }
}
Jfive-mvc��json���л������ɿ�Դ���codejson�ṩ֧�֡�������Ϊ�����ڿ��֮�ס�
```
###����һ��String����
��Щʱ��������Ҫ����һЩ�ض���ʽ���ַ�������ʱ�����ֱ�ӷ���һ��String����ܻὫ��ֱ����Ӧ�����������С�
###���ض���������
��һЩ�緵����֤�����ͼƬ�ĳ��ϣ���Ҫ���ض����Ƶ��������������ݡ�������ʾ������
```java
@ActionMethod(resultType = ResultType.Bytes)
    public byte[] getTicket(HttpServletResponse response, HttpSession session)
    {
        // ������Ӧ�����͸�ʽΪͼƬ��ʽ
        response.setContentType("image/jpeg");
        // ��ֹͼ�񻺴档
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String code = VerifyCodeUtils.generateVerifyCode(4);
        session.setAttribute("code", code.toLowerCase());
        //������֤���ͼƬ����
        return new byte[1024];
    }
```
##Rest֧��
Jfire-mvc�ṩ��Rest������֧�֡�֧����ȡurl�е��ض���ϢΪ�����������а󶨡������Ǹ�����
```java
@ActionMethod(url="login/{username}/{password}")
//���һ��url��login/linbin/123456��ᱻ�÷����󶨵�urlʶ���ҵ��ø÷���������Ӧ��ͨ����������Ҳ����������ͬ�ķ����󶨵���Ӧ������ϡ�
public void login(String username,String password);
```
������������ʹ��Rest��ʵ�ܼ򵥡�ֻ��Ҫ��url�����н���Ҫ��ȡ�Ĳ�����`{}`��Χ������Ȼ���ڷ��������ʹ����ͬ�����ƣ���ܾͿ����Զ���ɰ󶨡�
##��Ƕ�Ļ��湦��
�ܶ�ʱ��webҳ������ôһ�������һ��url��Ҫ����һ����beetl��Ⱦ��ҳ�档�����״���Ⱦ���벻��Ҫ�仯����ʱ����ʹ�ÿ��Ĭ�ϵĻ��湦�ܣ����������ķ��ʾ�������ʹ��beetl��Ⱦ�ˡ�����������
```java
@Resource
@ActionClass("admin")
public class AdminAction
{
    //���췽���ĵڶ��������ʹ����Ƿ������湦��
    private ViewAndModel vm = new ViewAndModel("/admin/login.html",true);
    
    //����/admin/login1��ÿ�ζ���������Ⱦģ��ҳ��/admin/login.html��������Ⱦ���
    @ActionMethod(resultType=Beetl)
    public ViewAndModel login1()
    {
        return new ViewAndModel("/admin/login.html");
    }
    
    //����/admin/login2��᷵��ģ��ҳ��/admin/login.html����Ⱦ����������״��Ժ��ÿ�η��ʶ��Ƿ����ڿ���еĻ��档����������Ⱦ��
    @ActionMethod(resultType=Beetl)
    public ViewAndModel login2()
    {
        return vm;
    }
    
}
```
����ҳ�滺�湦�ܶ��ڱȽϾ�̬��ҳ���Ƿǳ��а����ġ��������ԣ����Դ���50%��io��������������beetl��Ⱦ����ʹ�ã����ҷ���html��ʱ��Ҳ����ʹ�á��÷���ͬ��
##�����ع��ܣ��޸�Դ������������������
�ڿ����У�������������ع��ܣ��Ϳ��Դﵽ�޸�Դ�������������tomcat�ȷ������Ĺ��ܡ���Ϊ������ɱ����������Ҳ��Jfire-mvc�б��ṩ�ˡ�Ϊ��ʹ��������ܣ�������Ҫ��web.xml������������
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
	http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">
	<display-name>Archetype Created Web Application</display-name>
	<servlet>
	    //�̶�����
		<servlet-name>EasyMvcDispathServlet</servlet-name>
		//�̶�����
		<servlet-class>link.jfire.mvc.core.EasyMvcDispathServlet</servlet-class>
		<init-param>
			//debugΪtrue�������������ع��ܡ����������������¹ر�
			<param-name>debug</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
		    //��ص�λ�ã���ʾ��ܼ�ظ�Ŀ¼�Լ���Ŀ¼�µ�class�ļ��仯������ļ������˱仯������һ��url���ʵ�ʱ��ͻ����¼���class���������
			<param-name>monitorPath</param-name>
			<param-value>E:/workspace/blog/target/classes</param-value>
		</init-param>
		<init-param>
		    //��Ҫ���ص�class���ڵ�package������ʹ��`,`������ֻ���ڶ�Ӧ��package�µ�class���ܱ�����
			<param-name>reloadPackages</param-name>
			<param-value>link.jfire.blog</param-value>
		</init-param>
	</servlet>
</web-app>
```
������ɼ��Ŀ¼������package�󡣾Ϳ����������������п�����һ��������Ҫ�޸Ĵ���ĵط���ֱ�ӽ����޸ġ����ŷ���url�ͻᷢ��������Ч�ˡ����ڸ�������ʹ�����Զ����ClassLoader�����Բ���Է���������κ�Ӱ�죬**���غ�ԭ�е�session��Ϣ��ServletContext��ϢҲ���ᶪʧ**