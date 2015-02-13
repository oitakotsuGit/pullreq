package org.sakaiproject.trainingsupport.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.genericdao.api.finders.ByPropsFinder;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.trainingsupport.bean.TrainingJobTitleBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.bean.TrainingStatusBean;
import org.sakaiproject.trainingsupport.dao.TrainingDao;
import org.sakaiproject.trainingsupport.model.Item;
import org.sakaiproject.trainingsupport.model.RosterShelfRegistration;
import org.sakaiproject.trainingsupport.model.TrainingJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingRosterBase;
import org.sakaiproject.trainingsupport.model.TrainingRosterDiff;
import org.sakaiproject.trainingsupport.model.TrainingRosterJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingSectionOrder;
import org.sakaiproject.trainingsupport.model.TrainingStatus;
import org.sakaiproject.trainingsupport.model.TrainingTotal;
import org.sakaiproject.trainingsupport.model.TrainingTotalsBySection;
import org.sakaiproject.trainingsupport.util.Constant;
import org.sakaiproject.trainingsupport.util.TrainingJobtitleComparator;
import org.sakaiproject.trainingsupport.util.TrainingSectionComparator;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.ResourceLoader;

/**
 * Implementation of {@link ProjectLogic}
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public class ProjectLogicImpl implements ProjectLogic {

	private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);
	private static final int STATUS_ACCEPT = 1;
	private static final int STATUS_REJECT = 0;
	private static final int STATUS_NO_STUDENT = -1;
	private static final String PASS_STRING = "P";
	
	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";
	
	@Setter
	@Getter
    private static ResourceLoader rb = null;	

	@Setter
	@Getter
	private SakaiProxy sakaiProxy;

	@Setter
	@Getter
	private TrainingDao dao;

	@Setter
	@Getter
	private List<String> titlePattern;

	public List<Item> getTitlePatternMenu(){
		List<Item> titlePatternMenu = new ArrayList<Item>();
		int i=0;
		titlePatternMenu.add(new Item(i,Constant.TITLE_ALL_LABEL)) ;
		for(String pattern: titlePattern){
			i++;
			titlePatternMenu.add(new Item(i,pattern));
			// titlePatternMenu.add(new Item(i, rb.getString("pattern_name" + i)));
		}
		return titlePatternMenu;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Item> getItems() {
		
		List<Item> items = new ArrayList<Item>();
		
		items.add(new Item(1,"hello"));
		items.add(new Item(2,"world"));
		
		return items;
		
	}

	public TrainingStatus getCurrentTrainingStatus(){
		try{
			String userEid = sakaiProxy.getCurrentUser().getEid();
			String siteId = sakaiProxy.getCurrentSiteId();
			TrainingStatus trainingStatus = getStatusByUserEid(siteId, userEid);
			return trainingStatus;
		}catch(Exception e){
			log.debug(e.getMessage());
		}
		return null;
	}

	public List<TrainingStatusBean> getVisibleTrainingStatus(){
		try{
			User user = sakaiProxy.getCurrentUser();
			List<TrainingStatus> trainingStatusList = null;
			trainingStatusList = dao.findStatusesByOnlyUserEid(user.getEid());
			HashMap<String, TrainingStatus> map = new HashMap<String, TrainingStatus>();
			if( trainingStatusList != null){
				for(TrainingStatus trainingStatus : trainingStatusList){
					map.put(trainingStatus.getTrainingRoster().getSakaiSiteId(), trainingStatus);
				}
			}
			List<Site> sites = sakaiProxy.getSites(Constant.TRAINING_SITE_TYPE);
			ArrayList<TrainingStatusBean> resultList = new ArrayList<TrainingStatusBean>();
			if( sites != null ){
				for(Site site : sites){
					TrainingStatus trainingStatus = map.get(site.getId());
					Role userRole = site.getUserRole(user.getId());
					if(! userRole.isAllowed(Constant.STUDENT_MARKER)){
						resultList.add(new TrainingStatusBean(null, site.getId(), site.getTitle(), STATUS_NO_STUDENT, getStatusDisplayString(site.getId()),null));
					}else if(trainingStatus == null){
						resultList.add(new TrainingStatusBean(null, site.getId(), site.getTitle(), STATUS_REJECT, null, null));
					}else{
						resultList.add(
								new TrainingStatusBean(trainingStatus.getId(), site.getId(),site.getTitle(),
										trainingStatus.getStatus(),null, trainingStatus.getStatusUpdateDateString()));
					}
				}
			}
			return resultList;
		}catch (Exception e){
			log.debug(e.getMessage());
		}
		return null;
	}

	private String getStatusDisplayString(String siteId){
		List<TrainingTotal> statusList = totalize(siteId, null, Constant.SECTION_ALL, null, null);
		if( statusList == null || statusList.isEmpty()){
			return "-";
		}
		TrainingTotal total = statusList.get(0);
		return total.getStatusDisp(); 
	}
	
	public TrainingStatus getStatusByUserEid(String siteId, String userEid){
		List<TrainingStatus> l = dao.findStatusesByUserEid(siteId, userEid);
	    if( l == null || l.size()<1){
	    	return null;
	    }
	    return l.get(0);
	}
	
	private TrainingStatus getStatusByUserEid(String siteId, String userEid, String listType){
		List<TrainingStatus> l = dao.findStatusesByUserEid(siteId, userEid);
		if (l != null){
			for (TrainingStatus status : l) {
				if (StringUtils.equals(listType, status.getListType())) {
					return status;
				}
			}
		}
		return null;
	}

	public String getEnrollmentStatusByUserEid(String siteId, String userEid){
		List<TrainingStatus> l = dao.findStatusesByUserEid(siteId, userEid);
	    if( l == null || l.size()<1){
	    	return null;
	    }
	    return l.get(0).getEnrollmentStatus();
	}
	
	public List<TrainingJobTitle> getJobTitles() {
		List<TrainingJobTitle> l = dao.findAll(TrainingJobTitle.class);
	    if( l == null || l.size()<1){
	    	return null;
	    }
		Collections.sort(l, new TrainingJobtitleComparator());
	    return l;
	}
	
	/**
	 * get job titles order by pattern, viewRank
	 */
	public List<TrainingJobTitle> getJobTitleByPattern(String pattern){
		List<String> patternList = new ArrayList<String>();
		patternList.add(pattern);
		if(pattern == null || pattern.isEmpty() ||(! titlePattern.contains(pattern))){
			patternList = getTitlePattern();
		}
		List<TrainingJobTitle> resultList = new ArrayList<TrainingJobTitle>();
		for(String p1 : patternList){
			List<TrainingJobTitle> titles = dao.findJobTitleByPattern(p1);
			Collections.sort(titles, new TrainingJobtitleComparator());
			resultList.addAll(titles);
		}
		return resultList;
	}
	
	public List<TrainingJobTitleBean> getJobTitlesBean() {
		return getJobTitlesBean(null);
	}

	public List<TrainingJobTitleBean> getJobTitlesBean(String pattern) {
		List<TrainingJobTitle> l = null;
		if( pattern == null ){
			l = getJobTitles();
		}else{
			l = getJobTitleByPattern(pattern);
		}
		if(l == null || l.size() < 1){
			return null;
		}
	    List<TrainingJobTitleBean> trainingJobTitleBeanList= new ArrayList<TrainingJobTitleBean>();
	    for( TrainingJobTitle trainingJobTitle: l){
		    TrainingJobTitleBean trainingJobTitleBean = new TrainingJobTitleBean(trainingJobTitle);
		    trainingJobTitleBeanList.add(trainingJobTitleBean);
	    }
	    return trainingJobTitleBeanList;
	}

	public boolean addJobTitle(TrainingJobTitle jobTitle){
		try{
			Search search = null;
			if( jobTitle.getJobTitlePattern() == null || jobTitle.getJobTitlePattern().isEmpty() ){
				search = new Search(new String[] {"jobTitle"}, new Object[] {jobTitle.getJobTitle()});
			}else{
				search = new Search(new String[] {"jobTitle","jobTitlePattern"}, new Object[] {jobTitle.getJobTitle(),jobTitle.getJobTitlePattern()});
			}
			TrainingJobTitle existObj = dao.findOneBySearch(TrainingJobTitle.class, search);
			if(existObj == null){
				dao.save(jobTitle);
			}else{
				jobTitle.setId(existObj.getId());
				dao.update(jobTitle);
			}
		}catch(Exception e){
			log.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean deleteJobTitle(String id){
		try{
			dao.delete(TrainingJobTitle.class, Long.parseLong(id));
		}catch(Exception e){
			log.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	public TrainingRoster getTrainingRosterInfo(String siteId, String sectionId) {
		List<TrainingRoster> l = 
			dao.findByProperties(TrainingRoster.class, 
					new String[]{"sakaiSiteId", "sakaiSectionId"}, 
					new Object[]{siteId, sectionId}, new int[] {ByPropsFinder.EQUALS, ByPropsFinder.EQUALS}, new String[] {"sakaiSiteId"+ByPropsFinder.ASC, "sakaiSectionId"+ByPropsFinder.DESC}); // fixed by Shoji Kajita 2014/04/19shu
	    if( l == null || l.size()<1){
	    	return null;
	    }
	    return l.get(0);
	}

	public List<TrainingSectionRosterBean> getSectionRoster(String siteId, String sectionId){
		List<TrainingSectionRosterBean> rosterList = sakaiProxy.getSectionRoster(sectionId);
		List<TrainingRoster> trainingRosterList = dao.findTrainingRostersBySectionId(sectionId);
		return getRoster(rosterList, trainingRosterList);
	}
	
	public List<TrainingSectionRosterBean> getTargetRosterByJobTitle(String siteId, String sectionId, String pattern, String[] jobTitles){
		List<TrainingSectionRosterBean> rosterList = sakaiProxy.getTargetRosterByJobTitle(siteId, sectionId, pattern,jobTitles);
		List<TrainingRoster> trainingRosterList = 
			dao.findByProperties(TrainingRoster.class, 
					new String[]{"sakaiSiteId"}, 
					new Object[]{siteId});
		return getRoster(rosterList, trainingRosterList);
	}
	
	@Override
	public List<TrainingSectionRosterBean> getSectionRoster(String siteId, String sectionId, String listType){
		if (Constant.LIST_TYPE_ENROLLED.equals(listType)) {
			return getSectionRoster(siteId, sectionId);
		}
		if (!Constant.LIST_TYPE_ADDED.equals(listType) && !Constant.LIST_TYPE_REMOVED.equals(listType)) {
			throw new IllegalArgumentException("invalid list type: " + listType);
		}
		List<TrainingSectionRosterBean> result = new ArrayList<TrainingSectionRosterBean>();
		List<TrainingStatus> trainingStatusList = dao.getAddDeleteList(siteId, sectionId, listType);
		for (TrainingStatus trainingStatus : trainingStatusList) {
			TrainingSectionRosterBean trainingSectionRosterBean = getTargetUserByEid(trainingStatus.getSakaiEid(), siteId, listType);
			result.add(trainingSectionRosterBean);
		}
		return result;
	}
	
	public List<TrainingJobTitle> getUnregisteredTitlesFromLdap(){
		List<TrainingJobTitle>resultList = new ArrayList<TrainingJobTitle>();
		List<User> users = sakaiProxy.getSearchExternalUsers("");
		List<TrainingJobTitle>existList = getJobTitles();
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		// create existTitle Map
		for(TrainingJobTitle obj: existList){
			putTitleMap(map, obj.getJobTitlePattern(), obj.getJobTitle());
		}// end
		// create unregisterd title Map
		Map<String, Map<String, String>> map_new = new HashMap<String, Map<String, String>>();
		for(User user: users){
			ResourceProperties resourceProperties = user.getProperties();						
			for(String p1 : titlePattern){
				String val1 = resourceProperties.getProperty(p1);
				if( val1 == null || val1.isEmpty()){
					continue;
				}
				String existVal = getTitleMap(map, p1, val1);
				if( existVal == null ){
					putTitleMap(map_new, p1, val1);
				}
			}
		}// end
		// create unregistered TrainingJobTitle List
		for(String p1 : titlePattern){
			Map<String, String>map1 = map_new.get(p1);
			if( map1 == null){
				continue;
			}
			for(String val1 : map1.keySet()){
				TrainingJobTitle item = new TrainingJobTitle();
				item.setJobTitle(val1);
				item.setJobTitlePattern(p1);
				resultList.add(item);
			}
		}// end
		return resultList;
	}

	private void putTitleMap(Map<String, Map<String, String>> map,String param1, String param2){
		Map<String, String> map_d = map.get(param1);
		if(map_d == null){
			map_d = new HashMap<String, String>();
		}
		map_d.put(param2,param2);
		map.put(param1,map_d);
	}
	
	private String getTitleMap(Map<String, Map<String, String>>map, String param1, String param2){
		Map<String, String> map_d = map.get(param1);
		if( map_d == null ){
			return null;
		}
		return map_d.get(param2);
	}
	
	private List<TrainingSectionRosterBean> getRoster(List<TrainingSectionRosterBean> rosterList, List<TrainingRoster> trainingRosterList){
		if(rosterList != null && rosterList.size()>0){
			HashMap<String, TrainingStatus> trainingStatusMap = new HashMap<String, TrainingStatus>();
			for( TrainingRoster trainingRoster: trainingRosterList){
				Set<TrainingStatus> trainingStatusSet = trainingRoster.getTrainingStatuses();
				if(trainingStatusSet != null && trainingStatusSet.size()>0){
					for(TrainingStatus trainingstatus : trainingStatusSet){ // Here, overwrite status randomly. This must be fixed. BUG BUG! Shoji Kajita 2014/04/16
						trainingStatusMap.put(trainingstatus.getSakaiEid(), trainingstatus);
					}
				}
			}
			for(TrainingSectionRosterBean roster : rosterList) {
				TrainingStatus trainingStatus = trainingStatusMap.get(roster.getUserEid());
				roster.setStatus(STATUS_REJECT);
				if(trainingStatus != null){
					roster.setStatus(trainingStatus.getStatus());
					roster.setEnrollmentStatus(trainingStatus.getEnrollmentStatus());
					roster.setUpdateDate(trainingStatus.getStatusUpdateDateString());
					roster.setListType(trainingStatus.getListType());
					roster.setListTypeUpdateDate(trainingStatus.getListTypeUpdateDate());
				}
			}			
		}
		return rosterList;
	}
	
	public TrainingSectionRosterBean getTargetUserByEid(String userEid, String siteId){
		return getTargetUserByEid(userEid, siteId, Constant.LIST_TYPE_ENROLLED);
	}
	
	@Override
	public TrainingSectionRosterBean getTargetUserByEid(String userEid, String siteId, String listType){
	    TrainingSectionRosterBean trainingSectionRosterBean = sakaiProxy.getTargetUserByEid(userEid);
	    if( trainingSectionRosterBean == null){
	    	// no user
	    	return null;
	    }
	    TrainingStatus trainingStatus = getStatusByUserEid(siteId, userEid, listType);
	    trainingSectionRosterBean.setStatus(STATUS_REJECT);
	    if(trainingStatus != null){
		    trainingSectionRosterBean.setStatus(trainingStatus.getStatus());
		    trainingSectionRosterBean.setEnrollmentStatus(trainingStatus.getEnrollmentStatus());
		    trainingSectionRosterBean.setUpdateDate(trainingStatus.getStatusUpdateDateString());
		    trainingSectionRosterBean.setListType(trainingStatus.getListType());
		    trainingSectionRosterBean.setListTypeUpdateDate(trainingStatus.getListTypeUpdateDate());
		    trainingSectionRosterBean.setNote(trainingStatus.getNote());
	    }
	    return trainingSectionRosterBean;
	}


		
	public void updateRoster(List<TrainingSectionRosterBean> rosterList, String role, String siteId, String sectionId, boolean createRosterFlg){
		if(createRosterFlg){
			if(rosterList != null && rosterList.size() > 0){
				for(int i = 0; i < rosterList.size(); i++){
					TrainingSectionRosterBean bean = rosterList.get(i);
					if(!bean.isAvailableFlg()){
						rosterList.remove(i);
						i = i-1;
					}
				}
			}
		}
		TrainingSectionBean sectionBean = sakaiProxy.getSectionBySectionId(sectionId);
		List<TrainingSectionRosterBean> updateRosterList = new ArrayList<TrainingSectionRosterBean>();
		sakaiProxy.updateRoster(rosterList, role, siteId, sectionId, sectionBean.getSection().getCourse().getUuid());
		
		// TrainingRoster trainingRoster = getTrainingRosterInfo(siteId, sectionId);
		TrainingRoster trainingRoster = dao.findTrainingRosterBySectionId(sectionId);
		
		HashMap<String, TrainingStatus> preStatusMap = new HashMap<String, TrainingStatus>();
		if(trainingRoster == null){
			trainingRoster = new TrainingRoster();
			trainingRoster.setSakaiSiteId(siteId);
			trainingRoster.setSakaiSectionId(sectionId);
			trainingRoster.setInsertDate(new Date());
			trainingRoster.setUpdateDate(new Date());
			trainingRoster.setInsertUserId(sakaiProxy.getCurrentUser().getEid());
			trainingRoster.setTrainingStatuses(new HashSet<TrainingStatus>());
			updateRosterList = new ArrayList<TrainingSectionRosterBean>(rosterList);
		} else {
			Set<TrainingStatus> statusSet = trainingRoster.getTrainingStatuses();

			// save meta info at first
			trainingRoster.setUpdateDate(new Date());
			trainingRoster.setUpdateUserId(sakaiProxy.getCurrentUser().getEid());
			if(trainingRoster.getId() != null){
				dao.update(trainingRoster);
			}
			
			if(statusSet != null && statusSet.size()>0){
				for(TrainingStatus preStatus: statusSet){
					if(preStatus.getStatus() == 0 || preStatus.getStatus() == 1){   // fixed by Shoji Kajita 2014/04/06 Jira: CLS-5
						preStatusMap.put(preStatus.getSakaiEid(), preStatus);
					}
				}
				for(TrainingSectionRosterBean bean:rosterList){
					TrainingStatus status = preStatusMap.get(bean.getUserEid());
						if(status == null){
							updateRosterList.add(bean);
						} else if (bean.getEnrollmentStatus() != null && !bean.getEnrollmentStatus().equals(status.getEnrollmentStatus()))  {
							updateRosterList.add(bean);
							preStatusMap.remove(bean.getUserEid());
						} else {
							preStatusMap.remove(bean.getUserEid());
						}
				}
			}else{
				updateRosterList = new ArrayList<TrainingSectionRosterBean>(rosterList);
			}
		}

		// del
		if(preStatusMap != null && preStatusMap.size()>0) {
			Set<TrainingStatus> delSet = new HashSet<TrainingStatus>();
			for(TrainingStatus status: preStatusMap.values()){
				delSet.add(status);
				addToRemovedList(status.getTrainingRoster().getSakaiSiteId(), status.getSakaiEid());
			}
//			dao.deleteSet(delSet) ;
		}
		// add
		if(updateRosterList != null && updateRosterList.size()>0) {
			if(trainingRoster.getId() == null){
				dao.save(trainingRoster);
			}
			Set<TrainingStatus> updateSet = new HashSet<TrainingStatus>();
			for(TrainingSectionRosterBean roster : updateRosterList){
				List<TrainingStatus> existingStatuses =  dao.findStatusesByUserEid(siteId, roster.getUserEid());
				if (!existingStatuses.isEmpty()) {
					boolean addedToUpdateSet = false;
					for (TrainingStatus status : existingStatuses) {
						if (status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
							status.setEnrollmentStatus(roster.getEnrollmentStatus());
							addedToUpdateSet = true;
							updateSet.add(status);
						} else {
							if (addedToUpdateSet) {
								dao.delete(status);
							} else {
								status.setTrainingRoster(trainingRoster);
								status.setListType(Constant.LIST_TYPE_ENROLLED);
								status.setListTypeUpdateDate(new Date());
								updateSet.add(status);
								addedToUpdateSet = true;
							}
						}
					}
					continue;
				}
				TrainingStatus updateStatus = new TrainingStatus();
				updateStatus.setSakaiEid(roster.getUserEid());
				updateStatus.setTrainingRoster(trainingRoster);
				updateStatus.setEnrollmentStatus(roster.getEnrollmentStatus());
				updateStatus.setStatus(roster.getStatus());  // fixed by Shoji Kajita 2014/04/06 Jira: CLS-5
				updateStatus.setStatusUpdateDate(roster.getUpdateDate());  // fixed by Shoji Kajita 2014/04/06 Jira: CLS-5
				if (roster.getListType() == null) {
					updateStatus.setListType(Constant.LIST_TYPE_ENROLLED);
					updateStatus.setListTypeUpdateDate(new Date());
				} else {
					updateStatus.setListType(roster.getListType());
					updateStatus.setListTypeUpdateDate(roster.getListTypeUpdateDate());
				}
				updateSet.add(updateStatus);
			}
			dao.saveSet(updateSet);
		}
	}
	
	@Override
	public boolean addToEnrolledList(String siteId, String userEid) {
		List<TrainingStatus> trainingStatus =  dao.findStatusesByUserEid(siteId, userEid);
		for (TrainingStatus status : trainingStatus) {
			if (status.getListType().equals(Constant.LIST_TYPE_ADDED)) {
				// リスト区分を受講者に変更する
				status.setListType(Constant.LIST_TYPE_ENROLLED);
				status.setListTypeUpdateDate(new Date());
				dao.update(status);
				// セクションのメンバに登録する
				sakaiProxy.addSectionMembershipStudent(status.getSakaiEid(), status.getTrainingRoster().getSakaiSectionId());
			} else if (status.getListType().equals(Constant.LIST_TYPE_REMOVED)) {
				// 削除者リストからは削除
				dao.delete(status);
			}
		}
		return true;
	}
	
	@Override
	public boolean removeFromRemovedList(String siteId, String userEid) {
		List<TrainingStatus> trainingStatus =  dao.findStatusesByUserEid(siteId, userEid);
		boolean removeMemberFromSite = trainingStatus.size() == 1;
		// 削除者リストから削除する
		for (TrainingStatus status : trainingStatus) {
			if (status.getListType().equals(Constant.LIST_TYPE_REMOVED)) {
				dao.delete(status);
				// このサイトの他のどの部局にも登録されていなければ、サイトメンバからも削除
				if (removeMemberFromSite) {
					String userUid = sakaiProxy.getUserUid(userEid);
					sakaiProxy.removeSiteMember(userUid, siteId);
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean addToRemovedList(String siteId, String userEid) {
		List<TrainingStatus> trainingStatesus =  dao.findStatusesByUserEid(siteId, userEid);
		if (trainingStatesus.size() != 1) {
			return false;
		}
		
		TrainingStatus status = trainingStatesus.get(0);
		if (!status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
			return false;
		}
		status.setListType(Constant.LIST_TYPE_REMOVED);
		status.setListTypeUpdateDate(new Date());
		dao.update(status);
		return true;
	}
	
	/**
	 * update Training Status from course gradebook
	 */
	public boolean updateStatus(String siteId){
		try{
			Site site = sakaiProxy.getSite(siteId);
			List<Group> groups = getStatusGroups(site);
			Group acceptGroup = groups.get(0);
			Group nonAcceptGroup = groups.get(1);
			acceptGroup.removeMembers();
			nonAcceptGroup.removeMembers();
			Map<String ,String> courseGradeMap = sakaiProxy.getEnteredCourseGrade(siteId);
			if( courseGradeMap == null || courseGradeMap.isEmpty()){
				log.error("Gradebook data is not found:" + siteId);
				return true;
			}
			List<TrainingSectionBean> trainingSections = sakaiProxy.getSections(siteId,null);
			Set<TrainingStatus> updateSet = new HashSet<TrainingStatus>();
			boolean siteUpdateFlg = false;
			for(TrainingSectionBean trainingSection : trainingSections){
				TrainingRoster trainingRoster = getTrainingRosterInfo(siteId, trainingSection.getUuid());
				if(trainingRoster == null){
					continue;
				}
				Set<TrainingStatus> statusSet = trainingRoster.getTrainingStatuses();
				for(TrainingStatus status: statusSet){
					String userEid = status.getSakaiEid();
					User user = sakaiProxy.getUserByEid(userEid);
					String score = courseGradeMap.get(user.getDisplayId());
					if(PASS_STRING.equals(score)){
						if(status.getStatus() == STATUS_REJECT){
							status.setStatus(STATUS_ACCEPT);
							status.setStatusUpdateDate(new Date());
							updateSet.add(status);
						}
						// add acceptGroup
						nonAcceptGroup.removeMember(user.getId());
						acceptGroup.addMember(user.getId(), Constant.STUDENT_ROLE, true, false);
						siteUpdateFlg = true;
					}else{
						// add nonAcceptGroup
						acceptGroup.removeMember(user.getId());
						nonAcceptGroup.addMember(user.getId(),Constant.STUDENT_ROLE, true, false);
						siteUpdateFlg = true;
					}
				}
			}
			if(updateSet.size()>0){
				dao.saveSet(updateSet);
			}
			if(siteUpdateFlg){
				sakaiProxy.saveSite(site);
			}
			return true;
		}catch(GradebookNotFoundException e){
			log.error("Gradebook is not found:" + siteId);
		}catch(Exception e){
			log.error(e.getMessage());
		}
		return false;
	}
	
	public Date getLastDateOfStatusUpdated(String siteId){
		return dao.findLastUpadteDateOfStatusBySiteId(siteId);
	}

	public List<TrainingTotalsBySection> totalize(String pattern, Date startDate, Date endDate){
		String siteId = sakaiProxy.getCurrentSiteId();
		List<TrainingSectionBean> sectionList = getSections(siteId, null);
		List<TrainingTotalsBySection> resultList= new ArrayList<TrainingTotalsBySection>();
		
		if(sectionList != null && sectionList.size()>0){
			// resultList.add(new TrainingTotalsBySection(Constant.SECTION_ALL,totalize(siteId, pattern, Constant.SECTION_ALL,startDate,endDate)));
			for( TrainingSectionBean section : sectionList ){
				resultList.add(new TrainingTotalsBySection(section.getTitle(),totalize(siteId, pattern, section.getUuid(),startDate,endDate)));
			}		
		}
		return resultList;
	}
	
    public TrainingSectionBean getSelectedSection(String sectionId, List<TrainingSectionBean> sectionList){
        if(sectionId == null || sectionId.length()<1){
                return sectionList.get(0);
        }
        for(TrainingSectionBean section: sectionList){
                if(sectionId.equals(section.getUuid())){
                        return section;
                }
        }
        return sectionList.get(0);
    }
    
	public List<TrainingTotal> totalize(String pattern, String sectionId, Date startDate, Date endDate){
		String siteId = sakaiProxy.getCurrentSiteId();
		return totalize(siteId, pattern, sectionId, startDate, endDate);
	}

	public List<TrainingTotal> totalize(String siteId, String pattern, String sectionId, Date startDate, Date endDate){
		
		List<TrainingSectionRosterBean> rosterBeanList = getSectionRoster(siteId, sectionId);
		rosterBeanList.addAll(getSectionRoster(siteId, sectionId, Constant.LIST_TYPE_REMOVED));
		
		Map<String, Integer> all = new HashMap<String, Integer>();
		Map<String, Integer> done = new HashMap<String, Integer>();
		String title = Constant.PATTERN_ALL;
		
		for(TrainingSectionRosterBean rosterBean: rosterBeanList){
			
			if (rosterBean.getEnrollmentStatus() != null && rosterBean.getEnrollmentStatus().equals("required") != true)  {
				continue;
			}
			
			String eid = rosterBean.getUserEid();
			User user = sakaiProxy.getUserByEid(eid);
			if( user == null ){
				continue;
			}
			if( pattern != null){
				title = (String)user.getProperties().get(pattern);
			}

			Integer n1 = all.get(title);
			if(n1 == null){ n1 = 0; }
			all.put(title, n1+1);
			
			Integer n2 = done.get(title);
			if(rosterBean.getStatus() == STATUS_ACCEPT && rosterBean.isValidRangeUpdateDate(startDate,endDate)){
				if(n2 == null){ n2 = 0; }
				done.put(title,n2+1);
			}
		}
		List<TrainingTotal> result = new ArrayList<TrainingTotal>();
		for ( Iterator it = all.keySet().iterator(); it.hasNext();){
			String key = (String)it.next();
			TrainingTotal total = new TrainingTotal();
			total.setJobTitle(key);
			total.setNum(all.get(key));
			Integer doneNum = done.get(key);
			if(doneNum == null){
				total.setAcceptNum(0);
			}else{
				total.setAcceptNum(doneNum);
			}
			result.add(total);
		}
		return result;
	}
	
	public List<TrainingTotal> totalize_old(String siteId, String pattern, String sectionId, Date startDate, Date endDate){
		List<TrainingStatus> trainingStatusList = new ArrayList<TrainingStatus>();
		if(sectionId.equals(Constant.SECTION_ALL)){
			trainingStatusList = dao.findStatusesBySiteId(siteId);
		}else{
			trainingStatusList = dao.findStatuses(siteId, sectionId);
		}
		Map<String, Integer> all = new HashMap<String, Integer>();
		Map<String, Integer> done = new HashMap<String, Integer>();
		String title = Constant.PATTERN_ALL;
		for(TrainingStatus status: trainingStatusList){
			
			if (status.getEnrollmentStatus() != null && status.getEnrollmentStatus().equals("required") != true)  {
				continue;
			}
			
			String eid = status.getSakaiEid();
			User user = sakaiProxy.getUserByEid(eid);
			if( user == null ){
				continue;
			}
			if( pattern != null){
				title = (String)user.getProperties().get(pattern);
			}

			Integer n1 = all.get(title);
			if(n1 == null){ n1 = 0; }
			all.put(title, n1+1);
			
			Integer n2 = done.get(title);
			if(status.getStatus() == STATUS_ACCEPT && isValidRangeStatus(status,startDate,endDate)){
				if(n2 == null){ n2 = 0; }
				done.put(title,n2+1);
			}
		}
		List<TrainingTotal> result = new ArrayList<TrainingTotal>();
		for ( Iterator it = all.keySet().iterator(); it.hasNext();){
			String key = (String)it.next();
			TrainingTotal total = new TrainingTotal();
			total.setJobTitle(key);
			total.setNum(all.get(key));
			Integer doneNum = done.get(key);
			if(doneNum == null){
				total.setAcceptNum(0);
			}else{
				total.setAcceptNum(doneNum);
			}
			result.add(total);
		}
		return result;
	}


    private boolean isValidRangeStatus(TrainingStatus status, Date startDate, Date endDate){
            if(status.getStatusUpdateDate() == null){
                    return false;
            }
            if(startDate != null){
                    if(status.getStatusUpdateDate().compareTo(startDate)< 0){
                            return false;
                    }
            }
            if(endDate != null){
                    if(endDate.compareTo(status.getStatusUpdateDate()) < 0){
                            return false;
                    }
            }
            return true;
    }
	
	private List<Group> getStatusGroups(Site site){
		Collection allGroups = site.getGroups();
		ArrayList<Group> groups = new ArrayList<Group>(2);
		Group acceptGroup = null;
		Group nonAcceptGroup = null;
        for (Iterator gIterator = allGroups.iterator(); gIterator.hasNext();) {
            Group gNext = (Group) gIterator.next();
            String gProp = gNext.getProperties().getProperty(
            		SiteConstants.GROUP_PROP_WSETUP_CREATED);
            if (gProp != null && gProp.equals(Boolean.TRUE.toString())) {
            	if(Constant.ACCEPT_GROUP_NAME.equals(gNext.getTitle())){
            		acceptGroup = gNext;
            	}else if (Constant.NON_ACCEPT_GROUP_NAME.equals(gNext.getTitle())){
            		nonAcceptGroup = gNext;
            	}
            }
         }
        // if not exist , create group
        if(acceptGroup == null){
        	acceptGroup= sakaiProxy.addGroup(site, Constant.ACCEPT_GROUP_NAME, SiteConstants.GROUP_PROP_WSETUP_CREATED);
        }
        if(nonAcceptGroup == null){
        	nonAcceptGroup= sakaiProxy.addGroup(site, Constant.NON_ACCEPT_GROUP_NAME, SiteConstants.GROUP_PROP_WSETUP_CREATED);
        }
        groups.add(acceptGroup);
        groups.add(nonAcceptGroup);
        return groups;
	}

	// for SectionOrder
	public List<TrainingSectionBean> getSections(String siteId, String taId){
		List<TrainingSectionBean> sectionsList = sakaiProxy.getSections(siteId, taId);
		List<TrainingSectionOrder> orderList = dao.findSectionOrderBySiteId(siteId);
		Map<String,Integer> map = new HashMap<String, Integer>();
		for(TrainingSectionOrder order: orderList){
			map.put(order.getSectionId(), order.getViewRank());
		}
		for(TrainingSectionBean section: sectionsList){
			Integer rank = map.get(section.getUuid());
			if(rank != null){
				section.setViewRank(rank);
			}
		}
		//order鬆�↓荳ｦ縺ｳ譖ｿ縺医ｋ
		Collections.sort(sectionsList, new TrainingSectionComparator());
		return sectionsList;
	}

	public List<TrainingSectionBean> addAutoSections(String siteId){
		List<TrainingSectionBean> sectionsList = sakaiProxy.getSections(siteId, null);
		List<String>sections = sakaiProxy.getUnregisteredSectionsNameFromLdap(siteId);
		return sectionsList;
	}
	
	public boolean addSectionOrder(TrainingSectionOrder sectionOrder){
		try{
			TrainingSectionOrder existObj = dao.findSectionOrderBySectionId(sectionOrder.getSectionId());
			if(existObj == null){
				dao.save(sectionOrder);
			}else{
				return false;
			}
		}catch(Exception e){
			log.debug(e.getMessage());
			return false;
		}
		return true;
	}
	public boolean updateSectionOrder(String siteId, String sectionId, String order){
		try{
			TrainingSectionOrder existObj = dao.findSectionOrderBySectionId(sectionId);
			int orderNum = Integer.parseInt(order);
			if(existObj == null){
				TrainingSectionOrder sectionOrder = new TrainingSectionOrder(null, siteId, sectionId, orderNum);
				dao.save(sectionOrder);
			}else{
				existObj.setViewRank(orderNum);
				dao.update(existObj);
			}
		}catch(Exception e){
			log.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	public void deleteSectionOrder(String[] sectionIdList){
		try{
			Set<TrainingSectionOrder> objs = new HashSet<TrainingSectionOrder>();
			for(String sectionId: sectionIdList){
				TrainingSectionOrder existObj = dao.findSectionOrderBySectionId(sectionId);
				if(existObj != null ){
					objs.add(existObj);
				}
			}
			dao.deleteSet(objs);
		}catch(Exception e){
			log.debug(e.getMessage());
		}
		return;
	}
	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init()
	{
		if (rb == null)
			rb = new ResourceLoader(TRAINING_BUNDLE);
	}

	
	@Override
	public boolean updateRosters(String siteId) {
		System.out.println("update rosters.");
		try {
			// サイトに登録されている部局を取得する
			List<TrainingSectionBean> sections = sakaiProxy.getSections(siteId, null);
			long currentVersion = System.currentTimeMillis();
			
			// 名簿基本データの旧世代のデータを削除
			dao.deleteOutdatedRosterBase(siteId);
			
			// 名簿基本データ作成
			for (TrainingSectionBean section : sections) {
				TrainingRosterJobTitle rosterJobTitle = dao.findRosterJobTitleBySectionId(section.getUuid(),siteId);
				if (rosterJobTitle == null) {
					continue;
				}
				String[] jobTitles = new String[rosterJobTitle.getJobTitles().size()];
				int i = 0;
				for (TrainingJobTitle t : rosterJobTitle.getJobTitles()) {
					jobTitles[i++] = t.getJobTitle();
				}
				
				// LDAPから部局と職名を条件にユーザを取得する
				List<TrainingSectionRosterBean> users = sakaiProxy.getTargetRosterByJobTitle(
						siteId, section.getUuid(), rosterJobTitle.getJobTitlePattern(), jobTitles);
				
				// 取得したユーザを名簿基本データに登録する
				for (TrainingSectionRosterBean user : users) {
					ResourceProperties props = getSakaiProxy().getUserByEid(user.getUserEid()).getProperties();
					TrainingRosterBase b = new TrainingRosterBase();
					b.setSiteId(siteId);
					b.setSectionId(section.getUuid());
					b.setUserEid(user.getUserEid());
					b.setTitle(props.getProperty(Constant.LDAP_JOB_TITLE));
					b.setTitle1(props.getProperty(Constant.LDAP_JOB_TITLE1));
					b.setTitle2(props.getProperty(Constant.LDAP_JOB_TITLE2));
					b.setTitle3(props.getProperty(Constant.LDAP_JOB_TITLE3));
					b.setVersion(currentVersion);
					dao.save(b);
				}
			}
			
			// 名簿差分データ作成
			dao.deleteRosterDiff(siteId);
			dao.createRosterDiff(siteId, currentVersion);
			
			// 差分（名簿差分データ）を名簿に適用する．
			for (TrainingSectionBean section : sections) {
				List<TrainingRosterDiff> diffs = dao.findRosterDiffBySection(section.getUuid());
				for (TrainingRosterDiff diff : diffs) {
					final String type = diff.getDiffType();
					
					if (type.equals(Constant.ROSTER_DIFF_TYPE_ADDED)) {
						applyAddedDiff(siteId, diff);
					} else if (type.equals(Constant.ROSTER_DIFF_TYPE_REMOVED)) {
						applyRemovedDiff(siteId, diff);
					} else if (type.equals(Constant.ROSTER_DIFF_TYPE_TRANSFERRED)) {
						applyTransferredDiff(siteId, diff);
					} else if (type.equals(Constant.ROSTER_DIFF_TYPE_JOB_TITLE_CHANGED)) {
						applyChangeJobTitleDiff(siteId, diff);
					}
				}
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 差分（追加）を名簿に適用する．
	 */
	private void applyAddedDiff(String siteId, TrainingRosterDiff diff) {
		List<TrainingStatus> statuses = dao.findStatusesByUserEid(siteId, diff.getUserEid());
		TrainingStatus source = null;
		
		for (TrainingStatus status : statuses) {
			if (isSameSection(status, diff)) {
				if (status.getListType().equals(Constant.LIST_TYPE_ADDED)) {
					dao.delete(status);
					source = status;
				} else if (status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
					return;
				} else if (status.getListType().equals(Constant.LIST_TYPE_REMOVED)) {
					source = status;
				}
			} else {
				if (status.getListType().equals(Constant.LIST_TYPE_ADDED)) {
					// 他部局の追加者リストに登録されているユーザが別の部局の追加者になることはないはず
					throw new RuntimeException();
				} else if (status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
					changeListToRemoved(status);
					source = status;
				} else if (status.getListType().equals(Constant.LIST_TYPE_REMOVED)) {
					source = status;
				}
			}
		}
		addRosterAsAddedUser(diff, source);
	}
	
	/**
	 * 差分（削除）を名簿に適用する．
	 */
	private void applyRemovedDiff(String siteId, TrainingRosterDiff diff) {
		List<TrainingStatus> statuses = dao.findStatusesByUserEid(siteId, diff.getUserEid());
		
		for (TrainingStatus status : statuses) {
			if (isSameSection(status, diff)) {
				// 部局が同じ場合
				if (status.getListType().equals(Constant.LIST_TYPE_ADDED)) {
					// 追加者リストは削除
					dao.delete(status);
				} else if (status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
					// 受講者リストにいるときは削除者リストに移す
					changeListToRemoved(status);
				} else if (status.getListType().equals(Constant.LIST_TYPE_REMOVED)) {
					// 削除者リストにいるときは何もしない。
				}
			} else {
				// 別の部局に登録されている場合は何もしない。
			}
		}
	}
	
	/**
	 * 差分（部局異動）を名簿に適用する．
	 */
	private void applyTransferredDiff(String siteId, TrainingRosterDiff diff) {
		List<TrainingStatus> statuses = dao.findStatusesByUserEid(siteId, diff.getUserEid());
		TrainingStatus source = null;
		
		if (statuses.size() == 2) {
			// 追加者リストと削除者リストの両方にいる場合
			// 削除者リストから削除する
			// 追加者リストから削除者リストに移す
			for (TrainingStatus status : statuses) {
				if (status.getListType().equals(Constant.LIST_TYPE_ADDED)) {
					changeListToRemoved(status);
					source = status;
				} else if (status.getListType().equals(Constant.LIST_TYPE_REMOVED)) {
					dao.delete(status);
				} else {
					throw new RuntimeException();
				}
			}
		} else if (statuses.size() == 1) {
			TrainingStatus status = statuses.get(0);
			
			if (isSameSection(status, diff)) {
				if (status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
					// 対象部局の 受講者リストに既に登録済みの場合はなにもしない
					return;
				}
			} else {
				// 他部局の削除者リスト以外にいる場合は削除者リストへ
				if (!status.getListType().equals(Constant.LIST_TYPE_REMOVED)) {
					changeListToRemoved(status);
				}
			}
			source = status;
		}
		addRosterAsAddedUser(diff, source);
	}
	
	/**
	 * 差分（職名変更）を名簿に適用する．
	 */
	private void applyChangeJobTitleDiff(String siteId, TrainingRosterDiff diff) {
		List<TrainingStatus> statuses = dao.findStatusesByUserEid(siteId, diff.getUserEid());
		TrainingStatus source = null;
		
		for (TrainingStatus status : statuses) {
			if (isSameSection(status, diff)) {
				if (status.getListType().equals(Constant.LIST_TYPE_ADDED)) {
					dao.delete(status);
				} else if (status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
					changeListToRemoved(status);
				}
			} else {
				if (status.getListType().equals(Constant.LIST_TYPE_ADDED)) {
					dao.delete(status);
				} else if (status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
					changeListToRemoved(status);
				}
			}
			source = status;
			
		}
		addRosterAsAddedUser(diff, source);
	}
	
	private void changeListToRemoved(TrainingStatus status) {
		if (status.getListType().equals(Constant.LIST_TYPE_ENROLLED)) {
			sakaiProxy.dropSectionMembership(status.getSakaiEid(),
					status.getTrainingRoster().getSakaiSectionId());
		}
		status.setListType(Constant.LIST_TYPE_REMOVED);
		status.setListTypeUpdateDate(new Date());
		dao.update(status);
	}
	
	private boolean isSameSection(TrainingStatus status, TrainingRosterDiff diff) {
		String current = status.getTrainingRoster().getSakaiSectionId();
		String next = diff.getSectionId();
		return StringUtils.equals(current, next);
	}
	
	/**
	 * 追加者リストにユーザを登録する．
	 * @param diff 名簿差分データ
	 * @param source 受講ステータスのコピー元
	 */
	private void addRosterAsAddedUser(TrainingRosterDiff diff, TrainingStatus source) {
		TrainingRoster trainingRoster = getTrainingRosterInfo(diff.getSiteId(), diff.getSectionId());
		TrainingStatus trainingStatus = new TrainingStatus();
		trainingStatus.setTrainingRoster(trainingRoster);
		trainingStatus.setSakaiEid(diff.getUserEid());
		trainingStatus.setEnrollmentStatus(Constant.ENROLLMENT_STATUS_OPTIONAL);
		trainingStatus.setListType(Constant.LIST_TYPE_ADDED);
		trainingStatus.setListTypeUpdateDate(new Date());
		if (source != null) {
			trainingStatus.setStatus(source.getStatus());
			trainingStatus.setStatusUpdateDate(source.getStatusUpdateDate());
		}
		dao.save(trainingStatus);
		String userUid = sakaiProxy.getUserUid(diff.getUserEid());
		sakaiProxy.addSiteMemberAsStudent(userUid, diff.getSiteId());
	}
	
	private final String propertyName = "ldap-auto-setting";
	
	@Override
	public boolean existSiteProperty(String siteId) {
		Site site = getSakaiProxy().getSite(siteId);
		String prop = site.getProperties().getProperty(propertyName);
		return prop != null;
	}

	@Override
	public boolean updateSiteProperty(String siteId, String autosetting) {
		Site site = getSakaiProxy().getSite(siteId);
		if (autosetting.equals("yes")) {
			site.getPropertiesEdit().addProperty(propertyName, "1");
		} else {
			site.getPropertiesEdit().removeProperty(propertyName);
		}
		getSakaiProxy().saveSite(site);
		return true;
	}

	public void setJobTitleBySectionId(TrainingRosterJobTitle trainingRosterJobTitle,String sectionId,String[] checkedValues,String currentSiteId) {

		// hashにセットするリスト
		List<TrainingJobTitle> jobTitles = new ArrayList<TrainingJobTitle>();

		TrainingRosterJobTitle rosterJobTitle = dao.findRosterJobTitleBySectionId(sectionId,currentSiteId);

		if (rosterJobTitle == null) {
			// セクションごとに取得した職名ID分
			for (int i=0;i < checkedValues.length; i++) {
				TrainingJobTitle trainingJobTitle = dao.findJobTitleById(Long.parseLong(checkedValues[i]));
				jobTitles.add(trainingJobTitle);
			}
			trainingRosterJobTitle.setId(null);
			trainingRosterJobTitle.setSiteId(sakaiProxy.getCurrentSiteId());
			trainingRosterJobTitle.setSectionId(sectionId);
			Set<TrainingJobTitle> job = new HashSet<TrainingJobTitle>(jobTitles);
			trainingRosterJobTitle.setJobTitles(job);
			// insert
			dao.save(trainingRosterJobTitle);
		} else {

			Long[] jobIds = new Long[rosterJobTitle.getJobTitles().size()];
			int i = 0;
			// 登録済みの職名IDを配列に格納
			for (TrainingJobTitle t : rosterJobTitle.getJobTitles()) {
				jobIds[i++] = t.getId();
				jobTitles.add(t);
			}
			for (int k=0;k<checkedValues.length;k++) {
				// 登録済み職名ID一覧内にチェックした職名IDが含まれていない
				if (!Arrays.asList(jobIds).contains(Long.parseLong(checkedValues[k]))) {
					TrainingJobTitle trainingJobTitle = dao.findJobTitleById(Long.parseLong(checkedValues[k]));
					jobTitles.add(trainingJobTitle);
				}
			}
			trainingRosterJobTitle.setId(rosterJobTitle.getId());
			trainingRosterJobTitle.setSiteId(sakaiProxy.getCurrentSiteId());
			trainingRosterJobTitle.setSectionId(sectionId);
			Set<TrainingJobTitle> job = new HashSet<TrainingJobTitle>(jobTitles);
			trainingRosterJobTitle.setJobTitles(job);
			dao.update(trainingRosterJobTitle);
		}
	}
	
	@Override
	public void updateUserStatus(String userEid, int statusCode, Date statusUpdateDate, String note) {
		String siteId = sakaiProxy.getCurrentSiteId();
		List<TrainingStatus> statuses = dao.findStatusesByUserEid(siteId, userEid);
		for (TrainingStatus status : statuses) {
			status.setStatus(statusCode);
			if (statusCode == Constant.STATUS_ACCEPT) {
				status.setStatusUpdateDate(statusUpdateDate);
			} else if (statusCode == Constant.STATUS_REJECT) {
				status.setStatusUpdateDate((Date) null);
			}
			status.setManuallyUpdated(true);
			status.setManuallyUpdateDate(new Date());
			if (StringUtils.isBlank(note)) {
				status.setNote(null);
			} else {
				status.setNote(note);
			}
			dao.update(status);
		}
	}
	
	@Override
	public int countRosterList(String siteId, String sectionId, String listType) {
		return dao.countRosterList(siteId, sectionId, listType);
	}

	public List<RosterShelfRegistration> getjobTitleBySectionIdSiteId(Item selectedItem,List<TrainingSectionBean> sections,String siteId,String selectedPatternId){

		List<RosterShelfRegistration> jobTitlesBySection = new ArrayList<RosterShelfRegistration>();
		TrainingRosterJobTitle rosterJobTitle = new TrainingRosterJobTitle();
		TrainingJobTitle defaultJobTitle = new TrainingJobTitle();

		for (int i=0;i<sections.size();i++) {

			List<TrainingJobTitleBean> jobTitles = getJobTitlesBean(selectedItem.getName());

			rosterJobTitle = dao.findRosterJobTitleBySectionId(sections.get(i).getUuid(),siteId);

			List<TrainingJobTitle> sectionjobTitles = new ArrayList<TrainingJobTitle>();

			for (TrainingJobTitle t : rosterJobTitle.getJobTitles()) {

				for (int j=0;j<jobTitles.size();j++) {
					defaultJobTitle = jobTitles.get(j).getTrainingJobTitle();
					Long tid = t.getId();
					Long deid = defaultJobTitle.getId();
					if (tid.equals(deid)) {
						jobTitles.get(j).setCheckFlg(true);
					}
				}
			}

			RosterShelfRegistration rosterShelfRegistration = new RosterShelfRegistration();
			rosterShelfRegistration.setSectionId(sections.get(i).getUuid());
			rosterShelfRegistration.setTitle(sections.get(i).getTitle());
			rosterShelfRegistration.setSelectedPatternId(selectedPatternId);
			rosterShelfRegistration.setJobList(jobTitles);
			jobTitlesBySection.add(rosterShelfRegistration);
		}

		return jobTitlesBySection;
	}

	public List<RosterShelfRegistration> getjobTitleBySelectedSectionId(List<RosterShelfRegistration> jobTitlesBySection,Item selectedItem,String SelectedSectionId,String siteId,String selectedPatternId){

		TrainingRosterJobTitle rosterJobTitle = new TrainingRosterJobTitle();
		TrainingJobTitle defaultJobTitle = new TrainingJobTitle();

		for (int i=0;i<jobTitlesBySection.size();i++) {
			// 表示リスト内のセクションIDと画面上から選択したセクションIDが同じとき
			if (SelectedSectionId.equals(jobTitlesBySection.get(i).getSectionId())) {
				List<TrainingJobTitleBean> jobTitles = getJobTitlesBean(selectedItem.getName());
				// 職名が登録されていないとき
				if (jobTitles == null) {
					jobTitlesBySection.get(i).setSelectedPatternId(selectedPatternId);
					jobTitlesBySection.get(i).setJobList(jobTitles);
				} else {
					rosterJobTitle = dao.findRosterJobTitleBySectionId(SelectedSectionId,siteId);
					for (TrainingJobTitle t : rosterJobTitle.getJobTitles()) {

						for (int j=0;j<jobTitles.size();j++) {
							defaultJobTitle = jobTitles.get(j).getTrainingJobTitle();
							Long tid = t.getId();
							Long deid = defaultJobTitle.getId();
							if (tid.equals(deid)) {
								jobTitles.get(j).setCheckFlg(true);
							}
						}
					}
					jobTitlesBySection.get(i).setSelectedPatternId(selectedPatternId);
					jobTitlesBySection.get(i).setJobList(jobTitles);
					break;
				}
			}
		}
		return jobTitlesBySection;
	}

	public List<RosterShelfRegistration> updateView(List<RosterShelfRegistration> jobTitlesBySection,List<TrainingSectionBean> sections,String siteId) {

		for (int i=0;i<jobTitlesBySection.size();i++) {
			for (int j=0;j<sections.size();j++) {
				if (jobTitlesBySection.get(i).getSectionId().equals(sections.get(j).getUuid())) {

					List<TrainingJobTitleBean> jobTitles = jobTitlesBySection.get(i).getJobList();
					TrainingJobTitle defaultJobTitle = new TrainingJobTitle();

					TrainingRosterJobTitle rosterJobTitle = dao.findRosterJobTitleBySectionId(sections.get(j).getUuid(),siteId);

					for (TrainingJobTitle t : rosterJobTitle.getJobTitles()) {

						for (int k=0;k<jobTitles.size();k++) {
							defaultJobTitle = jobTitles.get(k).getTrainingJobTitle();
							Long tid = t.getId();
							Long deid = defaultJobTitle.getId();
							if (tid.equals(deid)) {
								jobTitles.get(k).setCheckFlg(true);
							}
						}
					}

					jobTitlesBySection.get(i).setJobList(jobTitles);
					break;
				}
			}
		}
		return jobTitlesBySection;
	}

}
