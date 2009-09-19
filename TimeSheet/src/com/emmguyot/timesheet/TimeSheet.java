/*
 * Application
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

import javax.microedition.pim.Event;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.ToDo;

import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.menuitem.ApplicationMenuItemRepository;
import net.rim.blackberry.api.pdap.BlackBerryPIMList;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.UiApplication;

import com.emmguyot.bean.TimeSheetBean;

/**
 * 
 */
class TimeSheet extends UiApplication {

	private TimeSheetScreen _screen;
	

	/**
     * Entry point for application.
     * @param args Command line arguments.
     * @throws PIMException 
     */
    public static void main(String[] args) throws PIMException
    {
    	boolean autoStart = ApplicationManager.getApplicationManager().inStartup();
    	
        TimeSheet app = new TimeSheet(autoStart);
        app.enterEventDispatcher();        
    }    

    TimeSheet(boolean autoStart) throws PIMException {
    	if (autoStart) {
	        // Accroche le listener pour les tâches
	        BlackBerryPIMList taskList = (BlackBerryPIMList)PIM.getInstance().openPIMList(PIM.TODO_LIST, PIM.READ_WRITE);
	        TimeSheetTaskListener listener = new TimeSheetTaskListener(); 
	        taskList.addListener(listener);
	        taskList.close();

	        ApplicationMenuItemRepository amir = ApplicationMenuItemRepository.getInstance();
	        amir.addMenuItem(ApplicationMenuItemRepository.MENUITEM_SYSTEM, new MenuGeneral(0));
    	}
        // Push a new TimeSheetScreen onto the stack for rendering.
        _screen = new TimeSheetScreen(this);
        pushScreen(_screen);
    }
    
    
    public void activate() {
		super.activate();
    	if (_screen != null) {
    		_screen.majText(TimeSheetBean.log(new String[] {""}));
    	}
    }

	public void deactivate() {
		super.deactivate();
		_screen.doSave();
	}


	/**
	 * 
	 * @author Manu
	 *
	 */
	private class MenuGeneral extends ApplicationMenuItem
    {
		public MenuGeneral(int order) {
			super(order);
		}

		public Object run(Object context) {
			if (context instanceof Event) {
				Event event = (Event) context;
				TimeSheetBean.log(new String[] {event.getString(Event.SUMMARY, PIMItem.ATTR_NONE)});
			}
			else if (context instanceof ToDo) {
				ToDo tache = (ToDo) context;
				TimeSheetBean.log(new String[] {SimpleDateFormat.getInstance(SimpleDateFormat.TIME_SHORT).format(new Date(tache.getDate(ToDo.DUE, 0) 
						- 2 * 3600 * 1000)) 
						+ " " + tache.getString(ToDo.SUMMARY, PIMItem.ATTR_NONE)});
			}
			else {
				
				TimeSheetBean.log(new String[] {""});
			}
			return context;
		}

		public String toString() {
			return "Ajouter journal";
		}
    }
} 
