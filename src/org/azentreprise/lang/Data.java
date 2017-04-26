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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.lang;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Data {
	
	protected final List<Byte> data = new ArrayList<Byte>();
	
	protected byte unalignedByte = 0x0;
	protected byte bitIndex = 0;
	
	private Structure structure;
	
	// Format: [data][type char(b:binary / h:hex / i:integer / s:utf_string)]
	// Eg.: C0FFEEh -> hexadecimal data content=C0FFEE
	
	public void setData(String format) throws LangarionError {
		try {
			/*this.data.clear();
			
			int typeIndex = format.length() - 1;
			char type = format.charAt(typeIndex);
			String content = format.substring(0, typeIndex);
			int contentSize = content.length();

			switch(type) {
				case 'b':
					for(int i = 0; i < contentSize - 7; i += 8) {
						this.data.add((byte) Integer.parseInt(content.substring(i, i + 8), 2));
					}
					
					this.bitIndex = (byte) (contentSize % 8);
					
					if(this.bitIndex > 0) {
						this.unalignedByte = (byte) Integer.parseInt(content.substring(contentSize - this.bitIndex, contentSize), 2);
					}
					
					break;
				case 'h':
					for(int i = 0; i < contentSize - 1; i += 2) {
						this.data.add((byte) Integer.parseInt(content.substring(i, i + 2), 16));
					}
					
					this.bitIndex = (byte) ((contentSize % 2) * 4);
					
					if(this.bitIndex > 0) {
						this.unalignedByte = (byte) Integer.parseInt(content.substring(contentSize - this.bitIndex / 4, contentSize), 16);
					}
					
					break;
				case 'i':
					long l = Long.parseLong(content);
									
				    for (int i = 7; i >= 0; i--) {
				        this.data.add((byte) (l & 0xFF));
				        l >>= 8;
				    }
				    
				    this.reverse();
				case 's':
					for(char ch : content.toCharArray()) {
						this.data.add((byte) ch);
					}
					
					break;
			}*/
		} catch(Exception e) {
			throw new LangarionError("Invalid format", e);
		}
	}
	
	public void setData(byte[] bytes) {
		this.data.clear();
		
		for(byte b : bytes) {
			this.data.add(b);
		}
	}
	
	public void setData(BigInteger integer) {
		this.setData(integer.toByteArray());
	}

	public boolean isAligned(int alignement) {
		return this.bitIndex == 0 && data.size() % alignement == 0;
	}
	
	public void reverse() throws LangarionError {
		if(!this.isAligned(1)) {
			throw new LangarionError("The data is not aligned.");
		}
		
		Collections.reverse(this.data);
	}
	
	public byte[] asByteArray() {
		byte[] bytes = new byte[this.data.size()];
		
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = this.data.get(i);
		}
		
		return bytes;
	}
	
	public boolean asBoolean() {
		return this.asByte() != 0;
	}
	
	public byte asByte() {
		return this.data.get(0);
	}
	
	public long asInteger() {
		long output = 0L;
		
		for(int i = 0; i < 8; i++) {
			output = (output << 8) + this.data.get(i);
		}
		
		return output;
	}
	
	public BigInteger asBigInteger() {
		return new BigInteger(this.asByteArray());
	}
	
	public String asUTF8() {
		return new String(this.asByteArray());
	}
	
	public Data select(String start, String end) {
		int startIndex = this.structure.getStructureElement(this.structure.indexOf(start) - 1);
		int endIndex = this.structure.getStructureElement(this.structure.indexOf(end));
		
		return this.select(startIndex, endIndex);
	}
	
	public Data select(int start, int end) {
		BigInteger integer = new BigInteger(this.asByteArray());
		
		integer.shiftLeft(start);
		
		byte[] bytes = integer.toByteArray();
		byte unaligned = bytes[1 + (end - start) / 8];
				
		Data output = new Data();
		// output.data = Arrays.asList(Arrays.copyOf(bytes, (start - end) / 8));
		
		output.unalignedByte = unaligned;
		output.bitIndex = (byte) ((end - start) % 8);
		
		return output;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}
}
