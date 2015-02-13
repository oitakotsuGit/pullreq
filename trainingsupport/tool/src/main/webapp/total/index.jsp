<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="totalization"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

	<div class="instructions">
	</div>

<form method="post" id="sectionSelect" action=totalize.htm?action=totalize >
	<%-- Section List --%>
	<div class="act">
	<c:if test="${not empty sections }">
		<label class="topLabel" ><c:out value="${msgs.label_section}"/></label>
		<select id="sectionId" name="sectionId" size="1" onchange="hiddenArea('listDiv')">
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
		<label class="topLabel" ><c:out value="${msgs.label_jobtitle_pattern}"/></label>
		<select id="selectedPatternId" name="selectedPatternId" size="1">
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
		
		<br/><br/>
		<label class="topLabel" ><c:out value="${msgs.label_term }" /></label>
		<input type="text" id="startDate" name="startDate" value="<c:out value="${startDate}" />" maxlength="12" size="16"/>
		&#xFF5E;
		<input type="text" id="endDate" name="endDate" value="<c:out value="${endDate}" />" maxlength="12" size="16"/>
		<input type="hidden" id="submitKind" name="submitKind" value="submit" />
		<input type="submit" id="submitButton" name="submitButton" value="<c:out value="${msgs.label_button_totalization}"/>" 
		    onclick="setValue('submitKind','submit')"/>
		<input type="submit" id="csvOutput" name="csvOutput" value="<c:out value="${msgs.label_button_csvoutput}"/>" 
			onclick="setValue('submitKind','csvoutput')"/>

	</c:if>
	<c:if test="${empty sections }">
		<c:out value="${msgs.no_sections_available }"/>
	</c:if>
	</div>
</form>
	<%-- Section List end--%>
	
	<%-- Result List --%>
<form method="post" id="sectionSelect" action=totalize.htm?action=csvoutput >
<div id="listDiv">
	<c:if test="${not empty msg }">
		<c:out value="${msg}" />
		<input type="hidden" id="sectionId0" name="sectionId" value="<c:out value="${selectedId }" />" />
		<input type="hidden" id="startDate0" name="startDate" value="<c:out value="${startDate}" />" />
		<input type="hidden" id="endDate0" name="endDate" value="<c:out value="${endDate}" />"/>
		<c:forEach var="totalresult" items="${totalresultlist}">
		<div style="width:80%;"><br/>
		<table border="0" width="100%">
		<tr><td><font style="font-weight: bold;"><c:out value="${ totalresult.sectionName }"/></font>
		</td>
		<td align="right"><font style="font-weight: bold;"><c:out value="${totalresult.statusDisp}" /></font></td>
		</tr>
		</table>
		
		<table id="totalResult"  class="tablesorter lines sectionTable" cellspacing="0" cellpadding="0"  summary="totalResult" >
		<thead>
			<tr>
				<th scope="col" style="width:120px;"><c:out value="${msgs.job_title }"/></th>
				<th scope="col" style="width:40px;"><c:out value="${msgs.label_result}"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="total" items="${totalresult.totalList }">
				<tr>
					<td style="white-space:nowrap">
						<c:out value="${total.jobTitle }"/>
					</td>
					<td style="white-space:nowrap" align="right">
						<c:out value="${total.statusDisp}"/>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
		</div>
		<br/>
		</c:forEach>
	</c:if>
	<%-- Result List end--%>
</div>
</form>
<br/>
<div style="height:300px"></div>
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
jQuery( function() {
	jQuery( '#startDate' ).datepicker({
		showOn: "button",
		buttonImage: "/training-tool/lib/css/ui-lightness/images/calendar.gif",
		buttonImageOnly: true,
		changeMonth: true,
		changeYear: true,
		dateFormat: 'yy/mm/dd',
	});
	jQuery( '#endDate' ).datepicker({
		showOn: "button",
		buttonImage: "/training-tool/lib/css/ui-lightness/images/calendar.gif",
		buttonImageOnly: true,
		changeMonth: true,
		changeYear: true,
		dateFormat: 'yy/mm/dd',
	});
	jQuery( '#totalResult*' ).tablesorter({
		headers: {
		}
	});
	});

function hiddenArea(divname){
	var area = document.getElementById(divname);
	if(area != null){
		area.style.display= 'none';
	}
}

function setValue(arg1,arg2){
	document.getElementById(arg1).value=arg2;
}
// -->
</script>