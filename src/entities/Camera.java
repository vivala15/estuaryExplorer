package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Reperesent our virtual camera
 * @author chris
 *
 */
public class Camera {
	
	private Vector3f position = new Vector3f(0,2f,0);

	private float pitch;
	private float yaw = 175f;
	private float roll;
	
	public Camera(){
		
	}
	
	public void move(){
		//mathc to keyboard for now
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			position.z-=1.3f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			position.z+=1.3f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			position.x+=1.3f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			position.x-=1.3f;
		}
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_E)){
			yaw-=1.6f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			yaw+=1.6f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_X)){
			position.y +=1.3f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C)){
			position.y -= 1.3f;
		}
		int dWheel = Mouse.getDWheel();
		pitch = pitch + dWheel/30.0f;
	}
	
	public void invertPitch(){
		this.pitch =- pitch;
	}
	
	public void invertRoll(){
		this.roll =- roll;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}
	

}
