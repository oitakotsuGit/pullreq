package org.sakaiproject.trainingsupport.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.Getter;
import lombok.Setter;

import org.sakaiproject.util.ResourceLoader;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import org.sakaiproject.trainingsupport.bean.TrainingJobTitleBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionTABean;
import org.sakaiproject.trainingsupport.bean.TrainingStatusBean;
import org.sakaiproject.trainingsupport.logic.ProjectLogic;
import org.sakaiproject.trainingsupport.model.TrainingJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingStatus;
import org.sakaiproject.trainingsupport.model.Item;
import org.sakaiproject.trainingsupport.model.RosterShelfRegistration;
import org.sakaiproject.trainingsupport.model.TrainingRosterJobTitle;
import org.sakaiproject.trainingsupport.util.Constant;


public class TrainingController extends TrainingBase implements Controller{
	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";

	private static final String MENU_ROSTER = "roster";
	private static final String ACTION_ADD_SECTION  = "addSection";
	private static final String ACTION_EDIT_SECTION = "editSection";
	private static final String ACTION_DEL_SECTION = "delSection";
	private static final String ACTION_EDIT_TA = "editTA";
	private static final String ACTION_ADD_TA = "addTA";
	private static final String ACTION_DEL_TA = "delTA";
	private static final String ACTION_CREATE_ROSTER = "createRoster";
	private static final String ACTION_EDIT_ROSTER = "editRoster";
	private static final String ACTION_DEL_ROSTER = "delRoster";
	private static final String ACTION_UPDATE_ROSTER = "updateRoster";
	private static final String FROM_CREATE_ROSTER = "createRoster";
	private static final String FROM_EDIT_ROSTER = "editRoster";
	private static final String ACTION_UPDATE_ENROLLMENTSTATUS = "updateEnrollmentStatus";
	private static final String MENU_LDAPAUTOSETTING = "ldapAutoSetting";
	private static final String ACTION_LDAP_CHANGE_SETTING = "ldapchangeSetting";
	private static final String ACTION_ROSTER_SHELF_REGISTRATION = "rosterShelfRegistration";
	private static final String ACTION_CREATE_ROSTER_SHELF_REGISTRATION = "createRosterShelfRegistration";
	private static final String ACTION_EDIT_ADDED_USER_LIST = "editAddedUserList";
	private static final String ACTION_EDIT_REMOVED_USER_LIST = "editRemovedUserList";

	
	/**
	 * Training Controller
	 * 
	 * @author 
	 * 
	 */
	@Setter
	@Getter
	private ProjectLogic projectLogic = null;
    private static ResourceLoader rb = null;	
    
    public void init()
    {
        if (rb == null)
            rb = new ResourceLoader(TRAINING_BUNDLE);
    }
    
	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		
		Map<String, Object> map = new HashMap<String,Object>();
		String currentSiteId = projectLogic.getSakaiProxy().getCurrentSiteId();
		map.put("currentSiteId", currentSiteId);
		map.put("userDisplayName", projectLogic.getSakaiProxy().getCurrentUserDisplayName());
		String role = projectLogic.getSakaiProxy().getCurrentUserRole();
		setRole(role, map);
		String menu = ServletRequestUtils.getStringParameter(req, "menu");
		String action = ServletRequestUtils.getStringParameter(req, "action");
		String autosetting = ServletRequestUtils.getStringParameter(req, "autosetting");

		// Add Section
		if (ACTION_ADD_SECTION.equals(action)){	
			String sectionName = ServletRequestUtils.getStringParameter(req, "sectionName");
			if(sectionName == null || sectionName.equals("")){
				map.put("err", rb.getString("section_name_required"));
				return new ModelAndView(overview(map, currentSiteId, role), map);
			}
			if(projectLogic.getSakaiProxy().isDuplicateSectionTitle(sectionName, currentSiteId)){
				map.put("err", rb.getString("section_add_failure_duplicate_title"));
				return new ModelAndView(overview(map, currentSiteId, role), map);
			}		
			String addSectionUuid = projectLogic.getSakaiProxy().addSection(sectionName);
			if(addSectionUuid == null){
				map.put("err", rb.getString("add_section_error"));
				return new ModelAndView(overview(map, currentSiteId, role), map);
			}			
			map.put("msg", rb.getString("add_section_successful"));
			return new ModelAndView(overview(map, currentSiteId, role), map);
		}
		
