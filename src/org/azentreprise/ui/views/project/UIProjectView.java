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
package org.azentreprise.ui.views.project;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.azentreprise.Arionide;
import org.azentreprise.Debug;
import org.azentreprise.Projects;
import org.azentreprise.configuration.Project;
import org.azentreprise.lang.Function;
import org.azentreprise.lang.Object;
import org.azentreprise.lang.ObjectType;
import org.azentreprise.ui.UIEvents;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.animations.Animation;
import org.azentreprise.ui.animations.FieldModifierAnimation;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.render.ComplexProjectRenderer;
import org.azentreprise.ui.render.ProjectRenderer;
import org.azentreprise.ui.render.RoundRectRenderer;
import org.azentreprise.ui.render.SimpleProjectRenderer;
import org.azentreprise.ui.views.UIView;

public abstract class UIProjectView extends UIView implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, UIButtonClickListener {
	
	private static Map<String, Point2D> selected = new HashMap<String, Point2D>();
	
	private final Animation selectionAnimation = new FieldModifierAnimation("selectionOpacity", UIProjectView.class, this);
	private final Animation errorMessageAnimation = new FieldModifierAnimation("errorMessageOpacity", UIProjectView.class, this);
	private final Animation overlayAnimation = new FieldModifierAnimation("overlayOpacity", UIProjectView.class, this);
	private final Animation pixelsPerCellAnimation = new FieldModifierAnimation("pixelsPerCell", Project.class, Projects.getCurrentProject());
	private final Animation rendererOpacityAnimation = new FieldModifierAnimation("rendererOpacity", UIProjectView.class, this);
	private final Animation centerXAnimation = new FieldModifierAnimation("gridPosX", Project.class, Projects.getCurrentProject());
	private final Animation centerYAnimation = new FieldModifierAnimation("gridPosY", Project.class, Projects.getCurrentProject());
	
	protected final Project project = Projects.getCurrentProject();
	protected Shape controls;

	private Point selectionStart;
	private Point currentMousePosition;
	private int selectionOpacity = 0;
	private int errorMessageOpacity = 0;
	private int overlayOpacity = 0;
	private float rendererOpacity = 1.0f;
	private String errorMessage = new String();
	private Rectangle bounds;
	private Map<Point2D, Color> highlighted = new HashMap<>();
	private Shape miniGrid;
	private float xMult, yMult;
	private boolean isMovingUsingMinigrid = false;
	private boolean needValidation = false;
	private String newFunction;
	private boolean editableView = true;
	
	public static final ProjectRenderer simpleRenderer = new SimpleProjectRenderer(Projects.getCurrentProject());
	public static final ProjectRenderer complexRenderer = new ComplexProjectRenderer(Projects.getCurrentProject());

