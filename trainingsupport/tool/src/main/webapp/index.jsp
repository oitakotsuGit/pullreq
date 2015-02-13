<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.trainingsupport.bundle.messages"/>
</jsp:useBean>

		<table class="listHier lines nolines" cellspacing="0" cellpadding="0" border="0" summary="status" >
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.training_done_status }"/></th>
					<th scope="col"><c:out value="${msgs.training_done_datetime }"/></th>
				</tr>
			</thead>
			<c:if test="${not empty trainingStatus }">
			<tbody>
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${status}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${statusDate}"/>
						</td>
					</tr>
			</tbody>
			</c:if>
			<c:if test="${empty trainingStatus }">
			<tbody>
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${msgs.status_reject}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value=""/>
						</td>
					</tr>
			</tbody>
			</c:if>
		</table>
<jsp:directive.include file="/templates/footer.jsp"/>

