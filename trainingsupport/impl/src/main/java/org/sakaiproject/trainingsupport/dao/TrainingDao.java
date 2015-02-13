package org.sakaiproject.trainingsupport.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.sakaiproject.genericdao.api.GeneralGenericDao;

import org.sakaiproject.trainingsupport.bean.TrainingJobTitleBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.model.TrainingJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingRosterDiff;
import org.sakaiproject.trainingsupport.model.TrainingRosterJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingSectionOrder;
import org.sakaiproject.trainingsupport.model.TrainingStatus;

public interface TrainingDao  extends GeneralGenericDao{
	public List<TrainingStatus> findStatuses(final String siteId, final String sectionId);
	public List<TrainingStatus> findStatusesBySiteId(final String siteId);
	public List<TrainingStatus> findStatusesByUserEid(final String siteId, final String userEid);
	public List<TrainingRoster> findTrainingRostersBySectionId(final String sectionId);
	public TrainingRoster findTrainingRosterBySectionId(final String sectionId);
	public List<TrainingStatus> findStatusesByOnlyUserEid(final String userEid);
	public Date findLastUpadteDateOfStatusBySiteId(final String siteId);
	public TrainingSectionOrder findSectionOrderBySectionId(final String sectionId);
	public List<TrainingSectionOrder> findSectionOrderBySiteId(final String siteId);
	public List<TrainingJobTitle> findJobTitleByPattern(final String jobTitlePattern);
	public TrainingRosterJobTitle findRosterJobTitleBySectionId(String sectionId,String siteId);
	public int deleteRosterDiff(String siteId);
	public int deleteOutdatedRosterBase(String siteId);
	public List<TrainingRosterDiff> findRosterDiffBySection(String sectionId);
	public int createRosterDiff(String siteId, long currentVersion);
	public List<TrainingStatus> getAddDeleteList(
			String currentSiteId, String sectionId, String listType);
	public TrainingJobTitle findJobTitleById(long id);
	public int countRosterList(String siteId, String sectionId, String listType);
}
