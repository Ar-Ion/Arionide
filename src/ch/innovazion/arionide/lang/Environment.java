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
package ch.innovazion.arionide.lang;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ClickEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.TargetUpdateEvent;
import ch.innovazion.arionide.events.TeleportEvent;
import ch.innovazion.arionide.events.WheelEvent;
import ch.innovazion.arionide.lang.peripherals.SyncPeripheral;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Button;
import ch.innovazion.arionide.ui.overlay.components.Label;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;

public abstract class Environment implements EventHandler {

	private final long frequency;
	
	private final AtomicLong programCounter = new AtomicLong();
	private final AtomicLong clock = new AtomicLong();
	
	private final AtomicBoolean manualMode = new AtomicBoolean();
	private final Semaphore controlSemaphore = new Semaphore(0);
	private final AtomicBoolean interrupts = new AtomicBoolean();
	
	private long timerStart = 0L;
	private final AtomicBoolean timerStepRequest = new AtomicBoolean();
	private final AtomicBoolean timerState = new AtomicBoolean();
	
	private final Map<String, Peripheral> peripherals = new LinkedHashMap<>();
	
	private AppManager manager;
	
	private Bounds bounds;
	
	private Label pcLabel;
	private Label clkLabel;
	private Label timLabel;

	private Button runModeButton;
	private Button resetButton;
	private Button timerStateButton;
	private Button timerStepButton;

	protected Environment(long frequency) {
		this.frequency = frequency;
	}
	
	protected void registerPeripheral(Peripheral peripheral) {
		peripherals.put(peripheral.getUID(), peripheral);
	}
	
	public void sample() {
		peripherals.values().forEach(Peripheral::sample);
		
		if(!timerState.get()) {
			timerStart = clock.get();
		}
		
		if(pcLabel != null) {
			pcLabel.setLabel("Program Counter: 0x" + Long.toHexString(programCounter.get()));
		}
		
		if(clkLabel != null) {
			clkLabel.setLabel("Clock: " + formatTimestamp(clock.get()));
		}
		
		if(timLabel != null) {
			timLabel.setLabel("Timer: " + readTimer());
		}
		
		if(runModeButton != null) {
			if(manualMode.get()) {
				runModeButton.setLabel("Automatic");
			} else {
				runModeButton.setLabel("Manual");
			}
		}
		
		if(timerStateButton != null) {
			if(timerState.get()) {
				timerStateButton.setLabel("Stop");
			} else {
				timerStateButton.setLabel("Start");
			}
		}
	}
	
	public void init() {
		controlSemaphore.drainPermits();
	}
	
	public void asyncInterrupt(long address) {
		if(interrupts.get()) {
			boolean state = manualMode.getAndSet(true);
			
			synchronized(controlSemaphore) {
				controlSemaphore.drainPermits();
				
				while(!controlSemaphore.hasQueuedThreads()); // Poll the termination of the last instruction
				
				try {
					executeInterrupt(address);
				} catch (EvaluationException e) {
					e.printStackTrace();
				}
			}
			
			manualMode.set(state);	// Restore state
			controlSemaphore.release(); // Execute interrupt
		}
	}
	
