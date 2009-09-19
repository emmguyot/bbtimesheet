/*
 * Utilitaire pour les chaines de caractères. Pour pallier la faiblesse de J2ME sur J2SE
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

public class StringUtils {

	/**
	 * @param cherche
	 * @param dans
	 * @return position, -1 si pas trouvé
	 */
	public static int lastIndexOf(String dans, String cherche) {
		int lastTrouve = -1;
		int trouve = -1;
		while ((trouve = dans.indexOf(cherche, trouve + 1)) != -1) {
			lastTrouve = trouve;
		}
		return lastTrouve;
	}


}
