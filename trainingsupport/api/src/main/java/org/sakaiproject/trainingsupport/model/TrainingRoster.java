package org.sakaiproject.trainingsupport.model;

import java.util.Date;
import java.util.Set;

import org.sakaiproject.trainingsupport.util.DateFormatUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRoster {

	private Long id;
	private String sakaiSiteId;
	private String sakaiSectionId;
	private Date insertDate;
	private String insertUserId;
	private Date updateDate;
	private String updateUserId;
	private Set trainingStatuses;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSakaiSiteId() {
		return sakaiSiteId;
	}
	public void setSakaiSiteId(String sakaiSiteId) {
		this.sakaiSiteId = sakaiSiteId;
	}
	public String getSakaiSectionId() {
		return sakaiSectionId;
	}
	public void setSakaiSectionId(String sakaiSectionId) {
		this.sakaiSectionId = sakaiSectionId;
	}
	public Date getInsertDate() {
		return insertDate;
	}
	public String getInsertDateString() {
		return DateFormatUtil.getDateString(insertDate);
	}
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}
	public String getInsertUserId() {
		return insertUserId;
	}
	public void setInsertUserId(String insertUserId) {
		this.insertUserId = insertUserId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public String getUpdateDateString() {
		return DateFormatUtil.getDateString(updateDate);
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public Set getTrainingStatuses() {
		return trainingStatuses;
	}
	public void setTrainingStatuses(Set trainingStatuses) {
		this.trainingStatuses = trainingStatuses;
	}
}
