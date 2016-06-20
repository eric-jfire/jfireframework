<#
for(int i=0;i<persons.size();i++)
{ 
#>
hello:${persons[i].name}
<#
}
#>
<~include("include/a.tl")~>
这是标题:${title}
今天是:${today,"yyyy-MM-dd"}
