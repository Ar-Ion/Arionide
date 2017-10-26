package org.azentreprise.arionide.coders;

public class CameraEncoder implements Encoder<CameraInfo> {
	public int getVersionUID() {
		return 102;
	}

	public int getBackwardCompatibileVersionUID() {
		return 102;
	}

	public byte[] encode(CameraInfo decoded) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(decoded.getX());
		sb.append('|');
		sb.append(decoded.getY());
		sb.append('|');
		sb.append(decoded.getZ());
		sb.append('|');
		sb.append(decoded.getYaw());
		sb.append('|');
		sb.append(decoded.getPitch());
		
		return sb.toString().getBytes(Coder.charset);
	}
}