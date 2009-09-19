/*
 * Bean assurant la gestion du log
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
package com.emmguyot.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

import net.rim.device.api.i18n.SimpleDateFormat;

import com.emmguyot.utils.DateUtils;
import com.emmguyot.utils.StringUtils;

public class TimeSheetBean {

	private Event eventJournal = null;
	
	public TimeSheetBean() {
        try {
        	eventJournal = TimeSheetBean.getJournal();
		} catch (PIMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void set(String text) {
		try {
			eventJournal.setString(Event.NOTE, 0, Event.ATTR_NONE, text);
			eventJournal.commit();
			eventJournal.getPIMList().close();
		} catch (PIMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String log(String[] libelle) {
        String chaine = "+" + SimpleDateFormat.getInstance(SimpleDateFormat.TIME_SHORT).format(new Date()) + ": ";
        String res = "";
        try {
			Event eventJournal = null;
        	eventJournal = TimeSheetBean.getJournal();
			for (int i = 0; i < libelle.length; i++) {
	        	String newString  = "";
				if (libelle[i].length() > 0) {
					libelle[i] += "\n";
				}
				newString = chaine + libelle[i];

				String previousString = eventJournal.getString(Event.NOTE, Event.ATTR_NONE);
				
				// Recherche l'heure seule pour recoller
				int lastTrouve = StringUtils.lastIndexOf(previousString, chaine);
				if ((lastTrouve != -1) && (lastTrouve + chaine.length() == previousString.length())) {
					previousString = previousString.substring(0, lastTrouve);
				}
				
				lastTrouve = StringUtils.lastIndexOf(previousString, newString);
				// Evite de répéter en cas de sollicitations successives
				if ((lastTrouve == -1) 
						|| (lastTrouve != (previousString.length() - newString.length()))) {
					if ((previousString.length() > 0) && (previousString.charAt(previousString.length() - 1) != '\n')) {
						newString = "\n" + newString;
					}
					eventJournal.setString(Event.NOTE, 0, Event.ATTR_NONE, previousString + newString);
				}
			}
			res = eventJournal.getString(Event.NOTE, Event.ATTR_NONE);
			eventJournal.commit();
			eventJournal.getPIMList().close();
		} catch (PIMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
    }

	public static Event getJournal() throws PIMException {
		Event eventJournal = null;
		EventList eventList = (EventList)PIM.getInstance().openPIMList(PIM.EVENT_LIST, PIM.READ_WRITE);
		Calendar cal = DateUtils.getDateActive();
		long date = cal.getTime().getTime();
		Enumeration enumEvent = eventList.items(EventList.OCCURRING, date, date, true);
		while (enumEvent.hasMoreElements()) {
			eventJournal = (Event) enumEvent.nextElement();
			if (eventJournal.getDate(Event.START, Event.ATTR_NONE) != eventJournal.getDate(Event.END, Event.ATTR_NONE)) {
				// Ignore
				eventJournal = null;
			}
		}
		if (eventJournal == null) {
			// Il faut créer l'instance
			eventJournal = eventList.createEvent();
			eventJournal.addDate(Event.START, Event.ATTR_NONE, date);
			eventJournal.addDate(Event.END, Event.ATTR_NONE, date);
			eventJournal.addString(Event.SUMMARY, Event.ATTR_NONE, "Journal");
			eventJournal.addString(Event.NOTE, Event.ATTR_NONE, "Début du journal\n");
			eventJournal.commit();
		}
		return eventJournal;
	}

}
