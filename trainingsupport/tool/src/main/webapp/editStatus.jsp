<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:set var="viewName" value="editStatus"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.trainingsupport.bundle.messages"/>
</jsp:useBean>

<div class="instructions">
	<h3 class="instructions">
		<c:out value="${msgs.edit_status}" />
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

<p>
	<label for="status" class="topLabel editStatus"/>
		<c:out value="${msgs.label_section}"/>
	</label>
	<c:out value="${sectionName}"/>
</p>
<p>
	<label for="status" class="topLabel editStatus"/>
		<c:out value="${msgs.user_name}"/>
	</label>
	<c:out value="${userName}"/>
</p>
<form method="post" id="statusEditForm" action=statusEdit.htm?action=save&sectionId=<c:out value="${sectionId}"/> >
	<p>
		<label for="status" class="topLabel editStatus"/>
			<c:out value="${msgs.training_done_status}"/>
		</label>
		<select id="status" name="status">
			<option value="0" <c:if test="${status == 0}">selected="selected"</c:if>>
				<c:out value="${msgs.status_reject}"/>
			</option>
			<option value="1" <c:if test="${status == 1}">selected="selected"</c:if>>
				<c:out value="${msgs.status_accept}"/>
			</option>
		</select>
	</p>
	<p>
		<label for="datepicker" class="topLabel editStatus">
			<c:out value="${msgs.training_done_datetime}"/>
		</label>
		<input type="text" id="datepicker" name="date" value="<c:out value="${date}"/>"/>
		<input type="text" id="timepicker" name="time" value="<c:out value="${time}"/>"/>
	</p>
	<p>
		<label for="note" class="topLabel editStatus">
			<c:out value="${msgs.manually_update_note}"/>
		</label>
		<textarea id="note" name="note" rows="4" cols="50"><c:out value="${note}"/></textarea>
	</p>
	
	<div class="buttons">
		<input type="submit" name="submitButton" value="<c:out value="${msgs.add}"/>" />
		<input type="button" id="returnButton" value="<c:out value="${msgs.label_return}"/>" />
	</div>
</form>
<jsp:directive.include file="/templates/footer.jsp"/>
<link type="text/css" href="lib/css/jquery.ui.timepicker.css" rel="stylesheet" media="screen">
<script type="text/javascript" src="lib/jquery.ui.timepicker.js"></script>
<script type="text/javascript">
$(function() {
	$('#datepicker').datepicker({
		dateFormat: "yy/mm/dd"
	});
	$('#timepicker').timepicker({
		showPeriodLabels: false
	});
	$('#returnButton').on('click', function() {
		window.location.href = 'index.htm?sectionId=<c:out value="${sectionId}"/>';
	});
	$('#status').on('change', function() {
		change($(this).val() == "1");
	});
	change($('#status').val() == "1");

	function change(enable) {
		if (enable) {
			$('#datepicker').removeAttr('disabled');
			$('#timepicker').removeAttr('disabled');
		} else {
			$('#datepicker').attr('disabled', 'disabled');
			$('#timepicker').attr('disabled', 'disabled');
		}
	}
});
</script>

