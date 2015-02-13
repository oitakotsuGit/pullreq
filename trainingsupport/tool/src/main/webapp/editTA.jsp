<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="editTA"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.list_title_ta}" />
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
	
	<form method="post" id="addTAForm" action=index.htm?action=addTA&sectionId=<c:out value="${sectionId}"/> >
	   	<div class="act">
			<label class="topLabel" ><c:out value="${msgs.label_section}" /></label>
			<c:out value="${sectionName}" />&nbsp;&nbsp;&nbsp;
	   	    <label class="topLabel" ><c:out value="${msgs.label_id}"/></label>
	   		<input type="text" id="taId" name="taId" />
	    	<input type="submit" name="submitButton" value="<c:out value="${msgs.add}"/>" />
	    </div>
	</form>
	
<form method="post" id="overviewTAForm" action=index.htm?action=delTA&sectionId=<c:out value="${sectionId}"/> >
	<table id="taList"  class="listHier sectionTable" cellspacing="0" cellpadding="0" border="0" summary="taList" >
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.section_name }"/></th>
				<th scope="col"><c:out value="${msgs.job_title }"/></th>
				<th scope="col"><c:out value="${msgs.personal_number }"/></th>
				<th scope="col"><c:out value="${msgs.user_name }"/></th>
				<th scope="col"><c:out value="${msgs.delete }"/></th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${empty tas }">
				<tr><td style="white-space:nowrap" colspan="5"><c:out value="${msgs.no_tas_available }"/></td></tr>
			</c:if>
			<c:if test="${not empty tas }">
				<c:forEach var="ta" items="${tas }">
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${ta.sectionName }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${ta.jobTitle }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${ta.personalNumber }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${ta.userName }"/>
						</td>
						<td style="white-space:nowrap">
							<input type="checkbox" name="taremove"  id="taremove"  value="<c:out value="${ta.userUid }"/>">
						</td>
					</tr>
				</c:forEach>
				<tr>
					<td colspan="4"></td>
					<td class="act" >					
						<input type="submit" name="submitButton" value="<c:out value="${msgs.delete}"/>" />
				    </td>
				</tr>
			</c:if>
		</tbody>
	</table>
</form>
<br>

<jsp:directive.include file="/templates/footer.jsp"/>