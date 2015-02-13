package org.sakaiproject.trainingsupport.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class TrainingTotalsBySection  extends TrainingTotalBase{

	@Setter
	@Getter
	private String sectionName;
	@Setter
	@Getter
	private List<TrainingTotal> totalList;
	
	public String getStatusDisp(){
		if(totalList == null || totalList.size()<1 ){
			return NOPARCENT;
		}
		int num = 0;
		int acceptNum = 0;
		for(TrainingTotal total : totalList){
			num += total.getNum();
			acceptNum += total.getAcceptNum();
		}
		return getStatusDisp(num, acceptNum);
	}
}
