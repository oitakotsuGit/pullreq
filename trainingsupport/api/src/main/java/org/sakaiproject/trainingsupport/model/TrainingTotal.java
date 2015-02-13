package org.sakaiproject.trainingsupport.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class TrainingTotal extends TrainingTotalBase {

	
	@Setter
	@Getter
	private String jobTitle;
	
	@Setter
	@Getter
	private int acceptNum;

	@Setter
	@Getter
	private int num;

	public String getParcent(){
		return getParcent(num, acceptNum);
	}
	
	public String getStatusDisp(){
		return getStatusDisp(num, acceptNum);
	}
}
