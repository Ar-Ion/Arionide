package ch.innovazion.arionide.ui.overlay;

import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.layout.Surface;

public class Transition {
	
	public static Transition slowReplace = new Transition(255, 0, 1000);
	public static Transition replace = new Transition(255, 0, 500);
	public static Transition fade = new Transition(255, 63, 500);
	public static Transition none = new Transition(255, 0, 0);
	
	private final int activeOpacity;
	private final int inactiveOpacity;
	private final int duration;
	
	private Transition(int activeOpacity, int inactiveOpacity, int duration) {
		this.activeOpacity = activeOpacity;
		this.inactiveOpacity = inactiveOpacity;
		this.duration = duration;
	}
	
	private void enable(Component component) {
		component.setEnabled(true);
	}
	
	private void show(Surface surface) {
		surface.setVisible(true);
	}
	
	private void disable(Component component) {
		component.setEnabled(false);
	}
	
	private void hide(Surface surface) {
		surface.setVisible(false);
	}
	
	public void show(View view, Animation animation) {
		view.getComponents().forEach(this::enable);
		view.getComponents().forEach(this::show);

		view.viewWillAppear();
		show(view);
		
		animation.startAnimation(duration, activeOpacity);
	}
	
	public void hide(View view, Animation animation) {
		// Async to avoid a dead lock on the event dispatcher (flush)
		new Thread(() -> {
			IEventDispatcher dispatcher = view.getAppManager().getEventDispatcher();
			
			dispatcher.flush();
			dispatcher.pause();
			
			view.getComponents().forEach(this::disable);
			view.viewWillDisappear();
			
			animation.startAnimation(duration, after -> {
				if(inactiveOpacity == 0) {
					view.getComponents().forEach(this::hide);
					hide(view);
				}
								
				dispatcher.resume();
			}, inactiveOpacity);
		}).start();
	}
}
