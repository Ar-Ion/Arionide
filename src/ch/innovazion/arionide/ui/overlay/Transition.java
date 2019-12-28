package ch.innovazion.arionide.ui.overlay;

import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.ui.animations.Animation;

public class Transition {
	
	public static Transition replace = new Transition(255, 0, 500);
	public static Transition fade = new Transition(255, 127, 500);
	public static Transition none = new Transition(255, 0, 0);
	
	private final int activeOpacity;
	private final int inactiveOpacity;
	private final int duration;
	
	private Transition(int activeOpacity, int inactiveOpacity, int duration) {
		this.activeOpacity = activeOpacity;
		this.inactiveOpacity = inactiveOpacity;
		this.duration = duration;
	}
	
	public void show(View view, Animation animation) {				
		view.show();
		animation.startAnimation(duration, activeOpacity);
	}
	
	public void hide(View view, Animation animation) {
		// Async to avoid a dead lock on the event dispatcher (flush)
		new Thread(() -> {
			IEventDispatcher dispatcher = view.getAppManager().getEventDispatcher();
			
			dispatcher.flush();
			dispatcher.pause();
			
			animation.startAnimation(duration, after -> {
				if(inactiveOpacity == 0) {
					view.hide();
				} else {
					for(Component component : view.getComponents()) {
						component.hide();
					}
				}
				
				dispatcher.resume();
			}, inactiveOpacity);
		}).start();
	}
}
