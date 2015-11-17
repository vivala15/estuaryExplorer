package renderengine;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.IDebugDraw;

import bulletphysics.PhysicsWorld;
import entities.Camera;
import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.DebugShader;
import textures.ModelTexture;
import toolbox.Maths;

public class BulletDebugRenderer extends IDebugDraw{

	PhysicsWorld physicsWorld;
	private int DEBUG_MODE = DebugDrawModes.DRAW_WIREFRAME;
	//private int DEBUG_MODE = DebugDrawModes.DRAW_AABB;
	
	private Loader loader;
	private DebugShader shader = new DebugShader();
	private float[] positionData;
	private float[] colorData;
	private ArrayList<Float> positions = new ArrayList<Float>();
	private ArrayList<Float> colors = new ArrayList<Float>();
	
	public BulletDebugRenderer(PhysicsWorld pw, Loader loader, Matrix4f projectionMatrix){
		physicsWorld = pw;
		physicsWorld.attachDebugWorld(this);
		this.loader = loader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop(); //dont; need to load up again always there
	}
	
	
	public float[] listToArray(ArrayList<Float> in){
		int size = in.size();
		float[] a = new float[size];
		for(int i =0; i<size; i ++){
			a[i] = in.get(i);
		}
		return a;
	}
	
	public void render(Camera camera){
//		positions.clear();
//		colors.clear();
		shader.start();
		shader.loadViewMatrix(camera);
		System.out.println("Renderering debug pts: " + Integer.toString(positions.size()));
		positionData = listToArray(positions);
		colorData = listToArray(colors);
		//System.out.println("Draw data --- position data length: " + String.valueOf(positionData.length));
		//System.out.println(positions);
		positions.clear();
		colors.clear();
		//if(positionData.length > 2){
		//create vao for this round
		RawModel rawModel = loader.loadToVAO(positionData, colorData);
		int vaoID = rawModel.getVaoID();
		//bind vao
		//System.out.println(vaoID);
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		//attrib list 0
		GL20.glEnableVertexAttribArray(0); //only position
		GL20.glEnableVertexAttribArray(1); //color
		
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new org.lwjgl.util.vector.Vector3f(positionData[0], positionData[1], positionData[2]) ,
				0,0,0,1f);
		//in this case may need just camera adjusted transform...
		shader.loadTransformationMatrix(transformationMatrix);
		//render
		
		
		GL11.glDrawArrays(GL11.GL_LINES, 0, rawModel.getVertexCount());
		
		
		
		//GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, rawModel.getVertexCount());
		
		//GL11.glDrawArrays(GL11.GL_LINES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT);
		//unbind
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		shader.stop();
		
		
		//GL11.glDrawArrays(GL11.GL_POINT, first, count);
		//}
		
	}
	@Override
	public void draw3dText(Vector3f arg0, String arg1) {
		//HAVE NOT ENABLED THIS TYPE OF DEBUG
		System.out.println("Draw 3d text CAlled");
	}

	@Override
	public void drawContactPoint(Vector3f arg0, Vector3f arg1, float arg2, int arg3, Vector3f arg4) {
		//HAVE NOT ENABLED THIS TYPE OF DEBUG
		System.out.println("Draw contact pt CAlled");
	}


	/**
	 * From , to 
	 */
	@Override
	public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
		//System.out.println("Draw Line CAlled");
		//save them up then a call from the masterRenderer to render them all.... ja that
		positions.add(from.x);
		positions.add(from.y);
		positions.add(from.z);
		positions.add(to.x);
		positions.add(to.y);
		positions.add(to.z);
		//color
		colors.add(color.x);
		colors.add(color.y);
		colors.add(color.z);
		colors.add(color.x);
		colors.add(color.y);
		colors.add(color.z);
		//System.out.println(color);
		
//		for(int i = 0; i < 6; i++){
//		colors.add(1.0f);
//		colors.add(0.0f);
//		colors.add(0.0f);
//	}
	}

	@Override
	public int getDebugMode() {
		// TODO Auto-generated method stub
		return DEBUG_MODE;
	}

	@Override
	public void reportErrorWarning(String arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0);
	}

	@Override
	public void setDebugMode(int debug) {
		DEBUG_MODE = debug;
		//this.setDebugMode(debug);
		//this.physicsWorld.setDebugMode(debug);
	}

}
