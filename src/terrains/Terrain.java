package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;

import bulletphysics.Physical;
import models.RawModel;
import renderengine.Loader;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Terrain implements Physical{
	//size per tile, this stretches textures and maps
	private static final float SIZE = 800;
	//private static final int VERTEX_COUNT = 128;
	
	//sets maximum value compared to map,higher value greater height contrast
	private static final float MAX_HIEGHT = 55;
	//could this really be anything else? - don't play with
	private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;
	
	//dimensions of total of this terrain, scales with size and grid number
	private float x;
	private float z;
	private float y;
	private Vector3f position;
	//links to id of terrain VAO and vertex number
	private RawModel model;
	//Holds links to 4 textures, field names correspond to color designators in blend map
	private TerrainTexturePack texturePack;
	// Terrain texture colors signal which texture from texture Pack to apply
	private TerrainTexture blendMap;
	
	//store height of each vertex for collision logic
	private float[][] heights;
	
	private CollisionShape collisionShape;
	private IndexedMesh indMesh = new IndexedMesh();
	private static float TriangleSizeX;
	private static float TriangleSizeZ;
	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack,
			TerrainTexture blendMap, String heightMap){
		
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader, heightMap);
		this.y = heights[0][0];
		position = new Vector3f(x,y,z);
	}
	
	private RawModel generateTerrain(Loader loader, String heightMap){
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int VERTEX_COUNT = image.getHeight();
		
		//For triangle mesh
		
		// create a triangle-mesh ground
		int vertStride = 3*4;
		int indexStride = 3*4;

		final int NUM_VERTS_X = VERTEX_COUNT;
		final int NUM_VERTS_Y = VERTEX_COUNT;
		final int totalVerts = NUM_VERTS_X*NUM_VERTS_Y;

		this.TriangleSizeX= SIZE / (float)(NUM_VERTS_X-1);
		this.TriangleSizeZ= SIZE / (float)(NUM_VERTS_Y-1);
		final int totalTriangles = 2*(NUM_VERTS_X-1)*(NUM_VERTS_Y-1);

		ByteBuffer verticesBuffer = ByteBuffer.allocateDirect(totalVerts*vertStride).order(ByteOrder.nativeOrder());
		ByteBuffer gIndices = ByteBuffer.allocateDirect(totalTriangles*3*4).order(ByteOrder.nativeOrder());
		
		//end-----------------------------------------------------------------------
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = ((float)j/((float)VERTEX_COUNT - 1) * SIZE);
				float height = getHeight(j,i, image);
				heights[j][i] = height;
				vertices[vertexPointer*3+1] = height;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				//for mesh
				int idx = (j+i*NUM_VERTS_Y)*3*4;
				verticesBuffer.putFloat(idx+2*4, (i)*TriangleSizeZ  + this.z);
				verticesBuffer.putFloat(idx+1*4, height);
				verticesBuffer.putFloat(idx+0*4, (j)*TriangleSizeX + this.x);
				//
//				verticesBuffer.putFloat(idx+0*4, (j-NUM_VERTS_X*0.5f)*TriangleSizeX);
//				verticesBuffer.putFloat(idx+1*4, height);
//				verticesBuffer.putFloat(idx+2*4, (i-NUM_VERTS_Y*0.5f)*TriangleSizeY);
				//end
				//calculate normal
				Vector3f normal = calculateNormal(j,i,image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
				
				//for mesh --flip
//				gIndices.putInt(gx*NUM_VERTS_X+gz);
//				gIndices.putInt(gx*NUM_VERTS_X+gz+1);
//				gIndices.putInt((gx+1)*NUM_VERTS_X+gz+1);
//
//				gIndices.putInt(gx*NUM_VERTS_X+gz);
//				gIndices.putInt((gx+1)*NUM_VERTS_X+gz+1);
//				gIndices.putInt((gx+1)*NUM_VERTS_X+gz);
				//
//				gIndices.putInt(gz*NUM_VERTS_X+gx);
//				gIndices.putInt(gz*NUM_VERTS_X+gx+1);
//				gIndices.putInt((gz+1)*NUM_VERTS_X+gx+1);
//
//				gIndices.putInt(gz*NUM_VERTS_X+gx);
//				gIndices.putInt((gz+1)*NUM_VERTS_X+gx+1);
//				gIndices.putInt((gz+1)*NUM_VERTS_X+gx);
				
				gIndices.putInt(topLeft);
				gIndices.putInt(bottomLeft);
				gIndices.putInt(topRight);

				gIndices.putInt(topRight);
				gIndices.putInt(bottomLeft);
				gIndices.putInt(bottomRight);
				
			}
		}
		
		//instantiate the indexMesh values here because it will be substantially easier
