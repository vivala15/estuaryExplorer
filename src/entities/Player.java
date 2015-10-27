package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderengine.DisplayManager;

public abstract class Player{

	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED =  160;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	
	
//	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
//		//super(model, position, rotX, rotY, rotZ, scale);
//		// TODO Auto-generated constructor stub
//	}

//	public void move(){
//		checkInputs();
//		//super.increasePosition(0, 0, 0);
//		float distance = currentSpeed* DisplayManager.getFrameTimeSeconds();
//		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
//	}
	
	public abstract void checkInputs();
	public abstract void shiftCamera();
}
