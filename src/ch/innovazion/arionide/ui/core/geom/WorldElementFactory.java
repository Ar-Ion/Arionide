/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package ch.innovazion.arionide.ui.core.geom;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldElementFactory {	
	
	private final long seed;
	private final Random random;
	
	private Supplier<Vector3f> axisGenerator;
	private Function<Vector3f, Vector3f> baseGenerator;

	protected WorldElementFactory() {
		this(System.nanoTime());
	}
	
	protected WorldElementFactory(long seed) {
		this(seed, new Random(seed));
	}
	
	protected WorldElementFactory(long seed, Function<Vector3f, Vector3f> baseGenerator, Supplier<Vector3f> axisGenerator) {
		this(seed, new Random(seed), baseGenerator, axisGenerator);
	}
	
	private WorldElementFactory(long seed, Random random) {
		this(seed, random, getDefaultBaseGenerator(random), getDefaultAxisGenerator(random));
	}
	
	private WorldElementFactory(long seed, Random random, Function<Vector3f, Vector3f> baseGenerator, Supplier<Vector3f> axisGenerator) {
		this.seed = seed;
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

	protected void reset() {
		this.random.setSeed(this.seed);
		this.baseGenerator = getDefaultBaseGenerator(random);
		this.axisGenerator = getDefaultAxisGenerator(random);
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