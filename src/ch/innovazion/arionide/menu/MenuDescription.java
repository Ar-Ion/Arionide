package ch.innovazion.arionide.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.ui.ApplicationTints;

public class MenuDescription {
	
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
 	}
}
