/*
 * Interface graphique de l'application
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.TaskArguments;
import net.rim.blackberry.api.pdap.BlackBerryToDo;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.emmguyot.bean.TimeSheetBean;
import com.emmguyot.utils.DateUtils;
import com.emmguyot.utils.EventUtils;
import com.emmguyot.utils.ListItem;
import com.emmguyot.utils.StringUtils;
import com.emmguyot.utils.TaskUtils;

/**
 * The MainScreen class for our application.
 */
final class TimeSheetScreen extends MainScreen
{
	private final class ComboToLogListener implements FieldChangeListener {
		public void fieldChanged(Field field, int context) {
			if (context == 2) {
				doSave();
				majText(TimeSheetBean.log(new String[] {
						field.toString()
					}));
			}
		}
	}

	private EditField _champJournal;
    private ObjectChoiceField _champTaches;
    private ObjectListField _champRDV;
    private TimeSheet _app;  
        
  /**
   * Constructor
   * @param app Reference to the TimeSheet UiApplication.
   */
    TimeSheetScreen(TimeSheet app)
    {
        _app = app;
        
        // Add UI components to the screen.        
        setTitle(new LabelField("Journal de bord & tâches", LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH));
        SeparatorField separator = new SeparatorField();
        add(separator);
        _champJournal = new AutoTextEditField("Journal : ", "");
        _champJournal.setNonSpellCheckable(false);
        add(_champJournal);  
        SeparatorField separator2 = new SeparatorField();
        add(separator2);
        /*************************/
        Vector lstTaches = new Vector();
        lstTaches.addElement(new ListItem("", "Ajouter ..."));
        ListItem[] tabTaches = TaskUtils.getTodayTasks(lstTaches);
        _champTaches = new ObjectChoiceField("Tâches : ", tabTaches);
        _champTaches.setChangeListener(new ComboToLogListener());
        add(_champTaches);  

        /*************************/
        Vector lstRDV = new Vector();
        //lstRDV.addElement("Ajouter ...");
        ListItem[] tabRDV = EventUtils.getTodayEvents(lstRDV);
        if (tabRDV.length > 0) {
	        _champRDV = new ObjectListField();
	        _champRDV.set(tabRDV);
	        //("RDV : ", tabRDV);
	        _champRDV.setChangeListener(new ComboToLogListener());
	        SeparatorField separator3 = new SeparatorField();
	        add(separator3);
	        add(_champRDV);
        }
        app.activate();
    }


    // Menu item classes ------------------------------------------------------------------------------------------------
    
    protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);

		int numMenu = 196939;
		MenuItem _supprLigneItem = new MenuItem("Supprimer ligne",numMenu++,0)
        {
            public void run()
            {
            	if (Dialog.ask(Dialog.D_OK_CANCEL, "Supprimer la ligne en cours?") == Dialog.D_OK) {
	            	int pos = _champJournal.getCursorPosition();
	    			// Simule le lastIndexOf
	    			String debut = _champJournal.getText(0, pos + _champJournal.getLabelLength());
	    			String fin = _champJournal.getText(pos + _champJournal.getLabelLength(), _champJournal.getTextLength() - pos);
	            	int lastTrouve = StringUtils.lastIndexOf(debut, "\n");
	            	if (lastTrouve >= 0) {
	            		debut = debut.substring(_champJournal.getLabelLength(), lastTrouve);
	            	}
	            	else {
	            		debut = "";
	            	}
	    			int trouve = fin.indexOf('\n');
	            	if (trouve >= 0) {
	            		fin = fin.substring(trouve); // Conserve le retour charriot
	            	}
	            	else {
	            		fin = "";
	            	}
	            	_champJournal.setText(debut + fin);
					doSave();
            	}
           }
        };  
		menu.add(_supprLigneItem);

		MenuItem _regrLigneItem = new MenuItem("Regrouper ligne",numMenu++,0)
        {
            public void run()
            {
            	int pos = _champJournal.getCursorPosition();
    			String debut = _champJournal.getText(_champJournal.getLabelLength(), pos + _champJournal.getLabelLength());
    			String fin = _champJournal.getText(pos + _champJournal.getLabelLength(), _champJournal.getTextLength() - pos);
    			int trouve = fin.indexOf('\n');
    			int blanc = fin.indexOf(' ', trouve); 
            	if (trouve >= 1) {
            		fin = fin.substring(0, trouve - 1) + fin.substring(blanc + 1);
            	}
            	else if (trouve == 0) {
            		fin = fin.substring(blanc + 1);
            		
            	}
            	_champJournal.setText(debut + fin);
				doSave();
           }
        };  
		menu.add(_regrLigneItem);

		MenuItem _gotoTasksItem = new MenuItem("Tâches",numMenu++,0)
        {
            public void run()
            {
            	if (_champTaches.isFocus() && (_champTaches.getSelectedIndex() > 0)) {
            		// Ouvre directement la tâche
            		ListItem item = (ListItem) _champTaches.getChoice(_champTaches.getSelectedIndex());
            		ToDoList todoList = null;
            		try {
            			todoList = (ToDoList)PIM.getInstance().openPIMList(PIM.TODO_LIST, PIM.READ_WRITE);
            			ToDo matching = todoList.createToDo();
            			matching.addString(ToDo.UID, ToDo.ATTR_NONE, item.getId());
	            		Enumeration enumTodo = todoList.items(matching);
	            		if (enumTodo.hasMoreElements()) {
	            			Invoke.invokeApplication(Invoke.APP_TYPE_TASKS, new TaskArguments(TaskArguments.ARG_VIEW, (ToDo) enumTodo.nextElement()));
	            		}
            		} catch (PIMException  pe) {
                    	pe.printStackTrace();
            		}
            	}
            	else {
	            	int handle = CodeModuleManager.getModuleHandle("e$2dMobileTaskPro");
	            	if (handle > 0) {
	            		ApplicationDescriptor[] appDesc = CodeModuleManager.getApplicationDescriptors(handle);
	            		if (appDesc.length > 0) {
	            			try {
								ApplicationManager.getApplicationManager().runApplication(appDesc[0]);
							} catch (ApplicationManagerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	            		}
	            		//Invoke.invokeApplication(Invoke.APP_TYPE_TASKS, null);
	            	}
            	}
            }
        };  
		menu.add(_gotoTasksItem);

		MenuItem _gotoAgendaItem = new MenuItem("Calendrier",numMenu++,0)
        {
            public void run()
            {
            	if (_champRDV.isFocus() && (_champRDV.getSelectedIndex() > 0)) {
            		// Ouvre directement la tâche
            		ListItem item = (ListItem) _champRDV.get(_champRDV.getSelectedIndex());
            		EventList eventList = null;
            		try {
            			eventList = (EventList)PIM.getInstance().openPIMList(PIM.EVENT_LIST, PIM.READ_WRITE);
            			Event matching = eventList.createEvent();
            			matching.addString(Event.UID, ToDo.ATTR_NONE, item.getId());
	            		Enumeration enumEvent = eventList.items(matching);
	            		if (enumEvent.hasMoreElements()) {
	            			// l'accès direct à un RDV ne marche pas :-(
	            			//Invoke.invokeApplication(Invoke.APP_TYPE_CALENDAR, new CalendarArguments(CalendarArguments.ARG_VIEW_DEFAULT, (Event) enumEvent.nextElement()));
	            			// Ajoute au journal
	            			TimeSheetBean.log(new String[] {((Event) enumEvent.nextElement()).getString(Event.SUMMARY, PIMItem.ATTR_NONE)});
	            		}
            		} catch (PIMException  pe) {
                    	pe.printStackTrace();
            		}
            	}
            	else {
            		Invoke.invokeApplication(Invoke.APP_TYPE_CALENDAR, null);
            	}
            }
        };  
		menu.add(_gotoAgendaItem);
		menu.add(MenuItem.separator(196942));
        


        MenuItem _moveTasksItem = new MenuItem("Déplacer tâches",numMenu++,0)
        {
            public void run()
            {
                try {
        			ToDoList todoList = (ToDoList)PIM.getInstance().openPIMList(PIM.TODO_LIST, PIM.READ_WRITE);
        			Calendar cal = Calendar.getInstance();
        			cal.setTimeZone(TimeZone.getDefault());
        			cal.set(Calendar.HOUR_OF_DAY, 0);
        			cal.set(Calendar.MINUTE, 0);
        			cal.set(Calendar.SECOND, 0);
        			cal.set(Calendar.MILLISECOND, 0);
        			int nb;
        			do {
        				nb = 0;
            			Enumeration enumToDo = todoList.items(ToDo.DUE, 0, cal.getTime().getTime());
	        			while (enumToDo.hasMoreElements()) {
	        				BlackBerryToDo tache = (BlackBerryToDo) enumToDo.nextElement();
	        				if (tache.getInt(TaskUtils.TODO_FIELD_STATUS, 0) != BlackBerryToDo.STATUS_COMPLETED) {
	        					if (tache.getRepeat() == null) {
	            					tache.setDate(ToDo.DUE, 0, ToDo.ATTR_NONE, cal.getTime().getTime() + DateUtils.SEPT_HEURES);
	        					}
	        					else {
	        						// Tache qui se répete : Conserve les heures
	            					tache.setDate(ToDo.DUE, 0, ToDo.ATTR_NONE, tache.getDate(ToDo.DUE, 0) + DateUtils.ONE_DAY);
	        					}
	        					tache.setInt(TaskUtils.TODO_FIELD_STATUS, 0, ToDo.ATTR_NONE, BlackBerryToDo.STATUS_NOT_STARTED);
	        					tache.commit();
		        				nb++;
	        				}
	        			}
        			}
        			while (nb > 0);
                }
                catch (PIMException pe) {
                	pe.printStackTrace();
                }
            }
        };  
		menu.add(_moveTasksItem);

        MenuItem _calcule = new MenuItem("Calcule temps",numMenu++,0)
        {
            public void run()
            {
            	HttpConnection connection = null;
                try {
                	connection = (HttpConnection) Connector.open("http://emmguyot.free.fr/index.php");
                	InputStreamReader isr = new InputStreamReader(connection.openInputStream());
                	String contenu = "";
                	int count = 0;
                	char[] buffer = new char[1024];
                	while ((count = isr.read(buffer)) != -1) {
                		contenu += new String(buffer);
                	}
                	isr.close();
                	System.out.println(contenu);
                }
                catch (Exception pe) {
                	pe.printStackTrace();
                }
                finally {
                	try {
						connection.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        };  
		menu.add(_calcule);

		MenuItem _aboutItem = new MenuItem("A propos", 268501000,0)
        {
            public void run()
            {
            	int handle = CodeModuleManager.getModuleHandle ("TimeSheet");
                String version = CodeModuleManager.getModuleVersion (handle);

            	Dialog.inform("Journal de Bord\n\n" +
		            			"Développé par Emmanuel Guyot\n" +
		            			"V" + version + " Copyright 2009");
            }
        };  
		menu.add(_aboutItem);
	}



	protected boolean onSave() {
		boolean res = super.onSave();
		doSave();
		return res;
	}

	public void doSave() {
		TimeSheetBean bean = new TimeSheetBean();
		bean.set(_champJournal.getText());
	}

	public void majText(String chaine) {
		_champJournal.setText(chaine);
		
        // Refresh de la liste
        Vector lstTaches = new Vector();
        lstTaches.addElement(new ListItem("", "Ajouter ..."));
        ListItem[] tabTaches = TaskUtils.getTodayTasks(lstTaches);
        boolean focus = _champTaches.isFocus();
        int index = _champTaches.getSelectedIndex();
        _champTaches.setChoices(tabTaches);
        if (index < _champTaches.getSize()) _champTaches.setSelectedIndex(index);
        if (focus) _champTaches.setFocus();
	}      
} 