	public UIProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent, 0, 0, 1, 1);
		
		rootComponent.addMouseListener(this);
		rootComponent.addMouseMotionListener(this);
		rootComponent.addMouseWheelListener(this);
		((JFrame) rootComponent).getContentPane().addKeyListener(this);
		
		this.add(new UIButton(this, 0.3f, -0.01f, 0.4f, 0.05f, "Hierarchy").setHandler(this, "hierarchy"));
		this.add(new UIButton(this, 0.45f, -0.01f, 0.55f, 0.05f, "Inheritance").setHandler(this, "inheritance"));
		this.add(new UIButton(this, 0.6f, -0.01f, 0.7f, 0.05f, "Callers - Callees").setHandler(this, "call"));

		this.add(new UIButton(this, 0.05f, 0.45f, 0.1f, 0.55f, "+").setHandler(this, "add"));
	}
	
	public void draw(Graphics2D g2d) {
		this.bounds = g2d.getClipBounds();
		
		Composite context = g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.rendererOpacity * ((AlphaComposite) context).getAlpha()));
		
		UIProjectView.complexRenderer.render(g2d);
		
		g2d.setComposite(context);

		this.centerGridPosition(this.bounds.width, this.bounds.height);
				
		if(this.selectionStart != null && this.currentMousePosition != null) {
			int x = this.selectionStart.x, y = this.selectionStart.y;
			int w = this.currentMousePosition.x - x, h = this.currentMousePosition.y - y;
			
			Shape selection = new RoundRectangle2D.Float(w > 0 ? x : x + w, h > 0 ? y : y + h, Math.abs(w), Math.abs(h), 15, 15);
			
			g2d.setColor(new Color(0xC0, 0xFF, 0xEE, this.selectionOpacity));
			g2d.fill(selection);
			
			g2d.setColor(new Color(255, 255, 255, this.selectionOpacity * 4));
			g2d.draw(selection);
		}
		
		g2d.setColor(new Color(0x42C0FFEE, true));
						
		for(Point2D point : selected.values()) {
			g2d.fillRect(this.project.gridPosX + (int) (point.getX() * this.project.pixelsPerCell), this.project.gridPosY + (int) (point.getY() * this.project.pixelsPerCell), this.project.pixelsPerCell, this.project.pixelsPerCell);
		}
		
		for(Entry<Point2D, Color> entry : this.highlighted.entrySet()) {
			g2d.setColor(entry.getValue());
			g2d.fillRect(this.project.gridPosX + (int) (entry.getKey().getX() * this.project.pixelsPerCell), this.project.gridPosY + (int) (entry.getKey().getY() * this.project.pixelsPerCell), this.project.pixelsPerCell, this.project.pixelsPerCell);
		}
		
		for(Object object : this.project.objects) {
			if(object.shouldBeRendered()) {
				double x = this.project.gridPosX + object.getPosition(1, 0).getX() * this.project.pixelsPerCell;
				double y = this.project.gridPosY + object.getPosition(1, 0).getY() * this.project.pixelsPerCell;
				
				if(this.bounds.contains(new Point2D.Double(x + this.project.pixelsPerCell / 2.0D, y + this.project.pixelsPerCell / 2.0D))) {
					new UILabel(this, 0.0f, 0.0f, 1.0f, 1.0f, object.getName()).setColor(0x80C0FFEE).draw((Graphics2D) g2d.create((int) x + 10, (int) y + 10, this.project.pixelsPerCell - 20, this.project.pixelsPerCell - 20));					
				}
			}
		}
		
		g2d.setColor(new Color(0, 0, 0, this.overlayOpacity));
		g2d.fillRect(0, 0, this.bounds.width, this.bounds.height);
		
		this.controls = new RoundRectangle2D.Float(this.bounds.width * 0.75f, this.bounds.height * 0.05f, this.bounds.width * 0.255f, this.bounds.height * 0.9f, 15, 15);
		
		g2d.setColor(new Color(0, 0, 0, 127));
		RoundRectRenderer.fill(g2d, this.controls.getBounds());
		g2d.setColor(new Color(0xCAFE));
		RoundRectRenderer.draw(g2d, this.controls.getBounds());
		
		g2d.setFont(Arionide.getSystemFont());
		g2d.setColor(new Color(255, 0, 0, this.errorMessageOpacity));
		g2d.drawString(this.errorMessage, 20, 30);
		
		g2d.setColor(new Color(0xCAFE));
	}
		
	private void centerGridPosition(int width, int height) {
		if(!this.project.isGridInitialized) {
			this.project.isGridInitialized = true;
			this.project.gridPosX = width / 2 - this.project.pixelsPerCell / 2;
			this.project.gridPosY = height / 2 - this.project.pixelsPerCell / 2;
		}
	}
	
	protected void error(String message) {
		this.errorMessage = message;
		this.errorMessageAnimation.startAnimation(1, animation -> {
			this.errorMessageAnimation.startAnimation(100, 0);
		}, 255);
	}
	
	protected Map<String, Point2D> getSelection() {
		return selected;
	}
	
	private void select(Point selectionEnd) {
		int x1 = (int) Math.round((float) (this.selectionStart.getX() - this.project.gridPosX) / this.project.pixelsPerCell - 0.5f);
		int y1 = (int) Math.round((float) (this.selectionStart.getY() - this.project.gridPosY) / this.project.pixelsPerCell - 0.5f);
		int x2 = (int) Math.round((float) (selectionEnd.getX() - this.project.gridPosX) / this.project.pixelsPerCell - 0.5f);
		int y2 = (int) Math.round((float) (selectionEnd.getY() - this.project.gridPosY) / this.project.pixelsPerCell - 0.5f);

		Rectangle selection = new Rectangle((x1 < x2 ? x1 : x2), (y1 < y2 ? y1 : y2), Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1);
				
		for(Object object : this.project.objects) {
			if(object.shouldBeRendered()) {			
				if(selection.contains(object.getPosition(1, 0))) {
					this.select(object.getID(), object.getPosition(1, 0));
				}
			}
		}
		
		this.resetSelection();
	}
	
	private void select(String id, Point2D position) {
		if(!this.editableView) {
			this.error("You can't edit anything in a representation view");
			return;
		}
		
		if(selected.containsKey(id)) {
			selected.remove(id);
		} else {
			selected.put(id, position);
		}
		
		if(this instanceof UISelectionProjectView) {
			if(selected.size() == 0) {
				UIMain.show(new UICodeProjectView(this.getParentView(), this.getRootComponent()));
			}
		} else {
			if(selected.size() > 0) {
				
				Object object = this.project.objectMapping.get(id);
				
				if(object != null && object.isMutable()) {
					UIMain.show(new UISelectionProjectView(this, this.getRootComponent()));
				} else {
					this.error("This object is immutable");
					selected.remove(id);
				}
			}
		}
	}
	
	protected Map<Point2D, Color> accessHighlighting() {
		return this.highlighted;
	}
	
	protected void open(String id) {
		if(!this.editableView) {
			this.error("You can't open anything in a representation view");
			return;
		}
		
		if(id != this.project.current) {
			if(id == "" || this.project.objectMapping.get(id).getType().equals(ObjectType.STRUCTURE)) {
				int animationDuration = 50;
				
				this.needValidation = true;

				Debug.info("The current branch is '" + (id != "" ? id : "root") + "'");
				
				selected.clear();
				this.highlighted.clear();

				int initialPixelsPerCell = this.project.pixelsPerCell;
				boolean zoomIn = Object.getHierarchyLevel(this.project.current) < Object.getHierarchyLevel(id);
				
				if(zoomIn) {
					this.centerOn(this.project.objectMapping.get(id), 2048, animationDuration);
				}
				
				this.pixelsPerCellAnimation.startAnimation(animationDuration, after -> {
					this.project.pixelsPerCell = zoomIn ? 8 : 2048;
					
					String old = this.project.current;
					this.project.current = id;
					
					Object.uploadCurrentID(id);
					
					UIProjectView.complexRenderer.reset(this.project);
					UIProjectView.simpleRenderer.reset(this.project);
					
					after.startAnimation(50, after2 -> {
						this.needValidation = false;
					}, initialPixelsPerCell);
					
					if(zoomIn) {
						this.project.gridPosX = this.bounds.width / 2 - initialPixelsPerCell / 2;
						this.project.gridPosY = this.bounds.height / 2 - initialPixelsPerCell / 2;
						this.rendererOpacity = 0.0f;
						this.rendererOpacityAnimation.startAnimation(animationDuration, 1.0f);
					} else {
						Object object = this.project.objectMapping.get(old);
						
						Point2D point = new Point(0, 0);
						
						if(object != null) {
							point = object.getPosition(initialPixelsPerCell, 0.5f);
						}
						
						this.project.gridPosX = (int) (this.bounds.width / 2 - point.getX());
						this.project.gridPosY = (int) (this.bounds.height / 2 - point.getY());
					}
				}, zoomIn ? 2048 : 8);
				
				this.rendererOpacityAnimation.startAnimation(animationDuration, after -> {
					this.rendererOpacity = 1.0f;
				}, 0.0f);
			} else {
				this.error("This object is atomic");
			}
		}
	}
	
	private void centerOn(Object object, int pixelsPerCell, int animationDuration) {
		Point2D point = new Point(0, 0);
		
		if(object != null) {
			point = object.getPosition(pixelsPerCell, 0.5f);
		}
		
		this.centerXAnimation.startAnimation(animationDuration, this.bounds.width / 2 - point.getX());
		this.centerYAnimation.startAnimation(animationDuration, this.bounds.height / 2 - point.getY());
	}
	
	private void resetSelection() {
		if(this.selectionOpacity > 0) {
			this.selectionAnimation.startAnimation(20, after-> {
				this.selectionStart = null;
				this.currentMousePosition = null;
			}, 0);
		}
	}
	
	private void checkGridPosition() {
		int halfSize = (this.project.gridSize * this.project.pixelsPerCell) / 2;
		
		if(this.project.gridPosX > halfSize) {
			this.project.gridPosX = halfSize;
		} else if(this.project.gridPosX < this.bounds.width - halfSize) {
			this.project.gridPosX = this.bounds.width - halfSize;
		}
		
		if(this.project.gridPosY > halfSize) {
			this.project.gridPosY = halfSize;
		} else if(this.project.gridPosY < this.bounds.height - halfSize) {
			this.project.gridPosY = this.bounds.height - halfSize;
		}
	}
	
	protected void setNeedValidationFlag() {
		this.needValidation = true;
		this.overlayAnimation.startAnimation(30, 127);
	}
	
	protected void clearNeedValidationFlag() {
		this.needValidation = false;
		this.overlayAnimation.startAnimation(30, 0);
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		Point point = event.getPoint();
		
		UIEvents.transform(point);
		
		if(this.controls != null && !this.controls.contains(point)) {
			int next = this.project.pixelsPerCell + event.getWheelRotation();
							
			if(next < 256) {
				if(next < 8) {
					next = 8;
					this.onClick("back");
				}
			} else {
				next = 256;
			}
			
			this.project.pixelsPerCell = next;
		}
	}

	public void mouseDragged(MouseEvent event) {
		Point point = event.getPoint();
		
		UIEvents.transform(point);
		
		if(!this.needValidation && this.currentMousePosition != null && this.selectionStart == null && SwingUtilities.isLeftMouseButton(event)) {
			this.project.gridPosX += (this.currentMousePosition.x - point.getX()) * (this.isMovingUsingMinigrid ? this.xMult : -1);
			this.project.gridPosY += (this.currentMousePosition.y - point.getY()) * (this.isMovingUsingMinigrid ? this.yMult : -1);
			this.checkGridPosition();
		}
		
		this.currentMousePosition = point;
	}

	public void mouseMoved(MouseEvent event) {
		;
	}

	public void mouseEntered(MouseEvent event) {
		;
	}
	
	public void mouseClicked(MouseEvent event) {
		;
	}

	public void mouseExited(MouseEvent event) {
		this.resetSelection();
	}

	public void mousePressed(MouseEvent event) {
		Point point = event.getPoint();
				
		UIEvents.transform(point);
		
		if(this.needValidation) return;
		
		this.currentMousePosition = null;

		if(this.controls != null && !this.controls.contains(point)) {
			if(event.getButton() == MouseEvent.BUTTON3) {
				if(this.selectionAnimation != null) {
					this.selectionAnimation.stopAnimation();
				}
				
				this.selectionOpacity = 63;
				this.selectionStart = point;
			} else if(event.getButton() == MouseEvent.BUTTON1) {
				this.isMovingUsingMinigrid = false;
				this.currentMousePosition = point;
				
				int x = (int) Math.round((float) (point.getX() - this.project.gridPosX) / this.project.pixelsPerCell - 0.5f);
				int y = (int) Math.round((float) (point.getY() - this.project.gridPosY) / this.project.pixelsPerCell - 0.5f);			
				
				if(this.newFunction != null) {
					if(!this.project.current.isEmpty()) {
						this.newFunction = this.project.current + "." + this.newFunction;
					}
					
					Object object = new Object(this.newFunction, new Point2D.Double(x, y), 0, ObjectType.STRUCTURE, true);
					
					this.project.objects.add(object);
					this.project.objectMapping.put(this.newFunction, object);
					this.project.source.put(this.newFunction,new Function(new String()));
					this.newFunction = null;
				} else {
					for(Entry<String, Object> object : this.project.objectMapping.entrySet()) {
						if(object.getValue().shouldBeRendered() && object.getValue().getPosition(1, 0).equals(new Point2D.Double(x, y))) {
							this.open(object.getKey());
							break;
						}
					}
				}
			}
		} else if(this.miniGrid != null && this.miniGrid.contains(point) && event.getButton() == MouseEvent.BUTTON1) {
			this.isMovingUsingMinigrid = true;
			this.currentMousePosition = point;
			
			int xOrigin = (int) this.miniGrid.getBounds2D().getCenterX();
			int yOrigin = (int) this.miniGrid.getBounds2D().getCenterY();
			
			this.project.gridPosX = (int) (-this.xMult * (point.getX() - xOrigin) + this.bounds.width / 2);
			this.project.gridPosY = (int) (-this.yMult * (point.getY() - yOrigin) + this.bounds.height / 2);
			this.checkGridPosition();
		} else {
			this.currentMousePosition = null;
		}
	}

	public void mouseReleased(MouseEvent event) {
		if(!this.needValidation && this.selectionStart != null) {
			Point point = event.getPoint();
			UIEvents.transform(point);
			this.select(point);
		}
	} 

	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_CONTROL) {			
			UIMain.show(new UIMiscProjectView(this, this.getRootComponent()));
		} else if(event.getKeyCode() == KeyEvent.VK_ALT) {			
			UIMain.show(new UIDesignProjectView(this, this.getRootComponent()));
		} else if(event.getKeyCode() == KeyEvent.VK_SPACE) {
			this.centerOn(new Object("Object.center", new Point2D.Float(0, 0), 0, null, false), this.project.pixelsPerCell, 50);
		}
	}
	
	public void keyReleased(KeyEvent event) {
		;
	}

	public void keyTyped(KeyEvent event) {
		;
	}
	
	public void unregisterListeners() {
		this.getRootComponent().removeMouseListener(this);
		this.getRootComponent().removeMouseMotionListener(this);
		this.getRootComponent().removeMouseWheelListener(this);
		((JFrame) this.getRootComponent()).getContentPane().removeKeyListener(this);
	}
	
	public void onClick(java.lang.Object... signals) {
		if(signals[0] instanceof String && !this.needValidation)  {
			switch((String) signals[0]) {
				case "back":
					if(this.project.current.indexOf('.') > -1) {
						this.open(this.project.current.substring(0, this.project.current.lastIndexOf('.')));
					} else if(this.project.current != "") {
						this.open("");
					}
					break;
				case "add":
					new Thread() {
						public void run() {
							UIProjectView.this.newFunction = JOptionPane.showInputDialog(UIProjectView.this.getRootComponent(), "Enter function's name:", "New function", JOptionPane.QUESTION_MESSAGE);
						}
					}.start();
					
					break;
			}
		}
	}
}
