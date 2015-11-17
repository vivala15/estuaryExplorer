package waterdynamic;

import java.nio.FloatBuffer;
import java.util.List;

import models.DynamicRawModel;
import models.RawModel;
import renderengine.DisplayManager;
import renderengine.Loader;
import renderengine.MasterRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL43;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import toolbox.BufferTools;
import toolbox.Maths;
import water.WaterFrameBuffers;
import entities.Camera;
import entities.Light;

public class DynamicWaterRenderer {

	
	private static final String NORMAL_MAP = "res/matchingNormalMap.png";
	private static final String DUDV_MAP = "res/Dudv.png";
	private static final float WAVE_SPEED = 0.03f;
	
	//private RawModel quad;
	private DynamicWaterShader shader;
	private WaterFrameBuffers fbos;
	
	private int dudvTexture;
	private int normalMap;
	
	private float moveFactor = 0;
	
	
	
	public DynamicWaterRenderer(Loader loader, DynamicWaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers fbos) {
		this.shader = shader;
		this.fbos = fbos;
//		normalMap = loader.loadTexture(NORMAL_MAP);
//		dudvTexture= loader.loadTexture(DUDV_MAP);
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		setUpVAO(loader);
		
	}

	/**
	 * Use camera to render only a certain number of water tiles stored in water due to the intense calculation required...
	 * Note to self? think its possible to calculate with different N on demand??? hmm questionable due to high allocation
	 * requirement
	 * @param water
	 * @param camera
	 * @param sun
	 */
	public void render(OceanModel water, Camera camera,  Light sun) {
		//System.out.println("OeanDraw called");
		prepareRender(camera,sun, water);
		
//		Matrix4f modelMatrix = Maths.createTransformationMatrix(
//				new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
//				DynamicWaterTile.TILE_SIZE);
		
		for(int i=0; i < 1; i++){
			for(int j = 0; j < 1; j++){
		Matrix4f modelMatrix = Maths.createTransformationMatrix(
				new Vector3f(160*i, 2, 160*j), 0, 0, 0,
				4f);
		shader.loadModelMatrix(modelMatrix);
//		GL11.glDrawElements(GL11.GL_TRIANGLES, water.get_oceanMesh().getIndicesCount(),
//				GL11.GL_UNSIGNED_INT, 0);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, water.getRawModel().getVertexCount(),
				GL11.GL_UNSIGNED_INT, 0);
		//GL11.glDrawArrays(GL11.GL_LINES, 0, water.getRawModel().getVertexCount());
			}
			
			
		}
		//GL11.glDrawArrays(GL11.GL_LINES, 0, water.getRawModel().getVertexCount());
		
		//		for (DynamicWaterTile tile : water) {
//			Matrix4f modelMatrix = Maths.createTransformationMatrix(
//					new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
//					DynamicWaterTile.TILE_SIZE);
//			shader.loadModelMatrix(modelMatrix);
//			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
//		}
		unbind();
	}
	
	private void prepareRender(Camera camera, Light sun, OceanModel ocean){
		//MasterRenderer.enableCulling();
		DynamicRawModel rawModel = ocean.getRawModel(); //get from ocean
		shader.start();
		shader.loadViewMatrix(camera);
//		moveFactor += WAVE_SPEED * DisplayManager.getFrameTimeSeconds();
//		moveFactor %= 1;
//		shader.loadMoveFactor(moveFactor);
		shader.loadLight(sun);
		
		
		GL30.glBindVertexArray(ocean.getRawModel().getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, rawModel.getPos_vboID());
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, ocean.get_oceanMesh().get_position_buffer());
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0,0);//0 = position attrib
		
		GL20.glEnableVertexAttribArray(1);
		
		GL20.glEnableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ocean.getRawModel().getNormal_vboID());
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, ocean.get_oceanMesh().get_normal_buffer());
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0,0); //2 = texutre attrib
		
		//Textures for refraction and reflection
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		//bind depth map
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
		
		

		//NOTE: THIS IS SAME CODE THAT MAKE GUI BEHAVE WEIRD - gui fbo transparent when shouldn't...
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_ALPHA);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
	}
	
	private void unbind(){
		//MasterRenderer.disableCulling();
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void setUpVAO(Loader loader) {
		
	}

}