		// Edit Section
		if (ACTION_EDIT_SECTION.equals(action)){			
			String updateFlg = ServletRequestUtils.getStringParameter(req, "updateFlg");
			String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
			String preSectionName = ServletRequestUtils.getStringParameter(req, "preSectionName");
			map.put("sectionId", sectionId);
			map.put("preSectionName", preSectionName);
			if(updateFlg == null){
				// go editSection page
				TrainingSectionBean section = projectLogic.getSakaiProxy().getSectionBySectionId(sectionId);
				String sectionName = section.getSection().getTitle();
				map.put("sectionName", sectionName);
				map.put("preSectionName", sectionName);
				return new ModelAndView("editSection", map);
			}
			if(updateFlg.equals("true")){
				// update
				String sectionName = ServletRequestUtils.getStringParameter(req, "sectionName");
				if(sectionName == null || sectionName.equals("")){
					map.put("err", rb.getString("section_name_required"));
					return new ModelAndView("editSection", map);
				}
				if(projectLogic.getSakaiProxy().isDuplicateSectionTitle(sectionName, currentSiteId)){
					map.put("err", rb.getString("section_add_failure_duplicate_title"));
					return new ModelAndView("editSection", map);
				}
				projectLogic.getSakaiProxy().updateSection(sectionId, sectionName);
				map.put("msg", rb.getString("update_section_successful"));
				return new ModelAndView(overview(map, currentSiteId, role), map);
			} else {
				// cancel
				return new ModelAndView(overview(map, currentSiteId, role), map);
			}

		}
		
		// Del Section
		if (ACTION_DEL_SECTION.equals(action)){
			String delFlg = ServletRequestUtils.getStringParameter(req, "delFlg");
			String sectionremove[] = ServletRequestUtils.getStringParameters(req, "sectionremove");
			if(delFlg == null){
				// go delSection page
				if(sectionremove.length > 0){
					map.put("sections", projectLogic.getSakaiProxy().getSectionsBySectionIds(sectionremove));
					map.put("sectionremove", sectionremove);
					return new ModelAndView("deleteSections", map);
				}else {
					map.put("err", rb.getString("overview_delete_section_choose"));
					return new ModelAndView(overview(map, currentSiteId, role), map);
				}
			}
			if(delFlg.equals("true")){
				// delete
				projectLogic.getSakaiProxy().delSections(sectionremove);
				projectLogic.deleteSectionOrder(sectionremove);
				map.put("msg", rb.getString("overview_delete_section_success"));
			} else {
				// cancel
			}
			return new ModelAndView(overview(map, currentSiteId, role), map);
		}
		
		// Edit TA
		if (ACTION_EDIT_TA.equals(action)){
			return new ModelAndView(overviewTA(map, req), map);
		}
		
		// Add TA
		if (ACTION_ADD_TA.equals(action)){
			String taId = (ServletRequestUtils.getStringParameter(req, "taId")).trim();
			String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
			if(taId == null || taId.equals("")){
				map.put("err", rb.getString("id_required"));
				return new ModelAndView(overviewTA(map, req), map);
			}
			TrainingSectionTABean ta = projectLogic.getSakaiProxy().getTABeanByEid(taId, sectionId);
			if(ta == null){
				map.put("err", rb.getString("add_failure_no_user"));
				return new ModelAndView(overviewTA(map, req), map);
			}
			String userRole = projectLogic.getSakaiProxy().getUserRole(ta.getUserUid(), currentSiteId);
			if(userRole == null){
				// add
				projectLogic.getSakaiProxy().addSiteMemberTA(ta.getUserUid(), Constant.TA_ROLE, currentSiteId, sectionId);
				map.put("msg", rb.getString("add_ta_successful"));
			} else if(userRole.equals(Constant.ADMIN_ROLE)){
				map.put("err", rb.getString("add_failure_already_admin"));
			} else if(userRole.equals(Constant.INSTRUCTOR_ROLE)){
				map.put("err", rb.getString("add_failure_already_instructor"));
			} else if(userRole.equals(Constant.TA_ROLE)){
				// add
				if(projectLogic.getSakaiProxy().isSectionTA(sectionId, taId)){
					map.put("err", rb.getString("ta_add_failure_already_ta"));
				}else{
					projectLogic.getSakaiProxy().addSectionMembershipTA(ta.getUserUid(), sectionId);
					map.put("msg", rb.getString("add_ta_successful"));
				}
			} else if(userRole.equals(Constant.STUDENT_ROLE)){
				map.put("err", rb.getString("add_failure_already_student"));
			}
			return new ModelAndView(overviewTA(map, req), map);			
		}
		
