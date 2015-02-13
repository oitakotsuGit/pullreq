package org.sakaiproject.trainingsupport.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingJobTitleByPattern {

	private String jobTitlePattern;
	private List<TrainingJobTitle> jobTitles;
}
