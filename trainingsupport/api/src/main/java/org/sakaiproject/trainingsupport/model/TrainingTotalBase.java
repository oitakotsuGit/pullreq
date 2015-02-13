package org.sakaiproject.trainingsupport.model;

public class TrainingTotalBase {
	protected final String NOPARCENT = "-";
	protected final String PARCENT = "%";
	
	protected String getParcent(int num, int acceptNum){
		if(num == 0){
			return NOPARCENT;
		}
		try{
			int n = acceptNum*100/num;
			return n + PARCENT;
		}catch(Exception e){
		}
		return NOPARCENT;
	}
	
	protected String getStatusDisp(int num, int acceptNum){
		return getParcent(num, acceptNum) + "(" + acceptNum + "/" + num + ")";
	}
}