		// Del TA
		if (ACTION_DEL_TA.equals(action)){
			String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
			String taremove[] = ServletRequestUtils.getStringParameters(req, "taremove");
			if(taremove.length > 0) {
				projectLogic.getSakaiProxy().deleteSectionTAs(currentSiteId, sectionId, taremove);
				map.put("msg", rb.getString("delete_ta_success"));
			} else {
				map.put("err", rb.getString("ta_delete_ta_choose"));
			}
			return new ModelAndView(overviewTA(map, req), map);
		}
		
		// Create roster
		if (ACTION_CREATE_ROSTER.equals(action)){
			String createFlg = ServletRequestUtils.getStringParameter(req, "createFlg");
			String selectedPatternId = ServletRequestUtils.getStringParameter(req, "selectedPatternId");
			HttpSession session = req.getSession();
			String sectionId = null;
			TrainingSectionBean section = null;			
			List<String> titlePattern = projectLogic.getTitlePattern();
			List<Item>titlePatternMenu = getTitlePatternMenu(titlePattern,false);
			Item selectedItem = getPattern(selectedPatternId,titlePatternMenu);
			map.put("sectionId", sectionId);
			map.put("patterns", titlePatternMenu);
			map.put("selectedPatternId", selectedItem.getId());
			if(createFlg == null){
				sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
				session.setAttribute("sectionId", sectionId);	
				section = projectLogic.getSakaiProxy().getSectionBySectionId(sectionId);
				session.setAttribute("sectionName", section.getTitle());
				List<TrainingJobTitleBean> jobTitles = projectLogic.getJobTitlesBean(selectedItem.getName());		
				session.setAttribute("jobTitles", jobTitles);

			}
			if(createFlg != null && createFlg.equals("true")){
				String selectJobTitle[] = ServletRequestUtils.getStringParameters(req, "selectJobTitle");
				List<TrainingJobTitleBean> selectedJobTitle = (List<TrainingJobTitleBean>)session.getAttribute("jobTitles");
				for( TrainingJobTitleBean trainingJobTitleBean: selectedJobTitle){
					trainingJobTitleBean.setCheckFlg(false);
					if(Arrays.asList(selectJobTitle).contains(trainingJobTitleBean.getJobTitle())){
						trainingJobTitleBean.setCheckFlg(true);
					}
				}
				sectionId =  (String)session.getAttribute("sectionId");
				String pattern = getPattern(selectedPatternId,titlePatternMenu).getName();
				List<TrainingSectionRosterBean> targetRosterList = projectLogic.getTargetRosterByJobTitle(currentSiteId, sectionId, pattern, selectJobTitle);
				session.setAttribute("jobTitles", selectedJobTitle);
				session.setAttribute("targetRoster", targetRosterList);	
				session.setAttribute("createFlg", createFlg);
			}
			
			return new ModelAndView("createRoster", map);
		}
		
