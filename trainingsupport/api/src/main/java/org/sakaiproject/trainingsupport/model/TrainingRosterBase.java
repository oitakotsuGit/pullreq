package org.sakaiproject.trainingsupport.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRosterBase {
	private Long id;
	private String siteId;
	private String sectionId;
	private String userEid;
	private String title;
	private String title1;
	private String title2;
	private String title3;
	private long version;
}
