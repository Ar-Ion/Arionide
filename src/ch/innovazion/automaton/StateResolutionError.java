package ch.innovazion.automaton;

public class StateResolutionError extends RuntimeException {

	private static final long serialVersionUID = -2026204288735934952L;

	protected StateResolutionError(String identifier) {
		super("State resolution failed for identifier '" + identifier + "'");
	}
}