//		ByteBuffer vertexBase = ByteBuffer.wrap(Maths.floatArray2ByteArray(vertices));
//		ByteBuffer indexBase = ByteBuffer.wrap(Maths.intArray2ByteArray(indices));
		gIndices.flip();
		indMesh.triangleIndexBase = gIndices;
		indMesh.vertexBase = verticesBuffer;
		indMesh.numTriangles = totalTriangles;
		indMesh.numVertices = totalVerts;
		indMesh.triangleIndexStride = indexStride; //?
		indMesh.vertexStride = vertStride;//?
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	/**
	 * Return a normal vector for specified grid points ongiven buffered image
	 * @param x x coord
	 * @param z z coord
	 * @param image rgb color image signifying height values
	 * @return
	 */
	private Vector3f calculateNormal(int x, int z, BufferedImage image){
		float heightL = getHeight(x-1, z, image);
		float heightR = getHeight(x+1, z, image);
		float heightD = getHeight(x, z-1, image);
		float heightU = getHeight(x, z+1, image);
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		normal.normalise();
		return normal;
	}
	
	/**
	 * Return height for x,y coord in the input buffer image. The rgb colors
	 * of the buffered image represent a height map
	 * @param x
	 * @param y
	 * @param image
	 * @return
	 */
	private float getHeight(int x, int y, BufferedImage image){
		if(x<0 || x>=image.getHeight()  || y<0 || y>=image.getHeight()){
			return 0; // out of bounds
		}
		float height = image.getRGB(x, y);
		height += MAX_PIXEL_COLOUR/2f;
		height /= MAX_PIXEL_COLOUR/2f;
		height *= MAX_HIEGHT;
		//normalize it to +- max height
		return height;
	}
	
	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	/**
	 * Return height of terrain at the worldX and worldZ coord. The coordinates are
	 * normalized to the terrain and then uses a algorithm barryCentric to calculate height
	 * of a point from the three vertex points comprising that points mesh triangle
	 * 
	 * CALLING THIS METHOD WILL ONLY GIVE PROPER ANSWER IF COORDINATES CORRESPOND
	 * TO THIS TERRAIN
	 * 
	 * @param worldX x coordinate on the world terrain
	 * @param worldZ z coordinate on the world terrain
	 * @return height of terrain at given coordinate
	 */
	public float getHeightOfTerrain(float worldX, float worldZ){
		//convert world coord to relative to terrain
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		//sides equal one less than vertices
		float gridSquareSize = SIZE / ((float )heights.length - 1);
		int gridX = (int)Math.floor(terrainX /gridSquareSize);
		int gridZ = (int)Math.floor(terrainZ /gridSquareSize);
		if(gridX >= heights.length - 1 || gridZ >= heights.length -1 || gridX < 0 || gridZ < 0){
			return 0; //off grid
		}
		//determine grid coord
		float xCoord = (terrainX % gridSquareSize)/ gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize)/ gridSquareSize;
		float answer;
		//line dividing square is x = 1- z so top square if  x < 1-z, and opposite
		//This determines which triangle of grid we are on, ie each grid is represented
		//by two triangles
		if (xCoord <= (1-zCoord)) {
			answer = Maths
					.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths
					.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		//return height
		return answer;
	}

	public CollisionShape getCollisionShape() {
		//create collision shape from the triangle height mesh?
		
		
		TriangleIndexVertexArray strider = new TriangleIndexVertexArray();
		CollisionShape collisionShape = new BvhTriangleMeshShape(strider, true, true);
		return collisionShape;
	}

	@Override
	public float getMass() {
		//return 0 means it is static
		return 0f;
	}

	@Override
	public void setMass(float mass) {
		// DO NOTHINg , mabye in future something cool , prob not
	}

	public Matrix4f getTransformationMatrix() {
		// not moving so irrelevant
		return null;
	}
	public Matrix4f getInitialTransformationMatrix(){
		return null;
	}
	@Override
	public Vector3f getPosition() {
		// TODO Auto-generated method stub
		return this.position;
	}
	
	public IndexedMesh getIndexedMeshShape(){
		return this.indMesh;
	}
	
}
