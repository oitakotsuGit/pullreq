<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.trainingsupport.bundle.messages"/>
</jsp:useBean>
	<ul class="navIntraTool actionToolBar" role="menu">
		<li role="menuitem">
			<a href=index.htm?menu=roster >
				<c:out value="${msgs.menu_title_go_menu }"/>
			</a>
		</li>
		<li role="menuitem">
			<c:if test="${viewName ne 'editRoster'}">
			<a href=index.htm?action=editRoster&sectionId=<c:out value='${sectionId}'/> >
				<c:out value="${msgs.title_roster_list}"/>
			</a>
			</c:if>
			<c:if test="${viewName eq 'editRoster'}">
				<span class="current"><c:out value="${msgs.title_roster_list}"/>
				</span>
			</c:if>
		</li>
		<li role="menuitem">
			<c:if test="${viewName ne 'editAddRoster'}">
			<a href=index.htm?action=editAddedUserList&sectionId=<c:out value='${sectionId}'/> >
				<c:out value="${msgs.menu_add_user_list }"/>
				(<c:out value='${addListCount}'/>)
			</a>
			</c:if>
			<c:if test="${viewName eq 'editAddRoster'}">
				<span class="current"><c:out value="${msgs.menu_add_user_list }"/>
				(<c:out value='${addListCount}'/>)
				</span>
			</c:if>
		</li>
		<li role="menuitem">
			<c:if test="${viewName ne 'editDeleteRoster' }">
			<a href=index.htm?action=editRemovedUserList&sectionId=<c:out value='${sectionId}'/> >
				<c:out value="${msgs.menu_delete_user_list }"/>
				(<c:out value='${deleteListCount}'/>)
			</a>
			</c:if>
			<c:if test="${viewName eq 'editDeleteRoster' }">
				<span class="current"><c:out value="${msgs.menu_delete_user_list }"/>
				(<c:out value='${deleteListCount}'/>)</span>
			</c:if>
		</li>
	</ul>
</br>

