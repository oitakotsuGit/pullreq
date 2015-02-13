package org.sakaiproject.trainingsupport.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

public class TrainingRosterUpdateJob extends TrainingRosterUpdate implements Job {
	private static final Log log = LogFactory.getLog(TrainingRosterUpdateJob.class);
	
	public void init() {
		if(log.isInfoEnabled()) log.info("init()");
	}
	
	public void destroy() {
		if(log.isInfoEnabled()) log.info("destroy()");
	}
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		loginToSakai();
		rosterUpdate();
		logoutFromSakai();
	}

	protected void loginToSakai() {
		Session sakaiSession = SessionManager.getCurrentSession();
		sakaiSession.setUserId("admin");
		sakaiSession.setUserEid("admin");

		// establish the user's session
		UsageSessionService.startSession("admin", "127.0.0.1", "TrainingRosterUpdate");

		// update the user's externally provided realm definitions
		AuthzGroupService.refreshUser("admin");

		// post the login event
		EventTrackingService.post(EventTrackingService.newEvent(UsageSessionService.EVENT_LOGIN, null, true));
	}
	
	protected void logoutFromSakai() {
		// post the logout event
		EventTrackingService.post(EventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true));
	}
}