		// Edit roster
		if (ACTION_EDIT_ROSTER.equals(action)){
			String addFlg = ServletRequestUtils.getStringParameter(req, "addFlg");
			//String delFlg = ServletRequestUtils.getStringParameter(req, "delFlg");
			HttpSession session = req.getSession();
			String sectionId = null;
			List<TrainingSectionRosterBean> targetRosterList = null;
			TrainingSectionBean section = null;
						
			// Pattern Selection added by Shoji Kajita
			String selectedPatternId = ServletRequestUtils.getStringParameter(req, "selectedPatternId");
			List<String> titlePattern = projectLogic.getTitlePattern();
			List<Item>titlePatternMenu = getTitlePatternMenu(titlePattern,false);
			session.setAttribute("titlePatternMenu", titlePatternMenu);
			Item selectedItem = getPattern(selectedPatternId,titlePatternMenu);
			map.put("patterns", titlePatternMenu);
			map.put("selectedPatternId", selectedItem.getId());
			List<TrainingJobTitleBean> jobTitles = projectLogic.getJobTitlesBean(selectedItem.getName());		
			session.setAttribute("jobTitles", jobTitles);
			
			if(addFlg == null){
				sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
				session.setAttribute("sectionId", sectionId);	
				section = projectLogic.getSakaiProxy().getSectionBySectionId(sectionId);
				session.setAttribute("sectionName", section.getTitle());
				targetRosterList = projectLogic.getSectionRoster(currentSiteId, sectionId);				
			} else 	if(addFlg != null && addFlg.equals("true")){
				targetRosterList = (List<TrainingSectionRosterBean>)session.getAttribute("targetRoster");
				sectionId =  (String)session.getAttribute("sectionId");
				section = projectLogic.getSakaiProxy().getSectionBySectionId(sectionId);
				String userId = ServletRequestUtils.getStringParameter(req, "userId").trim();
				if(userId == null || userId.equals("")){
					map.put("err", rb.getString("id_required"));
					return new ModelAndView("editRoster", map);
				}
				if(targetRosterList != null && targetRosterList.size() > 0){
					for(TrainingSectionRosterBean roster : targetRosterList){
						if(roster.getUserEid().equals(userId)){
							map.put("err", rb.getString("target_roster_add_failure_already_target_roster"));
							return new ModelAndView("editRoster", map);
						}
					}				
				}
				TrainingSectionRosterBean user = projectLogic.getTargetUserByEid(userId, currentSiteId);
				if(user == null){
					map.put("err", rb.getString("add_failure_no_user"));
					return new ModelAndView("editRoster", map);
				}
				String userRole = projectLogic.getSakaiProxy().getUserRole(user.getUserUid(), currentSiteId);
				if(userRole == null){
					targetRosterList.add(user);
					map.put("msg", rb.getString("add_target_roster_list_successful"));
				} else if(userRole.equals(Constant.ADMIN_ROLE)){
					map.put("err", rb.getString("add_failure_already_admin"));
				} else if(userRole.equals(Constant.INSTRUCTOR_ROLE)){
					map.put("err", rb.getString("add_failure_already_instructor"));
				} else if(userRole.equals(Constant.TA_ROLE)){
					map.put("err", rb.getString("add_failure_already_ta"));
				} else if(userRole.equals(Constant.STUDENT_ROLE)){
					if(projectLogic.getSakaiProxy().availableAddRoster(currentSiteId, user.getUserUid(), section.getSection())){
						targetRosterList.add(user);
						map.put("msg", rb.getString("add_target_roster_list_successful"));
					}else{
						map.put("err", rb.getString("target_roster_add_failure_already_another_roster"));
					}
				}

			}
			session.setAttribute("targetRoster", targetRosterList);
			map.put("addListCount", projectLogic.countRosterList(currentSiteId,sectionId,Constant.LIST_TYPE_ADDED));
			map.put("deleteListCount", projectLogic.countRosterList(currentSiteId,sectionId,Constant.LIST_TYPE_REMOVED));
			return new ModelAndView("editRoster", map);
		}
		
