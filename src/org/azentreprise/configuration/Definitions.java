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
package org.azentreprise.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Definitions extends Configuration {

	public volatile List<String> instructions = new ArrayList<>();
	public volatile Map<String, String> transcriptions = new HashMap<>();
	public volatile List<String> properties = new ArrayList<>();
	
	public Definitions(File path, String identifier) throws IOException, SecurityException, IllegalArgumentException, IllegalAccessException {
		super(path, identifier);
		this.loadConfiguration();
	}

	public long getHash() {
		return ((long) this.instructions.hashCode() << 32) | (this.transcriptions.hashCode() & 0xFFFFFFFFL);
	}
}