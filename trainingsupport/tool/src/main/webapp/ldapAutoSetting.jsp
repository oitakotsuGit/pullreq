<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="ldapAutoSetting"/>
<jsp:directive.include file="/inc/goMenu.jsp"/>

<div class="instructions">
	<c:out value="${msgs.explanation_ldap_auto_setting}" />
</div>

	<c:if test="${not empty msg }">
		<div class="success">
			<c:out value="${msg}" />
		</div>
	</c:if>
	<c:if test="${not empty err }">
		<div class="validation">
			<c:out value="${err}" />
		</div>
	</c:if>

<br>

<div class="instructions">
	<h3 class="instructions">
		<c:out value="${msgs.ldap_auto_setting}" />
	</h3>
</div>

<form method="post" id="changeLdapSetting" action=index.htm?action=ldapchangeSetting >
	<div style="width:50%;">
	  <div style="padding-top:10px; float:left;">
	    <c:out value="${msgs.ldap_auto_setting}" />
	  </div>
	  <div style="float:left;">
	    <c:choose>
	    <c:when test="${autosetting == 'yes'}">
	      <input type="radio" name="autosetting" value="yes" checked="checked" /><c:out value="${msgs.ldap_auto}" />
	      &nbsp;&nbsp;
	      <input type="radio" name="autosetting" value="no" /><c:out value="${msgs.ldap_not_auto}" />
	    </c:when>
	    <c:when test="${autosetting == 'no'}">
	      <input type="radio" name="autosetting" value="yes"/><c:out value="${msgs.ldap_auto}" />
	      &nbsp;&nbsp;
	      <input type="radio" name="autosetting" value="no"  checked="checked" /><c:out value="${msgs.ldap_not_auto}" />
	    </c:when>
	    </c:choose>
	  </div>
	  <div class="clear:both;"></div>
	  <br /><br />
	  <input type="submit" name="submitButton" value="<c:out value="${msgs.button_label_update}"/>" />
	</div>
</form>

<jsp:directive.include file="/templates/footer.jsp"/>