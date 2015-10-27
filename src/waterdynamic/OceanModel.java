package waterdynamic;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.apache.commons.math3.util.FastMath;

import entities.Camera;
import models.RawModel;
import renderengine.Loader;

public class OceanModel{

	ComplexOceanEngine ocean;
	final int N = 32;
	final float amplitude = 2.0f;
	final float length = 400f;
	float _elapsedTime = 0f;
	float _timeMultiplier = 1.0f;
	DynamicWaterShader _oceanMaterial;
	OceanMesh _oceanMesh;
	
	public OceanModel(Loader loader){
		ocean = new ComplexOceanEngine(N, amplitude, new Vector2f(1.0f, 0.0f), length);
		_oceanMesh = new  OceanMesh(N, length, loader);
		
		
		
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
		_elapsedTime += tpf * _timeMultiplier;

		ocean.calcWaves(_elapsedTime);

		int vertexCountPerSide = (int) FastMath.sqrt(ocean.getVerticesOcean().length);

		// now pass the result to the mesh
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

		//I don't think i'm using a multi=threaded queue
		//_oceanMesh.setUpdateNeeded();

		_oceanMaterial.loadTime( _elapsedTime);
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

	
}
