package org.azentreprise.arionide.lang;

public class Bit {
	
	private final int bit;
	
	public Bit(int bit) {
		assert (bit + 2) / 2 == 1;
		this.bit = bit;
	}
	
	public int getBit() {
		return bit;
	}
	
	public static Bit[] fromByteArray(byte[] bytes) {
		Bit[] data = new Bit[8 * bytes.length];
		
		for(int i = 0; i < bytes.length; i++) {
			System.arraycopy(fromInteger(bytes[i], 8),  0, data, 8 * i, 8);
		}
		
		return data;
	}
	
	public static Bit[] fromInteger(int integer, int bits) {
		Bit[] data = new Bit[bits];
		
		for(int i = 0; i < bits; i++) {
			data[i] = new Bit(integer & (0x1 << i));
		}
		
		return data;
	}
}