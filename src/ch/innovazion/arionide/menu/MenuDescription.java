/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.ui.ApplicationTints;

public class MenuDescription {
	
	public static final MenuDescription EMPTY = new MenuDescription();
	public static final int MAX_LINES = 6;
	
	private final List<DescriptionLine> lines = new ArrayList<>();
	private final int baseColor;
	private final float highlightFactor;
	
	private int scrollBase;
	private int highlight;
	
	public MenuDescription(String... objects) {
		this(ApplicationTints.MENU_INFO_DEFAULT_COLOR, objects);
	}
	
	public MenuDescription(int baseColor, String... objects) {
		this(baseColor, 0.0f, objects);
	}
	
	public MenuDescription(int baseColor, float highlightFactor, String... objects) {
		this.baseColor = baseColor;
		this.highlightFactor = highlightFactor;
		
		Arrays.asList(objects).stream().forEach(this::add);
		
		scrollBase = 0;
		highlight = 0;
	}
	
	public void reset() {
		lines.clear();
		scrollBase = 0;
		highlight = 0;
	}
	
	public void remove(int index) {
		lines.remove(index);
		decrementHighlight();
	}
	
	public int spacer() {
		return add(new String());
	}
	
	public int add(String line) {
		return add(line, baseColor);
	}
	
	public int add(String line, int color) {
		lines.add(new DescriptionLine(line, color));
		return lines.size() - 1;
	}
	
	public void setColor(int index, int color) {
		lines.get(index).color = color;
	}
	
	public void resetColor(int index) {
		setColor(index, baseColor);
	}
	
	public void insert(int position, String line) {
		insert(position, line, baseColor);
	}
	
	public void insert(int position, String line, int color) {
		lines.add(position, new DescriptionLine(line, color));
		
		if(highlight >= position) {
			incrementHighlight();
		}
	}
	
	public void setHighlight(int index) {
		highlight = index;
		scrollBase = index;
	}
	
	public void incrementHighlight() {
		if(isStepAllowed(highlight, +1) && ++highlight - scrollBase >= MAX_LINES) {
			scrollBase++;
		}
	}
	
	public void decrementHighlight() {
		if(isStepAllowed(highlight, -1) && --highlight < scrollBase) {
			scrollBase--;
		}
	}
	
	private boolean isStepAllowed(int index, int direction) {
		int potential = index + direction;
		return potential >= 0 && potential < lines.size();
	}
	
	public DescriptionLine[] getDisplay() {
		DescriptionLine[] display = new DescriptionLine[MAX_LINES];
		
		for(int i = 0; i < MAX_LINES; i++) {
			if(i < lines.size()) {
				DescriptionLine line = lines.get(scrollBase + i);
				
				if(line != null) {
					display[i] = line.clone();
				}
			}
		}
		
		DescriptionLine highlighted = display[highlight - scrollBase];
		
		if(highlighted != null) {
			highlighted.color = Utils.brighter(highlighted.color, highlightFactor);
		}
		
		return display;
	}
	
	public String toString() {
		return lines.stream().map(Object::toString).collect(Collectors.toList()).toString();
	}
	
	public MenuDescription clone() {
		MenuDescription clone = new MenuDescription(baseColor, highlightFactor);
		clone.lines.addAll(lines);
		clone.highlight = highlight;
		clone.scrollBase = scrollBase;
		
		return clone;
	}
	
	public class DescriptionLine {
		private final String text;
		private int color;
		
		private DescriptionLine(String text, int color) {
			this.text = text;
			this.color = color;
		}
		
		public String getText() {
			return text;
		}
		
		public int getColor() {
			return color;
		}
		
		public DescriptionLine clone() {
			return new DescriptionLine(text, color);
		}
		
		public String toString() {
			return text;
		}
 	}
}
