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
package ch.innovazion.arionide.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Node;

public class Skeleton {
	
	private final List<Node> data = new ArrayList<>();
	private final List<Node> rodata = new ArrayList<>();
	private final List<Node> bss = new ArrayList<>();
	private final List<Callable> text = new ArrayList<>();

	private final Map<Node, Long> dataMap = new HashMap<>();
	private final Map<Node, Long> rodataMap = new HashMap<>();
	private final Map<Node, Long> bssMap = new HashMap<>();
	private final Map<Callable, Long> textMap = new HashMap<>();

	private long textLength = 0;

	public void registerData(Node info) {
		data.add(info);
		dataMap.put(info, getDataLength());
	}
	
	public void registerRodata(Node info) {
		rodata.add(info);
		rodataMap.put(info, getRodataLength());
	}
	
	public void registerBSS(Node info) {
		bss.add(info);
		bssMap.put(info, getBSSLength());
	}
	
	public void registerText(Callable target, int length) {
		text.add(target);
		textMap.put(target, textLength);
		
		textLength += length;
	}
	
	public boolean hasData(Node info) {
		return dataMap.containsKey(info);
	}
	
	public boolean hasRodata(Node info) {
		return rodataMap.containsKey(info);
	}
	
	public boolean hasBSSAddress(Node info) {
		return bssMap.containsKey(info);
	}
	
	public boolean hasTextAddress(Callable target) {
		return textMap.containsKey(target);
	}
	
	public long getDataAddress(Node info) {
		return dataMap.get(info);
	}
	
	public long getRodataAddress(Node info) {
		return rodataMap.get(info);
	}
	
	public long getBSSAddress(Node info) {
		return bssMap.get(info);
	}
	
	public long getTextAddress(Callable target) {
		return textMap.get(target);
	}
	
	public long getDataLength() {
		if(data.size() > 0) {
			Node last = data.get(data.size() - 1);
			return dataMap.get(last) + last.getSize();
		} else {
			return 0;
		}
	}
	
	public long getRodataLength() {
		if(rodata.size() > 0) {
			Node last = rodata.get(rodata.size() - 1);
			return rodataMap.get(last) + last.getSize();
		} else {
			return 0;
		}
	}
	
	public long getBSSLength() {
		if(bss.size() > 0) {
			Node last = bss.get(bss.size() - 1);
			return bssMap.get(last) + last.getSize();
		} else {
			return 0;
		}
	}
	
	public long getTextLength() {
		return textLength;
	}
	
	public List<Node> getData() {
		return Collections.unmodifiableList(data);
	}
	
	public List<Node> getRodata() {
		return Collections.unmodifiableList(rodata);
	}
	
	public List<Node> getBSS() {
		return Collections.unmodifiableList(rodata);
	}
	
	public List<Callable> getText() {
		return Collections.unmodifiableList(text);
	}
}
