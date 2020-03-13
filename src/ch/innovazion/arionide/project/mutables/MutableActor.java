package ch.innovazion.arionide.project.mutables;

import ch.innovazion.arionide.lang.symbols.Actor;

public class MutableActor extends MutableStructure {

	private static final long serialVersionUID = -5833983024064882736L;

	private final Actor wrapper = new Actor();
	
	public MutableActor(int structureID, int specID) {
		super(structureID, specID);
	}
	
	public Actor getWrapper() {
		return wrapper;
	}
}
