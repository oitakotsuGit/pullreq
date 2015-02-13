<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="createRoster"/>
<jsp:directive.include file="/inc/goMenu.jsp"/>

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
    <div>
	    <table id="createRosterSelectTable">
	    	<tbody>
	    		<tr class="paddedSectionRow" style="white-space:nowrap">
	    			<td><label class="topLabel" ><c:out value="${msgs.label_section}"/></label></td>
	    			<td><c:out value="${sessionScope.sectionName}"/></td>
	    		</tr>
	    		<c:if test="${not empty patterns }">
	<form method="post" id="patternSelect" action=index.htm?action=createRoster&sectionId=<c:out value='${sectionId}'/> >
	    		<tr class="paddedSectionRow">
			  <td colspan="2">
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
	    		  </td>
          		</tr>
      	</form>
			</c:if>
	<form method="post" id="createRosterForm" action=index.htm?action=createRoster&createFlg=true&selectedPatternId=<c:out value='${selectedPatternId}'/> >
	    		<tr class="paddedSectionRow">
	    		  <td colspan="2">
			    <label class="topLabel" style="white-space:nowrap"><c:out value="${msgs.label_job_title}"/></label>
			    <input type="checkbox" id="allCheck" /><c:out value="${msgs.jobtitle_all}"/>
				<div id="check"><c:forEach var="jobTitle" items="${sessionScope.jobTitles }">
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
			<input type="submit" name="submitButton" value="<c:out value="${msgs.create_list}"/>" />
        </div>
        </c:if>
</form>
    </div>
<c:if test="${not empty createFlg }">
	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.title_roster_list}" />
		</h3>
	</div>
	<form method="post" id="createRosterForm" action=index.htm?action=updateRoster&from=createRoster >
		<table id="rosterList" class="listHier sectionTable" cellspacing="0" cellpadding="0"  summary="rosterList" >
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.job_title }"/></th>
					<th scope="col"><c:out value="${msgs.personal_number }"/></th>
					<th scope="col"><c:out value="${msgs.user_name }"/></th>
					<th scope="col"><c:out value="${msgs.training_done_status }"/></th>
					<th scope="col"><c:out value="${msgs.training_done_datetime }"/></th>
					<th scope="col"><c:out value="${msgs.delete }"/></th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${empty sessionScope.targetRoster }">
					<tr><td style="white-space:nowrap" colspan="6"><c:out value="${msgs.no_target_roster_available }"/></td></tr>
				</c:if>
				<c:if test="${not empty sessionScope.targetRoster }">
					<c:forEach var="roster" items="${sessionScope.targetRoster }">
						<c:if test="${roster.availableFlg == false }">
							<tr bgcolor="gray">
						</c:if>
						<c:if test="${roster.availableFlg == true }">
							<tr>
						</c:if>
							<td style="white-space:nowrap">
								<c:out value="${roster.jobTitle }"/>
							</td>
							<td style="white-space:nowrap">
								<c:out value="${roster.personalNumber }"/>
							</td>
							<td style="white-space:nowrap">
								<c:out value="${roster.userName }"/>
							</td>
							<c:if test="${roster.availableFlg == true }">
								<td style="white-space:nowrap">
									<c:if test="${roster.status == 0 }">
										<c:out value="${msgs.status_reject }"/>
									</c:if>
									<c:if test="${roster.status == 1 }">
										<c:out value="${msgs.status_accept }"/>
									</c:if>
								</td>
							</c:if>
							<c:if test="${roster.availableFlg == false }">
								<td style="white-space:nowrap">
									<c:if test="${roster.role == msgs.training_role_admin }">
										<c:out value="${msgs.add_failure_already_admin }"/><c:out value="${msgs.unregistrable_roster }"/>
									</c:if>
									<c:if test="${roster.role == msgs.training_role_instructor }">
										<c:out value="${msgs.add_failure_already_instructor }"/><c:out value="${msgs.unregistrable_roster }"/>
									</c:if>
									<c:if test="${roster.role == msgs.training_role_ta }">
										<c:out value="${msgs.add_failure_already_ta }"/><c:out value="${msgs.unregistrable_roster }"/>
									</c:if>
									<c:if test="${roster.role == msgs.training_role_student }">
										<c:out value="${msgs.target_roster_add_failure_already_another_roster }"/><c:out value="${msgs.unregistrable_roster }"/>
									</c:if>
								</td>
							</c:if>
							<td style="white-space:nowrap">
								<c:out value="${roster.updateDate }"/>
							</td>
							<td style="white-space:nowrap">
								<a href=index.htm?action=delRoster&from=createRoster&delUserId=<c:out value="${roster.userEid }"/> >
									<c:out value="${msgs.delete }"/>
								</a>
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
</c:if>
<br>

<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
$(function(){
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
