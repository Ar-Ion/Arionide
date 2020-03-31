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
import ch.innovazion.arionide.lang.symbols.Information;

public class Skeleton {
	
	private final List<Information> data = new ArrayList<>();
	private final List<Information> rodata = new ArrayList<>();
	private final List<Information> bss = new ArrayList<>();
	private final List<Callable> text = new ArrayList<>();

	private final Map<Information, Long> dataMap = new HashMap<>();
	private final Map<Information, Long> rodataMap = new HashMap<>();
	private final Map<Information, Long> bssMap = new HashMap<>();
	private final Map<Callable, Long> textMap = new HashMap<>();

	private long textLength = 0;

	public void registerData(Information info) {
		data.add(info);
		dataMap.put(info, getDataLength());
	}
	
	public void registerRodata(Information info) {
		rodata.add(info);
		rodataMap.put(info, getRodataLength());
	}
	
	public void registerBSS(Information info) {
		bss.add(info);
		bssMap.put(info, getBSSLength());
	}
	
	public void registerText(Callable target, int length) {
		text.add(target);
		textMap.put(target, textLength);
		
		textLength += length;
	}
	
	public boolean hasData(Information info) {
		return dataMap.containsKey(info);
	}
	
	public boolean hasRodata(Information info) {
		return rodataMap.containsKey(info);
	}
	
	public boolean hasBSSAddress(Information info) {
		return bssMap.containsKey(info);
	}
	
	public boolean hasTextAddress(Callable target) {
		return textMap.containsKey(target);
	}
	
	public long getDataAddress(Information info) {
		return dataMap.get(info);
	}
	
	public long getRodataAddress(Information info) {
		return rodataMap.get(info);
	}
	
	public long getBSSAddress(Information info) {
		return bssMap.get(info);
	}
	
	public long getTextAddress(Callable target) {
		return textMap.get(target);
	}
	
	public long getDataLength() {
		if(data.size() > 0) {
			Information last = data.get(data.size() - 1);
			return dataMap.get(last) + last.getSize();
		} else {
			return 0;
		}
	}
	
	public long getRodataLength() {
		if(rodata.size() > 0) {
			Information last = rodata.get(rodata.size() - 1);
			return rodataMap.get(last) + last.getSize();
		} else {
			return 0;
		}
	}
	
	public long getBSSLength() {
		if(bss.size() > 0) {
			Information last = bss.get(bss.size() - 1);
			return bssMap.get(last) + last.getSize();
		} else {
			return 0;
		}
	}
	
	public long getTextLength() {
		return textLength;
	}
	
	public List<Information> getData() {
		return Collections.unmodifiableList(data);
	}
	
	public List<Information> getRodata() {
		return Collections.unmodifiableList(rodata);
	}
	
	public List<Information> getBSS() {
		return Collections.unmodifiableList(rodata);
	}
	
	public List<Callable> getText() {
		return Collections.unmodifiableList(text);
	}
}
