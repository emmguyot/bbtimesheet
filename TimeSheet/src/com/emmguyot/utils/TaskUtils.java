/*
 * Utilitaire pour les tâches
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

import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;

import com.emmguyot.bean.TimeSheetBean;

import net.rim.blackberry.api.pdap.BlackBerryToDo;
import net.rim.device.api.i18n.SimpleDateFormat;

public class TaskUtils {

	public static final int TODO_FIELD_STATUS = ToDo.EXTENDED_FIELD_MIN_VALUE + 9;
	
	public static final int OFFSET = 1;

	/**
	 * @param lstTaches
	 * @return
	 */
	public static ListItem[] getTodayTasks(Vector lstTaches) {
		try {
			ToDoList todoList = (ToDoList)PIM.getInstance().openPIMList(PIM.TODO_LIST, PIM.READ_ONLY);
			Calendar cal = DateUtils.getDateActive();
			Enumeration enumToDo = todoList.items(ToDo.DUE, 0, cal.getTime().getTime() + DateUtils.ONE_DAY);
			while (enumToDo.hasMoreElements()) {
				BlackBerryToDo tache = (BlackBerryToDo) enumToDo.nextElement();
				if (tache.getInt(TODO_FIELD_STATUS, PIMItem.ATTR_NONE) != BlackBerryToDo.STATUS_COMPLETED) {
					lstTaches.addElement(new ListItem(tache.getString(ToDo.UID, PIMItem.ATTR_NONE), 
							SimpleDateFormat.getInstance(SimpleDateFormat.TIME_SHORT).format(new Date(tache.getDate(ToDo.DUE, 0) 
																				- TaskUtils.OFFSET * 3600 * 1000)) 
							+ " " + tache.getString(ToDo.SUMMARY, PIMItem.ATTR_NONE)));
				}
			}
			todoList.close();
		}
		catch (PIMException pe) {
			pe.printStackTrace();
		}
		ListItem[] tabTaches = new ListItem[lstTaches.size()];
		lstTaches.copyInto(tabTaches);
		return tabTaches;
	}

	public static void log(ToDo tache) {
        TimeSheetBean.log(new String[] {
        		SimpleDateFormat.getInstance(SimpleDateFormat.TIME_SHORT).format(new Date(tache.getDate(ToDo.DUE, 0) 
						- TaskUtils.OFFSET * 3600 * 1000)) 
				+ " " + tache.getString(ToDo.SUMMARY, PIMItem.ATTR_NONE)});
	}

}
