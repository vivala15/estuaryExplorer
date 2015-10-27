package renderengine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import models.DynamicRawModel;
import models.RawModel;
import textures.TextureData;

public class Loader {

	
	
	//keep track of vaos and vbos created
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer> ();
	//so we canremove textures
	private List<Integer> textures = new ArrayList<Integer>();
	
	/**
	 * 
	 * @param positions
	 * @return
	 */
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		//store zero, no paticular reason its first
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,2, textureCoords);
		storeDataInAttributeList(2,3, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
		
	}
	
	public DynamicRawModel loadDynamicToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices){
		int vaoID = createVAO();
		int indicesBuffer = bindDynamicIndicesBuffer(indices);
		//store zero, no paticular reason its first
		int vbo_pos = storeDynamicDataInAttributeList(0,3, positions);
		int vbo_texture = storeDynamicDataInAttributeList(1,2, textureCoords);
		int vbo_normals = storeDynamicDataInAttributeList(2,3, normals);
		unbindVAO();
		DynamicRawModel drm = new DynamicRawModel(vaoID, indices.length, indicesBuffer, vbo_pos,
				vbo_texture, vbo_normals);
		return drm;
		
	}
	
	
	/**
	 * 
	 * @param positions
	 * @return
	 */
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents,
			int[] indices){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		//store zero, no paticular reason its first
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,2, textureCoords);
		storeDataInAttributeList(2,3, normals);
		storeDataInAttributeList(3,3, tangents);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
		
	}
	
	
	
	/**
	 * 2d for for gui, 3d for skybox
	 * @param positions
	 * @param dimensions
	 * @return
	 */
	public RawModel loadToVAO(float[] positions, int dimensions){
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length/2);
	}
	
	public RawModel loadToVAO(float[] positions, float[] colors){
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, 3, positions);
		this.storeDataInAttributeList(1, 3, colors);
		unbindVAO();
		return new RawModel(vaoID, positions.length/3);
	}
	
	
	public int loadCubeMap(String[] textureFiles){
		//gen empty texture and get id
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		
		for(int i=0; i<textureFiles.length; i++){
			TextureData data = decodeTextureFile("res/" + textureFiles[i] + ".png");
			//target is which face to load to, ex GL_TEXTURE_CUBE_MAP_POSITIVE_X
			//these are ints so we can do something cool
			//depends on order
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(),
					0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		//make texture appear smooth
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		textures.add(texID);
		//if seems appear on edges (I don't see any - based on computer hardware)
		//then add these lines
//		GL11.glTexParameteri(GL13.GL_TEXTURE_CUB­E_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
//		GL11.glTexParameteri(GL13.GL_TEXTURE_CUB­E_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		return texID;
	}
	
	
	private TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}
	
	public int loadTexture(String relativeFilePath){
		Texture texture = null;
		//only using png in example code
		try {
			//take last three characters and caps them for getTexture
			String fileEnd = relativeFilePath.substring(relativeFilePath.length()-3, relativeFilePath.length());
			texture = TextureLoader.getTexture(fileEnd.toUpperCase(), new FileInputStream(relativeFilePath));
			//activate mipmapping
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D); //create low res from high res texture
			//arg 2 if angle/diatance makes it cover less of screen, do arg 3
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			//how much mipmapping - render higher resolution more negative
			//sometimes mipmapping blurs normalmaps, if so quick fix is to make it more negative
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -.5f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	
	
	public int loadTexture(String fileName, boolean Old){
		Texture texture = null;
		//only using png in example code
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
			//activate mipmapping
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D); //create low res from high res texture
			//arg 2 if angle/diatance makes it cover less of screen, do arg 3
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			//how much mipmapping - render higher resolution more negative
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -.5f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	public int loadTextureTGA(String fileName, boolean Old){
		Texture texture = null;
		//only using png in example code
		try {
			texture = TextureLoader.getTexture("TGA", new FileInputStream("res/"+fileName+".tga"));
			//activate mipmapping
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D); //create low res from high res texture
			//arg 2 if angle/diatance makes it cover less of screen, do arg 3
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			//how much mipmapping - render higher resolution more negative
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -.5f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	
	public void cleanUp(){
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
			
		}
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture:textures){
			GL11.glDeleteTextures(texture);
		}
	}
	
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		//bind by the ID, bound till unbound
		GL30.glBindVertexArray(vaoID);
		return vaoID;
		
	}
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize,float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		//never want to edit it once in buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber,  coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		//unbind current VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private int storeDynamicDataInAttributeList(int attributeNumber, int coordinateSize,float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		// want to edit it once in buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber,  coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		//unbind current VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}
	
	
	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		//tell opengl to use it as indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private int bindDynamicIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		//tell opengl to use it as indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
		return vboID;
	}
	
	
	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private void unbindVAO(){
		//unbinds teh current buffer
		GL30.glBindVertexArray(0);
	}
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		//knows we are done writing so can flip it for reading
		buffer.flip();
		return buffer;
		
	}
}
