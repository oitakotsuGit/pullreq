package org.sakaiproject.trainingsupport.model;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;


import org.sakaiproject.trainingsupport.util.DateFormatUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingStatus {

	private Long id;
	private String sakaiEid;
	private TrainingRoster trainingRoster;
	private int status;
	private Date statusUpdateDate;
	private String enrollmentStatus;
	private boolean manuallyUpdated;
	private Date manuallyUpdateDate;
	private String note;
	private String listType;
	private Date listTypeUpdateDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSakaiEid() {
		return sakaiEid;
	}
	public void setSakaiEid(String sakaiEid) {
		this.sakaiEid = sakaiEid;
	}
	public TrainingRoster getTrainingRoster() {
		return trainingRoster;
	}
	public void setTrainingRoster(TrainingRoster trainingRoster) {
		this.trainingRoster = trainingRoster;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getStatusUpdateDate() {
		return statusUpdateDate;
	}
	public String getStatusUpdateDateString() {
		return DateFormatUtil.getDateString(statusUpdateDate);
	}
	public void setStatusUpdateDate(Date statusUpdateDate) {
		this.statusUpdateDate = statusUpdateDate;
	}
	public void setStatusUpdateDate(String statusUpdateDateString)  {

		try  {
			if (statusUpdateDateString != null)  {
				this.statusUpdateDate = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss").parse(statusUpdateDateString);
			}  else  {
				this.statusUpdateDate = null;
			}
		} catch (ParseException e) {
			this.statusUpdateDate = null;
		}
	}
	public void setEnrollmentStatus(String enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;
	}
	public String getEnrollmentStatus() {
		return enrollmentStatus;
	}

	public int hashCode(){
		int tmp = 0;
		tmp = (id + sakaiEid).hashCode();
		return tmp;
	}
	public String getListType() {
		return listType;
	}
	public void setListType(String listType) {
		this.listType = listType;
	}
	public Date getListTypeUpdateDate() {
		return listTypeUpdateDate;
	}
	public void setListTypeUpdateDate(Date listTypeUpdateDate) {
		this.listTypeUpdateDate = listTypeUpdateDate;
	}
}
