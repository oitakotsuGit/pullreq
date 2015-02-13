<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="overview"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.list_title_section}" />
		</h3>
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
<c:if test="${createSectionEnabled}">
	<table border="0" width="100%"><tr>
	<td><form method="post" id="addSectionForm" action=index.htm?action=addSection >
	   	<div class="act">
	   	    <label class="topLabel" ><c:out value="${msgs.label_section}"/></label>
	   		<input type="text" id="sectionName" name="sectionName" maxlength="255"/>
	    	<input type="submit" name="submitButton" value="<c:out value="${msgs.add}"/>" />
	    </div>
	</form></td>

	<td align="right"><form method="post" id="csvSectionForm" action=section.htm?action=sectionCsv>
		<div align="right">
			<input type="hidden" id="submitKind" name="submitKind" value="submit" />
			<input type="submit" name="unRegisterdDownload" value="<c:out value="${msgs.section_unregistered_csv_download }"/>"
				onclick="setValue('submitKind','unRegisteredCsvDownload')" />
			<input type="submit" name="csvDownload" value="<c:out value="${msgs.section_csv_download }"/>"
				onclick="setValue('submitKind','csvDownload')" />
			<input type="submit" name="csvUpload" value="<c:out value="${msgs.section_csv_upload }"/>"
				onclick="setValue('submitKind','csvUpload')"/>
		</div>
	</form></td>
	</tr>
	<tr>
	<td colspan="2" align="right"><div class="strongRed"><c:out value="${msgs.msgs_section_download_1 }"/></div>
	</td></tr>
	</table>
	<br/>
</c:if>
<form method="post" id="rosterShelfRegistration" action=index.htm?action=rosterShelfRegistration>
<c:if test="${rosterShelfRegistrationEnabled }">
<input type="submit" name="rosterShelfRegistration" value="${msgs.roster_shelf_registration }" />
</c:if>
</form>
<form method="post" id="overviewForm" action=index.htm?action=delSection >
	<table id="sectionList"  class="tablesorter lines sectionTable"  cellspacing="0" cellpadding="0"  summary="section" >
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.section_name }"/></th>
				<th scope="col"><c:out value="${msgs.training_role_ta }"/></th>
				<th scope="col"><c:out value="${msgs.current_size }"/></th>
				<th scope="col"><c:out value="${msgs.delete }"/></th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${empty sections }">
				<tr><td style="white-space:nowrap" colspan="4"><c:out value="${msgs.no_sections_available }"/></td></tr>
			</c:if>
			<c:if test="${not empty sections }">
				<c:forEach var="section" items="${sections }">
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${section.section.title }"/>
							<div class="itemAction">
								<c:if test="${createSectionEnabled}">
									<a href=index.htm?action=editSection&sectionId=<c:out value="${section.section.uuid }"/> >
										<c:out value="${msgs.edit }"/>
									</a>
									<c:out value=" ${msgs.sep_char } "/>
								</c:if>
								<c:if test="${editTaEnabled}">
									<a href=index.htm?action=editTA&sectionId=<c:out value="${section.section.uuid }"/> >
										<c:out value="${msgs.edit_ta }"/>
									</a>
									<c:out value=" ${msgs.sep_char } "/>
								</c:if>
								<c:if test="${createSectionEnabled}">
								      <a href=index.htm?action=createRoster&sectionId=<c:out value="${section.section.uuid }"/> >
								      	<c:out value="${msgs.create_roster }"/>
								      </a>
								      <c:out value=" ${msgs.sep_char } "/>
								</c:if>
								<c:if test="${editSectionEnabled}">
								      <a href=index.htm?action=editRoster&sectionId=<c:out value="${section.section.uuid }"/> >
									<c:out value="${msgs.edit_roster }"/>
								      </a>
								</c:if>
							</div>
						</td>
						<td style="white-space:nowrap">
							<c:forEach var="instructorName" items="${section.instructorNames }">
								<c:out value="${instructorName }"/><br>
							</c:forEach>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${section.totalEnrollments }"/>
						</td>
						<c:if test="${editSectionEnabled}">
							<td style="white-space:nowrap">
							  <c:if test="${createSectionEnabled}">
								<input type="checkbox" name="sectionremove"  id="sectionremove"  value="<c:out value="${section.section.uuid }"/>">
							  </c:if>
							</td>
						</c:if>
					</tr>
				</c:forEach>
			</c:if>
		</tbody>
	</table>
	<c:if test="${editSectionEnabled && not empty sections}">
	<table id="delete"  class="listHier nolines"  cellspacing="0" cellpadding="0"  summary="section" >
		<tr>
			<td class="act" align="right">
			  <c:if test="${createSectionEnabled}">
				<input type="submit" name="submitButton" value="<c:out value="${msgs.delete}"/>" />
			  </c:if>
		    </td>
		</tr>
	</table>
	</c:if>
</form>
<br>

<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!--
jQuery( function() {
	jQuery( '#sectionList' ).dataTable({
		"aoColumnDefs": [
			{ "bSortable": false, "aTargets":[3]}
		],
		"bSort": false,
		"bLengthChange": false,
		"iDisplayLength": 15,
		"bStateSave": false,
	});
});
function setValue(arg1,arg2){
	document.getElementById(arg1).value=arg2;
}
// -->
</script>
