package ch.innovazion.arionide.lang.symbols;

public class ShadowCallable implements Callable {
	
	private final Callable source;
	
	public ShadowCallable(Callable source) {
		this.source = source;
	}

	public int getIdentifier() {
		return source.getIdentifier();
	}

	public String getName() {
		return source.getName();
	}

	public Specification getSpecification() {
		return source.getSpecification();
	}

	public String getLanguage() {
		return source.getLanguage();
	}

}
