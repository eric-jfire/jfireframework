<# for(person in persons){ #>
<# if(person){#>
你好:${person.name}
<# }#>
<# } #>
题目是:${title}
<# if(today){#>
今天是:${today,"yyyy-MM-dd"}
<#}#>
<~include("include/a.tl")~>