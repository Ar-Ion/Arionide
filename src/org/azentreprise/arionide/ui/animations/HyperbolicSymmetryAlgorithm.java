package org.azentreprise.arionide.ui.animations;

public class HyperbolicSymmetryAlgorithm implements TransformationAlgorithm {
	
	private final double base;
	private final double scaling;
	private final double factor;
	
	public HyperbolicSymmetryAlgorithm(double base, double scaling) {
		this.base = base;
		this.scaling = scaling;
		this.factor = 0.5d / this.computeWithoutVectorTranslation(0.5d, 1.0d);
	}
	
	public double compute(double x) {
		if(x < 0.5d) {
			return 0.5d - this.computeWithoutVectorTranslation(-x + 0.5d, this.factor);
		} else {
			return 0.5d + this.computeWithoutVectorTranslation(x - 0.5d, this.factor);
		}
	}
	
	private double computeWithoutVectorTranslation(double x, double factor) { // preserves function oddness
		return factor * (1.0d - Math.pow(this.base, -this.scaling * x));
	}
}