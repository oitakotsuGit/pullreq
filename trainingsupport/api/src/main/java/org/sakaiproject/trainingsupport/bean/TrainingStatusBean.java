package org.sakaiproject.trainingsupport.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingStatusBean {
	private Long id;
	private String siteId;
	private String siteTitle;
	private Integer status;
	private String total;
	private String updateDate;
	
	private String getSiteUrl(){
		return "/portal/site/" + siteId;
	}
}