		if (ACTION_EDIT_ADDED_USER_LIST.equals(action)) {
			HttpSession session = req.getSession();
			String updateFlg = ServletRequestUtils.getStringParameter(req, "updateFlg");
			String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
			session.setAttribute("sectionId", sectionId);
			TrainingSectionBean section = projectLogic.getSakaiProxy().getSectionBySectionId(sectionId);
			session.setAttribute("sectionName", section.getTitle());
			
			if("true".equals(updateFlg)){
				String[] addUserIds = ServletRequestUtils.getStringParameters(req, "addUserIds");
				int addCount = 0;
				for (String addUserId : addUserIds) {
					if (projectLogic.addToEnrolledList(currentSiteId, addUserId)) {
						addCount++;
					}
				}
				if (addUserIds.length == addCount) {
					map.put("msg", rb.getString("add_enrollee_list_successful"));
				} else {
					map.put("err", rb.getString("add_enrollee_list_failure"));
				}
			}
			
			List<TrainingSectionRosterBean> viewAddRosterList = projectLogic.getSectionRoster(currentSiteId, sectionId, Constant.LIST_TYPE_ADDED);
			map.put("viewaddRosterList", viewAddRosterList);
			map.put("addListCount", viewAddRosterList.size());
			map.put("deleteListCount", projectLogic.countRosterList(currentSiteId, sectionId, Constant.LIST_TYPE_REMOVED));
			return new ModelAndView("editAddRoster", map);
		}
		
		if (ACTION_EDIT_REMOVED_USER_LIST.equals(action)) {
			HttpSession session = req.getSession();
			String updateFlg = ServletRequestUtils.getStringParameter(req, "updateFlg");
			String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
			session.setAttribute("sectionId", sectionId);
			TrainingSectionBean section = projectLogic.getSakaiProxy().getSectionBySectionId(sectionId);
			session.setAttribute("sectionName", section.getTitle());
			
			if("true".equals(updateFlg)){
				String[] delUserIds = ServletRequestUtils.getStringParameters(req, "deleteUserIds");
				int deleteCount = 0;
				for (String delUserId : delUserIds) {
					if (projectLogic.removeFromRemovedList(currentSiteId, delUserId)) {
						deleteCount++;
					}
				}
				if (delUserIds.length == deleteCount) {
					map.put("msg", rb.getString("remove_from_removed_list_successful"));
				} else {
					map.put("err", rb.getString("remove_from_removed_list_failure"));
				}
			}
			
			List<TrainingSectionRosterBean> viewDeleteRosterList = projectLogic.getSectionRoster(currentSiteId, sectionId, Constant.LIST_TYPE_REMOVED);
			map.put("viewdeleteRosterList", viewDeleteRosterList);
			map.put("addListCount", projectLogic.countRosterList(currentSiteId, sectionId, Constant.LIST_TYPE_ADDED));
			map.put("deleteListCount", viewDeleteRosterList.size());
			return new ModelAndView("editDeleteRoster", map);
		}
		
		if ( ACTION_DEL_ROSTER.equals(action) ){			
			HttpSession session = req.getSession();
			String sectionId = (String) session.getAttribute("sectionId");
			session.setAttribute("sectionId", sectionId);
			List<TrainingSectionRosterBean> targetRosterList = (List<TrainingSectionRosterBean>)session.getAttribute("targetRoster");
			String from = ServletRequestUtils.getStringParameter(req, "from");			
			String delUserId = ServletRequestUtils.getStringParameter(req, "delUserId");
			targetRosterList = (List<TrainingSectionRosterBean>)session.getAttribute("targetRoster");
			for (int i = 0 ; i < targetRosterList.size() ; i++){
				if(targetRosterList.get(i).getUserEid().equals(delUserId)){
					targetRosterList.remove(i);
					break;
				}
			}
			session.setAttribute("targetRoster", targetRosterList);
			map.put("addListCount", projectLogic.countRosterList(currentSiteId, sectionId, Constant.LIST_TYPE_ADDED));
			map.put("deleteListCount", projectLogic.countRosterList(currentSiteId, sectionId, Constant.LIST_TYPE_REMOVED));
			if(FROM_CREATE_ROSTER.equals(from)){
				// fromCreateRoster
				return new ModelAndView("createRoster", map);
			} else {
				return new ModelAndView("editRoster", map);
			}
			
		}

