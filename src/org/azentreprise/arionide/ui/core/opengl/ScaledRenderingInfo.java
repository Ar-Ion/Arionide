package org.azentreprise.arionide.ui.core.opengl;

public class ScaledRenderingInfo {
	
	private final int vao;
	private final int ebo;
	private final int vertices;
	
	protected ScaledRenderingInfo(int vao, int ebo, int vertices) {
		this.vao = vao;
		this.ebo = ebo;
		this.vertices = vertices;
	}
	
	protected int getVAO() {
		return this.vao;
	}
	
	protected int getEBO() {
		return this.ebo;
	}
	
	protected int getVertices() {
		return this.vertices;
	}
}
