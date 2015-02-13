<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.trainingsupport.bundle.messages"/>
</jsp:useBean>
	<ul class="navIntraTool actionToolBar" role="menu">
		<li role="menuitem" aria-disabled="true">
			<c:if test="${viewName ne 'managerIndex'}">
				<a href=index.htm?menu=status >
					<c:out value="${msgs.menu_title_status }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'managerIndex'}">
				<span class="current"><c:out value="${msgs.menu_title_status }"/></span>
			</c:if>
		</li>
		<li role="menuitem">
			<c:if test="${viewName ne 'overview'}">
				<a href=index.htm?menu=roster >
					<c:out value="${msgs.menu_title_roster }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'overview'}">
				<span class="current"><c:out value="${msgs.menu_title_roster }"/></span>
			</c:if>
		</li>
		<c:if test="${statusUpdateEnabled}">
		<li role="menuitem">
			<c:if test="${viewName ne 'statusUpdate'}">
				<a href=statusUpdate.htm?menu=init >
					<c:out value="${msgs.menu_title_status_update }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'statusUpdate'}">
				<span class="current"><c:out value="${msgs.menu_title_status_update}"/></span>
			</c:if>
		</li>
		</c:if>
		<c:if test="${totalizationEnabled}">
		<li role="menuitem">
			<c:if test="${viewName ne 'totalization'}">
				<a href=totalize.htm?menu=init >
					<c:out value="${msgs.menu_title_totalization }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'totalization'}">
				<span><c:out value="${msgs.menu_title_totalization }"/></span>
			</c:if>
		</li>
		</c:if>
		<c:if test="${allTotalizationEnabled}">
		<li role="menuitem">
			<c:if test="${viewName ne 'allTotalization'}">
				<a href=allTotalize.htm?menu=init >
					<c:out value="${msgs.menu_title_all_totalization }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'allTotalization'}">
				<span><c:out value="${msgs.menu_title_all_totalization }"/></span>
			</c:if>
		</li>
		</c:if>
		<c:if test="${jobtitleMainteEnabled}">
		<li role="menuitem">
			<c:if test="${viewName ne 'jobtitle'}">
				<a href=jobtitle.htm?menu=list >
					<c:out value="${msgs.menu_title_jobtitle }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'jobtitle'}">
				<span class="current"><c:out value="${msgs.menu_title_jobtitle}"/></span>
			</c:if>
		</li>
		</c:if>
		<c:if test="${ldapAutoSettingEnabled }">
			<li role="menuitem">
				<c:if test="${viewName ne 'ldapAutoSetting'}">
					<a href=index.htm?menu=ldapAutoSetting >
						<c:out value="${msgs.menu_ldap_setting }"/>
					</a>
				</c:if>
				<c:if test="${viewName eq 'ldapAutoSetting'}">
					<span class="current"><c:out value="${msgs.menu_ldap_setting}"/></span>
				</c:if>
			</li>
		</c:if>
	</ul>
</br>

