package org.sakaiproject.trainingsupport.tool;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingTotal;
import org.sakaiproject.trainingsupport.model.TrainingTotalsBySection;
//import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingStatus;
//import org.sakaiproject.trainingsupport.model.TrainingSection;
import org.sakaiproject.trainingsupport.model.Item;
import org.sakaiproject.trainingsupport.util.Constant;
import org.sakaiproject.trainingsupport.util.DateFormatUtil;


public class TrainingStatusAllTotalController extends TrainingBase implements Controller{

	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";

	private static final String MENU_INIT = "init";
	private static final String ACTION_TOTALIZE  = "totalize";
	private static final String ACTION_CSVOUTPUT  = "csvoutput";
	
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
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
		String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
		String selectedPatternId = ServletRequestUtils.getStringParameter(req, "selectedPatternId");
		List<Item>titlePattern = getTitlePatternMenu(projectLogic.getTitlePattern(),false);

		setRole(role, map);
		if(Constant.ADMIN_ROLE.equals(role) || Constant.INSTRUCTOR_ROLE.equals(role)){
			String startDateStr = ServletRequestUtils.getStringParameter(req,"startDate");
			String endDateStr = ServletRequestUtils.getStringParameter(req,"endDate");
			if(ACTION_TOTALIZE.equals(action) || ACTION_CSVOUTPUT.equals(action)){
				Date startDate = getDate(startDateStr);
				Date endDate = getDate(endDateStr);
				if(! dateCompare(startDate, endDate)){
					Date tmpDate = endDate;
					endDate = startDate;
					startDate = tmpDate;
				}
				//
				// 
				//
				List<TrainingTotalsBySection> resultList = new ArrayList<TrainingTotalsBySection>();
				String pattern = getPattern(selectedPatternId,titlePattern).getName();
				if( Constant.SECTION_ALL.equals(sectionId)){
					resultList= projectLogic.totalize(pattern, startDate, endDate);
					resultList.get(0).setSectionName(rb.getString("label_total_all_section"));
				}else{
					List<TrainingTotal> result = projectLogic.totalize(pattern, sectionId, startDate, endDate);
					String sectionName = projectLogic.getSakaiProxy().getSectionName(sectionId);
					resultList.add(new TrainingTotalsBySection(sectionName,result));	
				}
				String msg = "";
				if( startDate == null && endDate == null){
					msg = rb.getString("msgs_noterm_result");
				}else{
					msg = rb.getFormattedMessage("msgs_term_result", new String[] {startDateStr,endDateStr});
				}
				map.put("msg", msg);
				map.put("totalresultlist", resultList);
				//CSV Output
				String submitAction = ServletRequestUtils.getStringParameter(req, "submitKind");
				if(ACTION_CSVOUTPUT.equals(submitAction)){
					String[] headers = {rb.getString("header_csv_1") ,rb.getString("header_csv_2")+"("+ pattern + ")",rb.getString("header_csv_3"),rb.getString("header_csv_4"),rb.getString("header_csv_5")}; 
					String header = concat(headers);
					res.setContentType("text/csv;charset=" + Constant.CSV_CHAR_CODE);
					String filename = new String("total.csv".getBytes(Constant.CSV_CHAR_CODE), "ISO-8859-1");
					res.setHeader("Content-Disposition","attachment; filename=" + filename);
					OutputStream out = res.getOutputStream();
					out.write(0xef);
					out.write(0xbb);
					out.write(0xbf);
					String data = header + Constant.NEW_LINE;
					out.write(data.getBytes(Constant.CSV_CHAR_CODE));
					for(TrainingTotalsBySection totals: resultList){
						for(TrainingTotal total: totals.getTotalList()){
							String[] datas = {totals.getSectionName(),total.getJobTitle(),total.getParcent(),Integer.toString(total.getAcceptNum()),Integer.toString(total.getNum())};
							data = concat(datas) + Constant.NEW_LINE;
							out.write(data.getBytes(Constant.CSV_CHAR_CODE));
						}
					}
					out.close();
					return null;
				}
			}
			String userId = null;
			if(Constant.TA_ROLE.equals(role)){
				userId = projectLogic.getSakaiProxy().getCurrentUserId();
			}
			// sections list
			List<TrainingSectionBean> sectionList = projectLogic.getSections(currentSiteId, userId);
			if(sectionList != null && sectionList.size()>0){
				map.put("selectedId", sectionId);
				map.put("sections", sectionList);
			}
			// title pattern
			Item selectedItem = getPattern(selectedPatternId,titlePattern);
			map.put("selectedPatternId", selectedItem.getId());
			map.put("patterns", titlePattern);
			
			map.put("startDate", startDateStr);
			map.put("endDate", endDateStr);

			return new ModelAndView("total/all",map);
		}
		return new ModelAndView("index",map);
	}
	
	/**
	 * Date string to Date data.
	 * @param dateStr
	 * @return
	 */
	private Date getDate(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try {
			Date date = sdf.parse(dateStr);
			return date;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * date compare
	 * @param date1
	 * @param date2
	 * @return
	 */
	private boolean dateCompare(Date date1, Date date2){
		if(date1 == null || date2 == null){
			return true;
		}
		if(date1.after(date2)){
			return false;
		}
		return true;
	}

}
