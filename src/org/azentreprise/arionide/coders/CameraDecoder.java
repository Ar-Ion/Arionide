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
		
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
		double z = Double.parseDouble(parts[2]);
		double yaw = Double.parseDouble(parts[3]);
		double pitch = Double.parseDouble(parts[4]);

		return new CameraInfo(x, y, z, yaw, pitch);
	}
}