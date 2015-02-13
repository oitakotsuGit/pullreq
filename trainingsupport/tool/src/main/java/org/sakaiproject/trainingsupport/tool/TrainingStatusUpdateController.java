package org.sakaiproject.trainingsupport.tool;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.Getter;
import lombok.Setter;

import org.sakaiproject.util.ResourceLoader;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionTABean;
import org.sakaiproject.trainingsupport.bean.TrainingStatusBean;
import org.sakaiproject.trainingsupport.logic.ProjectLogic;
import org.sakaiproject.trainingsupport.model.TrainingJobTitle;
//import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingStatus;
//import org.sakaiproject.trainingsupport.model.TrainingSection;
import org.sakaiproject.trainingsupport.model.Item;
import org.sakaiproject.trainingsupport.util.Constant;
import org.sakaiproject.trainingsupport.util.DateFormatUtil;


public class TrainingStatusUpdateController extends TrainingBase implements Controller{

	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";

	private static final String MENU_INIT = "init";
	private static final String ACTION_UPDATE  = "update";
	
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
		String role = projectLogic.getSakaiProxy().getCurrentUserRole();
		String menu = ServletRequestUtils.getStringParameter(req, "menu");
		String action = ServletRequestUtils.getStringParameter(req, "action");

		setRole(role, map);
		if(Constant.ADMIN_ROLE.equals(role) || Constant.INSTRUCTOR_ROLE.equals(role)){
			if(ACTION_UPDATE.equals(action)){
				boolean flg = projectLogic.updateStatus(currentSiteId);
				if(flg){
					map.put("msg", rb.getString("msgs_statusupdate_update"));
				}else{
					map.put("err", rb.getString("msgs_statusupdate_noupdate"));
				}
			}

			Date lastDate = projectLogic.getLastDateOfStatusUpdated(currentSiteId);
			if( lastDate != null ){
				String lastDateString = DateFormatUtil.getDateString(lastDate);
				lastDateString = rb.getFormattedMessage("msgs_statusupdate_last_update", new String[] {lastDateString});
				map.put("statusUpdateDate", lastDateString);
			}
			return new ModelAndView("status/index",map);
		}
		return new ModelAndView("index",map);
	}
	
}
