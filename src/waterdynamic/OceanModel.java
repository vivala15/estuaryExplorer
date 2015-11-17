package waterdynamic;

import java.util.ArrayList;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.apache.commons.math3.util.FastMath;

import entities.Camera;
import models.DynamicRawModel;
import models.RawModel;
import renderengine.BulletDebugRenderer;
import renderengine.Loader;

public class OceanModel{

	
	//Store where 
	ComplexOceanEngine ocean;
	final int N = 84;
	final float amplitude = .0003f;
	final float length = 40f;
	final float scale = 1.0f;
	float _elapsedTime = 0f;
	float _timeMultiplier = .5f;
	//public DynamicWaterShader _oceanMaterial;
	public OceanMesh _oceanMesh;
	private DynamicRawModel oceanRawModel;
	 BulletDebugRenderer debugRenderer;

	//For rapid sorting algorithm used elsewhere
	 //GUARANTEE this list is sorted in Z, then X order, y doesn't matter,
	ArrayList<Vector3f> waterTileOrigins = new ArrayList<Vector3f>();
	 Loader loader;
	 float[] vert ;
	 float[] norm ;
	public OceanModel(Loader loader, BulletDebugRenderer debugRenderer){
		//ocean = new ComplexOceanEngine(N, amplitude, new Vector2f(1.9f, 1.3f), length);
		ocean = new ComplexOceanEngine(N, amplitude, new Vector2f(5.9f,5.5f), length);
		_oceanMesh = new  OceanMesh(N, length, loader);
		
		//set oceanRawModel , created in _oceanMesh
		//this.oceanRawModel = _oceanMesh.getRawModel();
		this.debugRenderer = debugRenderer;
		this.loader = loader;
		
		int vertexCountPerSide = (int) FastMath.sqrt(ocean.getVerticesOcean().length);

		vert = new  float[3*(vertexCountPerSide*vertexCountPerSide+vertexCountPerSide)];
		norm = new float[3*(vertexCountPerSide*vertexCountPerSide+vertexCountPerSide)];
		for (int x = 0; x < vertexCountPerSide; x++)
		{
			for (int y = 0; y < vertexCountPerSide; y++)
			{
				int idx = x * (vertexCountPerSide) + y;
				vert[idx*3+0] = 0;
				vert[idx*3+1] = 0;
				vert[idx*3+2] = 0;
				norm[idx*3+0] = 0;
				norm[idx*3+1] = 0;
				norm[idx*3+2] = 0;
				
			}
		}
		float[] textureCoords = this._oceanMesh.getTextureCoord();
		int[] indices = this.ocean.get_indices();
		System.out.println("TextureCoords pts ie divide by two: " + Float.toString((float) (textureCoords.length/2.0)));
		this.oceanRawModel = loader.loadDynamicToVAO(vert, textureCoords, norm, indices);
	}
	
	
	public void tick(float dt){
		// calculate the wave state and pass it to the mesh
		updateOceanMesh(dt);

		// now another tricky part: move the tiles depending on the camera
		// position
		//updateTilesPosition(sceneCam);

		
		
		
	}
	/**
	 * creates these nifty waves on the ocean mesh
	 * 
	 * @param tpf
	 *            time per frame
	 */
	private void updateOceanMesh(float tpf)
	{
		//System.out.println("Update Ocean Mesh called");
		_elapsedTime += tpf * _timeMultiplier;

		ocean.calcWaves(_elapsedTime);

		int vertexCountPerSide = (int) FastMath.sqrt(ocean.getVerticesOcean().length);
		//System.out.println("Vertices in vertex ocean: " + Integer.toString(ocean.getVerticesOcean().length));
		for (int x = 0; x < vertexCountPerSide; x++)
		{
			for (int y = 0; y < vertexCountPerSide; y++)
			{
				int idx = x * (vertexCountPerSide) + y;

				VertexOcean vo = ocean.getVerticesOcean()[idx];
			
				
				
				_oceanMesh.setVertexPosition(idx, vo._x, -vo._y, vo._z);
				_oceanMesh.setNormalDirection(idx, vo._nx, vo._ny, vo._nz);
			}
		}
		
		
		//While debugging write to array then create buffer and new Vbo stuff
//		vert[idx*3+0]= vo._x;
//		vert[idx*3+1] = -vo._y;
//		vert[idx*3+2] = vo._z;
//		
//		norm[idx*3+0] = vo._nx;
//		norm[idx*3+1] = vo._ny;
//		norm[idx*3+2] = vo._nz;
		//int[] indices = this.ocean.get_indices();
		//float[] textureCoords = this._oceanMesh.getTextureCoord();
//		int maxInd  =0;
//		for(int i=0; i < indices.length; i++){
//			if(indices[i] > maxInd){
//				maxInd = indices[i];
//			}
//		}
//		System.out.println("Max indice");
//		System.out.println(maxInd);
//		System.out.println(vert.length);
		//this.oceanRawModel = loader.loadDynamicToVAO(vert, textureCoords, norm, indices);
		
//		int ind = this._oceanMesh.testIndices.length;
//		for(int i = 0; i < ind-2; i+=3){
//			int ind1 = this._oceanMesh.testIndices[i];
//			int ind2 = this._oceanMesh.testIndices[i+1];
//			int ind3 = this._oceanMesh.testIndices[i+2];
//			VertexOcean vo1 = ocean.getVerticesOcean()[ind1];
//			VertexOcean vo2 = ocean.getVerticesOcean()[ind2];
//			VertexOcean vo3 = ocean.getVerticesOcean()[ind3];
//			
//			Vector3f pt1 = new Vector3f(vo1._x, -vo1._y, vo1._z);
//			Vector3f pt2 = new Vector3f(vo2._x, -vo2._y, vo2._z);
//			Vector3f pt3 = new Vector3f(vo3._x, -vo3._y, vo3._z);
//			if(greaterThan(pt1,pt2,10) || greaterThan(pt2,pt3,10) || greaterThan(pt1,pt3,10)){
//				
//			}else{
//				this.debugRenderer.drawLine(pt1, pt2, new Vector3f(1,1,1));
//				this.debugRenderer.drawLine(pt2, pt3, new Vector3f(1,1,1));
//				this.debugRenderer.drawLine(pt3, pt1, new Vector3f(1,1,1));
//			}
//		}
		
		//_oceanMesh.flipBuffers();
		//I don't think i'm using a multi=threaded queue
		//_oceanMesh.setUpdateNeeded();

		//_oceanMaterial.loadTime( _elapsedTime);
	
	
	}

	
	public boolean greaterThan(Vector3f v1, Vector3f v2, float dist){
		if(Math.pow((v1.x-v2.x), 2) + Math.pow(v1.y - v2.y,2 ) + Math.pow(v1.z - v2.z, 2) > dist*dist){
			return true;
		}
		return false;
	}
//	private void updateTilesPosition(Camera sceneCam)
//	{
//		org.lwjgl.util.vector.Vector3f camPos = sceneCam.getPosition();
//
//		for (Spatial oceanNode : _oceanNode.getChildren())
//		{
//			float distanceX = camPos.x - oceanNode.getWorldTranslation().x;
//			float distanceZ = camPos.z - oceanNode.getWorldTranslation().z;
//
//			distanceX /= oceanNode.getWorldScale().x;
//			distanceZ /= oceanNode.getWorldScale().z;
//
//			if (distanceX > length / 2)
//			{
//				oceanNode.move(length, 0, 0);
//			} else if (distanceX < -(length / 2))
//			{
//				oceanNode.move(-length, 0, 0);
//			}
//
//			if (distanceZ > length / 2)
//			{
//				oceanNode.move(0, 0, length);
//			} else if (distanceZ < -(length / 2))
//			{
//				oceanNode.move(0, 0, -length);
//			}
//		}
//	}

	
	
