<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="sectionUpload"/>
<jsp:directive.include file="/inc/navMenu.jsp"/>

	<div class="instructions">
	</div>


<form action="fileupload.htm" enctype="multipart/form-data" method="post">
	<c:out value="${msgs.label_csv_upload_file}" />
	<input type="hidden" name="kind" value="jobtitle"/>
	<input type="file" name="file"/><input type="submit" value="${ msgs.label_csv_upload_done}"/>
</form>
<br/>
<form action="jobtitle.htm?menu=list" method="post">
	<input type="submit" value="${msgs.label_return }" />
 </form>
<c:if test="${not empty resultMess }">
<div class="success">
	<c:forEach var="mess" items="${resultMess}">
		<c:out value="${mess }" /> <br/>
	</c:forEach>
</div>
</c:if>
<div style="height:300px"></div>
<jsp:directive.include file="/templates/footer.jsp"/>