	public void syncInterrupt(long address) {
		if(interrupts.get()) {
			try {
				executeInterrupt(address);
			} catch (EvaluationException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void executeInterrupt(long address) throws EvaluationException {
		programCounter.set(address);
	}
	
	public void onInstructionTerminated() {
		for(Peripheral peripheral : peripherals.values()) {
			if(peripheral instanceof SyncPeripheral) {
				((SyncPeripheral) peripheral).tick();
			}
		}
	}
		
	public Container create(AppManager manager, Bounds renderBounds, LayoutManager dedicatedLayoutManager) {
		this.manager = manager;
		this.bounds = renderBounds;
		
		manager.getEventDispatcher().registerHandler(this, 0.7f);
		
		Container container = new Container(null, dedicatedLayoutManager) {
			public void drawComponents(AppDrawingContext context) {
				sample();
				super.drawComponents(context);
			}
			
			public AppManager getAppManager() {
				return manager;
			}
		};
		
		Point firstPoint = renderBounds.getFirstPoint();
		Point secondPoint = renderBounds.getSecondPoint();
		
		dedicatedLayoutManager.register(container, null, firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());

		container.add(this.pcLabel = new Label(container, new String()).setColor(0x00FFAA), 0.0f, 0.0f, 0.3f, 0.1f);
		container.add(this.clkLabel = new Label(container, new String()).setColor(0x00FFAA), 0.4f, 0.0f, 0.6f, 0.1f);
		container.add(this.timLabel = new Label(container, new String()).setColor(0x00FFAA), 0.7f, 0.0f, 0.9f, 0.1f);
		container.add(this.resetButton = new Button(container, "Reset").setSignal("reset"), 0.0f, 0.1f, 0.3f, 0.15f);
		container.add(this.runModeButton = new Button(container, new String()).setSignal("toggleRunMode"), 0.4f, 0.1f, 0.6f, 0.15f);
		container.add(this.timerStateButton = new Button(container, "Start").setSignal("toggleTimerState"), 0.7f, 0.1f, 0.79f, 0.15f);
		container.add(this.timerStepButton = new Button(container, "Step").setSignal("timerStep"), 0.81f, 0.1f, 0.9f, 0.15f);
		
		float height = 0.8f / peripherals.size();
		float y = 0.2f;
		
		for(Peripheral peripheral : peripherals.values()) {
			Container display = new Container(container, dedicatedLayoutManager);

			container.add(new Label(container, peripheral.toString()).setColor(0x00FFAA).setVisible(true), 0.0f, y, 1.0f, y + 0.2f * height);
			container.add(display.setVisible(true), 0.0f, y + 0.2f * height, 1.0f, y + height);
			
			y += height;
			
			peripheral.createDisplay(display);
		}
		
		pcLabel.setVisible(true);
		clkLabel.setVisible(true);
		timLabel.setVisible(true);
		runModeButton.setVisible(true);
		runModeButton.setEnabled(true);
		resetButton.setVisible(true);
		resetButton.setEnabled(true);
		timerStateButton.setVisible(true);
		timerStateButton.setEnabled(true);
		timerStepButton.setVisible(true);
		timerStepButton.setEnabled(true);
		
		return container;
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(!runModeButton.isVisible()) {
			return;
		}
				
		if(event instanceof ClickEvent) {
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargettingSignal("toggleRunMode")) {
				String mode = runModeButton.getText().toString().toLowerCase();
				
				if(mode.equals("manual")) {
					controlSemaphore.drainPermits();
					manualMode.compareAndSet(false, true);
				} else if(mode.equals("automatic")) {
					manualMode.compareAndSet(true, false);
					controlSemaphore.release();
				}
			} else if(click.isTargettingSignal("reset")) {
				manager.getWorkspace().getProgramThread().terminate();
				controlSemaphore.drainPermits();
				programCounter.set(0);
				clock.set(0);
				timerStart = 0;
				timerState.set(false);
			} else if(click.isTargettingSignal("toggleTimerState")) {
				String state = timerStateButton.getText().toString().toLowerCase();
				
				if(state.equals("start")) {
					timerState.compareAndSet(false, true);
				} else if(state.equals("stop")) {
					timerState.compareAndSet(true, false);
				}
			} else if(click.isTargettingSignal("timerStep")) {
				timerStepRequest.set(true);
			}
		} else if(event instanceof WheelEvent) {
			WheelEvent wheel = (WheelEvent) event;
					
			boolean running = manager.getWorkspace().getProgramThread().isRunning();
						
			if(running && manualMode.get() && bounds != null && bounds.contains(wheel.getPoint())) {
				synchronized(controlSemaphore) {
					controlSemaphore.release((int) Math.abs(wheel.getDelta()));	
				}
				
				wheel.abortDispatching();
			}
		}
	}
	
	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(ClickEvent.class, WheelEvent.class);
	}
	
	public String formatTimestamp(long timestamp) {
		 return timestamp + " [ticks] (" + (1000.0f * timestamp / frequency) + "ms)";
	}
	
	public AtomicLong getProgramCounter() {
		return programCounter;
	}
	
	public AtomicLong getClock() {
		return clock;
	}
	
	public AtomicBoolean getInterrupts() {
		return interrupts;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Peripheral> T getPeripheral(String uid) {
		return (T) peripherals.get(uid);
	}

	public AtomicBoolean isManual() {
		return manualMode;
	}
	
	public AtomicBoolean getTimerStepRequest() {
		return timerStepRequest;
	}
	
	public String readTimer() {
		return formatTimestamp(clock.get() - timerStart);
	}
	
	public void setNextInstruction(Callable target) {
		if(manager != null) {
			manager.getEventDispatcher().fire(new TargetUpdateEvent(target.getIdentifier()));
		}
	}
	
	public void setNextFrame(Callable target) {
		if(manager != null) {
			manager.getEventDispatcher().fire(new TeleportEvent(target.getIdentifier()));
		}
	}
	
	public Semaphore getManualModeSemaphore() {
		return controlSemaphore;
	}
	
	public abstract Language getLanguage();
}
