package org.sakaiproject.trainingsupport.model;

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
public class TrainingJobTitle {

	private Long id;
	private String jobTitle;
	private String jobTitlePattern;
	private Integer viewRank;

}
