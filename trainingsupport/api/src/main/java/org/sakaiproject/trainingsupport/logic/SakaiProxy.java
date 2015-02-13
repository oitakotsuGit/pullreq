package org.sakaiproject.trainingsupport.logic;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;
import org.sakaiproject.section.api.coursemanagement.Course;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionTABean;

/**
 * An interface to abstract all Sakai related API calls in a central method that can be injected into our app.
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface SakaiProxy {

	/**
	 * Get current siteid
	 * @return
	 */
	public String getCurrentSiteId();
	
	/**
	 * Get current user id
	 * @return
	 */
	public String getCurrentUserId();
	
	/**
	 * Get current user display name
	 * @return
	 */
	public String getCurrentUserDisplayName();
	
	/**
	 * Is the current user a superUser? (anyone in admin realm)
	 * @return
	 */
	public boolean isSuperUser();
	
	/**
	 * Post an event to Sakai
	 * 
	 * @param event			name of event
	 * @param reference		reference
	 * @param modify		true if something changed, false if just access
	 * 
	 */
	public void postEvent(String event,String reference,boolean modify);
	
	/**
	 * Wrapper for ServerConfigurationService.getString("skin.repo")
	 * @return
	 */
	public String getSkinRepoProperty();
	
	/**
	 * Gets the tool skin CSS first by checking the tool, otherwise by using the default property.
	 * @param	the location of the skin repo
	 * @return
	 */
	public String getToolSkinCSS(String skinRepo);
	
	/**
	 * Get SectionCategory CatCode
	 * @return
	 */
	public String getSectionCategoryCatCode();
	
	/**
	 * Gets to role of current user in the current site.
	 * @return
	 */
	public String getCurrentUserRole();
	
	/**
	 * Get current User
	 * @return
	 */
	public User getCurrentUser();
	
	/**
	 * Get current CourseUuid
	 * @return
	 */
	public String getCourseUuid();
	
	/**
	 * Get current Course
	 * @return
	 */
	
	public Course getCourse();
	
	/**
	 * Get Site
	 * @param siteId
	 * @return
	 */
	public Site getSite(String siteId);
	
	/**
	 * Get site list
	 * @param type
	 * @return
	 */
	public List<Site> getSites(String type);
	
	/**
	 * Get Section List
	 * @param siteId
	 * @param ta'sId
	 * @return
	 */
	public List<TrainingSectionBean> getSections(String siteId, String userId);
	
	/**
	 * Get Section List By SectionIds
	 * @param sectionIds
	 * @return
	 */
	public List<TrainingSectionBean> getSectionsBySectionIds(String[] sectionIds);
	
	/**
	 * Get Section By SectionId
	 * @param sectionId
	 * @return
	 */
	public TrainingSectionBean getSectionBySectionId(String sectionId);

	/**
	 * Get Section name
	 * @param sectionId
	 * @return
	 */
	public String getSectionName(String sectionId);
	
	/**
	 * isDuplicateSectionTitle
	 * @param sectionName
	 * @param siteId
	 * @return
	 */
	public boolean isDuplicateSectionTitle(String sectionName, String siteId);
	
	/**
	 * Add Section
	 * @param sectionName
	 * @return
	 */
	public String addSection(String sectionName);
	
	/**
	 * Update Section
	 * @param sectionId
	 * @param sectionName
	 */
	public void updateSection(String sectionId, String sectionName);
	
//	/**
//	 * Delete Sections
//	 * @param ids
//	 */
//	public void delSections(Set<String> ids);
//	
//	/**
//	 * Delete Section
//	 * @param sectionId
//	 */
//	public void delSection(String sectionId);
	public void delSections(String[] sectionIds);
	/**
	 * Get section's TA
	 * @param sectionId
	 * @return
	 */
	public List<TrainingSectionTABean> getSectionTAs(String sectionId);
	
	/**
	 * Get User By Eid
	 * @param userEid
	 * @param sectionId
	 * @return
	 */
	public TrainingSectionTABean getTABeanByEid(String userEid, String sectionId);
	
	/**
	 * Get User role
	 * @param userUid
	 * @param siteId
	 * @return
	 */
	public String getUserRole(String userUid, String siteId);
	
	/**
	 * exist TA in the Section
	 * @param sectionId
	 * @param userId
	 * @return
	 */
	public boolean isSectionTA(String sectionId, String userId);
	
	public void addSiteMemberTA(String userUid, String roleId, String siteId, String sectionId);
	public void addSectionMembershipTA(String userUid, String sectionId);
	
	/**
	 * delete section's TA
	 * @param siteId
	 * @param sectionId
	 * @param ids
	 */
	public void deleteSectionTAs(String siteId, String sectionId, String ids[]);
	public void removeSiteMember(String userUid, String siteId);
	public TrainingSectionRosterBean getTargetUserByEid(String userEid);
	public List<TrainingSectionRosterBean> getSectionRoster(String sectionId);
	public Set<Member> getSiteMember(String siteId);
	public List<TrainingSectionRosterBean> getTargetRosterByJobTitle(String siteId, String sectionId, String pattern, String[] jobTitles);
	public boolean availableAddRoster(String siteId, String userUid, CourseSection courseSection);
	public void updateRoster(List<TrainingSectionRosterBean> rosterList, String role, String siteId, String sectionId, String courseId);

	public List<String> getUnregisteredSectionsNameFromLdap(String siteId);
	public List<String> getUnregisteredSectionsFromCM(String siteId);
	
	/**
	 * Get CourseCrade for site
	 * @param siteId
	 * @return
	 */
	public Map getEnteredCourseGrade(String siteId);
	
	/**
	 * get User by EID
	 * @param eid
	 * @return
	 */
	public User getUserByEid(String eid);
	
	/**
	 * Add Group
	 * @param siteId
	 * @param title
	 * @param property
	 * @return
	 */
	public Group addGroup(Site site, String title, String property);
	
	/**
	 * Save site
	 * @param site
	 */
	public void saveSite(Site site);
	
	/**
	 * get users from ldap
	 * @param criteria
	 * @return
	 */
	public List<User>  getSearchExternalUsers(String criteria);
	
	public void dropSectionMembership(String userEid, String sectionId);
	public void addSectionMembershipStudent(String userUid, String sectionId);
	public String getUserUid(String userEid);
	public void addSiteMemberAsStudent(String userUid, String siteId);
}
