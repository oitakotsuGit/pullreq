<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="editSection"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.edit_section_page_header}" />
		</h3>
	</div>
	
	<c:if test="${not empty err }">
		<div class="validation">
			<c:out value="${err}" />
		</div>
	</c:if>
	
<br>
<form method="post" id="editSectionForm" action=index.htm?action=editSection >
    <div>
        <input type="hidden" id="sectionId" name="sectionId" value="<c:out value="${sectionId}" />" />
	    <input type="hidden" id="preSectionName" name="preSectionName" value="<c:out value="${preSectionName}" />" />
	    <label class="topLabel" ><c:out value="${msgs.label_section}"/></label>
	    <input type="text" id="sectionName" name="sectionName" value="<c:out value="${preSectionName}" />" maxlength="255"/>
		<div class="act">
			<input type="submit" name="submitButton" value="<c:out value="${msgs.edit}"/>"  onclick="javascript:document.getElementById('updateFlg').value=true"/>
	        <input type="hidden" id="updateFlg" name="updateFlg" value="false"/>
	        <input type="submit" name="cancelButton" value="<c:out value="${msgs.cancel}"/>" />
        </div>
    </div>
</form>

