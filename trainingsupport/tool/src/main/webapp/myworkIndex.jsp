<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.trainingsupport.bundle.messages"/>
</jsp:useBean>
	<c:if test="${not empty statusList }">

		<table class="listHier lines nolines" cellspacing="0" cellpadding="0" border="0" summary="status" >
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.training_site_title }"/></th>
					<th scope="col"><c:out value="${msgs.training_done_status }"/></th>
					<th scope="col"><c:out value="${msgs.training_done_datetime }"/></th>
				</tr>
			</thead>
			<c:forEach var="statusBean" items="${statusList }">
			<tbody>
					<tr>
						<td style="white-space:nowrap">
							<c:if test="${statusBean.status == 0 || statusBean.status == -1  }">
							<a href="#" onclick='javascript:window.parent.location.href="/portal/site/<c:out value='${statusBean.siteId}'/>";''>
							<c:out value="${statusBean.siteTitle }"/></a>
							</c:if>
							<c:if test="${statusBean.status == 1 }">
								<c:out value="${statusBean.siteTitle }"/>
							</c:if>
						</td>
						<td style="white-space:nowrap">
							<c:if test="${statusBean.status == 0 }">
								<c:out value="${msgs.status_reject }"/>
							</c:if>
							<c:if test="${statusBean.status == 1 }">
								<c:out value="${msgs.status_accept }"/>
							</c:if>
							<c:if test="${statusBean.status == -1 }">
								<c:out value="${statusBean.total }"/>
							</c:if>
						</td>
						<td style="white-space:nowrap">
							<c:if test="${statusBean.status != -1 }">
								<c:out value="${statusBean.updateDate }"/>
							</c:if>
							<c:if test="${statusBean.status == -1 }">
								<c:out value="-"/>
							</c:if>
						</td>
					</tr>
			</tbody>
			</c:forEach>
		</table>
	</c:if>
	<c:if test="${empty statusList }">
		<c:out value="${msgs.site_none }" />
	</c:if>
<jsp:directive.include file="/templates/footer.jsp"/>

