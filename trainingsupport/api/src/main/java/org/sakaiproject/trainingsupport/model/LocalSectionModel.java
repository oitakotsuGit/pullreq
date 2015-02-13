/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sections/tags/sakai-2.9.0-b07/sections-app/src/java/org/sakaiproject/tool/section/jsf/backingbean/LocalSectionModel.java $
 * $Id: LocalSectionModel.java 59686 2009-04-03 23:37:55Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.trainingsupport.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.section.api.coursemanagement.Course;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.Meeting;

public class LocalSectionModel implements CourseSection, Serializable {
	private static final long serialVersionUID = 1L;

	private Course course;
	private String uuid;
	private String title;
	private String category;
    private Integer maxEnrollments;

	// We need a string to represent size limit due to this JSF bug: http://issues.apache.org/jira/browse/MYFACES-570
	private String limitSize;

	private List<Meeting> meetings;

	public LocalSectionModel() {}
	
	public LocalSectionModel(Course course, String title, String category) {
		this.course = course;
		this.title = title;
		this.category = category;
		limitSize = Boolean.FALSE.toString();
	}
	
	public LocalSectionModel(CourseSection section, Course course, String title, String category) {
		this.course = course;
		this.title = title;
		this.category = category;
		limitSize = Boolean.FALSE.toString();

	}
    
    public Integer getMaxEnrollments() {
		return maxEnrollments;
	}
	public void setMaxEnrollments(Integer maxEnrollments) {
		this.maxEnrollments = maxEnrollments;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public List<Meeting> getMeetings() {
		if(meetings == null) {
			// Keep this out of the constructor to avoid it in deserialization
			this.meetings = new ArrayList<Meeting>();
		}
		return meetings;
	}

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

	public String getLimitSize() {
		return limitSize;
	}

	public void setLimitSize(String limitSize) {
		this.limitSize = limitSize;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}
	
	public String getUuid() {
		return uuid;
	}

	/**
	 * Enterprise ID is not needed in this app.
	 */
	public String getEid() {
		return null;
	}
}
