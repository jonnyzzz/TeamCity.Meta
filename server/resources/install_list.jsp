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
<jsp:useBean id="model" type="com.jonnyzzz.teamcity.plugins.meta.web.MetaRunners" scope="request"/>
<jsp:useBean id="projectId" type="java.lang.String" scope="request"/>
<jsp:useBean id="installPath" type="java.lang.String" scope="request"/>

<c:url var="installUrl" value="${installPath}"/>
<div class="jonnyzzzMetaModel">
  <table class="parametersTable" cellpadding="0" cellspacing="0">
    <thead>
      <tr>
        <th>Runner ID</th>
        <th>Name</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="it" items="${model.runners}">
      <tr data-runner-id="${it.id}">
        <td>
          <c:out value="${it.id}"/>
        </td>
        <td>
          <c:out value="${it.name}"/>
          <div class="smallNote">
            <c:out value="${it.description}"/>
          </div>
        </td>
        <td>
          <a href="#" class="install">Install</a>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>

<script type="text/javascript">
  $j(function(){
    $j("div.jonnyzzzMetaModel").on("click", "a.install", function() {
      var runnerId = $j(this).parents("tr").data("runner-id");

      $j(this).replaceWith($j("<div>" + BS.loadingIcon + "Installing</div>", {style: "width:2em"}));

      var ajaxUrl = "<bs:forJs>${installUrl}</bs:forJs>";
      BS.ajaxRequest(ajaxUrl, {
        method: "POST",
        parameters : {
          projectId : "<bs:forJs>${projectId}</bs:forJs>",
          meta : runnerId
        },
        onComplete : function() {
          BS.reload();
        }
      });

      return false;
    });
  });
</script>