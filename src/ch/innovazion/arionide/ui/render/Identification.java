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
package ch.innovazion.arionide.ui.render;

import java.math.BigInteger;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

import ch.innovazion.arionide.Utils;

public class Identification {
	
	public static final int PARTITION_SIZE = 32;
	
	private static final BigInteger partitionMask = BigInteger.ONE.shiftLeft(PARTITION_SIZE).subtract(BigInteger.ONE);
	private static final BinaryOperator<BigInteger> shor = (a, b) -> (a.shiftLeft(PARTITION_SIZE).or(b));
	
	private static final Random rand = new Random();
	
	public static BigInteger[] makeScheme(int size) {
		BigInteger[] scheme = new BigInteger[size];
		
		for(int i = 0; i < size; i++) {
			scheme[i] = partitionMask.shiftLeft(i * PARTITION_SIZE);
		}
		
		return scheme;
	}
	
	
	public static BigInteger randomTrace(int partitions) {
		return rand.longs(partitions).mapToObj(BigInteger::valueOf).reduce(shor).orElse(BigInteger.ZERO);
	}
	
	public static BigInteger generateFingerprint(int... fields) {
		return generateFingerprint(BigInteger.ZERO, fields);
	}
	
	public static BigInteger generateFingerprint(BigInteger parent, int... fields) {
		return parent.shiftLeft(PARTITION_SIZE * fields.length).or(IntStream.of(fields).mapToLong(Utils::convertToUnsigned).mapToObj(BigInteger::valueOf).reduce(shor).orElse(BigInteger.ZERO));
	}
}