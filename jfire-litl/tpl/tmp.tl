<% for(person in persons){ %>你好:${person.name}<% } %> 
<%if(stringparam == "12"){%>   asdasds
题目是:${title}
<%}%>       
 <%if(iparam == 12){%>                                                  dfgdfgdfgdfg       
 题目1是:${title}
题目2是:${title}
													<%}%>
<%if(iparam == 13){%>
题目4是:${title}
<%}%>
<%else if(iparam == 12){%>
题目4是elseif 的输出
<%}%>
<%else{%>
这里是else输出
<% } %>
<%if(dparam == 56.36){%>题目是:${title}<%}%>
今天是:${today,"yyyy-MM-dd"}
今天是:${today,dateFormat="yyyy-MM-dd"}
<% include("include/a.tl"){} %>