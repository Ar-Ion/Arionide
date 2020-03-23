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
package ch.innovazion.arionide.lang.symbols;

import java.util.stream.Stream;

public abstract class AtomicValue extends Information {

	private static final long serialVersionUID = 3707550318915733897L;

	public synchronized void connect(Information value) throws SymbolResolutionException {
		throw new SymbolResolutionException("Cannot connect information to an atomic value");
	}
	
	public synchronized void disconnect(Information value) throws SymbolResolutionException {
		throw new SymbolResolutionException("Cannot disconnect information from an atomic value");
	}
	
	public synchronized Information resolve(int id) throws SymbolResolutionException {
		throw new SymbolResolutionException("Cannot resolve symbols from an atomic value");
	}
	
	public synchronized Information resolve(String label) throws SymbolResolutionException {
		throw new SymbolResolutionException("Cannot resolve symbols from an atomic value");
	}
	
	protected abstract Stream<Bit> getRawStream();
	public abstract AtomicValue clone();
}
