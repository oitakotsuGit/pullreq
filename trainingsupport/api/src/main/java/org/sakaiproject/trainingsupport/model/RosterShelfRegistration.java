package org.sakaiproject.trainingsupport.model;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.trainingsupport.bean.TrainingJobTitleBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RosterShelfRegistration {

	private String sectionId;
	private String title;
	private String selectedPatternId;
	private List<TrainingJobTitleBean> jobList;
}
