package toolbox;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import terrains.Terrain;

/**
 * This class takes a camera and projection matrix and using those can provide a ray given
 * based on where the mouse pointer is on the screen. A lot of the computation deals
 * with converting between different "spaces"
 * 
 * These span
 * Local space, world space, eye space, homogeneous clip space, normalised device space
 * and viewport space. 
 * 
 * For more info on what these are and how to convert between them 
 * see http://antongerdelan.net/opengl/raycasting.html
 * 
 * @author chris
 *
 */
public class MousePicker {
	
	//Number of binary search recursions to find terrain intersection
	private static final int RECURSION_COUNT = 200;
	//end point to begin binary search
	private static final float RAY_RANGE = 600;
	
	private Vector3f currentRay;
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	private Terrain terrain;
	private Vector3f currentTerrainPoint;
	
	public MousePicker(Camera cam, Matrix4f projection, List<Terrain> terrains){
		this.camera = cam;
		this.projectionMatrix = projection;
		this.viewMatrix = Maths.createViewMatrix(camera);
		this.terrain = terrains.get(0);
	}
	
	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}
	
	public Vector3f getCurrentRay(){
		return currentRay;
	}
	
	/**
	 * Called to update the view matrix per frame and then calculate the current ray 
	 * from the mouse pointer
	 */
	public void update(){
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
		} else {
			currentTerrainPoint = null;
		}
	}
	
	/**
	 * Calculate the mouse ray. Takes the screen coordinates and applies inverse operations
	 * to take the screen pts into world space. For more information see
	 * 
	 * http://antongerdelan.net/opengl/raycasting.html
	 * 
	 * @return 
	 */
	private Vector3f calculateMouseRay(){
		//get screen coordinates
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		
		//to nds
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
		
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
		//clip space to eye space
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		//eye space  to world space
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}
	
	/**
	 * Convert from eye coordinate space to the 4d world coorindates
	 * @param eyeCoords
	 * @return
	 */
	private Vector3f toWorldCoords(Vector4f eyeCoords){
		//invert the view matrix
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		//use inverted view to transform eye coords into a ray in world space
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		//only need a 3d vector so convert
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		//normalise it into a unit vector
		mouseRay.normalise();
		return mouseRay;
	}
	
	
	private Vector4f toEyeCoords(Vector4f clipCoords){
		//store after inverting the projection matrix
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		//transform the clip coords into eye space
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		//z -1 to point ray into screen
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
		
	}
	
	/**
	 * Convert screen points into openGL coordinate space - expressed as a simple linear
	 * equation
	 * 
	 * @param mouseX x coordinate of screen
	 * @param mouseY y coordinate of screen
	 * @return 2d vector representing opengl normalized device space 
	 */
	private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY){
		float x = (2f*mouseX) / Display.getWidth() - 1;
		float y = (2f * mouseY) / Display.getHeight() - 1f;
		return new Vector2f(x,y);
	}
	
	
	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}
	
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint) {
		Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
		float height = 0;
		if (terrain != null) {
			height = terrain.getHeightOfTerrain(testPoint.getX(), testPoint.getZ());
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Terrain getTerrain(float worldX, float worldZ) {
		return terrain;
	}
}
