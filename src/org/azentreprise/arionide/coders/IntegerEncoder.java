package org.azentreprise.arionide.coders;

import java.nio.charset.Charset;

public class IntegerEncoder implements Encoder<Long> {
	public int getVersionUID() {
		return 1;
	}

	public int getBackwardCompatibileVersionUID() {
		return 1;
	}

	public byte[] encode(Long decoded) {
		return decoded.toString().getBytes(Charset.forName("utf8"));
	}
}