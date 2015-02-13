package org.sakaiproject.trainingsupport.logic;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sakaiproject.authz.api.GroupProvider;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;

import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.Course;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.SectionManager;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.Enrollment;

import org.sakaiproject.user.api.UserDirectoryProvider;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.service.gradebook.shared.GradebookService;

import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionTABean;
import org.sakaiproject.trainingsupport.model.LocalSectionModel;
import org.sakaiproject.trainingsupport.model.TrainingStatus;
import org.sakaiproject.trainingsupport.util.Constant;


/**
 * Implementation of {@link SakaiProxy}
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class SakaiProxyImpl extends Constant implements SakaiProxy {

	private static final Logger log = Logger.getLogger(SakaiProxyImpl.class);
	private static final int STATUS_ACCEPT = 1;
	private static final int STATUS_REJECT = 0;
	
	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";
    private static ResourceLoader rb = null;	
	
	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentSiteId(){
		return toolManager.getCurrentPlacement().getContext();
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentUserId() {
		return sessionManager.getCurrentSessionUserId();
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentUserDisplayName() {
	   return userDirectoryService.getCurrentUser().getDisplayName();
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public boolean isSuperUser() {
		return securityService.isSuperUser();
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public void postEvent(String event,String reference,boolean modify) {
		eventTrackingService.post(eventTrackingService.newEvent(event,reference,modify));
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public String getSkinRepoProperty(){
		return serverConfigurationService.getString("skin.repo");
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public String getToolSkinCSS(String skinRepo){
		
		String skin = siteService.findTool(sessionManager.getCurrentToolSession().getPlacementId()).getSkin();
		
		if(skin == null) {
			skin = serverConfigurationService.getString("skin.default");
		}
		
		return skinRepo + "/" + skin + "/tool.css";
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public String getSectionCategoryCatCode(){
		return serverConfigurationService.getString("training.tool.section.category.code");
	}

	public String getCurrentUserRole(){
		String siteId = getCurrentSiteId();
		if(isMyWorkspace(siteId)){
			return null;
		}
		Site site;
		try {
			site = getSiteService().getSite(siteId);
			Role role = site.getUserRole(getCurrentUserId());
			return role.getId();
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return null;
	}

	public List<Site> getSites(String type){
		return  siteService.getSites(
				SiteService.SelectionType.ACCESS, 
				type, null, null, 
				SiteService.SortType.ID_ASC, null);
	}
	
	private boolean isMyWorkspace(String siteId){
		boolean isMyWorkspace = false;
		if (siteService.isUserSite(siteId)) {
			if (siteService.getSiteUserId(siteId).equals(
					sessionManager.getCurrentSessionUserId())) {
				isMyWorkspace = true;
			}
		}
		return isMyWorkspace;
	}

	public User getCurrentUser(){
		return userDirectoryService.getCurrentUser();
	}
	
	public String getCourseUuid() {
		return getCourse().getUuid();
	}
	public Course getCourse() {
		return sectionManager.getCourse(getCurrentSiteId());
	}
	
	public Site getSite(String siteId) {
		try {
			Site site = siteService.getSite(siteId);
			return site;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return null;
	}

	public List<TrainingSectionBean> getSections(String siteId, String userId){
		List<CourseSection> sectionList = sectionManager.getSectionsInCategory(siteId, getSectionCategoryCatCode());
		// Get the total enrollments for all groups
		Map sectionSize = sectionManager.getEnrollmentCount(sectionList);
		// Get the TAs for all groups
		Map<String,List<ParticipationRecord>> sectionTAs = sectionManager.getSectionTeachingAssistantsMap(sectionList);
		List<TrainingSectionBean> sections = new ArrayList<TrainingSectionBean>();
		for(Iterator sectionIter = sectionList.iterator(); sectionIter.hasNext();) {
			CourseSection section = (CourseSection)sectionIter.next();
			// Generate the string showing the TAs
			List<ParticipationRecord> tas = (List<ParticipationRecord>) sectionTAs.get(section.getUuid());
			// not ta
			if(userId != null && (!isTa(tas, userId))) {
				continue;
			}
			List<String> taNames = generateTaNames(tas);
			List<String> taUids = generateTaUids(tas);			
			int totalEnrollments = sectionSize.containsKey(section.getUuid()) ?
					(Integer) sectionSize.get(section.getUuid()) : 0;
			TrainingSectionBean trainingSectionBean = new TrainingSectionBean(
					section, section.getUuid(), section.getTitle(),  taNames, totalEnrollments);
			sections.add(trainingSectionBean);			 
		}

		return sections;
	}
	
	private boolean isTa(List<ParticipationRecord> tas, String userId){
		for(ParticipationRecord ta:tas){
			if(userId.equals(ta.getUser().getUserUid())){
				return true;
			}
		}
		return false;
	}
	
	private boolean isTrainingDepartmentManger(String userId)  {
		
		
		
		return false;
	}
	
	public List<TrainingSectionBean> getSectionsBySectionIds(String[] sectionIds){
		List<TrainingSectionBean> sections = new ArrayList<TrainingSectionBean>();
		for(int i = 0; i < sectionIds.length; i++){
			CourseSection section = getCourseSection(sectionIds[i]);
			TrainingSectionBean trainingSectionBean = new TrainingSectionBean();
			trainingSectionBean.setSection(section);
			sections.add(trainingSectionBean);
		}
		return sections;
	}
	public TrainingSectionBean getSectionBySectionId(String sectionId){
		CourseSection courseSection = getCourseSection(sectionId);
		List<CourseSection> sectionList = new ArrayList<CourseSection>();
		sectionList.add(courseSection);
		Map sectionSize = sectionManager.getEnrollmentCount(sectionList);
		List<ParticipationRecord> tas= sectionManager.getSectionTeachingAssistants(sectionId);
		List<String> taNames = generateTaNames(tas);
		List<String> taUids = generateTaUids(tas);			
		int totalEnrollments = sectionSize.containsKey(courseSection.getUuid()) ?
				(Integer) sectionSize.get(courseSection.getUuid()) : 0;
		TrainingSectionBean trainingSectionBean = new TrainingSectionBean(
				courseSection, courseSection.getUuid(), courseSection.getTitle(), taNames, totalEnrollments);
		return trainingSectionBean;
	}
	public String getSectionName(String sectionId){
		String sectionName = "";
		try{
			CourseSection section = sectionManager.getSection(sectionId);
			sectionName = section.getTitle();
		}catch(Exception e){}
		return sectionName;
	}
	private CourseSection getCourseSection(String sectionId){
		CourseSection section = sectionManager.getSection(sectionId);
		return section;
	}
	
	public boolean isDuplicateSectionTitle(String sectionName, String siteId) {
		Collection<CourseSection> existingSections = sectionManager.getSections(siteId);
		for(Iterator iter = existingSections.iterator(); iter.hasNext();) {
			CourseSection section = (CourseSection)iter.next();
			if(section.getTitle().equals(sectionName)) {
				if(log.isDebugEnabled()) log.debug("Conflicting section name found: " + sectionName);
				return true;
			}
		}
		return false;
	}
	
	public String addSection(String sectionName){
		Collection<CourseSection> courseSections = new ArrayList<CourseSection>();
		CourseSection courseSection = new LocalSectionModel(getCourse(), sectionName, getSectionCategoryCatCode());
		courseSections.add(courseSection);
		Collection<CourseSection> addSections = sectionManager.addSections(getCourseUuid(), courseSections);
		String addSectionUuid = null;
		for(Iterator iter = addSections.iterator(); iter.hasNext();) {
			CourseSection addSection = (CourseSection)iter.next();
			addSectionUuid = addSection.getUuid();
		}
		return addSectionUuid;
	}
	
	public void updateSection(String sectionId, String sectionName){
		CourseSection courseSection = getCourseSection(sectionId);
		sectionManager.updateSection(courseSection.getUuid(),
				sectionName,
				courseSection.getMaxEnrollments(),
				courseSection.getMeetings());
	}

	public void delSections(String[] sectionIds) {
		Set<String> ids = new HashSet<String>();		
		for(int i = 0; i < sectionIds.length; i++){
			ids.add(sectionIds[i]);
		}
		sectionManager.disbandSections(ids);
	}
	
	static Comparator sortNameComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			ParticipationRecord manager1 = (ParticipationRecord)o1;
			ParticipationRecord manager2 = (ParticipationRecord)o2;
			return manager1.getUser().getSortName().compareTo(manager2.getUser().getSortName());
		}
	};
	
	public String getUserEidById(String userUid){
		String userEid = null;
		try {
			userEid = userDirectoryService.getUserEid(userUid);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return userEid;
	}
	
	public List<TrainingSectionTABean> getSectionTAs(String sectionId){
		List<ParticipationRecord> participationRecords= sectionManager.getSectionTeachingAssistants(sectionId);
		Collections.sort(participationRecords, sortNameComparator);
		List<TrainingSectionTABean> trainingSectionTABeans = new ArrayList<TrainingSectionTABean>();
		for(Iterator sectionTAsIter = participationRecords.iterator(); sectionTAsIter.hasNext();) {
			ParticipationRecord sectionTA = (ParticipationRecord)sectionTAsIter.next();
			TrainingSectionTABean trainingSectionTABean = new TrainingSectionTABean(
					sectionTA.getLearningContext().getUuid(), sectionTA.getLearningContext().getTitle(),
					sectionTA.getUser().getSortName(), sectionTA.getUser().getUserUid(), getUserEidById(sectionTA.getUser().getUserUid()));
			try {
				ResourceProperties resourceProperties = userDirectoryService.getUser(sectionTA.getUser().getUserUid()).getProperties();
				trainingSectionTABean.setJobTitle(resourceProperties.getProperty(Constant.LDAP_JOB_TITLE));
				trainingSectionTABean.setPersonalNumber(resourceProperties.getProperty(Constant.LDAP_STAFF_NUMBER));
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
			trainingSectionTABeans.add(trainingSectionTABean);
		}		
		return trainingSectionTABeans;
	}
	
	public TrainingSectionTABean getTABeanByEid(String userEid, String sectionId){
		CourseSection courseSection = getCourseSection(sectionId);
		User user;
		try {
			user = userDirectoryService.getUserByEid(userEid);
		} catch (UserNotDefinedException e) {
			log.debug(e.getMessage());
			return null;
		}
		if(user != null){
			TrainingSectionTABean trainingSectionTABean = new TrainingSectionTABean(
					courseSection.getUuid(), courseSection.getTitle(), user.getSortName(), user.getId(), user.getEid());

			return trainingSectionTABean;			
		}
		return null;
	}
	
	public String getUserRole(String userUid, String siteId){
		Site site = getSite(siteId);
		if(site != null){
			try {
				Role role = site.getUserRole(userUid);
				return role.getId();
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
		return null;
	}
	
	public boolean isSectionTA(String sectionId, String userId){
		boolean isSectionTA = false;
		List<TrainingSectionTABean> trainingSectionTABeans = getSectionTAs(sectionId);
		if(trainingSectionTABeans.isEmpty()){
			return isSectionTA;
		}
		for(Iterator sectionTAsIter = trainingSectionTABeans.iterator(); sectionTAsIter.hasNext();) {
			TrainingSectionTABean sectionTA = (TrainingSectionTABean)sectionTAsIter.next();
			if(sectionTA.getUserEid().equals(userId)){
				isSectionTA = true;
				break;
			}
		}
		return isSectionTA;
	}

	public void addSiteMemberTA(String userUid, String roleId, String siteId, String sectionId){
		addSiteMember(userUid, roleId, siteId, sectionId);
		addSectionMembershipTA(userUid, sectionId);
	}
	
	@Override
	public void addSiteMemberAsStudent(String userUid, String siteId) {
		addSiteMember(userUid, Constant.STUDENT_ROLE, siteId, null);
	}
	
	private void addSiteMember(String userUid, String roleId, String siteId, String sectionId){		
		Site site = getSite(siteId);		
		if(site != null){
			site.addMember(userUid, roleId, true, false);
			try {
				siteService.save(site);
			} catch (Exception e) {
				log.debug(e.getMessage());
			}		
		}
	}
	
	public void addSectionMembershipTA(String userUid, String sectionId){
		addSectionMembership(userUid, org.sakaiproject.section.api.facade.Role.TA, sectionId);
	}
	
	@Override
	public void addSectionMembershipStudent(String userEid, String sectionId) {
		User user = getUserByEid(userEid);
		if (user != null) {
			addSectionMembership(user.getId(), org.sakaiproject.section.api.facade.Role.STUDENT, sectionId);
		}
		
	}
	
	private void addSectionMembership(String userUid, org.sakaiproject.section.api.facade.Role role, String sectionId){
		try {
			sectionManager.addSectionMembership(userUid, role, sectionId);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}
	
	public void deleteSectionTAs(String siteId, String sectionId, String ids[]){
		for(int i = 0; i < ids.length; i++) {
			String id = (String)ids[i];
			sectionManager.dropSectionMembership(id, sectionId);
			boolean delFlg = true;
			List<CourseSection> sectionList = sectionManager.getSectionsInCategory(siteId, getSectionCategoryCatCode());
			loop1:for(Iterator sectionIter = sectionList.iterator(); sectionIter.hasNext();) {
				CourseSection section = (CourseSection)sectionIter.next();
				if(!section.getUuid().equals(sectionId)){
					List<ParticipationRecord> participationRecords= sectionManager.getSectionTeachingAssistants(section.getUuid());
					for(Iterator sectionTAsIter = participationRecords.iterator(); sectionTAsIter.hasNext();) {
						ParticipationRecord sectionTA = (ParticipationRecord)sectionTAsIter.next();
						if(sectionTA.getUser().getUserUid().equals(id)){
							delFlg = false;
							break loop1;
						}
					}
				}
			}

			if(delFlg){
				// delete from site
				removeSiteMember(id, siteId);			
			}
		}
	}

	public void removeSiteMember(String userUid, String siteId){		
		Site site = getSite(siteId);		
		if(site != null){
			site.removeMember(userUid);
			try {
				siteService.save(site);
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
	}
	
	static Comparator sortNameComparatorForEnrollmentRecord = new Comparator() {
		public int compare(Object o1, Object o2) {
			EnrollmentRecord manager1 = (EnrollmentRecord)o1;
			EnrollmentRecord manager2 = (EnrollmentRecord)o2;
			return manager1.getUser().getSortName().compareTo(manager2.getUser().getSortName());
		}
	};
	
	public List<TrainingSectionRosterBean> getSectionRoster(String sectionId){
		List<EnrollmentRecord> enrollmentRecordRecords= sectionManager.getSectionEnrollments(sectionId);
		Collections.sort(enrollmentRecordRecords, sortNameComparatorForEnrollmentRecord);
		List<TrainingSectionRosterBean> trainingSectionRosterBeans = new ArrayList<TrainingSectionRosterBean>();
		for(Iterator rosterIter = enrollmentRecordRecords.iterator(); rosterIter.hasNext();) {
			EnrollmentRecord roster = (EnrollmentRecord)rosterIter.next();
			TrainingSectionRosterBean trainingSectionRosterBean = new TrainingSectionRosterBean(
					roster.getUser().getSortName(), roster.getUser().getUserUid(), getUserEidById(roster.getUser().getUserUid()), roster.getStatus());
			try {
				ResourceProperties resourceProperties = userDirectoryService.getUser(roster.getUser().getUserUid()).getProperties();
				trainingSectionRosterBean.setJobTitle(resourceProperties.getProperty(Constant.LDAP_JOB_TITLE));
				trainingSectionRosterBean.setJobTitle1(resourceProperties.getProperty(Constant.LDAP_JOB_TITLE1));
				trainingSectionRosterBean.setJobTitle2(resourceProperties.getProperty(Constant.LDAP_JOB_TITLE2));
				trainingSectionRosterBean.setJobTitle3(resourceProperties.getProperty(Constant.LDAP_JOB_TITLE3));
				trainingSectionRosterBean.setPersonalNumber(resourceProperties.getProperty(Constant.LDAP_STAFF_NUMBER));
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
			// We need to load the current status data. Fixed by Shoji Kajita 2014/04/05
			
			trainingSectionRosterBeans.add(trainingSectionRosterBean);
		}		
		return trainingSectionRosterBeans;
	}
	
	public TrainingSectionRosterBean getTargetUserByEid(String userEid){
		User user;
		try {
			user = userDirectoryService.getUserByEid(userEid);
		} catch (UserNotDefinedException e) {
			log.debug(e.getMessage());
			return null;
		}
		if(user != null){
			try {
				ResourceProperties resourceProperties = user.getProperties();
				TrainingSectionRosterBean trainingSectionRosterBean = new TrainingSectionRosterBean(
						resourceProperties.getProperty(Constant.LDAP_JOB_TITLE), resourceProperties.getProperty(Constant.LDAP_STAFF_NUMBER),
						user.getSortName(), user.getId(), user.getEid());
				return trainingSectionRosterBean;
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
		return null;
	}
	
	public boolean availableAddRoster(String siteId, String userUid, CourseSection courseSection){
		Set<EnrollmentRecord> sectionEnrollments = sectionManager.getSectionEnrollments(userUid, courseSection.getCourse().getUuid());
		if(sectionEnrollments == null){
			return true;
		}
		for(Iterator enrollmentIter = sectionEnrollments.iterator(); enrollmentIter.hasNext();) {
			EnrollmentRecord enrollment = (EnrollmentRecord)enrollmentIter.next();
			if(courseSection.getUuid().equals( enrollment.getLearningContext().getUuid())){
				return true;
			}else{
				CourseSection enrollmentCourseSection = getCourseSection(enrollment.getLearningContext().getUuid());
				if(enrollmentCourseSection.getCategory().equals(getSectionCategoryCatCode())){
					return false;
				}
			}			
		}
		return true;
	}

	public void updateRoster(List<TrainingSectionRosterBean> rosterList, String roleId, String siteId, String sectionId, String courseId){

		List<TrainingSectionRosterBean> updateRosterList = new ArrayList<TrainingSectionRosterBean>(rosterList);

		List<TrainingSectionRosterBean> preRosterList = getSectionRoster(sectionId);
		if(preRosterList != null) {
			boolean delFlg = false;
			for(int i=0; i < preRosterList.size(); i++) {  // fixed by Shoji Kajita 2014/04/06 for CLS-6
				if(delFlg){
					i = i - 1;
					delFlg = false;
				}				
				TrainingSectionRosterBean preRoster = preRosterList.get(i);
				if(updateRosterList != null) {
					for(int j=0; j < updateRosterList.size(); j++) {
						TrainingSectionRosterBean roster = updateRosterList.get(j);
						if(preRoster.getUserEid().equals(roster.getUserEid())){
							preRosterList.remove(i);
							updateRosterList.remove(j);
							delFlg = true;
							if (!preRoster.getEnrollmentStatus().equals(roster.getEnrollmentStatus()))  {
								// need to update
							}
							break;
						}
					}
				}
			}
		}
		if(preRosterList != null) {
			for(int i=0; i < preRosterList.size(); i++) {
				sectionManager.dropSectionMembership(preRosterList.get(i).getUserUid(), sectionId);
				Set<EnrollmentRecord> sectionEnrollments = sectionManager.getSectionEnrollments(preRosterList.get(i).getUserUid(), courseId);
				boolean delFlg = false;
				if(sectionEnrollments == null || sectionEnrollments.size() < 1){
					// 削除者リスト行きなのでサイトメンバからは外さない
					// removeSiteMember(preRosterList.get(i).getUserUid(), siteId);
				}
			}
		}
		if(updateRosterList != null) {
			for(int i=0; i < updateRosterList.size(); i++) {
				TrainingSectionRosterBean rosterBean = updateRosterList.get(i);
				if(getUserRole(rosterBean.getUserUid(), siteId) == null){
					try {
						// for ldap retrieve user
						User user = userDirectoryService.getUserByEid(rosterBean.getUserEid());
					} catch (UserNotDefinedException e) {
						e.printStackTrace();
					}
					addSiteMember(rosterBean.getUserUid(), roleId, siteId, sectionId);
				}				
				addSectionMembership(updateRosterList.get(i).getUserUid(), org.sakaiproject.section.api.facade.Role.STUDENT, sectionId);
			}
		}
	}	

	public Set<Member> getSiteMember(String siteId){
		Site site = getSite(siteId);
		if(site != null){
			try {
				Set<Member> members = site.getMembers();
				return members;
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
		return null;
	}		

	public List<TrainingSectionRosterBean> getTargetRosterByJobTitle(String siteId, String sectionId, String pattern, String[] jobTitles){
		List<TrainingSectionRosterBean> rosterList = new ArrayList<TrainingSectionRosterBean>();
		CourseSection courseSection = getCourseSection(sectionId);
		String sectionName = courseSection.getTitle();
		for(int i = 0; i < jobTitles.length; i++){
			List<User> users = new ArrayList<User>();
			try {
			    //	users = userDirectoryService.searchExternalUsers(pattern + "=" +jobTitles[i], -1, -1);
			    // users = userDirectoryService.findUsersByAlternativeId(jobTitles[i], pattern);
				if (sectionName.equals(rb.getString("all_affiliations")))  {
					users = userDirectoryService.findUsersByTitle(jobTitles[i], null, pattern);
				}  else  {
					users = userDirectoryService.findUsersByTitle(jobTitles[i], sectionName, pattern);
				}
			} catch (Exception e) {
				log.debug(e.getMessage());
				return null;
			}
			if(users != null && users.size() > 0){
				for(User user : users){
					try {
						ResourceProperties resourceProperties = user.getProperties();						
						String ldapSectionName = resourceProperties.getProperty(Constant.LDAP_SECTION_NAME);
						if(sectionName.equals(rb.getString("all_affiliations"))
								||ldapSectionName != null && ldapSectionName.equals(sectionName)){
							String ldapJobTitle = resourceProperties.getProperty(pattern);
							if( ldapJobTitle != null && ldapJobTitle.equals(jobTitles[i])){
								TrainingSectionRosterBean trainingSectionRosterBean = new TrainingSectionRosterBean(
										ldapJobTitle, resourceProperties.getProperty(Constant.LDAP_STAFF_NUMBER), user.getSortName(), user.getId(), user.getEid());
								rosterList.add(trainingSectionRosterBean);
							}
						}						
					} catch (Exception e) {
						log.debug(e.getMessage());
					}
				}
			}
		}
		
		if(rosterList == null || rosterList.size() == 0){
			return rosterList;
		}
		
		Set<Member> siteMember = getSiteMember(siteId);
		if(siteMember != null && siteMember.size() > 0){			
			HashMap<String, Member> siteMemberMap = new HashMap<String, Member>();
			for(Member member : siteMember){
				siteMemberMap.put(member.getUserEid(), member);
			}
			for(TrainingSectionRosterBean roster : rosterList){
				boolean availableFlg = true;
				Member member = siteMemberMap.get(roster.getUserEid());				
				if(member != null){
					if(member.getRole().getId().equals(Constant.STUDENT_ROLE)){	
						roster.setAvailableFlg(availableAddRoster(siteId, roster.getUserUid(), courseSection));
					}else{
						roster.setAvailableFlg(false);
					}
					roster.setRole(member.getRole().getId());
				}
			}
		}		
		return rosterList;
	}

	public List<String> getUnregisteredSectionsNameFromLdap(String siteId){
		// all users
		List<User> users = userDirectoryService.searchExternalUsers("", -1, -1);
		Map<String, String> sectionMap = new HashMap<String, String>();
		for(User user: users){
			ResourceProperties resourceProperties = user.getProperties();						
			String ldapSectionName = resourceProperties.getProperty(Constant.LDAP_SECTION_NAME);
			if( ldapSectionName == null || ldapSectionName.isEmpty()){
				continue;
			}
			sectionMap.put(ldapSectionName, ldapSectionName);
		}
		List<CourseSection> sections = sectionManager.getSections(siteId);
		Map<String, CourseSection>existMap = new HashMap<String, CourseSection>();
		for(CourseSection section: sections){
			existMap.put(section.getTitle(), section);
		}
		List<String>resultList = new ArrayList<String>();
		for(String sectionName : sectionMap.values()){
			if(existMap.get(sectionName) == null){
				// add Section
				resultList.add(sectionName);
			}
		}
		return resultList;
	}
	
	public List<String> getUnregisteredSectionsFromCM(String siteId){
		Map<String, String> sectionMap = new HashMap<String, String>();
		GroupProvider groupProvider = (GroupProvider) ComponentManager.get(GroupProvider.class);
		CourseManagementService courseManagementService = (CourseManagementService) ComponentManager.get(CourseManagementService.class);
		Map map = groupProvider.getGroupRolesForUser(getCurrentUserId());
		if (map == null)
			return null;
		Set keys = map.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String sectionEid = (String) i.next();
			Section section = courseManagementService.getSection(sectionEid);
			String courseId = section.getCourseOfferingEid();
			if(siteId.equals(courseId)){
				EnrollmentSet enrollmentSet = section.getEnrollmentSet();
				Set enrollments = courseManagementService.getEnrollments(enrollmentSet.getEid());
				for (Iterator eIterator = enrollments.iterator();eIterator.hasNext();)
				{
					Enrollment e = (Enrollment) eIterator.next();
					try{
						User user = userDirectoryService.getUserByEid(e.getUserId());
						ResourceProperties resourceProperties = user.getProperties();						
						String ldapSectionName = resourceProperties.getProperty(Constant.LDAP_SECTION_NAME);
						sectionMap.put(ldapSectionName, ldapSectionName);
					}catch(Exception e1){}
				}
			}
		}
		List<CourseSection> sections = sectionManager.getSections(siteId);
		Map<String, CourseSection>existMap = new HashMap<String, CourseSection>();
		for(CourseSection section: sections){
			existMap.put(section.getTitle(), section);
		}
		List<String>resultList = new ArrayList<String>();
		for(String sectionName : sectionMap.values()){
			if(existMap.get(sectionName) == null){
				// add Section
				resultList.add(sectionName);
			}
		}
		return resultList;
	}
	
	public Map getEnteredCourseGrade(String siteId){
		return gradebookService.getEnteredCourseGrade(siteId);
	}

	public User getUserByEid(String eid){
		try {
			return userDirectoryService.getUserByEid(eid);
		} catch (UserNotDefinedException e) {
			log.debug(e.getMessage());
		}
		return null;
	}

	public Group addGroup(Site site, String title, String property){
		Group group = null;
		try {
	        group= site.addGroup();
	        group.getProperties().addProperty(property, Boolean.TRUE.toString());
			group.setTitle(title);
			group.setDescription(createGroupDescription(site, title));
			siteService.save(site);
		} catch (IdUnusedException e) {
			log.debug(e.getMessage());
		} catch (PermissionException e) {
			log.debug(e.getMessage());
		}
		return group;
	}

	public List<User>  getSearchExternalUsers(String criteria){
		return userDirectoryService.searchExternalUsers(criteria, -1, -1);
	}
	
	private String createGroupDescription(Site site, String title){
		String desp = "";
		String siteTitle = site.getTitle();
		if(siteTitle != null && siteTitle.length()>0){
			desp = desp + siteTitle + ",";
		}
		if(title != null && title.length()>0){
			desp = desp + title ;
		}
		return desp;
	}
	
	public void saveSite(Site site){
		try {
			siteService.save(site);
		} catch (IdUnusedException e) {
			log.debug(e.getMessage());
		} catch (PermissionException e) {
			log.debug(e.getMessage());
		}
	}
	
	protected List<String> generateTaNames(List<ParticipationRecord> tas) {
		// Generate the string showing the TAs
		List<String> taNames = new ArrayList<String>();
		for(Iterator taIter = tas.iterator(); taIter.hasNext();) {
			ParticipationRecord ta = (ParticipationRecord)taIter.next();
			taNames.add(ta.getUser().getSortName());
		}

		Collections.sort(taNames);
		return taNames;
	}

	protected List<String> generateTaUids(List<ParticipationRecord> tas) {
		List<String> taUids = new ArrayList<String>();
		for(Iterator<ParticipationRecord> iter = tas.iterator(); iter.hasNext();) {
			taUids.add(iter.next().getUser().getUserUid());
		}
		return taUids;
	}
	
	@Override
	public void dropSectionMembership(String userEid, String sectionId) {
		User user = getUserByEid(userEid);
		if (user != null) {
			sectionManager.dropSectionMembership(user.getId(), sectionId);
		}
	}
	
	@Override
	public String getUserUid(String userEid) {
		try {
			return userDirectoryService.getUserId(userEid);
		} catch (UserNotDefinedException e) {
			log.debug(e.getMessage());
		}
		return null;
	}
	
	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
        if (rb == null)
            rb = new ResourceLoader(TRAINING_BUNDLE);

	}
	
	@Getter @Setter
	private ToolManager toolManager;
	
	@Getter @Setter
	private SessionManager sessionManager;
	
	@Getter @Setter
	private UserDirectoryService userDirectoryService;
	
	@Getter @Setter
	private SecurityService securityService;
	
	@Getter @Setter
	private EventTrackingService eventTrackingService;
	
	@Getter @Setter
	private ServerConfigurationService serverConfigurationService;
	
	@Getter @Setter
	private SiteService siteService;
	
	@Getter @Setter
	private SectionManager sectionManager;

	@Getter @Setter
	private GradebookService gradebookService;
	
	@Getter @Setter
	private CourseManagementService courseManagementService;
}
