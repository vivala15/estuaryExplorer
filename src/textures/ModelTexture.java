package textures;

public class ModelTexture {

	private int textureID;
	private int normalMap;
	


	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false; //if set true, renderer won't render alpha < .5 parts
	private boolean useFakeLighting = false; //for models with weird normals that give light artifacts
	
	//for texture atlases
	private int numberOfRows = 1; //default for normal texture
	
	public ModelTexture(int id){
		this.textureID = id;
	}
	
	
	public int getID(){
		return this.textureID;
	}

	
	public int getNumberOfRows() {
		return numberOfRows;
	}


	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}


	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}


	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}


	public boolean isHasTransparency() {
		return hasTransparency;
	}


	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}


	public float getShineDamper() {
		return shineDamper;
	}


	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}


	public float getReflectivity() {
		return reflectivity;
	}


	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	
	public int getNormalMap() {
		return normalMap;
	}


	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	
}
