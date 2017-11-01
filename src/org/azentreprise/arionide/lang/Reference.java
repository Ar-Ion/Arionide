package org.azentreprise.arionide.lang;

import java.util.List;

public class Reference extends SpecificationElement {

	private static final long serialVersionUID = -3387831385820354646L;

	private List<Data> parameters;

	public Reference(String name, String value, List<Data> parameters) {
		super(name, value);
		this.parameters = parameters;
	}
	
	public List<Data> getParameters() {
		return this.parameters;
	}
	
	public void setParameters(List<Data> parameters) {
		this.parameters = parameters;
	}
	
	public String toString() {
		return super.toString() + " <" + String.join("; ", this.parameters.stream().map(Data::toString).toArray(String[]::new)) + ">";
	}
	
	public boolean equals(Object other) {
		if(other instanceof Reference) {
			Reference casted = (Reference) other;
			return super.equals(other) && this.parameters.equals(casted.parameters);
		} else {
			return false;
		}
	}
	
	public Reference clone() {
		return new Reference(this.getName(), this.getRawValue(), this.parameters);
	}
}