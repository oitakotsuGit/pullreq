package org.sakaiproject.trainingsupport.bean;

import org.sakaiproject.trainingsupport.model.TrainingJobTitle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An example item
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)s
 * 
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingJobTitleBean {

	private TrainingJobTitle trainingJobTitle;
	private boolean checkFlg = false;
	
	public TrainingJobTitleBean(TrainingJobTitle trainingJobTitle){
		this.trainingJobTitle = trainingJobTitle;
		this.checkFlg = false;
	}
	
	public String getJobTitle(){
		try{
			return trainingJobTitle.getJobTitle();
		}catch(Exception e){}
		return "";
	}

	public TrainingJobTitle getTrainingJobTitle() {
		return trainingJobTitle;
	}

	public void setTrainingJobTitle(TrainingJobTitle trainingJobTitle) {
		this.trainingJobTitle = trainingJobTitle;
	}

	public boolean isCheckFlg() {
		return checkFlg;
	}

	public void setCheckFlg(boolean checkFlg) {
		this.checkFlg = checkFlg;
	}
}
