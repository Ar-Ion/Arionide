package org.azentreprise.arionide.ui;

import java.util.Stack;

public class AlphaLayeringSystem {
	
	private final AppDrawingContext context;
	private final Object[] layers = new Object[AlphaLayer.values().length];

	protected AlphaLayeringSystem(AppDrawingContext context) {
		this.context = context;
		
		for(int i = 0; i < this.layers.length; i++) {
			Stack<Integer> layer = new Stack<>();
			layer.push(0xFF);
			this.layers[i] = layer;
		}
	}
	
	/* Returns the resulting alpha value */
	@SuppressWarnings("unchecked")
	public int push(AlphaLayer layer, int value) {
		((Stack<Integer>) this.layers[layer.ordinal()]).push(value);
		return this.commit();
	}
	
	/* Returns the resulting alpha value */
	@SuppressWarnings("unchecked")
	public int pop(AlphaLayer layer) {		
		((Stack<Integer>) this.layers[layer.ordinal()]).pop();
		return this.commit();
	}
	
	@SuppressWarnings("unchecked")
	private int commit() {
		int alpha = 0xFF;
		
		for(int i = 0; i < this.layers.length; i++) {
			alpha *= ((Stack<Integer>) this.layers[i]).lastElement();
			alpha /= 255;
		}
		
		this.context.setAlpha(alpha);
		
		return alpha;
	}
}