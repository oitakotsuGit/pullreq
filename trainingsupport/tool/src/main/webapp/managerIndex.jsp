<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="managerIndex"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.title_status_list}" />
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
<table border="0">
<tr><td>
<form method="post" id="sectionSelect" action=index.htm?action=getSectionStatus >
	<div class="act">
	<c:if test="${not empty sections }">
		<label class="topLabel" ><c:out value="${msgs.label_section}"/></label>
		<select id="sectionId" name="sectionId" size="1" onchange="submit(this.form)">
			<c:forEach var="section" items="${sections }">
				<c:choose>
				<c:when test="${section.uuid eq selectedId}">
					<option value="<c:out value='${section.uuid}'/>" selected="selected"><c:out value="${section.title }" />
					</option>
				</c:when>
				<c:otherwise>
					<option value="<c:out value='${section.uuid}'/>"><c:out value="${section.title}" />
				</c:otherwise>
				</c:choose>
			</c:forEach>		
		</select>
	</c:if>
	</div>
<%-- 	<input type="submit" name="submitButton" value="<c:out value="${msgs.label_submit_select}"/>" />--%>
</form>
</td><td>
<c:out value="${rosterDate }" />
</td></tr>
</table>
<c:choose>
<c:when test="${not empty targetRoster }" >
	<table id="rosterList"  class="tablesorter lines sectionTable" cellspacing="0" cellpadding="0"  summary="rosterList" >
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.job_title }"/></th>
				<th scope="col"><c:out value="${msgs.personal_number }"/></th>
				<th scope="col"><c:out value="${msgs.user_name }"/></th>
				<th scope="col"><c:out value="${msgs.training_done_status }"/></th>
				<th scope="col"><c:out value="${msgs.enrollment_status }"/></th>
				<th scope="col"><c:out value="${msgs.training_done_datetime }"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="roster" items="${targetRoster }">
				<tr>
					<td style="white-space:nowrap">
						<c:out value="${roster.jobTitle }"/>
					</td>
					<td style="white-space:nowrap">
						<c:out value="${roster.personalNumber }"/>
						
					</td>
					<td style="white-space:nowrap">
						<a href="statusEdit.htm?action=open&userEid=<c:out value="${roster.userEid}"/>">
							<c:out value="${roster.userName }"/>
						</a>
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
						<c:out value="${roster.updateDate }"/>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:when>
<c:otherwise>
	<c:out value="${msgs.msgs_no_user}"/>
</c:otherwise>
</c:choose>	
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
jQuery( function() {
	jQuery( '#rosterList' ).tablesorter({
		headers: {
		}
	});
	});
// -->
</script>
