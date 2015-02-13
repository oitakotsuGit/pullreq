<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="editRoster"/>
<jsp:directive.include file="/inc/goMenu.jsp"/>
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

<!-- Title Selection -->
<form method="post" id="createRoster" name="createRoster" action=index.htm?action=createRosterShelfRegistration>

<table id="sectionList"  class="tablesorter lines sectionTable"  cellspacing="0" cellpadding="0"  summary="section" >
	<thead>
		<tr>
			<th scope="col"><c:out value="${msgs.section_name }" /></th>
	    	<th scope="col"><c:out value="${msgs.jobtitle_pattern }" /></th>
	    	<th scope="col" colspan="2"><c:out value="${msgs.job_title }"/></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="jobTitlesBySection" items="${sessionScope.jobTitlesBySection}" varStatus="loop">
			<tr>
			<td><c:out value="${jobTitlesBySection.title }"/></td>
			<td>
				<c:choose>
					<c:when test="${jobTitlesBySection.selectedPatternId ne null }">
						<select id="selectedPatternId<c:out value="${loop.index }" />" name="selectedPatternId" size="1"  onchange="selectChange(${loop.index},'${jobTitlesBySection.sectionId }')">
						<c:forEach var="pattern" items="${patterns }">
						<c:choose>
							<c:when test="${jobTitlesBySection.selectedPatternId ne null && pattern.id eq jobTitlesBySection.selectedPatternId}">
								<option value=<c:out value='${pattern.id}'/> selected="selected"><c:out value="${pattern.displayName}" />
								</option>
							</c:when>
							<c:when test="${jobTitlesBySection.selectedPatternId ne null && pattern.id ne jobTitlesBySection.selectedPatternId}">
								<option value=<c:out value='${pattern.id}'/>><c:out value="${pattern.displayName}" />
								</option>
							</c:when>
						</c:choose>
						</c:forEach>
						</select>
					</c:when>
					<c:when test="${jobTitlesBySection.selectedPatternId eq null }">
						<select id="selectedPatternId<c:out value="${loop.index }" />" name="selectedPatternId" size="1"  onchange="selectChange(${loop.index},'${jobTitlesBySection.sectionId }')">
						<c:forEach var="pattern" items="${patterns }">
							<option value=<c:out value='${pattern.id}'/>><c:out value="${pattern.displayName}" /></option>
						</c:forEach>
						</select>
					</c:when>
					</c:choose>
			</td>

			<td colspan="5">
				<c:choose>
					<c:when test="${jobTitlesBySection.jobList ne null }">
					<div style="float:left;">
						<input type="checkbox" id="allCheck" class="all" onclick="allcheck('${jobTitlesBySection.sectionId }')" /><c:out value="${msgs.jobtitle_all}"/>
					</div>
					<div id="check" class="allCheck<c:out value="${loop.index}" />" style="float:left;">
						<c:forEach var="jobTitle" items="${jobTitlesBySection.jobList}">
				  			<c:if test="${jobTitle.checkFlg}">
			  	    			<input type="checkbox" name="<c:out value="${jobTitlesBySection.sectionId }" />"  value="<c:out value="${jobTitle.trainingJobTitle.id }"/>" checked="checked">
			  	    			<label for="selectJobTitle"><c:out value="${jobTitle.jobTitle}"/></label>
				  			</c:if>
				  			<c:if test="${!jobTitle.checkFlg}">
				    			<input type="checkbox" name="<c:out value="${jobTitlesBySection.sectionId }" />"  value="<c:out value="${jobTitle.trainingJobTitle.id }"/>">
				    			<label for="selectJobTitle"><c:out value="${jobTitle.jobTitle}"/></label>
				  			</c:if>
						</c:forEach>
					</div>
					</c:when>
					<c:when test="${jobTitlesBySection.jobList eq null }">
					</c:when>
				</c:choose>
			</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

	<div class="act">
		<input type="button" id="submitButton" name="submitButton" value="<c:out value="${msgs.create_roster}"/>" onclick = "createRoster()" />
	</div>
</form>
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!--

$(function(){
	$( '#rosterList' ).tablesorter({
		headers: {
			5: { sorter: false}
		}
	});

	$('#submitButton').click(function(){
		document.createRoster.submit();
	});

});
function selectChange(obj1,obj2){

	var val = document.getElementById('selectedPatternId'+obj1);
	var valval = val.value;
	var frm = document.createElement('form');
	var ipt1 = document.createElement("input");
	var ipt2 = document.createElement("input");

	frm.action = "index.htm?action=rosterShelfRegistration";
	frm.method = "post";

	ipt1.name = "selectedPatternId";
	ipt1.value = valval;

	ipt2.name = "selectedsectionId";
	ipt2.value = obj2;

	frm.appendChild(ipt1);
	frm.appendChild(ipt2);

	document.body.appendChild(frm);

	frm.submit();
}

function allcheck(obj) {
	var chkbox = document.getElementsByName(obj);
	var count = chkbox.length;
	for (var i=0; i < count; i++) {
		document.getElementsByName(obj)[i].checked = check;
	}
}


// -->
</script>