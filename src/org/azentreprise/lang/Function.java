/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.configuration.Parseable;

public class Function extends Parseable {

	private final List<String> source = new ArrayList<String>();
	
	public Function(String serialized) {
		super(serialized);
		
		this.source.addAll(Arrays.asList(serialized.split("<§>")));
	}

	protected String serialize() {
		return String.join("<§>", this.source);
	}
	
	public void setSourceForLine(int line, String source) {
		if(!source.isEmpty()) {
			while(this.source.size() - 1 < line) {
				this.source.add(new String());
			}

			this.source.set(line, source);
		}
	}
	
	public String getSourceForLine(int line) {
		if(line < this.source.size()) {
			return this.source.get(line);
		} else {
			return new String();
		}
	}
}
