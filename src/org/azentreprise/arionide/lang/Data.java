package org.azentreprise.arionide.lang;

public class Data extends SpecificationElement {

	private static final long serialVersionUID = -3387831385820354646L;

	private int type;

	public Data(String name, String value, int type) {
		super(name, value);
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public boolean equals(Object other) {
		if(other instanceof Data) {
			Data casted = (Data) other;
			return super.equals(other) && this.type == casted.type;
		} else {
			return false;
		}
	}
	
	public Data clone() {
		return new Data(this.getName(), this.getRawValue(), this.type);
	}
}