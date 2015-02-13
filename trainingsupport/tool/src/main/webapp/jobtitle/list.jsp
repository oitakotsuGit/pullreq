<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="jobtitle"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

 	<div class="instructions">
		<h3 class="instructions">
			<c:out value="${msgs.menu_title_jobtitle}" />
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
	<c:if test="${not empty resultMess }">
		<div class="success">
		<c:forEach var="mess" items="${resultMess}">
			<c:out value="${mess }" /> <br/>
		</c:forEach>
		</div>
	</c:if>
	<table border="0" width="100%"><tr>
	<td align="right"><form method="post" id="csvSectionForm" action=jobtitle.htm?action=jobtitleCsv&selectedPatternId=<c:out value='${selectedPatternId}'/>>
		<div align="right">
			<input type="hidden" id="submitKind" name="submitKind" value="submit" />
			<input type="submit" name="cunRegisteredCsvDownload" value="<c:out value="${msgs.jobtitle_unregistered_csv_download }"/>"
				onclick="setValue('submitKind','unRegisteredCsvDownload')" />
			<input type="submit" name="csvDownload" value="<c:out value="${msgs.jobtitle_csv_download }"/>"
				onclick="setValue('submitKind','csvDownload')" />
			<input type="submit" name="csvUpload" value="<c:out value="${msgs.jobtitle_csv_upload }"/>" 
				onclick="setValue('submitKind','csvUpload')"/>
		</div>
	</form></td>
	<tr>
	<td colspan="2" align="right"><div class="strongRed"><c:out value="${msgs.msgs_jobtitle_download_1 }"/></div>
	</td></tr>
	</tr>
	</table>
	<table border="0"><tr>
	<c:if test="${not empty patterns }"><td>
	<form method="post" id="patternSelect" action=jobtitle.htm?action=changePattern >
		<label class="topLabel" ><c:out value="${msgs.label_jobtitle_pattern}"/></label>
		<select id="selectedPatternId" name="selectedPatternId" size="1" onchange="submit(this.form)">
			<c:forEach var="pattern" items="${patterns }">
				<c:choose>
				<c:when test="${pattern.id eq selectedPatternId}">
					<option value=<c:out value='${pattern.id}'/> selected="selected"><c:out value="${pattern.name}" />
					</option>
				</c:when>
				<c:otherwise>
					<option value=<c:out value='${pattern.id}'/>><c:out value="${pattern.name}" />
				</c:otherwise>
				</c:choose>
			</c:forEach>		
		</select>
	</form></td>
	</c:if>
	<td><form method="post" id="addJobtitleForm" action=jobtitle.htm?action=add&selectedPatternId=<c:out value='${selectedPatternId}'/> >
	   	<div class="act">
	   	    <label class="topLabel" ><c:out value="${msgs.job_title}"/></label>
	   		<input type="text" id="jobtitle" name="jobtitle" maxlength="255"/>
	   		<c:if test="${empty registerDisabled }">
	    	<input type="submit" name="submitButton" value="<c:out value="${msgs.add}"/>" />
	    	</c:if>
	    </div>
	</form></td>
	</tr></table>
<br/>

<form method="post" id="listForm" action=jobtitle.htm?action=delJobtitle&selectedPatternId=<c:out value='${selectedPatternId}'/>>
<div width="50%">
	<table id="jobtitleList"  class="tablesorter lines jobtitleTable"  cellspacing="0" cellpadding="0"  summary="jobtitle">
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.job_title}"/></th>
				<th scope="col"><c:out value="${msgs.label_jobtitle_pattern}"/></th>
				<th scope="col"><c:out value=""/></th>
				<th scope="col"><c:out value="${msgs.delete }"/></th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${empty jobTitles }">
				<tr><td style="white-space:nowrap" colspan="3"><c:out value="${msgs.no_jobtitles }"/></td></tr>
			</c:if>
			<c:if test="${not empty jobTitles }">
				<c:forEach var="jobTitle" items="${jobTitles }">
					<tr>
						<td style="white-space:nowrap" width="100px">
							<c:out value="${jobTitle.jobTitle}"/>
						</td>
						<td style="white-space:nowrap" width="100px">
							<c:out value="${jobTitle.jobTitlePattern}"/>
						</td>
						<td width="80px">
						</td>
						<td width="40px">
							<a href=jobtitle.htm?action=delete&id=<c:out value="${jobTitle.id }"/>&selectedPatternId=<c:out value='${selectedPatternId}'/> >
								<c:out value="${msgs.delete }"/>
							</a>
						</td>
					</tr>
				</c:forEach>
			</c:if>
		</tbody>
	</table>
</div>
</form>
<br>

<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
jQuery( function() {
	jQuery( '#jobtitleList' ).tablesorter({
		headers: {
			2: { sorter: false},
			3: { sorter: false}
		}
	});
	});
function setValue(arg1,arg2){
	document.getElementById(arg1).value=arg2;
}
// -->
</script>