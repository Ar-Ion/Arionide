package ch.innovazion.arionide.project.mutables;

import ch.innovazion.arionide.project.StructureModel;

public class MutableAtomicStructure extends MutableStructure {
	
	private static final long serialVersionUID = -7922459397738938571L;

	public MutableAtomicStructure(int structureID, int specID) {
		super(structureID, specID);
	}
	
	public MutableAtomicStructure(int structureID, int specID, StructureModel model) {
		super(structureID, specID, model);
	}
	
	public boolean isAccessAllowed() {
		return false;
	}
	
	public void setAccessAllowed(boolean allowed) {
		throw new RuntimeException("Unsupported operation");
	}
	
	public boolean isLambda() {
		return false;
	}
	
	public void setLambda(boolean isLambda) {
		throw new RuntimeException("Unsupported operation");
	}
	
	public void setLanguage(String language) {
		throw new RuntimeException("Unsupported operation");
	}
	
	public String getLanguage() {
		return null;
	}
}
