<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.trainingsupport.bundle.messages"/>
</jsp:useBean>
	<ul class="navIntraTool actionToolBar" role="menu">
		<li role="menuitem">
			<a href=index.htm?menu=roster >
				<c:out value="${msgs.menu_title_go_menu }"/>
			</a>
		</li>
	</ul>
</br>

