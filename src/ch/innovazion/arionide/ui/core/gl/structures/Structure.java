package ch.innovazion.arionide.ui.core.gl.structures;

import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.BufferGenerator;
import ch.innovazion.arionide.ui.core.gl.StaticObject;

public class Structure extends StaticObject<StructureContext, StructureSettings> {

	private final int layers;
	
	private final StructureTessellator tessellator;
	private final StructureIndicesGenerator indicesGenerator;
	
	public Structure(int layers) {
		super(new StructureSettings());
		
		this.layers = layers;
		
		this.tessellator = new StructureTessellator(layers);
		this.indicesGenerator = new StructureIndicesGenerator(layers);
		
		setBufferInitializer(this.tessellator, (context) -> setupFloatAttribute(context.getPositionAttribute(), 3, 0, 0));
	}
	
	protected void update(GL4 gl, StructureContext context, StructureSettings settings) {
		
	}

	protected void renderObject(GL4 gl) {
		gl.glDrawElements(GL4.GL_TRIANGLE_STRIP, 4 * layers * (layers - 1), GL4.GL_UNSIGNED_INT, 0);		
	}

	protected List<BufferGenerator> getGenerators() {
		return Arrays.asList(this.indicesGenerator, this.tessellator);
	}

	protected int getBufferCount() {
		return 2;
	}
}