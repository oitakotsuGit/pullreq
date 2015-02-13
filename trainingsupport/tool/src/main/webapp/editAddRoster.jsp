<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="editAddRoster"/>
<jsp:directive.include file="/inc/rosterMenu.jsp"/>

<div class="instructions">
	<c:out value="${msgs.explanation_add_user_list}" />
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
<div>
	<table id="editRosterSelectTable">
		<tbody>
			<tr class="paddedSectionRow" style="white-space:nowrap">
				<td><label class="topLabel" ><c:out value="${msgs.label_section}"/></label></td>
				<td><c:out value="${sessionScope.sectionName}"/></td>
			</tr>
		</tbody>
	</table>
</div>
<div class="instructions">
	<h3 class="instructions">
		<c:out value="${msgs.menu_add_user_list}" />
	</h3>
</div>

<!-- Title Selection -->
<form method="post" id="updateRoster" action=index.htm?action=editAddedUserList&sectionId=<c:out value='${sectionId}'/>&updateFlg=true>
	<table id="rosterList"  class="tablesorter lines sectionTable" cellspacing="0" cellpadding="0"  summary="addList" >
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.job_title }"/></th>
				<th scope="col"><c:out value="${msgs.personal_number }"/></th>
				<th scope="col"><c:out value="${msgs.user_name }"/></th>
				<th scope="col"><c:out value="${msgs.training_done_status }"/></th>
				<th scope="col"><c:out value="${msgs.training_done_datetime }"/></th>
				<th scope="col"><c:out value="${msgs.enrollment_status }"/></th>
				<th scope="col"><c:out value="${msgs.view_reason }"/></th>
				<th scope="col"><c:out value="${msgs.list_type_update_date }"/></th>
				<th><input type="checkbox" id="allCheck"/></th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${empty viewaddRosterList }">
				<tr><td style="white-space:nowrap" colspan="9"><c:out value="${msgs.no_target_roster_available }"/></td></tr>
			</c:if>
			<c:if test="${not empty viewaddRosterList }">
				<c:forEach var="roster" items="${viewaddRosterList }">
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${roster.jobTitle }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${roster.personalNumber }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${roster.userName }"/>
						</td>
						<td style="white-space:nowrap">
							<c:if test="${roster.status == 0 }">
								<c:out value="${msgs.status_reject }"/>
							</c:if>
							<c:if test="${roster.status == 1 }">
								<c:out value="${msgs.status_accept }"/>
							</c:if>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${roster.updateDate }"/>
						</td>
						<td style="white-space:nowrap">
						 <c:choose>
						  <c:when test="${roster.enrollmentStatus == 'required' }">
						    <c:out value="${msgs.required}"/>
						  </c:when>
						  <c:otherwise>
						    <c:out value="${msgs.not_required}"/>
						  </c:otherwise>
					    </c:choose>
						</td>
						<td style="white-space:nowrap">
						</td>
						<td style="white-space:nowrap">
							<fmt:formatDate value="${roster.listTypeUpdateDate}" pattern="yyyy/MM/dd HH:mm:ss"/>
						</td>
						<td style="white-space:nowrap">
							<input type="checkbox" name="addUserIds" value="<c:out value="${roster.userEid }" />">
						</td>
					</tr>
				</c:forEach>
				</c:if>
		</tbody>
	</table>
	<div class="act">
		<input type="submit" name="submitButton" value="<c:out value="${msgs.update_roster}"/>" />
    </div>
</form>
<br>

<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!--
$(function(){
	$( '#rosterList' ).tablesorter({
		headers: {
			8: { sorter: false}
		}
	});
	$('#allCheck').click(function(){
		$('table tbody input[type=checkbox]').prop('checked', $(this).prop('checked'));
	});
});
// -->
</script>
