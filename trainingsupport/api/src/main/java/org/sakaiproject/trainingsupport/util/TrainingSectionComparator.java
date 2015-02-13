package org.sakaiproject.trainingsupport.util;

import java.util.Comparator;

import org.sakaiproject.trainingsupport.bean.TrainingSectionBean;

public class TrainingSectionComparator implements Comparator<TrainingSectionBean>{

	public int compare(TrainingSectionBean sec1, TrainingSectionBean sec2){
		Integer order1 = sec1.getViewRank();
		Integer order2 = sec2.getViewRank();
		if( order1 == null ){
			if( order2 == null ){
				return 0;
			}
			return 1;
		}
		if( order2 == null ){
			return -1;
		}
		return order1 < order2 ? -1 : 1 ;
	}
}
