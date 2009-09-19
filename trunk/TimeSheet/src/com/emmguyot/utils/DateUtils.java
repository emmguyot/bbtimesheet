/*
 * Utilitaire pour les dates. Pour pallier la faiblesse de J2ME sur J2SE
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
import java.util.TimeZone;

public class DateUtils {

	public static final long SEPT_HEURES = 1000l*60*60*7;
	public static final long ONE_DAY = 1000l*60*60*24;
	

	/**
	 * Donne la date du jour à 0:00
	 * @return Date
	 */
	public static Calendar getDateActive() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getDefault());
		cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 5);
		cal.get(Calendar.HOUR_OF_DAY); // Force le recalcul de la date complète avant d'aller plus loin
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}


}
