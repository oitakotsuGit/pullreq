package org.sakaiproject.trainingsupport.logic;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.trainingsupport.bean.TrainingJobTitleBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.bean.TrainingStatusBean;
import org.sakaiproject.trainingsupport.model.Item;
import org.sakaiproject.trainingsupport.model.RosterShelfRegistration;
import org.sakaiproject.trainingsupport.model.TrainingJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingJobTitleByPattern;
import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingRosterJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingSectionOrder;
import org.sakaiproject.trainingsupport.model.TrainingStatus;
import org.sakaiproject.trainingsupport.model.TrainingTotal;
import org.sakaiproject.trainingsupport.model.TrainingTotalsBySection;

/**
 * An example logic interface
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface ProjectLogic {

	public SakaiProxy getSakaiProxy();

	public List<String> getTitlePattern();
	public List<Item> getTitlePatternMenu();
	
	/**
	 * Get a list of Items
	 * @return
	 */
	public List<Item> getItems();
	
	/**
	 * Get current User's training status in the current Site.
	 * @return
	 */
	public TrainingStatus getCurrentTrainingStatus();	
	
	/**
	 * Get current User's training status.
	 * @return
	 */
	public List<TrainingStatusBean> getVisibleTrainingStatus();
	public TrainingStatus getStatusByUserEid(String siteId, String userEid);
	public List<TrainingJobTitle> getJobTitles();
	public List<TrainingJobTitle> getJobTitleByPattern(String pattern);
	public List<TrainingJobTitleBean> getJobTitlesBean();
	public List<TrainingJobTitleBean> getJobTitlesBean(String pattern);
	public boolean addJobTitle(TrainingJobTitle jobTitle);
	public boolean deleteJobTitle(String id);
	public TrainingRoster getTrainingRosterInfo(String siteId, String sectionId);	
	public List<TrainingSectionRosterBean> getSectionRoster(String siteId, String sectionId);
	public List<TrainingSectionRosterBean> getSectionRoster(String siteId, String sectionId, String listType);
	public List<TrainingSectionRosterBean> getTargetRosterByJobTitle(String siteId, String sectionId, String pattern, String[] jobTitles);
	public List<TrainingJobTitle> getUnregisteredTitlesFromLdap();
	public TrainingSectionRosterBean getTargetUserByEid(String userEid, String siteId);
	public TrainingSectionRosterBean getTargetUserByEid(String userEid, String siteId, String listType);
	public void updateRoster(List<TrainingSectionRosterBean> rosterList, String role, String siteId, String sectionId, boolean createRosterFlg);
	
	/**
	 * Update Training Status for specified Site.
	 * @param siteId
	 * @return
	 */
	public boolean updateStatus(String siteId);
	
	/**
	 * Get Last Date of Status Updated for specified Site.
	 * @param siteId
	 * @return
	 */
	public Date getLastDateOfStatusUpdated(String siteId);

	/**
	 * Totalize all Section's Statuses in current site.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrainingTotalsBySection> totalize(String pattern, Date startDate, Date endDate);
	
	/**
	 * Totalize Statuses in current site.
	 * @param pattern
	 * @param sectionId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrainingTotal> totalize(String pattern, String sectionId, Date startDate, Date endDate);
	/**
	 * Totalize Statuses.
	 * @param siteId
	 * @param sectionUuid
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrainingTotal> totalize(String siteId, String sectionId, String pattern, Date startDate, Date endDate);
	
	public List<TrainingSectionBean> getSections(String siteId, String taId);
	public List<TrainingSectionBean> addAutoSections(String siteId);
	public boolean addSectionOrder(TrainingSectionOrder sectionOrder);
	public boolean updateSectionOrder(String siteId, String sectionId, String order);
	public void deleteSectionOrder(String[]  sectionIdList);

	public TrainingSectionBean getSelectedSection(String sectionId, List<TrainingSectionBean> sectionList);

	public boolean existSiteProperty(String siteId);
	public boolean updateSiteProperty(String siteId,String autosetting);
	public boolean updateRosters(String siteId);
	public boolean addToEnrolledList(String siteId, String userEid);
	public boolean removeFromRemovedList(String siteId, String userEid);
	public boolean addToRemovedList(String siteId, String userEid);
	public void setJobTitleBySectionId(TrainingRosterJobTitle trainingRosterJobTitle,String sectionId,String[] checkedValues,String siteId);
	public void updateUserStatus(String userEid, int statusCode, Date date, String note);
	public int countRosterList(String siteId, String sectionId, String listType);
	public List<RosterShelfRegistration> getjobTitleBySectionIdSiteId(Item selectedItem,List<TrainingSectionBean> sections,String siteId,String selectedPatternId);
	public List<RosterShelfRegistration> getjobTitleBySelectedSectionId(List<RosterShelfRegistration> jobTitlesBySection,Item selectedItem,String SelectedSectionId,String siteId,String selectedPatternId);
	public List<RosterShelfRegistration> updateView(List<RosterShelfRegistration> jobTitlesBySection,List<TrainingSectionBean> sections,String siteId);
}
