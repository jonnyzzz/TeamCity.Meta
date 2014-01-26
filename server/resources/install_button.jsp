<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright 2000-2014 Eugene Petrenko
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@include file="/include-internal.jsp"%>
<jsp:useBean id="metaListPath" type="java.lang.String" scope="request"/>

<div class="jonnyzzzMetaInstaller" style="padding-top: 2em;">
  <h2 class="noBorder">Install Meta-Runners</h2>

  <c:set var="repo" value="https://github.com/JetBrains/meta-runner-power-pack"/>
  <div class="grayNote">
    Select Meta-Runner to install from <a href="${repo}" target="_blank">${repo}</a>
  </div>

  <div id="jonnyzzzMetaInstallerContainer">
    <div style="width: 5em"><forms:progressRing /></div>
  </div>

  <script type="text/javascript">
    $j(function(){
      <c:url var="metaListUrl" value="${metaListPath}"/>
      var ajaxUrl = "<bs:forJs>${metaListUrl}</bs:forJs>";
      BS.ajaxUpdater($("jonnyzzzMetaInstallerContainer"), ajaxUrl, {method:"get", evalScripts : true});
    });
  </script>
</div>
