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

import java.nio.charset.Charset;

public interface Coder<T> {
	
	/* Since UTF-8 is 8-bits, an index of 0 for the byte array is safe to use. */
	public static final byte sectionStart = "{".getBytes(Charset.forName("utf8"))[0];
	public static final byte sectionEnd = "}".getBytes(Charset.forName("utf8"))[0];
	public static final byte separator = ":".getBytes(Charset.forName("utf8"))[0];
	public static final String whitespaceRegex = "\\s";
	
	public int getVersionUID();
	public int getBackwardCompatibileVersionUID();
}
