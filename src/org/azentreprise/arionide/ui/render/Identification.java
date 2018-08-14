package org.azentreprise.arionide.ui.render;

import java.math.BigInteger;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

public class Identification {
	
	public static final int PARTITION_SIZE = 32;
	
	private static final BigInteger partitionMask = BigInteger.ONE.shiftLeft(PARTITION_SIZE).subtract(BigInteger.ONE);
	private static final BinaryOperator<BigInteger> shor = (a, b) -> (a.shiftLeft(PARTITION_SIZE).or(b));
	
	public static BigInteger[] makeScheme(int size) {
		BigInteger[] scheme = new BigInteger[size];
		
		for(int i = 0; i < size; i++) {
			scheme[i] = partitionMask.shiftLeft(i * PARTITION_SIZE);
		}
		
		return scheme;
	}
	
	public static BigInteger generateFingerprint(int... fields) {
		return IntStream.of(fields).mapToLong(Identification::convert).mapToObj(BigInteger::valueOf).reduce(shor).orElse(BigInteger.ZERO);
	}
	
	private static long convert(int in) {
		if(in < 0) {
			return 0b10000000000000000000000000000000l | -in;
		} else {
			return in;
		}
	}
}