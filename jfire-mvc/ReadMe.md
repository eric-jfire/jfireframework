#Jfire-mvc开发文档
[TOC]
##Jfire-mvc是什么
Jfire-mvc是一款高效强大的MVC框架。约定优于配置的原则使得通过该框架开发web程序可以完全基于注解。那么Jfire-mvc都有哪些强大的功能呢？

+ 简单的url拼写规则
+ 高效的数据绑定功能，网页参数自动转化为对象
+ 透明简单的文件上传
+ 提供Rest的完整支持
+ 内嵌的页面缓存功能，提高50%io性能
+ 开发热重载功能。修改源文件无需重启服务器即刻生效
+ 单例模式提供强大的性能

下面是一个Demo示例，让大家来看看使用Jfire-mvc多么的简单。

1. 首先需要定义个web.xml（做web开发的同学肯定对这个不陌生）。Jfire-mvc完全不需要在web.xml中定义任何内容。只需要有一个默认的文件即可。**当然，需要是支持servlet3.0以上的**。下面是一个例子
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
    http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">
    <display-name>demo</display-name>
</web-app>
```
接着编写Action类
```java
@Resource//这个注解表明类会被容器识别
@ActionClass("admin")//这个注解表明该类是url绑定类，并且绑定的url的前缀是admin
public class LoginAction
{
     @ActionMethod(resultType = ResultType.Beetl, url = "login", methods = { RequestMethod.GET })//这个注解表明这个方法和url绑定在一起。完整的访问url是/admin/login.并且http请求为get请求。该请求返回的结果是一个html页面视图，由beetl（类似freemarker，但是更为强大易用）进行渲染
    public ViewAndModel loginview(HttpSession session)
    {
	    //返回一个视图对象，该视图对象说明需要渲染的文件在web根目录下的/admin/login.html文件
        return new ViewAndModel("/admin/login.html");
    }
}
```
假设项目部署的名字是blog。那么上面例子中的方法绑定的url就是xx.xx.xx.xx:80/blog/admin/login。通过这个例子可以看到，使用Jfire-mvc开发web程序是一个非常简单的事情。零配置全注解开发。下面就让我们来详细来了解如何使用Jfire-mvc开发
###与Spring-Mvc相比
国内现在大部分开发者应该都选用了Spring-mvc而非Struts2了。Spring的mvc在该领域的确是非常易用的。MVC框架发展到现在其实已经没有多少神秘感和技术含量。应该说Spring-mvc在java领域的mvc上是一座很高的丰碑。Jfire-mvc所做的，也只是在易用性和开发方便性上做努力。比如**减少了大部分不需要的配置，默认配置足以开箱即用；增加了热重载机制，无需额外的插件就可以享受修改源代码即刻生效无需重启的方便**
##使用Jfire-mvc开发web程序
###定义一个Action类/使用@ActionClass注解
首先将一个类定义为Action类，表明该类中的public方法可以与url绑定。为了定义，需要两个注解。

1. @Resource，表明这个类受到容器的管理。Jfire-mvc背后依靠Jfire-core来管理所有的类和依赖关系。
2. @ActionClass，表明这个类是Action类，其中的public方法可以和url绑定。这个注解具备一个String的参数值。

 + @ActionClass("login")该值表明该类中所有绑定的url的前缀是login
 + @ActionClass()空表示使用默认值，为注解类的简单名称，比如类是LoginAction，则意味着这种情况下所有绑定的url前缀都是LoginAction
 + @ActionClass("/")该值表明忽略这个前缀。也就是说该类下绑定的url不需要这个前缀信息。

###定义一个Action方法/使用@ActionMethod注解
*假定项目部署名称是blog，actionclass注解的名称是admin*
定义一个Action方法需要使用注解@ActionMethod。使用了该注解的public方法会与一个url进行绑定。该注解具备多个参数。

1. url:表明这个方法锁绑定的url的后缀是什么。这个值有三种情况。
 + @ActionMethod(url="ctrl/login")意味着该url以ctrl/login结尾，即完整的url应该是*部署名称*/*@ActionClass注解的值*/该url参数值。示例：/blog/admin/ctrl/login
 + @ActionMethod()为空意味着url为默认情况取值也就是等于方法的名称
 + @ActionMethod(url="/")意味着该url无特别后缀，也就是说完整的url只包含部署名称和@ActionClass注解的url前缀。示例：/blog/admin
2. resultType:这是一个enum类型的值。取值内容包含如下几种
 + Json:表示返回的结果应该是一个对象，并且该对象会被序列化成json字符串
 + Beetl:表示返回的结果是一个ViewAndModel对象实例，该实例包含参数和模板文件路径，经过beetl渲染后以html页面的形式发送给浏览器
 + Html:表示返回的一个包含了页面文件路径的ViewAndModel对象实例。该实例以html页面的形式发送给浏览器
 + Redirect:表示需要进行一个页面重定向。返回值的是一个代表页面文件的路径的String值。
 + None:表示方法的返回值是void。也就是框架不会进行任何处理
 + String:表示方法返回的是字符串，框架直接将字符串输出
 + Bytes:表示方法返回的byte数组，框架直接将byte数组输出
 
3. contentType:表示该响应的contentType。是一个enum的值，有多重固定值可以写入。默认值为
4. methods:是一个数组，为RequestMethod的数组。表示该url绑定的http方法。支持put，post，delete，get。默认值为get，post
5. readStream:一个布尔值。表示该方法是否希望直接处理HttpServletRequest中的输入流。默认为false。如果为true，则框架不会进行参数绑定和文件上传处理等过程。

###完成
经过上面的两个步骤，一个Url访问地址就被绑定到了方法上。其中完整的访问url为@ActionClass的注解值和@ActionMethod中的url注解值共同组成。比如项目部署路径为blog，如下的代码，则代表url:*xx.xx.xx.xx:80/blog/admin/login*被绑定到了这个login方法。
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
###Action初始化完成接口
如果用户想在Action方法初始化完成的时候完成一些动作。则只需要有一个类实现了接口ActionInitListener即可。
```java
@Resource//这个注解不能少，否则就不会生效了
public class ActionInit implements ActionInitListener
{

