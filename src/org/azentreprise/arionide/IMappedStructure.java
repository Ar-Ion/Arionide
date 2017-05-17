package org.azentreprise.arionide;

import org.azentreprise.arionide.coders.Decoder;
import org.azentreprise.arionide.coders.Encoder;

public interface IMappedStructure {
	public <T> T getProperty(String key, Decoder<T> decoder);
	public <T> void setProperty(String key, T value, Encoder<T> encoder);
}
