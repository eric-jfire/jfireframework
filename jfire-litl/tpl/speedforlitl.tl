<% for(person in persons){ %>
你好:${person.name}
<% } %>
<%if(stringparam == "12"){%>
题目是:${title}
<%}%>
<%if(iparam == 12){%>
题目是:${title}
<%}%>
<% if(dparam == 56.36){%>
题目是:${title}
<%}%>
今天是:${today,"yyyy-MM-dd"}
<%include("include/a.tl"){}%>