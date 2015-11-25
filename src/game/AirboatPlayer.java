package game;

import javax.vecmath.Quat4f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.bulletphysics.linearmath.Transform;

import entities.Camera;
import entities.Player;

public class AirboatPlayer extends Player{

	float THRUST  = 40000f;
	
	Camera camera;
	AirBoat airboat;
	Transform desiredPositionTransform = new Transform();
	Transform forceTransform = new Transform();
	
	Vector3f dr = new Vector3f();
	Vector3f delta_angle = new Vector3f();
	Vector4f desiredPos = new Vector4f();
	Quat4f rot = new Quat4f();
	Vector4f desiredRot = new Vector4f();
	Vector3f cameraVel = new Vector3f();
	Vector3f angle_vel = new Vector3f();
	Vector4f desiredOffSet = new Vector4f(2f, 100f, -60f,1f);
//	Vector4f desiredOffSet = new Vector4f(2f, 200f, -300f,1f);
	Vector3f scale_vec = new Vector3f();
	
	public AirboatPlayer(Camera camera, AirBoat airboat){
		this.camera = camera;
		this.airboat = airboat;
		scale_vec.set(this.airboat.getScale(), this.airboat.getScale(), this.airboat.getScale());
	}
	
	
	/**
	 * Sets desiredPos field to proper spot given position of the airboat
	 * @param airboatPos
	 */
	public void setDesiredPosition(){

		Matrix4f currentTransform = this.airboat.getCurrentTransformationMatrix();
		//currentTransform.scale(scale_vec);
		Matrix4f.transform(currentTransform, desiredOffSet, desiredPos);
		//set back for next time
		//desiredOffSet.x = 0f; desiredOffSet.y = 27f; desiredOffSet.z = -40f;
	}
	
	//move camera by hooke's law to a position above and behind the boat....
	public void shiftCamera(){
		//camera position
		Vector3f camPos = camera.getPosition();
		setDesiredPosition();
		
		camera.setPitch(this.airboat.getRotX() + 17f);
		camera.setRoll(0);
		camera.setYaw(-this.airboat.getRotY() +180);
		
		
		camera.getPosition().x = desiredPos.x;
		camera.getPosition().y = desiredPos.y;
		camera.getPosition().z = desiredPos.z;
		
	}
	
	
	private Vector3f appliedForce = new Vector3f(0f,0f,0f);
	@Override
	public void checkInputs() {
		appliedForce.set(0, 0, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			appliedForce.set(0, 0, THRUST);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			appliedForce.set(0, 0, -THRUST);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			appliedForce.set(-THRUST, 0, 0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			appliedForce.set(THRUST, 0, 0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			this.airboat.setRotY(this.airboat.getRotY()+.5f);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E)){
			this.airboat.setRotY(this.airboat.getRotY()-.5f);
		}
		
		this.airboat.applyRelativeForce(appliedForce);
		
	}

}
