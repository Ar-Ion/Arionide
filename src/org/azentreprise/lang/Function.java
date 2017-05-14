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
