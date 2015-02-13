package org.sakaiproject.trainingsupport.tool;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;
import org.sakaiproject.trainingsupport.logic.ProjectLogic;
import org.sakaiproject.trainingsupport.model.TrainingJobTitle;
import org.sakaiproject.trainingsupport.util.Constant;
import au.com.bytecode.opencsv.CSVReader;

@Controller
@RequestMapping("/filepload.htm")
public class TrainingSectionFileUploadController extends TrainingBase {
	private static final Logger log = Logger.getLogger(TrainingSectionFileUploadController.class);

	@Setter
	@Getter
	private ProjectLogic projectLogic = null;

	/** Resource bundle using current language locale */
	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";
    private static ResourceLoader rb = null;
    private static final String TRAINING_UPLOAD_KIND_SECTION = "section";
    private static final String TRAINING_UPLOAD_KIND_JOBTITLE = "jobtitle";
    
    public void init()
    {
        if (rb == null)
            rb = new ResourceLoader(TRAINING_BUNDLE);
    }	

	@RequestMapping(method=RequestMethod.GET)
	public String showForm(){
		return "section/upload";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView upload(@RequestParam("file") MultipartFile file,@RequestParam("kind") String kind) throws Exception {
		Map<String, Object> map = new HashMap<String,Object>();
		String role = projectLogic.getSakaiProxy().getCurrentUserRole();
		setRole(role, map);
		List<String> messes = new ArrayList<String>();
		String nextView = "index";
		if (!file.isEmpty()){
			try{
				CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(),Constant.CSV_CHAR_CODE));
				List<String[]> datas = reader.readAll();
				if(TRAINING_UPLOAD_KIND_SECTION.equals(kind)){
					nextView = "section/upload";
					messes = doRegisterSections(datas);
				}else if(TRAINING_UPLOAD_KIND_JOBTITLE.equals(kind)){
					nextView = "jobtitle/upload";
					messes = doRegisterJobTitles(datas);
					map.put("resultMess", messes);
				}

			}catch(Exception e){
				log.debug(e.getMessage());
			}
		}
		map.put("resultMess", messes);
		return new ModelAndView(nextView, map);
	}

	private List<String> doRegisterSections(List<String[]> datas){
		String siteId = projectLogic.getSakaiProxy().getCurrentSiteId();
		HashMap<String, TrainingSectionBean> existSectionsMap = getExistSectionsMap(siteId);
		List<String> messes = new ArrayList<String>();
		int newCount = 0;
		int updateCount = 0;
		int errCount = 0;
		for(int i=1; i < datas.size(); i++){
			String[] data = datas.get(i);
			if(data == null || data.length < 2){
				messes.add("ERROR:" + i + ":NoData" );
				errCount++;
				continue;
			}
			TrainingSectionBean section = existSectionsMap.get(data[1]);
			if( section != null){
				projectLogic.updateSectionOrder(siteId, section.getUuid(), data[0]);
				updateCount++;
			}else{
				String addSectionUuid = projectLogic.getSakaiProxy().addSection(data[1]);
				if(addSectionUuid == null){
					messes.add("ERROR:" + i + ":" + data[1]);
					errCount++;
				}else{
					projectLogic.updateSectionOrder(siteId, addSectionUuid, data[0]);
					messes.add("SUCCESS:" + i + ":" + data[1]);
					newCount++;
				}
			}
		}
		String resultMess = rb.getFormattedMessage("msgs.reuslt.section.csv.upload", new String[] {""+newCount, ""+updateCount, ""+errCount});
		messes.add(0,resultMess);
		return messes;
	}
		
	private List<String> doRegisterJobTitles(List<String[]> datas){
		List<String> messes = new ArrayList<String>();
		int newCount = 0;
		int updateCount = 0;
		int errCount = 0;
		for(int i=1; i< datas.size(); i++){
			String[] data = datas.get(i);
			if(data == null || data.length < 3){
				messes.add("ERROR:" + i + ":NoData");
				errCount++;
				continue;
			}
			TrainingJobTitle jobTitleObj = new TrainingJobTitle();
			Integer viewRank = null;
			try{
				viewRank = Integer.valueOf(data[0]);
			}catch(Exception e){}
			if(viewRank != null){
				jobTitleObj.setViewRank(viewRank);
			}
			String pattern = data[1];
			if(pattern == null || pattern.isEmpty() || Constant.TITLE_ALL_LABEL.equals(pattern)){
				messes.add("ERROR:" + i + ":" + rb.getString("jobtitle_no_pattern"));
				errCount++;
				continue;
			}else{
				jobTitleObj.setJobTitle(data[2]);
				jobTitleObj.setJobTitlePattern(pattern);
				boolean flg = projectLogic.addJobTitle(jobTitleObj);
				if(flg){
					messes.add("SUCCESS:" + i + ":" + pattern + ":" + data[2]);
					newCount++;
				}else{
					messes.add("ERROR:" + i +  ":" + rb.getString("invalid_jotitle"));
					errCount++;
				}
			}
		}
		String resultMess = rb.getFormattedMessage("msgs.reuslt.jobtitle.csv.upload", new String[] {""+newCount,  ""+errCount});
		messes.add(0,resultMess);
		return messes;
	}
	private HashMap<String, TrainingSectionBean> getExistSectionsMap(String siteId){
		List<TrainingSectionBean> existSections = projectLogic.getSections(siteId, null);
		HashMap<String, TrainingSectionBean> map = new HashMap<String, TrainingSectionBean>();
		for(TrainingSectionBean section: existSections){
			map.put(section.getTitle(), section);
		}
		return map;
	}
}
