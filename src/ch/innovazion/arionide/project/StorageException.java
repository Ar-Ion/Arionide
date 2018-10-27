package ch.innovazion.arionide.project;

import java.io.IOException;

public class StorageException extends IOException {

	private static final long serialVersionUID = -8579466998677481750L;

	public StorageException(String exception) {
		super(exception);
	}
	
	public StorageException(Exception parent) {
		super(parent);
	}
	
	public StorageException(String message, Exception parent) {
		super(message, parent);
	}
}
