package waterdynamic;

import java.util.List;

import models.DynamicRawModel;
import models.RawModel;
import renderengine.DisplayManager;
import renderengine.Loader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;
import water.WaterFrameBuffers;
import entities.Camera;
import entities.Light;

public class DynamicWaterRenderer {

	
	private static final String NORMAL_MAP = "res/matchingNormalMap.png";
	private static final String DUDV_MAP = "res/Dudv.png";
	private static final float WAVE_SPEED = 0.03f;
	
	private RawModel quad;
	private DynamicWaterShader shader;
	private WaterFrameBuffers fbos;
	
	private int dudvTexture;
	private int normalMap;
	
	private float moveFactor = 0;
	
	public DynamicWaterRenderer(Loader loader, DynamicWaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers fbos) {
		this.shader = shader;
		this.fbos = fbos;
		normalMap = loader.loadTexture(NORMAL_MAP);
		dudvTexture= loader.loadTexture(DUDV_MAP);
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		setUpVAO(loader);
	}

	public void render(List<DynamicWaterTile> water, Camera camera,  Light sun) {
		prepareRender(camera,sun);	
		for (DynamicWaterTile tile : water) {
			Matrix4f modelMatrix = Maths.createTransformationMatrix(
					new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
					DynamicWaterTile.TILE_SIZE);
			shader.loadModelMatrix(modelMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		}
		unbind();
	}
	
	private void prepareRender(Camera camera, Light sun, DynamicRawModel rawModel){
		shader.start();
		shader.loadViewMatrix(camera);
		moveFactor += WAVE_SPEED * DisplayManager.getFrameTimeSeconds();
		moveFactor %= 1;
		shader.loadMoveFactor(moveFactor);
		shader.loadLight(sun);
		GL30.glBindVertexArray(quad.getVaoID());
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, rawModel.getInd_buffer());
		GL20.glVertexAttribPointer(rawModel.getPos_vboID(), 3, GL11.GL_FLOAT, GL11.GL_FALSE, 0,0);
		GL20.glEnableVertexAttribArray(rawModel.getPos_vboID());
		GL20.glVertexAttribPointer(rawModel.getNormal_vboID(), 3, GL11.GL_FLOAT, GL11.GL_FALSE, 0,0);
		GL20.glEnableVertexAttribArray(rawModel.getNormal_vboID());
		GL20.glVertexAttribPointer(rawModel.getTexture_vboID(), 2, GL11.GL_FLOAT, GL11.GL_FALSE, 0,0);
		GL20.glEnableVertexAttribArray(rawModel.getTexture_vboID());
		
		GL43.glBindVertexBuffer(bindingindex, buffer, offset, stride);
		GL20.glEnableVertexAttribArray(0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap);
		//bind depth map
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
		//enable alphA blending
		//NOTE: THIS IS SAME CODE THAT MAKE GUI BEHAVE WEIRD - gui fbo transparent when shouldn't...
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
	}
	
	private void unbind(){
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void setUpVAO(Loader loader) {
		// Just x and z vectex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = loader.loadToVAO(vertices, 2);
	}

}
