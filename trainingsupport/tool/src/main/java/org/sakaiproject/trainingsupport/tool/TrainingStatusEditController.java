package org.sakaiproject.trainingsupport.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.Getter;
import lombok.Setter;

import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.logic.ProjectLogic;
import org.sakaiproject.trainingsupport.util.Constant;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class TrainingStatusEditController extends TrainingBase implements Controller{
	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";
	private static final String ACTION_OPEN = "open";
	private static final String ACTION_SAVE = "save";
	
	@Setter
	@Getter
	private ProjectLogic projectLogic = null;
	private static ResourceLoader rb = null;	
	
	public void init() {
		if (rb == null)
			rb = new ResourceLoader(TRAINING_BUNDLE);
	}

	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<String, Object> map = new HashMap<String,Object>();
		String currentSiteId = projectLogic.getSakaiProxy().getCurrentSiteId();
		String role = projectLogic.getSakaiProxy().getCurrentUserRole();
		String action = ServletRequestUtils.getStringParameter(req, "action");
		
		setRole(role, map);
		
		if (!hasEditStatusPermission(role)) {
			return new ModelAndView("index", map);
		}
		
		if (ACTION_OPEN.equals(action)) {
			HttpSession session = req.getSession();
			String sectionId = (String) session.getAttribute("sectionId");
			TrainingSectionBean section = projectLogic.getSakaiProxy().getSectionBySectionId(sectionId);
			session.setAttribute("sectionName", section.getTitle());
			String userEid = ServletRequestUtils.getStringParameter(req, "userEid");
			session.setAttribute("userEid", userEid);
			
			TrainingSectionRosterBean trainingSectionRosterBean = projectLogic.getTargetUserByEid(userEid, currentSiteId);
			
			session.setAttribute("userName", trainingSectionRosterBean.getUserName());
			map.put("status", trainingSectionRosterBean.getStatus());
			if (trainingSectionRosterBean.getUpdateDateByDate() != null) {
				Date statusUpdateDate = trainingSectionRosterBean.getUpdateDateByDate();
				map.put("date", getDateString(statusUpdateDate));
				map.put("time", getTimeString(statusUpdateDate));
			}
			map.put("note", trainingSectionRosterBean.getNote());
			return new ModelAndView("editStatus", map);
		}
		
		if (ACTION_SAVE.equals(action)) {
			HttpSession session = req.getSession();
			String sectionId = (String) session.getAttribute("sectionId");
			String sectionName = (String) session.getAttribute("sectionName");
			String userEid = (String) session.getAttribute("userEid");
			String userName = (String) session.getAttribute("userName");
			String status = ServletRequestUtils.getStringParameter(req, "status");
			String date = ServletRequestUtils.getStringParameter(req, "date");
			String time = ServletRequestUtils.getStringParameter(req, "time");
			String note = ServletRequestUtils.getStringParameter(req, "note");
			
			session.setAttribute("sectionId", sectionId);
			session.setAttribute("sectionName", sectionName);
			session.setAttribute("userEid", userEid);
			session.setAttribute("userName", userName);
			map.put("status", status);
			map.put("date", date);
			map.put("time", time);
			map.put("note", note);
			
			if (validate(req, map)) {
				// status update
				projectLogic.updateUserStatus(userEid, Integer.parseInt(status), datetimeStringToDate(date, time), note);
				
				TrainingSectionRosterBean trainingSectionRosterBean = projectLogic.getTargetUserByEid(userEid, currentSiteId);
				if (trainingSectionRosterBean.getUpdateDateByDate() != null) {
					Date statusUpdateDate = trainingSectionRosterBean.getUpdateDateByDate();
					map.put("date", getDateString(statusUpdateDate));
					map.put("time", getTimeString(statusUpdateDate));
				}
				map.put("msg", rb.getString("status_update_complete"));
			}
			return new ModelAndView("editStatus", map);
		}
		return new ModelAndView("index", map);
	}
	
	private boolean validate(HttpServletRequest req, Map<String, Object> map) throws ServletRequestBindingException {
		String date = ServletRequestUtils.getStringParameter(req, "date");
		String time = ServletRequestUtils.getStringParameter(req, "time");
		if ((date != null && !date.equals("")) || (time != null && !time.equals(""))) {
			try {
				datetimeStringToDate(date, time);
			} catch (ParseException e) {
				map.put("err", rb.getString("error_invalid_datetime_format"));
				return false;
			}
		}
		
		String note = ServletRequestUtils.getStringParameter(req, "note");
		final int maxNoteLength = 255;
		if (note != null && maxNoteLength < note.length()) {
			map.put("err", rb.getFormattedMessage("error_over_max_note_length", new Object[] { maxNoteLength }));
			return false;
		}
		return true;
	}
	
	private boolean hasEditStatusPermission(String role) {
		return Constant.ADMIN_ROLE.equals(role)
				|| Constant.INSTRUCTOR_ROLE.equals(role)
				|| Constant.TA_ROLE.equals(role);
	}
	
	private Date datetimeStringToDate(String date, String time) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date now = new Date();
		if (date == null || date.equals("")) {
			date = getDateString(now);
		}
		if (time == null || time.equals("")) {
			time = getTimeString(now);
		}
		return format.parse(date + " " + time);
	}
	
	private String getDateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		return format.format(date);
	}
	
	private String getTimeString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(date);
	}
}
