/*
 * Classe g�n�rique pour un couple
 * Copyright (C) 2009 Emmanuel Guyot <See emmguyot on SourceForge>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation; either 
 * version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package com.emmguyot.utils;

import net.rim.device.api.util.Comparator;

public class ListItem {

	private String id;
	private String label;
	public ListItem(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String toString() {
		return label;
	}
	
	public static class ListItemComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			
			if ((o1 == null) && (o2 == null)) {
				return 0;
			}
			if (o1 == null) {
				return Integer.MAX_VALUE;
			}
			if (o2 == null) {
				return Integer.MIN_VALUE;
			}
			ListItem l1 = (ListItem) o1;
			ListItem l2 = (ListItem) o2;
			
			return l1.getLabel().compareTo(l2.getLabel());
		}
	
	}
}
