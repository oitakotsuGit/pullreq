package org.sakaiproject.trainingsupport.dao;

import org.apache.log4j.Logger;

import org.sakaiproject.trainingsupport.dao.TrainingDao;

public class PreloadDataImpl {
	private static final Logger log = Logger.getLogger(PreloadDataImpl.class);

	private TrainingDao dao;
	public void setDao(TrainingDao dao) {
		this.dao = dao;
	}

	public void init() {
		preloadItems();
	}

	/**
	 * Preload some items into the database
	 */
	public void preloadItems() {
	}
}
