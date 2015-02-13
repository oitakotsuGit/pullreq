<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="editRoster"/>
<jsp:directive.include file="/inc/rosterMenu.jsp"/>

	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.title_create_target_roster_list}" />
		</h3>
		<c:out value="${msgs.explanation_update_roster}" />
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
	<c:if test="${createSectionEnabled}">
<form method="post" id="editRosterForm" action=index.htm?action=editRoster&addFlg=true >
    <div>
	    <table id="editRosterSelectTable">
	    	<tbody>
	    		<tr class="paddedSectionRow" style="white-space:nowrap">
	    			<td><label class="topLabel" ><c:out value="${msgs.label_section}"/></label></td>
	    			<td><c:out value="${sessionScope.sectionName}"/></td>
	    		</tr>
	    		<tr class="paddedSectionRow">
	    			<td><label class="topLabel" ><c:out value="${msgs.label_id}"/></label></td>					
					<td><input type="text" id="userId" name="userId" /></td>
	    		</tr>
	    	</tbody>
	    </table>
		<div class="act">
			<input type="submit" name="submitButton" value="<c:out value="${msgs.add_list}"/>" />
        </div>
    </div>
</form>
	</c:if>

<!-- Title Selection -->

<br>
    <div>
	    <table id="updateSelectTable">
	    	<tbody>
	    		<c:if test="${not empty patterns }">
	    		<tr class="paddedSectionRow"><td colspan="2">
	<form method="post" id="patternSelect" action=index.htm?action=editRoster&sectionId=<c:out value='${sectionId}'/> >
		<label class="topLabel" ><c:out value="${msgs.label_jobtitle_pattern}"/></label>
		<select id="selectedPatternId" name="selectedPatternId" size="1" onchange="submit(this.form)">
			<c:forEach var="pattern" items="${patterns }">
				<c:choose>
				<c:when test="${pattern.id eq selectedPatternId}">
					<option value=<c:out value='${pattern.id}'/> selected="selected"><c:out value="${pattern.displayName}" />
					</option>
				</c:when>
				<c:otherwise>
					<option value=<c:out value='${pattern.id}'/>><c:out value="${pattern.displayName}" />
				</c:otherwise>
				</c:choose>
			</c:forEach>		
		</select>
	</form></td>
				</tr></c:if>
<form method="post" id="patternSelect" action=index.htm?action=updateEnrollmentStatus&sectionId=<c:out value='${sectionId}'/>&selectedPatternId=<c:out value='${selectedPatternId}'/> >
	    		<tr class="paddedSectionRow">
	    		  <td colspan="2">
	    		      <label class="topLabel" ><c:out value="${msgs.label_job_title}"/></label>
			      <input type="checkbox" id="allCheck" /><c:out value="${msgs.jobtitle_all}"/>
				<div id="check">
				  <c:forEach var="jobTitle" items="${sessionScope.jobTitles }">
				  <c:if test="${jobTitle.checkFlg}">
			  	    <input type="checkbox" name="selectJobTitle"  id="selectJobTitle"  value="<c:out value="${jobTitle.jobTitle }"/>" checked="checked">
				    <label for="selectJobTitle"><c:out value="${jobTitle.jobTitle}"/></label>
				  </c:if>
				  <c:if test="${!jobTitle.checkFlg}">
				    <input type="checkbox" name="selectJobTitle"  id="selectJobTitle"  value="<c:out value="${jobTitle.jobTitle }"/>">
				    <label for="selectJobTitle"><c:out value="${jobTitle.jobTitle}"/></label>
				  </c:if>
				  </c:forEach></div>
			  </td>
	    		</tr>
	    	</tbody>
	    </table>
			    <c:if test="${not empty sessionScope.jobTitles }">
			    	  <div class="act">
				  <input type="submit" name="submitButton" value="<c:out value="${msgs.jobtitle_update_requirements_as_required}"/>" />				  <input type="submit" name="submitButton" value="<c:out value="${msgs.jobtitle_update_requirements_as_optional}"/>" />
				  </div>
		    	    </c:if>
</form>

    </div>

<!-- -->

<div class="instructions">
	<h3 class="instructions">
		<c:out value="${msgs.title_roster_list}" />
	</h3>
</div>
<form method="post" id="updateRoster" action=index.htm?action=updateRoster&from=editRoster >
	<table id="rosterList"  class="tablesorter lines sectionTable" cellspacing="0" cellpadding="0"  summary="rosterList" >
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.job_title }"/></th>
				<th scope="col"><c:out value="${msgs.personal_number }"/></th>
				<th scope="col"><c:out value="${msgs.user_name }"/></th>
				<th scope="col"><c:out value="${msgs.training_done_status }"/></th>
				<th scope="col"><c:out value="${msgs.training_done_datetime }"/></th>
				<th scope="col"><c:out value="${msgs.enrollment_status }"/></th>
				<th scope="col"><c:out value="${msgs.list_type_update_date }"/></th>
				<th scope="col"><c:out value="${msgs.delete }"/></th>

			</tr>
		</thead>
		<tbody>
			<c:if test="${empty sessionScope.targetRoster }">
				<tr><td style="white-space:nowrap" colspan="8"><c:out value="${msgs.no_target_roster_available }"/></td></tr>
			</c:if>
			<c:if test="${not empty sessionScope.targetRoster }">
				<c:forEach var="roster" items="${sessionScope.targetRoster }">
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
						 <div id="check">
						  <select name="enrollmentStatus<c:out value="${roster.userUid}"/>" id="enrollmentStatus<c:out value="${roster.userUid}"/>">
						      <c:choose>
							<c:when test="${roster.enrollmentStatus == 'required' }">
							  <option selected value='required'><c:out value="${msgs.required}"/></option>
							  <option value='optional'><c:out value="${msgs.not_required}"/></option>
							</c:when>
							<c:otherwise>	
							  <option value='required'><c:out value="${msgs.required}"/></option>
							  <option selected value='optional'><c:out value="${msgs.not_required}"/></option>
							</c:otherwise>	
						      </c:choose>
					          </select>
						 </div>
						</td>
						<td style="white-space:nowrap">
							<fmt:formatDate value="${roster.listTypeUpdateDate}" pattern="yyyy/MM/dd HH:mm:ss"/>
						</td>
						<td style="white-space:nowrap">
						<c:choose>
						  <c:when test="${createSectionEnabled}">
							<a href=index.htm?action=delRoster&from=editRoster&delFlg=true&delUserId=<c:out value="${roster.userEid }"/> >
								<c:out value="${msgs.delete }"/>
							</a>
						  </c:when>
						  <c:otherwise>
								<c:out value="${msgs.no_right_to_delete }"/>
						  </c:otherwise>
						</c:choose>
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
			5: { sorter: false}
		}
	});
	$('#allCheck').click(function(){
		if(this.checked){
			$('#check input').attr('checked','checked');
		}else{
			$('#check input').removeAttr('checked');
		}
	});
});
// -->
</script>