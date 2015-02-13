package org.sakaiproject.trainingsupport.tool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.sakaiproject.util.ResourceLoader;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.logic.ProjectLogic;
import org.sakaiproject.trainingsupport.util.Constant;


public class TrainingSectionController extends TrainingBase implements Controller{

	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";

	private static final String ACTION_CSV  = "sectionCsv";
	private static final String ACTION_AUTO_SECTION = "autoSection";
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
		String role = projectLogic.getSakaiProxy().getCurrentUserRole();
		String action = ServletRequestUtils.getStringParameter(req, "action");
		String submitAction = ServletRequestUtils.getStringParameter(req, "submitKind");

		setRole(role, map);
		if(Constant.ADMIN_ROLE.equals(role)){
			if(ACTION_CSV.equals(action) && SUBMIT_CSV_DOWNLOAD.equals(submitAction)){
				outputCsv(res,projectLogic.getSections(projectLogic.getSakaiProxy().getCurrentSiteId(), null));
				return  null;
			}else if(ACTION_CSV.equals(action) && SUBMIT_CSV_UPLOAD.equals(submitAction)){
				return new ModelAndView("section/upload", map);
			}else if(ACTION_CSV.equals(action) && SUBMIT_UNREGISTERED_CSV_DOWNLOAD.equals(submitAction)){
				unRegisteredOutputCsv(res);
			//}else if (ACTION_AUTO_SECTION.equals(action)){
			//	List<TrainingSectionBean> sectionsList =projectLogic.addAutoSections(projectLogic.getSakaiProxy().getCurrentSiteId());
			}
		}
		return null;
	}

	private void unRegisteredOutputCsv(HttpServletResponse res){
		List<String> sections = projectLogic.getSakaiProxy().getUnregisteredSectionsNameFromLdap(projectLogic.getSakaiProxy().getCurrentSiteId());
		List<TrainingSectionBean> sectionList = new ArrayList<TrainingSectionBean>();
		for(String section: sections){
			TrainingSectionBean sectionBean = new TrainingSectionBean();
			sectionBean.setTitle(section);
			sectionList.add(sectionBean);
		}
		outputCsv(res, sectionList);
	}
	
	private void outputCsv(HttpServletResponse res, List<TrainingSectionBean> sectionList){
		res.setContentType("text/csv;charset=" + Constant.CSV_CHAR_CODE);
		String filename = Constant.SECTION_LIST_CSV_FILENAME;
		if( filename == null || filename.isEmpty()){
			filename="section.csv";
		}
		res.setHeader("Content-Disposition","attachment; filename=" + filename);
		OutputStream out;
		try {
			out = res.getOutputStream();
			out.write(0xef);
			out.write(0xbb);
			out.write(0xbf);
			String header = rb.getString("header_csv_order")+ Constant.CSV_DELIMITER +rb.getString("section_header_1");
			out.write((header + Constant.NEW_LINE).getBytes(Constant.CSV_CHAR_CODE));
			for(TrainingSectionBean section: sectionList){
				Integer order = section.getViewRank();
				String orderStr = "";
				if( order != null ){
					try{
						orderStr = String.valueOf(order);
					}catch(Exception e){}
				}
				out.write((orderStr + Constant.CSV_DELIMITER + section.getTitle() + Constant.NEW_LINE).getBytes(Constant.CSV_CHAR_CODE));
			}
			out.close();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			return;
		}
		return;

	}
	
}
