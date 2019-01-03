package ch.innovazion.arionide.project.mutables;

import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.project.StructureMeta;

public class MutableCodeMeta extends MutableStructureMeta {
	private static final long serialVersionUID = 1259382903286126347L;
	
	private final StructureMeta codeBase;
	private Specification specification;

	public MutableCodeMeta(StructureMeta codeBase) {
		super(-1);
		
		this.codeBase = codeBase;
		this.specification = new Specification(codeBase.getSpecification());
	}
	
	public String getName() {
		return codeBase.getName();
	}
	
	public int getColorID() {
		return codeBase.getColorID();
	}

	public int getSpotColorID() {
		return codeBase.getSpotColorID();
	}
	
	public boolean isAccessAllowed() {
		return codeBase.isAccessAllowed();
	}

	public Specification getSpecification() {
		return specification;
	}
	
	public int getLanguage() {
		return codeBase.getLanguage();
	}

	public String toString() {
		return "[Name: " + getName() + "; Comment: " + getComment() + "; Specification: [" + this.specification + "]]";
	}
}