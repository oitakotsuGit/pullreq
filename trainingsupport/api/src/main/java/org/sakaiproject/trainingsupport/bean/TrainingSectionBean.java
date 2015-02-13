package org.sakaiproject.trainingsupport.bean;

import java.util.List;

import org.sakaiproject.section.api.coursemanagement.CourseSection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TrainingSectionBean {
	

	private CourseSection section;
	private Integer id;
	private String uuid;
	private String title;
	private List<String> instructorNames;
	private int totalEnrollments;
	private Integer viewRank;
	
	/**
	 * constructor
	 * @param courselinkRequest
	 */
	public TrainingSectionBean(CourseSection section, List<String> instructorNames, int totalEnrollments){
		this.section = section;
		this.instructorNames = instructorNames;
		this.totalEnrollments = totalEnrollments;
	}
	public TrainingSectionBean(CourseSection section, String uuid, String title, List<String> instructorNames, int totalEnrollments ){
		this.section = section;
		this.uuid = uuid;
		this.title = title;
		this.instructorNames = instructorNames;
		this.totalEnrollments = totalEnrollments;
	}
	public TrainingSectionBean(){
		this.section = null;
		this.instructorNames = null;
		this.totalEnrollments = 0;
	}



	/**
	 * sectionを取得します。
	 * @return section
	 */
	public CourseSection getSection() {
	    return section;
	}



	/**
	 * sectionを設定します。
	 * @param section section
	 */
	public void setSection(CourseSection section) {
	    this.section = section;
	}



	/**
	 * idを取得します。
	 * @return id
	 */
	public Integer getId() {
	    return id;
	}



	/**
	 * idを設定します。
	 * @param id id
	 */
	public void setId(Integer id) {
	    this.id = id;
	}



	/**
	 * uuidを取得します。
	 * @return uuid
	 */
	public String getUuid() {
	    return uuid;
	}



	/**
	 * uuidを設定します。
	 * @param uuid uuid
	 */
	public void setUuid(String uuid) {
	    this.uuid = uuid;
	}



	/**
	 * titleを取得します。
	 * @return title
	 */
	public String getTitle() {
	    return title;
	}



	/**
	 * titleを設定します。
	 * @param title title
	 */
	public void setTitle(String title) {
	    this.title = title;
	}



	/**
	 * instructorNamesを取得します。
	 * @return instructorNames
	 */
	public List<String> getInstructorNames() {
	    return instructorNames;
	}



	/**
	 * instructorNamesを設定します。
	 * @param instructorNames instructorNames
	 */
	public void setInstructorNames(List<String> instructorNames) {
	    this.instructorNames = instructorNames;
	}



	/**
	 * totalEnrollmentsを取得します。
	 * @return totalEnrollments
	 */
	public int getTotalEnrollments() {
	    return totalEnrollments;
	}



	/**
	 * totalEnrollmentsを設定します。
	 * @param totalEnrollments totalEnrollments
	 */
	public void setTotalEnrollments(int totalEnrollments) {
	    this.totalEnrollments = totalEnrollments;
	}
	
	/**
	 * viewRankを取得します。
	 * @return
	 */
	public Integer getViewRank() {
		return viewRank;
	}
	
	/**
	 * viewRankを設定します。
	 * @param viewRank
	 */
	public void setViewRank(Integer viewRank) {
		this.viewRank = viewRank;
	}


	
}
