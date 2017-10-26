package org.azentreprise.arionide.coders;

public class CameraDecoder implements Decoder<CameraInfo> {
	public int getVersionUID() {
		return 102;
	}

	public int getBackwardCompatibileVersionUID() {
		return 102;
	}

	public CameraInfo decode(byte[] encoded) {
		String data = new String(encoded, Coder.charset);
				
		String[] parts = data.split("\\|");
		
		float x = Float.parseFloat(parts[0]);
		float y = Float.parseFloat(parts[1]);
		float z = Float.parseFloat(parts[2]);
		float yaw = Float.parseFloat(parts[3]);
		float pitch = Float.parseFloat(parts[4]);

		return new CameraInfo(x, y, z, yaw, pitch);
	}
}