    @Override
    public void init(Action action)//每一个action方法被初始化完成后都会调用该接口的这个方法
    {
        System.out.println(action.getRequestUrl());
    }

}
```
##使用Jfire-mvc完成参数绑定
任何一款mvc框架都会力求提供强大的参数绑定功能。Jfire-mvc同样不例外。Jfire-mvc提供了透明的参数绑定功能。用户只需要在Action方法中填写参数，则http请求中有名称相同的参数就会自动被绑定到方法的入参上。在这个过程中，基本不需要额外的注解帮助。如下是不同的示例。
###绑定基本类型，包装类，字符串的绑定
只要在方法的入参中使用基本类型，包装类型，String类型就可以自动将http请求中名称相同的参数绑定到该入参上。比如
```java
@ActionMethod()
//则hhtp请求中的user参数和password参数就会被自动绑定到这个方法的入参上。如果存在不存在，则为null
public void login(String user,String password)
```
###绑定自定义类型
框架会自动将http请求中包含"**参数名称.类属性名称**"的参数绑定到一个由框架生成的自定义类型的对象实例中。比如
```java
public class User
{
    String name;
    String password;
}
@ActionMethod()
//http请求中如果存在user.name和user.password名称的参数，则框架会生成一个User类实例并且将参数绑定到属性中。并且将这个实例绑定到入参上。
public void login(User user)
```
如果希望前缀不是参数名称，而是指定的值，可以使用@RequestParam注解进行设置。如下
```java
public class User
{
    String name;
    String password;
}
@ActionMethod()
//http请求中如果存在u.name和u.password名称的参数，则框架会生成一个User类实例并且将参数绑定到属性中。并且将这个实例绑定到入参上。
public void login(@RequestParam("u")User user)
```
如果@RequestParam("")则会忽略参数名称。也就是说
```java
@ActionMethod()
//http请求中如果存在name和password名称的参数，则框架会生成一个User类实例并且将参数绑定到属性中。并且将这个实例绑定到入参上。
public void login(@RequestParam("")User user)
```
###绑定日期类型
参数如果是日期类型，则默认情况下格式需要符合"yyyy-MM-dd HH:mm:ss"的要求。如果想要自己定义格式，使用@RequestParam(dateFormat="yyyy-MM-dd")来修改。如下
```java
@ActionMethod()//如果http请求中有参数date则使用yyyy-MM-dd进行格式化为Date对象，并且绑定到该参数上
public void login(@RequestParam(dateFormat="yyyy-MM-dd") Date date);
```
###绑定Map类型
这种类型其实没什么可说的，实际上就是所有的http参数名称和参数值的map对象
###绑定HttpServletRequest,HttpServletResponse,HttpServletSession,ServletContext
这种类型只需要方法入参定义这个接口，无论参数的名称是什么都可以被绑定
###绑定UploadItem类型/透明的文件上传
这种类型其实是为了进行文件上传使用的。透明的文件上传是一个合格的MVC框架所必备的。在Jfire-mvc中这一点变得特别容易。如果在一个http请求中只有一个文件需要上传，则方法入参中包含UploadItem即可。这个对象包含了上传文件的所有信息，如文件名称，上传表单名称，文件输入流。并且提供了一些便捷的方法。如果一个Http请求中包含了多个上传文件，则方法入参中只要包含java.lang.List<UploadItem>即可。每一个上传文件都是一个UploadItem对象实例。
示例
```java
@ActionMethod()//该http请求中包含一个上传文件，会被绑定到方法的UploadItem参数上。
public void login(UploadItem item)
```
##返回对象
一个url请求过来，被所绑定的方法执行后，必然需要返回一些信息给浏览器。框架准备了多种的返回值供使用者选择。
###ViewAndModel/视图模型对象
大多数情况下，一个url请求是需要返回一个页面的。而页面中也必然会包含一些需要进行渲染的参数信息。以前一般使用jsp或者FreeMarker。Jfire-mvc推荐使用Beetl来完成这个工作。不仅性能强大，而且使用简单。如下是一个简单的例子
```java
@Resource
@ActionClass("/")
public class IndexAction
{
    @ActionMethod(resultType=Beetl,contentType="text/html")
    public ViewAndModel index()
    {
        ViewAndModel vm = new ViewAndModel("/admin/index.bt");
        vm.addData("count",1);//vm可以看成是一个Map容器。可以放入任意的值，但是key必须是String类型
        vm.addData("name","eric");
        return vm;//在放入了两个参数后，返回对象。框架根据注解使用Beetl对模板文件进行渲染，并且将渲染后的结果发送给服务器。
    }
}
```
当然如果只是返回一个html页面而没有其他的渲染工作，则可以让返回类型为Html。如下
```java
@Resource
@ActionClass("/")
public class IndexAction
{
    @ActionMethod(resultType=Html,contentType="text/html")
    public ViewAndModel index()
    {
       //返回一个html页面
       return new ViewAndModel("/admin/helloworld.html");
    }
}
```
###返回json对象
返回一个json字符串对于现在的http请求来说是十分常见的。在Jfire-mvc中返回一个json字符串非常简单。只需要将返回结果设定为json，则框架会自动将返回结果的对象实例序列化为json字符串并且在浏览器响应中发出。如下
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
        //框架会将这个返回对象序列化为json字符串并返回响应
        return new User();
    }
}
Jfive-mvc的json序列化功能由开源框架codejson提供支持。其性能为当今众框架之首。
```
###返回一个String对象
有些时候，我们需要返回一些特定格式的字符串。这时候可以直接返回一个String。框架会将其直接响应输出到浏览器中。
###返回二进制数组
在一些如返回验证码或者图片的场合，需要返回二进制的数组来代表数据。如下是示例代码
```java
@ActionMethod(resultType = ResultType.Bytes)
    public byte[] getTicket(HttpServletResponse response, HttpSession session)
    {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String code = VerifyCodeUtils.generateVerifyCode(4);
        session.setAttribute("code", code.toLowerCase());
        //返回验证码的图片数据
        return new byte[1024];
    }
```
##Rest支持
Jfire-mvc提供对Rest的完整支持。支持提取url中的特定信息为方法参数进行绑定。如下是个例子
```java
@ActionMethod(url="login/{username}/{password}")
//如果一个url：login/linbin/123456则会被该方法绑定的url识别并且调用该方法进行响应。通过两个参数也会以名称相同的方法绑定到对应的入参上。
public void login(String username,String password);
```
从例子来看，使用Rest其实很简单。只需要在url定义中将需要抽取的参数用`{}`包围起来，然后在方法入参中使用相同的名称，框架就可以自动完成绑定。
##内嵌的缓存功能
很多时候web页面有这么一种情况，一个url需要访问一个被beetl渲染的页面。但是首次渲染后多半不需要变化。此时可以使用框架默认的缓存功能，这样后续的访问就无需再使用beetl渲染了。如下是例子
```java
@Resource
@ActionClass("admin")
public class AdminAction
{
    //构造方法的第二个参数就代表是否开启缓存功能
    private ViewAndModel vm = new ViewAndModel("/admin/login.html",true);
    
    //访问/admin/login1则每次都会重新渲染模板页面/admin/login.html并返回渲染结果
    @ActionMethod(resultType=Beetl)
    public ViewAndModel login1()
    {
        return new ViewAndModel("/admin/login.html");
    }
    
    //访问/admin/login2则会返回模板页面/admin/login.html的渲染结果。并且首次以后的每次访问都是访问在框架中的缓存。无需重新渲染。
    @ActionMethod(resultType=Beetl)
    public ViewAndModel login2()
    {
        return vm;
    }
    
}
```
这种页面缓存功能对于比较静态的页面是非常有帮助的。经过测试，可以带来50%的io性能提升。不仅beetl渲染可以使用，而且返回html的时候也可以使用。用法相同。
##热重载功能，修改源代码无需重启服务器
在开发中，如果具有热重载功能，就可以达到修改源代码而无需重启tomcat等服务器的功能。作为开发大杀器的热重载也在Jfire-mvc中被提供了。为了使用这个功能，首先需要在web.xml进行如下配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
	http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">
	<display-name>Archetype Created Web Application</display-name>
	<servlet>
	    //固定内容
		<servlet-name>EasyMvcDispathServlet</servlet-name>
		//固定内容
		<servlet-class>link.jfire.mvc.core.EasyMvcDispathServlet</servlet-class>
		<init-param>
			//debug为true代表开启了热重载功能。建议在生产环境下关闭
			<param-name>debug</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
		    //监控的位置，表示框架监控该目录以及子目录下的class文件变化。如果文件发生了变化，在下一次url访问的时候就会重新加载class到虚拟机中
			<param-name>monitorPath</param-name>
			<param-value>E:/workspace/blog/target/classes</param-value>
		</init-param>
		<init-param>
		    //需要重载的class所在的package，可以使用`,`隔开。只有在对应的package下的class才能被重载
			<param-name>reloadPackages</param-name>
			<param-value>link.jfire.blog</param-value>
		</init-param>
	</servlet>
</web-app>
```
配置完成监控目录和重载package后。就可以启动服务器进行开发。一旦遇到需要修改代码的地方，直接进行修改。接着访问url就会发生即刻生效了。由于该热重载使用了自定义的ClassLoader，所以不会对服务器造成任何影响，**重载后原有的session信息，ServletContext信息也不会丢失**