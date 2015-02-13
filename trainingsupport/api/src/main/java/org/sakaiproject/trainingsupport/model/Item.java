package org.sakaiproject.trainingsupport.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An example item
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)s
 * 
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

	private long id;
	private String name;
	private String displayName;
	
	public Item(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
}