		if ( ACTION_UPDATE_ENROLLMENTSTATUS.equals(action) ){
			HttpSession session = req.getSession();
			String sectionId = (String)session.getAttribute("sectionId");
			List<TrainingSectionRosterBean> targetRosterList = (List<TrainingSectionRosterBean>)session.getAttribute("targetRoster");
			String button = ServletRequestUtils.getStringParameter(req, "submitButton");
			String selectedPatternId = ServletRequestUtils.getStringParameter(req, "selectedPatternId");
			String selectJobTitle[] = ServletRequestUtils.getStringParameters(req, "selectJobTitle");
			List<TrainingJobTitleBean> selectedJobTitle = (List<TrainingJobTitleBean>)session.getAttribute("jobTitles");
			List<Item> titlePatternMenu = (List<Item>)session.getAttribute("titlePatternMenu");
			
			if(targetRosterList != null && targetRosterList.size() > 0){
				if (button.equals(rb.getString("jobtitle_update_requirements_as_required")))  {
					for(TrainingSectionRosterBean roster : targetRosterList){
						if(Arrays.asList(selectJobTitle).contains(roster.getJobTitle(selectedPatternId))){
							roster.setEnrollmentStatus("required");
						}
					}
				} else if (button.equals(rb.getString("jobtitle_update_requirements_as_optional")))  {
					for(TrainingSectionRosterBean roster : targetRosterList){
						if(Arrays.asList(selectJobTitle).contains(roster.getJobTitle(selectedPatternId))){
							roster.setEnrollmentStatus("optional");
						}
					}
				}
			}
			
			session.setAttribute("jobTitles", selectedJobTitle);
			session.setAttribute("targetRoster", targetRosterList);	
			map.put("patterns", titlePatternMenu);
			map.put("selectedPatternId", selectedPatternId);
			return new ModelAndView("editRoster", map);
		}		
		
		if ( ACTION_UPDATE_ROSTER.equals(action) ){
			HttpSession session = req.getSession();
			String sectionId = (String)session.getAttribute("sectionId");
			List<TrainingSectionRosterBean> targetRosterList = (List<TrainingSectionRosterBean>)session.getAttribute("targetRoster");
			String from = ServletRequestUtils.getStringParameter(req, "from");
			boolean createRosterFlg = false;
			if(FROM_CREATE_ROSTER.equals(from)){
				// fromCreateRoster
				createRosterFlg = true;
			}
			
			if (createRosterFlg)  {
				if(targetRosterList != null && targetRosterList.size() > 0){
					for(TrainingSectionRosterBean roster : targetRosterList){
						roster.setEnrollmentStatus("required");
					}				
				}
			}  else { // won't be able to get enrollment status at CreateRoster
				if(targetRosterList != null && targetRosterList.size() > 0){
					for(TrainingSectionRosterBean roster : targetRosterList){
						String enrollmentStatus = ServletRequestUtils.getStringParameter(req, "enrollmentStatus" + roster.getUserUid());
						if(!enrollmentStatus.equals(roster.getEnrollmentStatus())) {
							roster.setEnrollmentStatus(enrollmentStatus);
						}
					}				
				}
			}

			projectLogic.updateRoster(targetRosterList, Constant.STUDENT_ROLE, currentSiteId, sectionId, createRosterFlg);
			map.put("msg", rb.getString("update_roster_successful"));
			menu = MENU_ROSTER;
		}

		if ( ACTION_LDAP_CHANGE_SETTING.equals(action) ) {
			// update site_property_table ldapautosetting 1 or delete

			boolean updateFlg = projectLogic.updateSiteProperty(currentSiteId,autosetting);
			if (updateFlg) {
				map.put("msg", rb.getString("update_ldap_auto_setting_successful"));
			} else {
				map.put("msg", rb.getString("update_ldap_auto_setting_error"));
			}
			map.put("autosetting",autosetting);
			return new ModelAndView("ldapAutoSetting", map);
		}

