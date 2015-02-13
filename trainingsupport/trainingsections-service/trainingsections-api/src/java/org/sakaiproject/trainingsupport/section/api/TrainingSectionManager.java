package org.sakaiproject.trainingsupport.section.api;

import java.sql.Time;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Calendar;

import org.sakaiproject.section.api.SectionManager;

import org.sakaiproject.section.api.coursemanagement.Course;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.Meeting;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.coursemanagement.SectionEnrollments;
import org.sakaiproject.section.api.coursemanagement.User;
import org.sakaiproject.section.api.exception.MembershipException;
import org.sakaiproject.section.api.exception.SectionFullException;
import org.sakaiproject.section.api.exception.RoleConfigurationException;
import org.sakaiproject.section.api.facade.Role;

public interface TrainingSectionManager extends SectionManager {
	
    /**
     * Gets a list of {@link org.sakaiproject.section.api.coursemanagement.ParticipationRecord
     * ParticipationRecord}s for all training organizers in the current site.
     *
     * @param siteContext The current site context
     * @return The training organizers
     */
    public List<ParticipationRecord> getSiteTrainingOrganizers(String siteContext);
    
    /**
     * Gets a list of {@link org.sakaiproject.section.api.coursemanagement.ParticipationRecord
     * ParticipationRecord}s for all training department managers in the current site.
     *
     * @param siteContext The current site context
     * @return The training department managers
     */
    public List<ParticipationRecord> getSiteTrainingDepartmentManagers(String siteContext);
    
    /**
     * Gets a list of {@link org.sakaiproject.section.api.coursemanagement.ParticipationRecord
     * ParticipationRecord}s for all training department manager in a section.
     *
     * @param sectionUuid The section uuid
     * @return The training department manager
     */
    public List<ParticipationRecord> getSectionTrainingDepartmentManager(String sectionUuid);

    /**
     * Finds a list of {@link org.sakaiproject.section.api.coursemanagement.EnrollmentRecord
     * EnrollmentRecord}s belonging to the current site and whose sort name, display name,
     * or display id start with the given string pattern.
     *
     * @param siteContext The current site context
     * @param pattern The pattern to match students names or ids
     *
     * @return The enrollments
     */
    public List<EnrollmentRecord> findSiteEnrollments(String siteContext, String pattern);  // NOT used
  
    /**
     * Gets a SectionEnrollments data structure for the given students.
     *
     * @param siteContext The site context
     * @param studentUids The Set of userUids to include in the SectionEnrollments
     *
     * @return
     */
    public SectionEnrollments getSectionEnrollmentsForStudents(String siteContext, Set studentUids);  // NOT used

    
}
