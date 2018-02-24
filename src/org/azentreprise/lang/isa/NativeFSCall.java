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
package org.azentreprise.lang.isa;

import java.io.File;

import org.azentreprise.lang.Code;
import org.azentreprise.lang.Data;
import org.azentreprise.lang.LangarionError;

public abstract class NativeFSCall extends Instruction {
	
	private final String file;
	private final String data;
	
	public NativeFSCall(String file, String data) {
		this.file = file;
		this.data = data;
	}
	
	public File loadFile(Code code) throws LangarionError {
		File file = new File(code.var(this.file, true).asUTF8());
	
		if(file.exists()) {
			return file;
		} else {
			throw new LangarionError(file.getAbsolutePath() + " : File not found");
		}
	}
	
	public Data getOutput(Code code) throws LangarionError {
		return code.var(this.data, true);
	}
	
	public void execute(Code code) throws LangarionError {
		this.performNFSCall(this.loadFile(code), this.getOutput(code));
	}
	
	public abstract void performNFSCall(File file, Data data) throws LangarionError;
}