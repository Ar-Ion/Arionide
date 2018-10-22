package org.azentreprise.arionide.ui.core.geom;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldElementFactory {	
	
	private final Random random;
	
	private Supplier<Vector3f> axisGenerator;
	private Function<Vector3f, Vector3f> baseGenerator;

	protected WorldElementFactory() {
		this(new Random());
	}
	
	protected WorldElementFactory(long seed) {
		this(new Random(seed));
	}
	
	protected WorldElementFactory(Random random) {
		this(random, getDefaultBaseGenerator(random), getDefaultAxisGenerator(random));
	}
	
	protected WorldElementFactory(Random random, Function<Vector3f, Vector3f> baseGenerator, Supplier<Vector3f> axisGenerator) {
		this.random = random;
		this.baseGenerator = baseGenerator;
		this.axisGenerator = axisGenerator;
	}
	
	protected void updateBaseGenerator(Function<Vector3f, Vector3f> baseGenerator) {
		this.baseGenerator = baseGenerator;
	}
	
	protected void updateAxisGenerator(Supplier<Vector3f> axisGenerator) {
		this.axisGenerator = axisGenerator;
	}
	
	protected Function<Vector3f, Vector3f> getBaseGenerator() {
		return this.baseGenerator;
	}
	
	protected Supplier<Vector3f> getAxisGenerator() {
		return this.axisGenerator;
	}
	
	protected Random getRandom() {
		return this.random;
	}
	
	protected WorldElement makeRandomTrivial() {
		return this.make(-1, "(null)", "(null)", new Vector3f(), new Vector4f(), new Vector3f(), -1.0f, false);
	}
	
	protected WorldElement makeBaseRandomTrivial() {
		Vector3f virtual = this.axisGenerator.get();
		Vector3f base = this.baseGenerator.apply(virtual);
		return new WorldElement(-1, "(null)", "(null)", new Vector3f(), base, new Vector3f(0.0f, 1.0f, 0.0f), new Vector4f(), new Vector3f(), -1.0f, false);
	}
	
	protected WorldElement makeAxisRandomTrivial() {
		Vector3f axis = this.axisGenerator.get();
		return new WorldElement(-1, "(null)", "(null)", new Vector3f(), new Vector3f(1.0f, 0.0f, 0.0f), axis, new Vector4f(), new Vector3f(), -1.0f, false);
	}
	
	protected WorldElement make(int id, String name, String desc, Vector3f center, Vector4f color, Vector3f spotColor, float size, boolean accessAllowed) {
		Vector3f axis = this.axisGenerator.get();
		Vector3f base = this.baseGenerator.apply(axis).normalize();
				
		return new WorldElement(id, name, desc, center, base, axis, color, spotColor, size, accessAllowed);
	}
	
	protected static Function<Vector3f, Vector3f> getDefaultBaseGenerator(Random random) {
		return getCrossedBaseGenerator(random).andThen(Vector3f::normalize);
	}
	
	private static Function<Vector3f, Vector3f> getCrossedBaseGenerator(Random random) {
		return getRandomNormalized(random)::cross;
	}
	
	protected static Supplier<Vector3f> getDefaultAxisGenerator(Random random) {
		return () -> getRandomNormalized(random);
	}
	
	// It is """Random""": not uniform (more likely to generate a vertical vector)
	private static Vector3f getRandomNormalized(Random random) {
		return new Vector3f(random.nextFloat() - 0.5f, random.nextFloat() * 2.0f, random.nextFloat() - 0.5f).normalize();
	}
	
	protected static WorldElement makeTrivial() {
		return new WorldElement(-1, "(null)", "(null)", new Vector3f(), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector4f(), new Vector3f(), -1.0f, false);
	}
}