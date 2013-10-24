<#--
  #%L
  License Maven Plugin
  %%
  Copyright (C) 2012 Codehaus, Tony Chemit
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Lesser Public License for more details.

  You should have received a copy of the GNU General Lesser Public
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/lgpl-3.0.html>.
  #L%
  -->
<#-- To render the third-party file.
 Available context :

 - dependencyMap a collection of Map.Entry with
   key are dependencies (as a MavenProject) (from the maven project)
   values are licenses of each dependency (array of string)

 - licenseMap a collection of Map.Entry with
   key are licenses of each dependency (array of string)
   values are all dependencies using this license
-->
<#function licenseFormat licenses>
    <#assign result = "\t<licenses>\n"/>

    <#list licenses as license>
        <#assign result = result + "\t\t\t<license>" +license + "</license>\n"/>
    </#list>
    <#return result + "\t\t</licenses>\n">
</#function>
<#function artifactFormat p>
    <#assign group = "<groupId>"+ p.groupId +"</groupId>\n"/>
    <#assign artifact = "<artifactId>"+ p.artifactId +"</artifactId>\n"/>
    <#assign version = "<version>"+ p.version +"</version>\n"/>
    <#assign url = "<url>"+ (p.url!"") +"</url>\n"/>
    <#assign name = "<name>"+ p.name +"</name>"/>
    <#assign tab = "\t\t"/>

    <#return tab + group + tab + artifact + tab + version + tab + url + tab + name>
</#function>

<dependencies>
<#if dependencyMap?size &gt; 0>
    <#list dependencyMap as e>
        <#assign project = e.getKey()/>
        <#assign licenses = e.getValue()/>
        <dependency>
        ${licenseFormat(licenses)}
        ${artifactFormat(project)}
        </dependency>
    </#list>
</#if>
</dependencies>