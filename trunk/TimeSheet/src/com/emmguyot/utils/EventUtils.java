/*
 * Utilitaire pour les rendez-vous
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

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import net.rim.blackberry.api.pdap.BlackBerryEvent;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.util.SimpleSortingVector;

public class EventUtils {
	/**
	 * @param lstTaches
	 * @return
	 */
	public static ListItem[] getTodayEvents(Vector lstRDV) {
    	SimpleSortingVector lstTriee = new SimpleSortingVector();
        try {
        	lstTriee.setSortComparator(new ListItem.ListItemComparator());
        	lstTriee.setSort(true);
			EventList eventList = (EventList)PIM.getInstance().openPIMList(PIM.EVENT_LIST, PIM.READ_ONLY);
			Calendar cal = DateUtils.getDateActive();
			long date = cal.getTime().getTime() + 1;
			Enumeration enumEvent = eventList.items(EventList.OCCURRING, date, date + DateUtils.ONE_DAY - 2, false);
			while (enumEvent.hasMoreElements()) {
				BlackBerryEvent evenement = (BlackBerryEvent) enumEvent.nextElement();
				if ((evenement.countValues(BlackBerryEvent.ALLDAY) == 0) ||  !evenement.getBoolean(BlackBerryEvent.ALLDAY, PIMItem.ATTR_NONE)) {
					lstTriee.addElement(new ListItem(evenement.getString(Event.UID, PIMItem.ATTR_NONE),
							SimpleDateFormat.getInstance(SimpleDateFormat.TIME_SHORT).format(new Date(evenement.getDate(Event.START, 0))) 
							+ " " + evenement.getString(Event.SUMMARY, PIMItem.ATTR_NONE)));
				}
			}
        }
        catch (PIMException pe) {
        	pe.printStackTrace();
        }
        ListItem[] tabRDV = new ListItem[lstTriee.size()];
		lstTriee.copyInto(tabRDV);
		return tabRDV;
	}

}
