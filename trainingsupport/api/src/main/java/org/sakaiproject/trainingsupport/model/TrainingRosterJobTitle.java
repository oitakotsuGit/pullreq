package org.sakaiproject.trainingsupport.model;

import java.util.Iterator;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRosterJobTitle {
	private Long id;
	private String siteId;
	private String sectionId;
	private Set<TrainingJobTitle> jobTitles;
	
	public String getJobTitlePattern() {
		String jobTitlePattern = null;
		if (jobTitles != null) {
			Iterator<TrainingJobTitle> it = jobTitles.iterator();
			jobTitlePattern = it.next().getJobTitlePattern();
		}
		return jobTitlePattern;
	}
}
