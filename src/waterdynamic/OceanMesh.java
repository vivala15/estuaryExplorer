package waterdynamic;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import models.DynamicRawModel;
import models.RawModel;
import renderengine.Loader;
import toolbox.BufferTools;

/**
 * represents the geometry of the water.
 * @author sascha
 *
 */
class OceanMesh
{
	private int _vertexAxisSize;


	private int _vertexCount;

	private int indicesCount;
//	private VertexBuffer _position;
//	private VertexBuffer _normals;



	private FloatBuffer _position_buffer;
	private FloatBuffer _normal_buffer;
	
	//collectively buffer for all the VertexOcean stuff
	private FloatBuffer _ocean_buffer;
	

	private DynamicRawModel rawModel;
	private Loader loader;
	private float[] textureCoord;
	public float[] getTextureCoord() {
		return textureCoord;
	}


	public int[] testIndices;

	/**
	 * @param size
	 *            number of vertices on the x or z axis (whole number of quads
	 *            is always size^2 ). Its recommended to set size to something
	 *            power of two.
	 */
	public OceanMesh(int size, float edgeLength, Loader loader)
	{
		
		this.loader = loader;
		_vertexAxisSize = size+1;
		_vertexCount = (size+1) * (size+1);

		System.out.println("Mesh has vertexCount of :" + String.valueOf(_vertexCount));
		initGeometry(edgeLength);
	}


	private void initGeometry(float edgeLength)
	{
		float[] positions = new float[_vertexCount * 3 ];
		float[] normals = new float[_vertexCount * 3 ];
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
				
				//something weird giving middle seam ugh
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

		
		testIndices = indices;
		this.indicesCount = indexCount;
		this.textureCoord = texCoords;
		//public RawModel loadDynamicToVAO(float[] positions, float[] textureCoords,
//		float[] normals, int[] indices){

		//rawModel = loader.loadDynamicToVAO(positions, texCoords, normals, indices);
		

//		
//		this._position_buffer = BufferTools.storeDataInFloatBuffer(positions);
//		this._normal_buffer = BufferTools.storeDataInFloatBuffer(normals);
		//System.out.println("Buffer create with spots: " + String.valueOf(positions.length + 9));
		int vertexSide = _vertexAxisSize;
		//System.out.println("Vertex Count: " + Integer.toString(3*(vertexSide*vertexSide + vertexSide)));
		this._position_buffer = BufferUtils.createFloatBuffer(3*(vertexSide*vertexSide + vertexSide));
		this._normal_buffer = BufferUtils.createFloatBuffer(3*(vertexSide*vertexSide + vertexSide));
				
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
		//System.out.println(vertexIndex);
//		try{
		_position_buffer.put(vertexIndex*3+0, x);
		_position_buffer.put(vertexIndex*3+1, y);
		_position_buffer.put(vertexIndex*3+2, z);
//		}catch (Exception e){
//			System.out.println(vertexIndex*3+2);
//			System.out.println("Error wtf");
//		}
		
//		
//		_position.setElementComponent(vertexIndex, 0, x);
//		_position.setElementComponent(vertexIndex, 1, y);
//		_position.setElementComponent(vertexIndex, 2, z);
	}


	public void setNormalDirection(int normalIndex, float x, float y, float z)
	{
		
		_normal_buffer.put(normalIndex*3+0,x);
		_normal_buffer.put(normalIndex*3+1,y);
		_normal_buffer.put(normalIndex*3+2,z);
		
//		_normals.setElementComponent(normalIndex, 0, x);
//		_normals.setElementComponent(normalIndex, 1, y);
//		_normals.setElementComponent(normalIndex, 2, z);
	}

	public FloatBuffer get_position_buffer() {
		return _position_buffer;
	}


	public FloatBuffer get_normal_buffer() {
		return _normal_buffer;
	}

	
	public int getIndicesCount() {
		return indicesCount;
	}


	public DynamicRawModel getRawModel() {
		// TODO Auto-generated method stub
		return this.rawModel;
	}


	public void flipBuffers() {
		this._normal_buffer.flip();
		this._position_buffer.flip();
		
	}

	public int get_vertexAxisSize() {
		return _vertexAxisSize;
	}
//	public void setUpdateNeeded()
//	{
//		_normals.setUpdateNeeded();
//		_position.setUpdateNeeded();
//	}
}