		if (ACTION_ROSTER_SHELF_REGISTRATION.equals(action)) {

			HttpSession session = req.getSession();
			String sectionId = ServletRequestUtils.getStringParameter(req, "selectedsectionId");
			map.put("selectedsectionId",sectionId);
			RosterShelfRegistration rosterShelfRegistration = new RosterShelfRegistration();
			String selectedPatternId = ServletRequestUtils.getStringParameter(req, "selectedPatternId");
			map.put("selectedPatternId", selectedPatternId);

			String userId = null;
			setRole(role ,map);
			if(Constant.TA_ROLE.equals(role)){
				userId = projectLogic.getSakaiProxy().getCurrentUserId();
			}

			List<TrainingSectionBean> sections = projectLogic.getSections(currentSiteId, userId);
			session.setAttribute("sections",sections);

			TrainingRosterJobTitle trainingRosterJobTitle = new TrainingRosterJobTitle();

			if (sectionId == null) {

					List<RosterShelfRegistration> jobTitlesBySection = new ArrayList<RosterShelfRegistration>();
					List<String> titlePattern = projectLogic.getTitlePattern();
					List<Item>titlePatternMenu = getTitlePatternMenu(titlePattern,false);
					map.put("patterns", titlePatternMenu);
					Item selectedItem = getPattern(selectedPatternId,titlePatternMenu);
					jobTitlesBySection = projectLogic.getjobTitleBySectionIdSiteId(selectedItem,sections,currentSiteId,selectedPatternId);
					session.setAttribute("jobTitlesBySection", jobTitlesBySection);
			} else {

					if (selectedPatternId != null) {

						List<RosterShelfRegistration> jobTitlesBySection = (List<RosterShelfRegistration>)session.getAttribute("jobTitlesBySection");
						List<String> titlePattern = projectLogic.getTitlePattern();
						List<Item>titlePatternMenu = getTitlePatternMenu(titlePattern,false);
						map.put("patterns", titlePatternMenu);
						Item selectedItem = getPattern(selectedPatternId,titlePatternMenu);

						jobTitlesBySection = projectLogic.getjobTitleBySelectedSectionId(jobTitlesBySection,selectedItem,sectionId,currentSiteId,selectedPatternId);
						session.setAttribute("jobTitlesBySection", jobTitlesBySection);
					}
			}

			return new ModelAndView("rosterShelfRegistration", map);
		}

		if (ACTION_CREATE_ROSTER_SHELF_REGISTRATION.equals(action)) {

			HttpSession session = req.getSession();
			List<String> titlePattern = projectLogic.getTitlePattern();
			List<Item>titlePatternMenu = getTitlePatternMenu(titlePattern,false);
			map.put("patterns", titlePatternMenu);

			String userId = null;
			setRole(role ,map);
			if(Constant.TA_ROLE.equals(role)){
				userId = projectLogic.getSakaiProxy().getCurrentUserId();
			}
			List<TrainingSectionBean> sections = projectLogic.getSections(currentSiteId, userId);

			TrainingRosterJobTitle trainingRosterJobTitle = new TrainingRosterJobTitle();

			for (int i=0;i<sections.size();i++) {

				String uuid = sections.get(i).getUuid();
				// 画面からチェックボックスの情報取得
				String[] checkedValues = ServletRequestUtils.getStringParameters(req, uuid);
				// 画面から受け取ったチェックボックスが空じゃない
				if (checkedValues.length != 0) {
					// 受け取ったパラメータから職名テーブルにaddする
					projectLogic.setJobTitleBySectionId(trainingRosterJobTitle,uuid,checkedValues,currentSiteId);
				}
			}

			List<RosterShelfRegistration> jobTitlesBySection = (List<RosterShelfRegistration>)session.getAttribute("jobTitlesBySection");
			jobTitlesBySection = projectLogic.updateView(jobTitlesBySection,sections,currentSiteId);
			session.setAttribute("jobTitlesBySection", jobTitlesBySection);

			// 名簿作成・登録画面
			boolean flg = projectLogic.updateRosters(currentSiteId);

			if (flg) {
				map.put("msg", rb.getString("update_roster_successful"));
			}

			return new ModelAndView("rosterShelfRegistration", map);
		}
		// Case: roster
		if ( MENU_ROSTER.equals(menu) ){
			clearSession(req);
			return new ModelAndView(overview(map, currentSiteId, role), map);
		}	
		if ( MENU_LDAPAUTOSETTING.equals(menu) ) {

			boolean exists = projectLogic.existSiteProperty(currentSiteId);

			if (!exists) {
				map.put("autosetting","no");
			} else {
				map.put("autosetting","yes");
			}
			clearSession(req);
			return new ModelAndView("ldapAutoSetting", map);
		}

