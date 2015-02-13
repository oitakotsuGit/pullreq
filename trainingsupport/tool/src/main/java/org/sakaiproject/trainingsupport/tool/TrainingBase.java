package org.sakaiproject.trainingsupport.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sakaiproject.trainingsupport.model.Item;
import org.sakaiproject.trainingsupport.util.Constant;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.entity.api.ResourceProperties;

public class TrainingBase {
	
	private static final String TRAINING_BUNDLE = "org.sakaiproject.trainingsupport.bundle.messages";
    private static ResourceLoader rb = null;

	protected void setRole(String role, Map<String, Object> map){
		if(Constant.ADMIN_ROLE.equals(role)){
			map.put("sectionManagementEnabled" ,true);
			map.put("statusUpdateEnabled" ,true);
			map.put("totalizationEnabled" ,true);
			map.put("allTotalizationEnabled", true);
			map.put("jobtitleMainteEnabled" ,true);
			map.put("editSectionEnabled", true);
			map.put("createSectionEnabled", true);
			map.put("editTaEnabled", true);
			map.put("ldapAutoSettingEnabled", true);
			map.put("rosterShelfRegistrationEnabled", true);
		} else if(Constant.INSTRUCTOR_ROLE.equals(role)) {
			map.put("sectionManagementEnabled" ,true);
			map.put("statusUpdateEnabled" ,true);
			map.put("totalizationEnabled" ,true);
			map.put("allTotalizationEnabled", true);
			map.put("editSectionEnabled", true);
			map.put("createSectionEnabled", true);
			map.put("editTaEnabled", true);
		} else if(Constant.TA_ROLE.equals(role))  {
			map.put("totalizationEnabled" ,true);
			map.put("editSectionEnabled", true);	
		}
	}
	
	protected String concat(String[] datas){
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<datas.length; i++){
			builder.append(datas[i]);
			if( i < datas.length -1 ){
				builder.append(Constant.CSV_DELIMITER);
			}
		}
		return builder.toString();
	}
	
	protected String getUnnullString(String data){
		if(data == null ){
			return "";
		}
		return data;
	}
	
	protected List<Item> getTitlePatternMenu(List<String> titlePattern,boolean allFlg){
		List<Item> titlePatternMenu = new ArrayList<Item>();
		int i=0;
		init();
		for(String pattern: titlePattern){
			titlePatternMenu.add(new Item(i,pattern,rb.getString("pattern_name" + i)));
			// titlePatternMenu.add(new Item(i,rb.getString("pattern_name" + i)));
			i++;
		}
		if(allFlg){
			titlePatternMenu.add(new Item(i,Constant.TITLE_ALL_LABEL)) ;
		}
		return titlePatternMenu;
	}
	protected Item getPattern(String sid, List<Item>patternList){
		int num = 0;
		try{
			num = Integer.parseInt(sid);
			return patternList.get(num);
		}catch(Exception e){}
		return patternList.get(0);
	}
	
	private void init()
	{
		if (rb == null)
			rb = new ResourceLoader(TRAINING_BUNDLE);
	}

}
