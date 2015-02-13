package org.sakaiproject.trainingsupport.util;

import java.util.Comparator;

import org.sakaiproject.trainingsupport.model.TrainingJobTitle;

public class TrainingJobtitleComparator implements Comparator<TrainingJobTitle>{

	public int compare(TrainingJobTitle sec1, TrainingJobTitle sec2){
		int order1 = convertInt(sec1.getViewRank());
		int order2 = convertInt(sec2.getViewRank());
		String pattern1 = convertString(sec1.getJobTitlePattern());
		String pattern2 = convertString(sec2.getJobTitlePattern());
		if( pattern1.equals(pattern2)){
			return order1 < order2 ? -1 : 1;
		}
		return pattern1.compareTo(pattern2) ;
	}

	private int convertInt(Integer n){
		int nn = 0;
		try{
			nn = n.intValue();
		}catch(Exception e){
			nn = Integer.MAX_VALUE;
		}
		return nn;
	}
	
	private String convertString(String s){
		if(s == null || s.isEmpty()){
			s = "";
		}
		return s;
	}
}
