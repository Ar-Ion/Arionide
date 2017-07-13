/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.lang;

import java.util.Comparator;

public class AutoCompletionComparator implements Comparator<String> {

	private volatile String current = new String();
	
	public int compare(String s1, String s2) {
		
		s1 = s1.toUpperCase();
		s2 = s2.toUpperCase();
		
		if(s1.startsWith(this.current) && !s2.startsWith(this.current)) {
			return -1;
		} else if (s2.startsWith(this.current)) {
			return 1;
		}
		
		for(int i = 0; i < this.current.length(); i++) {
			for(int j = 0; j <= i; j++) {
				String sub = this.current.substring(j, j + this.current.length() - i);
							
				if(s1.contains(sub) && !s2.contains(sub)) {
					return -1;
				} else if(s2.contains(sub)) {
					return 1;
				}
			}
		}
			
		return 0;
	}
	
	public void update(String newValue) {
		this.current = newValue.toUpperCase();
	}
}
