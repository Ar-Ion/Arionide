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
package org.azentreprise.arionide.coders;

import org.azentreprise.arionide.lang.Function;

public class FunctionDecoder implements Decoder<Function> {
		
	public int getVersionUID() {
		return 0;
	}

	public int getBackwardCompatibileVersionUID() {
		return 0;
	}

	public Function decode(byte[] encoded) {
		IntRef index = new IntRef();
		
		int functionUID = (int) this.decodeIntegerWithSeparator(encoded, index);
		int superiorUID = (int) this.decodeIntegerWithSeparator(encoded, index);
		short properties = (short) this.decodeIntegerWithSeparator(encoded, index);
		int count = (int) this.decodeIntegerWithSeparator(encoded, index);
		int[] inheritance = new int[count];
		
		for(int i = 0; i < count; i++) {
			inheritance[i] = (int) this.decodeIntegerWithSeparator(encoded, index);
		}
		
		return new Function(functionUID, superiorUID, properties, inheritance);
	}
	
	private long decodeIntegerWithSeparator(byte[] encoded, IntRef index1) {
		int index2 = Coder.search(encoded, index1.value, encoded.length, Coder.internalSeparator);
		byte[] alloc = null;
		index1.value += index2;
		
		if(index2 > 0) {
			alloc = new byte[index2 - index1.value];
			System.arraycopy(encoded, index1.value, alloc, 0, alloc.length);
		} else if(index2 < 0) {
			alloc = new byte[index2 - index1.value];
			System.arraycopy(encoded, index1.value, alloc, 0, alloc.length);
		} else {
			return -666L;
		}
		
		return Coder.integerDecoder.decode(alloc);
	}
	
	private static class IntRef {
		private int value = 0;
	}
}