		//status list
		if(Constant.ADMIN_ROLE.equals(role) || Constant.INSTRUCTOR_ROLE.equals(role) || Constant.TA_ROLE.equals(role)){
			HttpSession session = req.getSession();
			String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
			String userId = null;
			if(Constant.TA_ROLE.equals(role)){
				userId = projectLogic.getSakaiProxy().getCurrentUserId();
			}
			List<TrainingSectionBean> sectionList = projectLogic.getSections(currentSiteId, userId);
			if(sectionList != null && sectionList.size()>0){
				TrainingSectionBean selectedSection = projectLogic.getSelectedSection(sectionId, sectionList);
				List<TrainingSectionRosterBean> targetRosterList = projectLogic.getSectionRoster(currentSiteId, selectedSection.getUuid());
				TrainingRoster roster = projectLogic.getTrainingRosterInfo(currentSiteId, selectedSection.getUuid());
				if(roster!=null){
					String rosterDate = rb.getFormattedMessage("msgs_roster_update_date", new String[] {roster.getUpdateDateString()});
					map.put("rosterDate", rosterDate);
				}
				session.setAttribute("sectionId", selectedSection.getUuid());
				map.put("selectedId", selectedSection.getUuid());
				map.put("sections", sectionList);
				map.put("targetRoster", targetRosterList);
			}
			return new ModelAndView("managerIndex", map);
		}
		
		// for student
		if( role == null){
			// MyWorkspace
			List<TrainingStatusBean> trainingStatus = projectLogic.getVisibleTrainingStatus();
			if(trainingStatus != null && trainingStatus.size()>0){
				map.put("statusList", trainingStatus);
			}
			return new ModelAndView("myworkIndex", map);
		}
		// Training Site
		TrainingStatus trainingStatus = projectLogic.getCurrentTrainingStatus();
		String status = rb.getString("status_reject");
		String statusDate = "";
		if(trainingStatus != null ){
			map.put("trainingStatus", trainingStatus);
			if (trainingStatus.getStatus()==1){
				status = rb.getString("status_accept");
				statusDate = trainingStatus.getStatusUpdateDate().toLocaleString();
			}
		}
		
		map.put("status", status);
		map.put("statusDate", statusDate);
		return new ModelAndView("index", map);
	}

	/* Moved to ProjectLogicImpl.java by Shoji Kajita 2014/04/18
	private TrainingSectionBean getSelectedSection(String sectionId, List<TrainingSectionBean> sectionList){
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
	*/
	
	private String overview(Map<String, Object> map, String siteId, String role) {
		String userId = null;
		setRole(role ,map);
		if(Constant.TA_ROLE.equals(role)){
			userId = projectLogic.getSakaiProxy().getCurrentUserId();
		}
		map.put("sections", projectLogic.getSections(siteId, userId));
		return "overview";
	}
	
	private String overviewTA(Map<String, Object> map, HttpServletRequest req) throws Exception {
		String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
		List<TrainingSectionTABean> tas = projectLogic.getSakaiProxy().getSectionTAs(sectionId);
		TrainingSectionBean section = projectLogic.getSakaiProxy().getSectionBySectionId(sectionId);
		map.put("sectionName", section.getTitle());
		map.put("sectionId", sectionId);
		map.put("tas", tas);
		return "editTA";
	}

	private void clearSession(HttpServletRequest req){
		HttpSession session = req.getSession();
		session.removeAttribute("sectionId");
		session.removeAttribute("sectionName");
		session.removeAttribute("targetRoster");
		session.removeAttribute("createFlg");
	}

}
