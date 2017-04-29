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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Executable {
	private final int magic = 0xC0FFEE;
	private final int type;
	private final long definitionsHashcode;
	private final int numberOfFunctions;
	private final FunctionDescriptor[] functionDescriptors;
	
	public Executable(InputStream stream) throws LangarionError {
		if(this.read(stream, 4) != this.magic) {
			throw new LangarionError("Invalid magic number");
		}
		
		this.type = (int) this.read(stream, 4);
		this.definitionsHashcode = this.read(stream, 8);
		this.numberOfFunctions = (int) this.read(stream, 4);
		this.functionDescriptors = new FunctionDescriptor[this.numberOfFunctions];
		
		for(int i = 0; i < this.numberOfFunctions; i++) {
			this.functionDescriptors[i] = new FunctionDescriptor(stream);
		}
	}
	
	public int getType() {
		return this.type;
	}
	
	public long getDefinitionsHashcode() {
		return this.definitionsHashcode;
	}
	
	public FunctionDescriptor[] getFunctionDescriptors() {
		return this.functionDescriptors;
	}
	
	private long read(InputStream stream, int length) throws LangarionError {
		byte[] data = new byte[length];

		try {
			stream.read(data);
		} catch (IOException e) {
			throw new LangarionError("Corrupted executable");
		}
		
		return ByteBuffer.wrap(data).getLong();
	}
	
	public class FunctionDescriptor {
		private final int uid;
		private final int superiorUID;
		private final int numberOfParents;
		private final int[] parentsUID;
		
		private FunctionDescriptor(InputStream stream) throws LangarionError {
			this.uid = (int) Executable.this.read(stream, 4);
			this.superiorUID = (int) Executable.this.read(stream, 4);
			this.numberOfParents = (int) Executable.this.read(stream, 4);
			
			this.parentsUID = new int[this.numberOfParents];
			
			for(int i = 0; i < this.numberOfParents; i++) {
				this.parentsUID[i] = (int) Executable.this.read(stream, 4);
			}
		}
		
		public int getUID() {
			return this.uid;
		}
		
		public int getSuperiorUID() {
			return this.superiorUID;
		}
		
		public int[] getParentsUID() {
			return this.parentsUID;
		}
	}
}