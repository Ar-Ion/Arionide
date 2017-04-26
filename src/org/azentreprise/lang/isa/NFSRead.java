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
package org.azentreprise.lang.isa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.azentreprise.lang.Data;
import org.azentreprise.lang.LangarionError;

public class NFSRead extends NativeFSCall {

	public NFSRead(String file, String data) {
		super(file, data);
	}

	public void performNFSCall(File file, Data data) throws LangarionError {
		try {
			data.setData(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			throw new LangarionError(file.getAbsolutePath() + " : Read failed");
		}
	}
}
