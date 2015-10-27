package waterdynamic;

import models.DynamicRawModel;
import models.RawModel;
import renderengine.Loader;

/**
 * represents the geometry of the water.
 * @author sascha
 *
 */
public class OceanMesh
{
	private int _vertexAxisSize;
	private int _vertexCount;

	private VertexBuffer _position;
	private VertexBuffer _normals;

	private DynamicRawModel rawModel;
	private Loader loader;

	/**
	 * @param size
	 *            number of vertices on the x or z axis (whole number of quads
	 *            is always size^2 ). Its recommended to set size to something
	 *            power of two.
	 */
	public OceanMesh(int size, float edgeLength, Loader loader)
	{
		
		this.loader = loader;
		_vertexAxisSize = size;
		_vertexCount = (size) * (size);

		initGeometry(edgeLength);
	}


	private void initGeometry(float edgeLength)
	{
		float[] positions = new float[_vertexCount * 3];
		float[] normals = new float[_vertexCount * 3];
		int[] indices = new int[(_vertexAxisSize - 1) * (_vertexAxisSize - 1) * 2 * 3];
		float[] texCoords = new float[_vertexCount * 2];


		int index = 0;
		int coordIndex = 0;
		
		// setup positions and texCoords
		for (int x = 0; x < _vertexAxisSize; x++)
		{
			for (int z = 0; z < _vertexAxisSize; z++)
			{
				positions[index] = x * edgeLength;
				positions[index + 1] = 0;
				positions[index + 2] = z * edgeLength;

				normals[index] = 0;
				normals[index + 1] = 1;
				normals[index + 2] = 0;

				index += 3;
				
				texCoords[coordIndex++] = (1.0f/(float)(_vertexAxisSize-1))*x;
				texCoords[coordIndex++] = (1.0f/(float)(_vertexAxisSize-1))*z;				
			}
		}

		int indexCount = 0;
		// setup indices
		for (int i = 0; i < _vertexAxisSize - 1; i++)
			for (int j = 0; j < _vertexAxisSize - 1; j++)
			{
				indices[indexCount + 2] =  (i * _vertexAxisSize + j);
				indices[indexCount + 1] =  (i * _vertexAxisSize + j + 1);
				indices[indexCount ] =  ((i + 1) * _vertexAxisSize + j + 1);

				indices[indexCount + 5] =  (i * _vertexAxisSize + j);
				indices[indexCount + 4] =  ((i + 1) * _vertexAxisSize + j + 1);
				indices[indexCount + 3] =  ((i + 1) * _vertexAxisSize + j);

				indexCount += 6;
			}

		//public RawModel loadDynamicToVAO(float[] positions, float[] textureCoords,
//		float[] normals, int[] indices){

		rawModel = loader.loadDynamicToVAO(positions, texCoords, normals, indices);
		
//		setBuffer(Type.Position, 3, positions);
//		_position = getBuffer(Type.Position);
//
//		setBuffer(Type.TexCoord, 2, texCoords);
//
//		setBuffer(Type.Normal, 3, normals);
//		_normals = getBuffer(Type.Normal);
//
//		setBuffer(Type.Index, 3, indices);
//
//		updateBound();
	}


	public void setVertexPosition(int vertexIndex, float x, float y, float z)
	{
		_position.setElementComponent(vertexIndex, 0, x);
		_position.setElementComponent(vertexIndex, 1, y);
		_position.setElementComponent(vertexIndex, 2, z);
	}


	public void setNormalDirection(int normalIndex, float x, float y, float z)
	{
		_normals.setElementComponent(normalIndex, 0, x);
		_normals.setElementComponent(normalIndex, 1, y);
		_normals.setElementComponent(normalIndex, 2, z);
	}


	public void setUpdateNeeded()
	{
		_normals.setUpdateNeeded();
		_position.setUpdateNeeded();
	}
}
