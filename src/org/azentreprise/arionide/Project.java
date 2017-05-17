package org.azentreprise.arionide;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.coders.Decoder;
import org.azentreprise.arionide.coders.Encoder;
import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;

public class Project implements IProject {

	private final File path;
	private final Map<String, byte[]> properties = new HashMap<>();
	
	protected Project(File path) {
		this.path = path;
		this.load();
	}
	
	@IAm("loading a project")
	private void load() {
		try {
			byte[] data = Files.readAllBytes(this.path.toPath());
			
			int startIndex = 0;
			int endIndex = 0;
			
			while((endIndex = Arrays.binarySearch(data, startIndex, data.length, Coder.separator)) > -1) {
				byte[] keyBuffer = new byte[endIndex - startIndex];
				System.arraycopy(data, startIndex, keyBuffer, 0, keyBuffer.length);
				
				startIndex = Arrays.binarySearch(data, endIndex + 1, data.length, Coder.sectionStart);
				endIndex = Arrays.binarySearch(data, startIndex, data.length, Coder.sectionEnd);
				byte[] valueBuffer = new byte[endIndex - startIndex];
				System.arraycopy(data, startIndex, valueBuffer, 0, valueBuffer.length);
				
				this.properties.put(new String(keyBuffer, Charset.forName("utf8")).replaceAll(Coder.whitespaceRegex, ""), valueBuffer);
				
				startIndex = endIndex;
			}
			
		} catch (Exception exception) {
			Debug.exception(exception);
		}
	}
	
	public String getName() {
		return null;
	}

	public File getPath() {
		return this.path;
	}

	public <T> T getProperty(String key, Decoder<T> decoder) {
		return decoder.decode(this.properties.get(key));
	}

	public <T> void setProperty(String key, T value, Encoder<T> encoder) {
		this.properties.put(key, encoder.encode(value));
	}

	public void save() {
		
	}

	@Override
	public void checkVersionCompatibility() {
	}
}