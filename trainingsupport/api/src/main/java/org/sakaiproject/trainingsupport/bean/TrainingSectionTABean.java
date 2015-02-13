package org.sakaiproject.trainingsupport.bean;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSectionTABean {

        @Getter @Setter
	private String sectionId;
        @Getter @Setter
	private String sectionName;
        @Getter @Setter
	private String jobTitle;
        @Getter @Setter
	private String personalNumber;
        @Getter @Setter
	private String userName;
        @Getter @Setter
	private String userUid;
        @Getter @Setter
	private String userEid;
	
	public TrainingSectionTABean(String sectionId, String sectionName, String userName, String userUid, String userEid){
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		this.userName = userName;
		this.userUid = userUid;
		this.userEid = userEid;
	}
}
