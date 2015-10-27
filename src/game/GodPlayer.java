package game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Player;
import models.TexturedModel;


/**
 * Rather than being a player in the game, this class will have ability to explore world 
 * with a free camera and includes some other abilities such as debug mode
 * @author chris
 *
 */
public class GodPlayer extends Player{

	private Camera camera;
	public GodPlayer(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void checkInputs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shiftCamera() {
		camera.move();
		
	}
	
	

}
