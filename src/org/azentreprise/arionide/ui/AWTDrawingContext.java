package org.azentreprise.arionide.ui;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import org.azentreprise.arionide.Arionide;
import org.azentreprise.arionide.IWorkspace;
import org.azentreprise.arionide.events.IEventDispatcher;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.primitives.Resources;

public class AWTDrawingContext extends Panel implements AppDrawingContext, WindowListener, ComponentListener {

	private static final long serialVersionUID = 1171699360872561984L;
	
	private final Map<RenderingHints.Key, Object> renderingHints = new HashMap<>();
	
	private final AppManager theManager;
	private final Frame theFrame;
	private IEventDispatcher dispatcher;
	
	public AWTDrawingContext(int width, int height) {
 		this.theManager = new AppManager();
		this.theFrame = new Frame("Arionide");
		
		this.theFrame.setSize(width, height);
		this.theFrame.setLocationRelativeTo(null);
		
		this.theFrame.addWindowListener(this);
		this.theFrame.addComponentListener(this);
	}
	
	public void setup(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public void paint(Graphics g) {
		this.draw((Graphics2D) g);
	}
	
	public void update() {
		this.repaint();
	}
	
	public void loadUI(Arionide theInstance, IWorkspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		this.theFrame.setVisible(true);
		this.theManager.loadUI(theInstance, workspace, resources, renderer, manager);
	}

	public void draw(Graphics2D g2d) {
		g2d.setRenderingHints(this.renderingHints);
		this.theManager.draw(g2d);
	}

	public void setupRenderingProperties() {
		this.renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}

	public void windowOpened(WindowEvent e) {
		;
	}

	public void windowClosing(WindowEvent e) {
		;
	}

	public void windowClosed(WindowEvent e) {
		this.theManager.shutdown();
	}

	public void windowIconified(WindowEvent e) {
		;
	}

	public void windowDeiconified(WindowEvent e) {
		;
	}

	public void windowActivated(WindowEvent e) {
		;
	}

	public void windowDeactivated(WindowEvent e) {
		;
	}

	public void componentResized(ComponentEvent e) {
		this.dispatcher.fire(new InvalidateLayoutEvent());
	}

	public void componentMoved(ComponentEvent e) {
		;
	}

	public void componentShown(ComponentEvent e) {
		;
	}

	public void componentHidden(ComponentEvent e) {
		;
	}
}