	public void setWindDirX(float value)
	{
		ocean.getWind().x = value;
	}


	public void setWindDirY(float value)
	{
		ocean.getWind().y = value;
	}


	public Vector2f getWindDir()
	{
		return ocean.getWind();
	}
	
	public OceanMesh get_oceanMesh() {
		return _oceanMesh;
	}


	public DynamicRawModel getRawModel() {
		// TODO Auto-generated method stub
		return this.oceanRawModel;
	}


	/**
	 * Given a world point 
	 * @param densityPt
	 * @return
	 */
	public Vector3f findClosestMeshPt(Vector3f densityPt, Vector3f lastOrigin) {
		//waterTileOrigins
		Vector3f tileOrigin = tileSearch(densityPt.x, densityPt.z);
		//not on tile so apply no buoncy by having wave super low
		if(tileOrigin == null){
			return new Vector3f(0,0, -9999999f);
		}
		return meshSearch(densityPt.x-tileOrigin.x, densityPt.z - tileOrigin.z);
	}
	
	
	public Vector3f tileSearch(float x, float z){
		//find which one contains it
		for(Vector3f tileOrigin : waterTileOrigins){
			if(inTile(x,z,tileOrigin)){
				return tileOrigin;
			}
		}
		return null;
		
	}
	
	private boolean inTile(float x, float z, Vector3f tileOrigin){
		if(tileOrigin.x+this.length*this.scale > x && x > tileOrigin.x
				&&
			tileOrigin.z+this.length*this.scale > z && z > tileOrigin.z){
			return true;
		}
		return false;
	}
	
	/**
	 * Mesh search given relative x, z coords (NOT WORLD COORDINATES)
	 * @param x
	 * @param z
	 * @return
	 */
	public Vector3f meshSearch(float x, float z){
		int xIndex = (int) (x/length*this._oceanMesh.get_vertexAxisSize());
		int zIndex = (int) (z/length*this._oceanMesh.get_vertexAxisSize());
		
		//Now that we have starting point, do a more local search
		//Ie jump up to ~5 spots
//		float guessDiff = ;
//		float sampleDiff = ;
		int index  = xIndex*this._oceanMesh.get_vertexAxisSize() + zIndex;
		Vector3f pt = new Vector3f(this.ocean.getVerticesOcean()[index]._x,
				this.ocean.getVerticesOcean()[index]._y,
				this.ocean.getVerticesOcean()[index]._z);
		return pt;
	}

	
}
