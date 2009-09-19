/*
 * Listener pour effectuer des tâches en tâche de fond
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

package com.emmguyot.timesheet;

import java.util.Date;

import javax.microedition.pim.PIMItem;
import javax.microedition.pim.ToDo;

import com.emmguyot.bean.TimeSheetBean;
import com.emmguyot.utils.TaskUtils;

import net.rim.blackberry.api.pdap.BlackBerryToDo;
import net.rim.blackberry.api.pdap.PIMListListener;
import net.rim.device.api.i18n.SimpleDateFormat;

/**
 * 
 */
class TimeSheetTaskListener implements PIMListListener {
    public TimeSheetTaskListener() {
    }
    
    public void itemAdded(PIMItem item) {
        ToDo tache = (ToDo) item;
        
        int etat = tache.getInt(TaskUtils.TODO_FIELD_STATUS, ToDo.ATTR_NONE);
        
        if ((etat == BlackBerryToDo.STATUS_COMPLETED) || (etat == BlackBerryToDo.STATUS_IN_PROGRESS)) { 
            // Ajoute au log
            TimeSheetBean.log(new String[] {
            		SimpleDateFormat.getInstance(SimpleDateFormat.TIME_SHORT).format(new Date(tache.getDate(ToDo.DUE, 0) 
							- 2 * 3600 * 1000)) 
					+ " " + tache.getString(ToDo.SUMMARY, PIMItem.ATTR_NONE)});
        }
    }
    
    public void itemRemoved(PIMItem item) {
    }
    
    public void itemUpdated(PIMItem oldItem, PIMItem newItem) {
        ToDo tacheOrig = (ToDo) oldItem;
        ToDo tache = (ToDo) newItem;
        
        int etat = tache.getInt(TaskUtils.TODO_FIELD_STATUS, ToDo.ATTR_NONE);
        int etatOrig = tacheOrig.getInt(TaskUtils.TODO_FIELD_STATUS, ToDo.ATTR_NONE);
        
        if (((etat == BlackBerryToDo.STATUS_COMPLETED)
        		&& (etatOrig != BlackBerryToDo.STATUS_COMPLETED)) 
            || ((etat == BlackBerryToDo.STATUS_IN_PROGRESS)
                    && (etatOrig != BlackBerryToDo.STATUS_IN_PROGRESS)))  {
            // Ajoute au log
        	TimeSheetBean.log(new String[] {
        			SimpleDateFormat.getInstance(SimpleDateFormat.TIME_SHORT).format(new Date(tache.getDate(ToDo.DUE, 0) 
							- 2 * 3600 * 1000)) 
					+ " " + tache.getString(ToDo.SUMMARY, PIMItem.ATTR_NONE),
					""});
        }
    }
} 
