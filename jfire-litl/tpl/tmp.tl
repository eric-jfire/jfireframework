<#
<!--做有个约定，参数要么是从data中获取，要么-->
for(person in persons)
{ 
#>
hello:${person.name}
<#
}
#>
<~include("include/a.tl")~>
这是标题:${title},名字是${person.name}
今天是:${today,"yyyy-MM-dd"}

<#if(has())#>