/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.ui.render;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

import org.azentreprise.configuration.Project;
import org.azentreprise.lang.Link;
import org.azentreprise.lang.Object;
import org.azentreprise.ui.SharedFramebuffer;
import org.azentreprise.ui.Style.MotionControl;

public class ComplexProjectRenderer extends ProjectRenderer implements Runnable {
	
	private final Queue<Object> transmission = new ArrayDeque<Object>();
	
	private final SharedFramebuffer framebuffer;
	private final List<Particle> particles = Collections.synchronizedList(new ArrayList<Particle>());
	private volatile List<Thread> threads = new ArrayList<Thread>();
	private volatile boolean running = true;
	
	public ComplexProjectRenderer(Project project) {
		super(project);
		
		this.framebuffer = new SharedFramebuffer(project.gridSize * project.pixelsPerCell, project.gridSize * project.pixelsPerCell);
		
		this.init();
	}
	
	public void run() {
		Object obj = this.transmission.poll();
		
		int x = (int) ((obj.getPosition(1).getX() + 0.5f) * this.project.pixelsPerCell);
		int y = (int) ((obj.getPosition(1).getY() + 0.5f) * this.project.pixelsPerCell);
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		List<Line2D> lines = new ArrayList<Line2D>();
		
		for(Link link : this.project.links) {
			if(link.shouldBeRendered()) {
				Point2D p1 = link.getLine(1).getP1();
				Point2D p2 = link.getLine(1).getP2();
				p1 = new Point2D.Double((p1.getX() + 0.5f) * this.project.pixelsPerCell, (p1.getY() + 0.5f) * this.project.pixelsPerCell);
				p2 = new Point2D.Double((p2.getX() + 0.5f) * this.project.pixelsPerCell, (p2.getY() + 0.5f) * this.project.pixelsPerCell);
				lines.add(new Line2D.Double(p1, p2));
			}
		}
		
		while(this.running) {
			synchronized(this.particles) {

				for(int i = 0; i < obj.getStyle().getWaveControl().getParticlesPerWave(); i++) {
					this.particles.add(new Particle(x, y, obj.getStyle().getColorRange().getRandomColor(), random.nextDouble(0.0D, Math.PI * 2), obj.getStyle().getMotionControl(), obj.getStyle().getAlphaConstant(), lines));
				}
			}
			
			try {
				Thread.sleep(obj.getStyle().getWaveControl().getTimeBetweenWaves());
			} catch (InterruptedException e) {
				;
			}
		}
	}
	
	public void render(Graphics2D g2d) {
		synchronized(this.particles) {
			Iterator<Particle> iterator = this.particles.iterator();
			while(iterator.hasNext()) {
				if(iterator.next().render(this.framebuffer)) {
					iterator.remove();
				}
			}
		}
		
		this.framebuffer.apply(g2d, this.project.gridPosX, this.project.gridPosY);
	}
	
	public void clean() {
		this.running = false;
		
		for(Thread thread : this.threads) {
			thread.interrupt();
		}
		
		this.threads.clear();
		this.particles.clear();
		this.framebuffer.clear();
		this.transmission.clear();
		
		System.gc();
	}
	
	public void init() {
		this.running = true;
		
		for(Object obj : this.project.objects) {
			if(obj.shouldBeRendered()) {
				this.transmission.add(obj);

				Thread thread = new Thread(this);
				
				thread.setName("Arionide Particle Generator");
				thread.setDaemon(true);
				
				this.threads.add(thread);
				
				thread.start();
			}
		}
	}
	
	public void reset(Project project) {
		this.project = project;
		this.clean();
		this.init();
	}
	
	private static class Particle {
		
		private final int color, x, y;
		private final long initialTime = System.currentTimeMillis();
		private final double direction, alphaConstant;
		private final MotionControl motionControl;
		private final List<Line2D> alphaLines;
				
		private Particle(int x, int y, int color, double direction, MotionControl motionControl, double alphaConstant, List<Line2D> alphaLines) {
			this.x = x;
			this.y = y;
			this.color = color;
			this.direction = direction;
			this.motionControl = motionControl;
			this.alphaConstant = alphaConstant;
			this.alphaLines = alphaLines;
		}
		
		private boolean render(SharedFramebuffer framebuffer) {
			float delta = (System.currentTimeMillis() - this.initialTime) / 1000.0f;
			
			double linearDistance = 0.0D;
			double angle = this.direction;
			
			for(int i = 0; i < this.motionControl.getLinear().length; i++) {
				linearDistance += this.motionControl.getLinear()[i] * Math.pow(delta, i);
			}
			
			for(int i = 0; i < this.motionControl.getRotation().length; i++) {
				angle += this.motionControl.getRotation()[i] * Math.pow(delta, i);
			}
			
			
			double posX = linearDistance * Math.sin(angle);
			double posY = linearDistance * Math.cos(angle);
			
			double alphaLineDelta = 255.0D;
			
			for(Line2D line : this.alphaLines) {
				alphaLineDelta = Math.min(alphaLineDelta, line.ptSegDist(this.x + posX, this.y + posY));
			}
			
			int alpha = (int) (255 * Math.pow(Math.E, -delta * this.alphaConstant) + 255.0D / alphaLineDelta);
			
			if(alpha > 1) {
				framebuffer.draw(this.x + (int) posX, this.y + (int) posY, (Math.min(alpha, 255) << 24) + this.color);
				return false;
			} else {
				return true;
			}
		}
	}
}
