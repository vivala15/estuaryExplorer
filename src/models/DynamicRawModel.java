package models;

public class DynamicRawModel extends RawModel{
	
	private int pos_vboID;
	private int normal_vboID;
	private int texture_vboID;
	private int ind_buffer;
	


	/**
	 * Also remembers some vbo id so they can be assessed and do stuff
	 * @param vaoID
	 * @param vertexCount
	 * @param vbo_normals 
	 */
	public DynamicRawModel(int vaoID, int vertexCount,  int ind_buffer ,int pos_vboID, int normal_vboID, int texture_vboID ) {
		super(vaoID, vertexCount);
		this.pos_vboID = pos_vboID;
		this.normal_vboID = normal_vboID;
		this.texture_vboID = texture_vboID;
	}

	
	public int getPos_vboID() {
		return pos_vboID;
	}


	public int getNormal_vboID() {
		return normal_vboID;
	}


	public int getTexture_vboID() {
		return texture_vboID;
	}
	
	public int getInd_buffer() {
		return ind_buffer;
	}




}
