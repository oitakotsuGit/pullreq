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
public class TrainingSectionOrder {

	private Long id;
	private String siteId;
	private String sectionId;
	private Integer viewRank;

}
