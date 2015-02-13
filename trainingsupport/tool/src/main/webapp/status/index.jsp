<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="statusUpdate"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.menu_title_status_update}" />
		</h3>
		<c:out value="${msgs.msgs_statusupdate}" /></br>
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

	<form method="post" id="statusUpdateForm" action=statusUpdate.htm?action=update >
	   	<div class="act">
	    	<input type="submit" name="submitButton" value="<c:out value="${msgs.msgs_do_statusupdate}"/>" />
			<c:if test="${not empty statusUpdateDate }">
					<c:out value="${statusUpdateDate}" />
			</c:if>
	    </div>
	</form>
<br>

<jsp:directive.include file="/templates/footer.jsp"/>
