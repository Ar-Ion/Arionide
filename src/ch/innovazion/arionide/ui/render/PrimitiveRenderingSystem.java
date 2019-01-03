/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package ch.innovazion.arionide.ui.render;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PrimitiveRenderingSystem {
	
	private final Map<PrimitiveType, RenderingQueue> queues = new HashMap<>();
	
	public void registerPrimitive(PrimitiveType type, RenderingContext context) {
		assert type != null;
		assert context != null;
		
		this.queues.put(type, new RenderingQueue(context));
		
		context.load();
	}
	
	public void renderLater(Object object) {
		this.dispatch(object, this::enqueue);
	}
	
	public void renderDirect(Object object) {
		this.dispatch(object, this::render);
	}
	
	private void dispatch(Object object, BiConsumer<Primitive, RenderingQueue> action) {
		assert object != null;
		
		if(object instanceof Primitive) {
			Primitive primitive = (Primitive) object;
			RenderingQueue queue = this.queues.get(primitive.getType());
			
			if(queue != null) {
				action.accept(primitive, queue);
			} else {
				throw new IllegalArgumentException("Passed primitive has no matching rendering queue: " + object.getClass().getCanonicalName());
			}
		} else if(object instanceof PrimitiveMulticaster) {
			PrimitiveMulticaster multicaster = (PrimitiveMulticaster) object;
			
			for(Primitive primitive : multicaster.getPrimitives()) {
				this.dispatch(primitive, action);
			}
		} else {
			throw new IllegalArgumentException("Passed object neither a primitive nor a primitive multicaster: " + object.getClass().getCanonicalName());
		}
	}
	
	private void enqueue(Primitive primitive, RenderingQueue queue) {
		queue.add(primitive);
	}
	
	private void render(Primitive primitive, RenderingQueue queue) {
		RenderingContext context = queue.getContext();
		BigInteger difference = queue.getAndUpdateStateDifference(primitive);
				
		context.enter();
		
		for(BigInteger identifier : context.getIdentificationScheme()) {
			if(!difference.and(identifier).equals(BigInteger.ZERO)) {
				primitive.updateProperty(context.getIdentificationScheme().length - 1 - identifier.getLowestSetBit() / Identification.PARTITION_SIZE);
			}
		}
		
		int actions = primitive.getRequestedActions();
		
		for(int mask = 1; mask < 1 << 30; mask <<= 1) {
			if((actions & mask) != 0) {
				primitive.processAction(mask);
			}
		}
		
		primitive.render();
			
		context.exit();
	}
	
	public void processRenderingQueue() {		
		for(RenderingQueue queue : this.queues.values()) {
			while(!queue.isEmpty()) {
				this.render(queue.poll(), queue);
			}
		}
	}

	public void updateAspectRatio(float newRatio) {
		for(RenderingQueue queue : this.queues.values()) {
			queue.getContext().onAspectRatioUpdate(newRatio);
		}
	}
}