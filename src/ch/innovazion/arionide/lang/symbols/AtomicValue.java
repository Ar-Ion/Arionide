package ch.innovazion.arionide.lang.symbols;

import java.util.stream.Stream;

public abstract class AtomicValue extends Information {

	private static final long serialVersionUID = 3707550318915733897L;

	public synchronized void connect(Information value) throws SymbolResolutionException {
		throw new SymbolResolutionException("Cannot connect information to an atomic value");
	}
	
	public synchronized void disconnect(Information value) throws SymbolResolutionException {
		throw new SymbolResolutionException("Cannot disconnect information from an atomic value");
	}
	
	public synchronized Information resolve(int id) throws SymbolResolutionException {
		throw new SymbolResolutionException("Cannot resolve symbols from an atomic value");
	}
	
	public synchronized Information resolve(String label) throws SymbolResolutionException {
		throw new SymbolResolutionException("Cannot resolve symbols from an atomic value");
	}
	
	protected abstract Stream<Bit> getRawStream();
}
