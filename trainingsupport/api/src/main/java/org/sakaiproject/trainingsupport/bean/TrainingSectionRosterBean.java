package org.sakaiproject.trainingsupport.bean;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.text.ParseException;

import org.sakaiproject.trainingsupport.util.DateFormatUtil;
import org.sakaiproject.trainingsupport.util.Constant;

import lombok.Getter;
import lombok.Setter;

public class TrainingSectionRosterBean {

	private String jobTitle;
	private String jobTitle1;
	private String jobTitle2;
	private String jobTitle3;
	private String personalNumber;
	private String userName;
	private int status;
	private String enrollmentStatus;
	private String updateDate;
	private Date updateDateByDate;
	private String userUid;
	private boolean availableFlg;
	private String Role;
	private String userEid;
	private String uuid;

	private String listType;
	private Date listTypeUpdateDate;
	private String note;


	/**
	 * constructor
	 */
	public TrainingSectionRosterBean(){
		this.availableFlg = true;
	}
	
	public TrainingSectionRosterBean(String jobTitle, String personalNumber, String userName, String userUid, String userEid){
		this.jobTitle = jobTitle;
		this.personalNumber = personalNumber;
		this.userName = userName;
		this.userUid = userUid;
		this.userEid = userEid;
		this.availableFlg = true;
	}
		
	public TrainingSectionRosterBean(String jobTitle, String userName, int status, String updateDate, String userUid, String userEid){
		this.jobTitle = jobTitle;
		this.userName = userName;
		this.status = status;
		setUpdateDate(updateDate);
		this.userUid = userUid;
		this.userEid = userEid;
		this.availableFlg = true;
	}
	
	public TrainingSectionRosterBean(String userName, String userUid, String userEid, String enrollmentStatus){
		this.userName = userName;
		this.userUid = userUid;
		this.userEid = userEid;
		if (enrollmentStatus == null || enrollmentStatus.equals(""))  {
			this.enrollmentStatus = "required";
		}  else  {
			this.enrollmentStatus = enrollmentStatus;
		}
		this.availableFlg = true;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getJobTitle1() {
		return jobTitle1;
	}

	public void setJobTitle1(String jobTitle1) {
		this.jobTitle1 = jobTitle1;
	}

	public String getJobTitle2() {
		return jobTitle2;
	}

	public void setJobTitle2(String jobTitle2) {
		this.jobTitle2 = jobTitle2;
	}

	public String getJobTitle3() {
		return jobTitle3;
	}
	
	public String getJobTitle(String pattern)  {
		
		String ret=null;
		
		if (pattern.equals(Constant.LDAP_JOB_TITLE_NUM))  ret = this.getJobTitle();
		if (pattern.equals(Constant.LDAP_JOB_TITLE1_NUM))  ret = this.getJobTitle1();
		if (pattern.equals(Constant.LDAP_JOB_TITLE2_NUM))  ret = this.getJobTitle2();
		if (pattern.equals(Constant.LDAP_JOB_TITLE3_NUM))  ret = this.getJobTitle3();
	
		return ret;
		
	}

	public void setJobTitle3(String jobTitle3) {
		this.jobTitle3 = jobTitle3;
	}

	public String getPersonalNumber() {
		return personalNumber;
	}

	public void setPersonalNumber(String personalNumber) {
		this.personalNumber = personalNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getEnrollmentStatus() {
		return enrollmentStatus;
	}

	public void setEnrollmentStatus(String enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
		try  {
			if (updateDate != null)  {
				this.updateDateByDate = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss").parse(updateDate);
			}  else  {
				this.updateDateByDate = null;
			}
		} catch (ParseException e) {
			this.updateDateByDate = null;
		}
	}
	
	public Date getUpdateDateByDate() {
		return updateDateByDate;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public boolean isAvailableFlg() {
		return availableFlg;
	}

	public void setAvailableFlg(boolean availableFlg) {
		this.availableFlg = availableFlg;
	}

	public String getRole() {
		return Role;
	}

	public void setRole(String role) {
		Role = role;
	}

	public String getUserEid() {
		return userEid;
	}

	public void setUserEid(String userEid) {
		this.userEid = userEid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isValidRangeUpdateDate(Date startDate, Date endDate){
		if(this.updateDateByDate == null){
			return false;
		}
		if(startDate != null){
			if(this.updateDateByDate.compareTo(startDate)< 0){
				return false;
			}
		}
		if(endDate != null){
			if(endDate.compareTo(this.updateDateByDate) < 0){
				return false;
			}
		}
		return true;
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
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
}
