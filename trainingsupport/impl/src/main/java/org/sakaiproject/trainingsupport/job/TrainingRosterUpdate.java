package org.sakaiproject.trainingsupport.job;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.site.api.Site;

import org.sakaiproject.trainingsupport.logic.ProjectLogic;
import org.sakaiproject.trainingsupport.util.Constant;

public class TrainingRosterUpdate {
	private static final Log log = LogFactory.getLog(TrainingRosterUpdate.class);
	@Setter
	@Getter
	protected ProjectLogic projectLogic = null;
	
	protected synchronized void rosterUpdate() {
		List<Site> sites = projectLogic.getSakaiProxy().getSites(Constant.TRAINING_SITE_TYPE);
		int successCount = 0;
		int errorCount = 0;
		String mess = "";
		for (Site site: sites) {
			boolean automaticallyUpdate = projectLogic.existSiteProperty(site.getId());
			if (!automaticallyUpdate) {
				continue;
			}
			
			boolean result = projectLogic.updateRosters(site.getId());
			if (result) {
				mess = "success";
				successCount++;
			}else{
				mess = "error";
				errorCount++;
			}
			System.out.println("Training Roster Update:" + mess + ":siteId=" + site.getId());
		}
		System.out.println("-----------------------------------------------------------");
		System.out.println("         Training Roster Update Job Result Summary         ");
		System.out.println("-----------------------------------------------------------");
		System.out.println("Roster Update    success=" + successCount + " error=" + errorCount );
		System.out.println("-----------------------------------------------------------");
	}
}
