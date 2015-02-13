<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="deleteSections"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.del_section_page_header}" />
		</h3>
	</div>

<form method="post" id="deleteSectionForm" action=index.htm?action=delSection >
	<div class="alertMessage">
		<c:out value="${msgs.overview_delete_section_confirmation_pre}"/>
		<ul id="editSectionForm:deleteSectionsTable">
			<c:forEach var="section" items="${sections }">	
				<li><c:out value="${section.section.title}"/></li>
				<input type="hidden" id="sectionremove" name="sectionremove" value="<c:out value="${section.section.uuid}" />" />
			</c:forEach>
		</ul>
		<div>
            <c:out value="${msgs.overview_delete_section_confirmation_post}"/>
        </div>
        <div class="act deleteButtons">
	        <input type="submit" name = "submitButton" value="${msgs.delete}" onclick="javascript:document.getElementById('delFlg').value=true" class="active" />
	        <input type="hidden" id="delFlg" name="delFlg" value="false"/>
	        <input type="submit" name="cancelButton" value="<c:out value="${msgs.cancel}"/>" />
        </div>
	</div>
</form>

