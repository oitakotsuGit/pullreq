package org.sakaiproject.trainingsupport.tool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import org.sakaiproject.trainingsupport.model.TrainingTotal;
import org.sakaiproject.trainingsupport.model.TrainingTotalsBySection;
//import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingStatus;
//import org.sakaiproject.trainingsupport.model.TrainingSection;
import org.sakaiproject.trainingsupport.model.Item;
import org.sakaiproject.trainingsupport.util.Constant;


public class TrainingJobtitleController extends TrainingBase implements Controller{

	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";

	private static final String MENU_ROSTER = "roster";
	private static final String ACTION_ADD  = "add";
	private static final String ACTION_DELETE  = "delete";
	private static final String ACTION_CSV  = "jobtitleCsv";
	private static final String SUBMIT_CSV_DOWNLOAD = "csvDownload";
	private static final String SUBMIT_CSV_UPLOAD = "csvUpload";
	private static final String SUBMIT_UNREGISTERED_CSV_DOWNLOAD = "unRegisteredCsvDownload";


	
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
		String action = ServletRequestUtils.getStringParameter(req, "action");
		String selectedPatternId = ServletRequestUtils.getStringParameter(req, "selectedPatternId");
		setRole(role, map);
		List<Item> titlePattern = getTitlePatternMenu();
		if(Constant.ADMIN_ROLE.equals(role)){
			if(ACTION_ADD.equals(action)){
				String jobtitle = ServletRequestUtils.getStringParameter(req, "jobtitle");
				boolean flg = false;
				if( jobtitle != null && jobtitle.length()>0){
					TrainingJobTitle jobTitleObj = new TrainingJobTitle();
					String pattern = getPattern(selectedPatternId,titlePattern).getName();
					if(pattern == null || pattern.isEmpty() || Constant.TITLE_ALL_LABEL.equals(pattern)){
						map.put("err", rb.getString("jobtitle_no_pattern"));
					}else{
						jobTitleObj.setJobTitle(jobtitle);
						jobTitleObj.setJobTitlePattern(pattern);
						flg = projectLogic.addJobTitle(jobTitleObj);
					}
				}
				if(flg){
					map.put("msg", rb.getString("success_add"));
				}else{
					map.put("err", rb.getString("invalid_jotitle"));
				}
			}else if(ACTION_DELETE.equals(action)){
				String id = ServletRequestUtils.getStringParameter(req, "id");
				boolean flg = projectLogic.deleteJobTitle(id);
				if(flg){
					map.put("msg", rb.getString("delete_success"));
				}else{
					map.put("err", rb.getString("delete_failure"));
				}
			}else if(ACTION_CSV.equals(action)){
				String submitAction = ServletRequestUtils.getStringParameter(req, "submitKind");
				if(SUBMIT_CSV_DOWNLOAD.equals(submitAction)){
					outputCsv(res,projectLogic.getJobTitleByPattern(null));
					return null;
				}else if(SUBMIT_CSV_UPLOAD.equals(submitAction)){
					return new ModelAndView("jobtitle/upload", map);
				}else if(SUBMIT_UNREGISTERED_CSV_DOWNLOAD.equals(submitAction)){
					unRegisteredOutputCsv(res);
					return null;
				}
			}
			Item selectedItem = getPattern(selectedPatternId,titlePattern);
			if(Constant.TITLE_ALL_LABEL.equals(selectedItem.getName())){
				map.put("registerDisabled","registerDisabled" );
			}
			map.put("selectedPatternId", selectedItem.getId());
			map.put("patterns", titlePattern);
			List<TrainingJobTitle> jobTitles = projectLogic.getJobTitleByPattern(selectedItem.getName());
			map.put("jobTitles" ,jobTitles);
		}
		return new ModelAndView("jobtitle/list",map);
	}

	private List<Item> getTitlePatternMenu(){
		List<String> titlePattern = projectLogic.getTitlePattern();
		return getTitlePatternMenu(titlePattern,true);
	}

	private void unRegisteredOutputCsv(HttpServletResponse res){
		List<TrainingJobTitle> titlesList = projectLogic.getUnregisteredTitlesFromLdap();
		outputCsv(res, titlesList);
	}
	
	private void outputCsv(HttpServletResponse res,List<TrainingJobTitle> titleList){
		res.setContentType("text/csv;charset=" + Constant.CSV_CHAR_CODE);
		String filename = Constant.JOBTITLE_LIST_CSV_FILENAME;
		if( filename == null || filename.isEmpty()){
			filename="section.csv";
		}
		res.setHeader("Content-Disposition","attachment; filename=" + filename);
		String[] headers = {rb.getString("header_csv_order") ,rb.getString("jobtitle_header_csv_1"),rb.getString("jobtitle_header_csv_2")}; 
		String header = concat(headers);
		OutputStream out;
		try {
			out = res.getOutputStream();
			out.write(0xef);
			out.write(0xbb);
			out.write(0xbf);
			String data = header + Constant.NEW_LINE;
			out.write(data.getBytes(Constant.CSV_CHAR_CODE));
			for(TrainingJobTitle title: titleList){
				Integer order = title.getViewRank();
				String orderStr = "";
				if( order != null ){
					try{
						orderStr = String.valueOf(order);
					}catch(Exception e){}
				}
				String[] datas = {orderStr,getUnnullString(title.getJobTitlePattern()),getUnnullString(title.getJobTitle())};
				data = concat(datas) + Constant.NEW_LINE;
				out.write(data.getBytes(Constant.CSV_CHAR_CODE));
			}
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return;

	}

}
