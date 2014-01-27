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
<%@include file="/include.jsp"%>
<jsp:useBean id="jonnyzzzMetaInstallButtonController" type="java.lang.String" scope="request"/>

<script type="text/javascript">
  $j(function(){
    $j("div.metaRunnersSettings").append($j("<div></div>", {id: "jonnyzzzMetaInstaller"}));

    <c:url var="ajaxUrl" value="${jonnyzzzMetaInstallButtonController}" />
    var ajaxUrl = "<bs:forJs>${ajaxUrl}</bs:forJs>";
    BS.ajaxUpdater($("jonnyzzzMetaInstaller"), ajaxUrl, {method : "GET", evalScripts : true})
  });
</script>
