package game;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector4f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.Transform;

import entities.Camera;
import entities.Player;

public class AirboatPlayer extends Player{


	Camera camera;
	AirBoat airboat;
	Transform desiredPositionTransform = new Transform();
	Transform forceTransform = new Transform();
	
	Vector3f dr = new Vector3f();
	Vector3f delta_angle = new Vector3f();
	Vector3f desiredPos = new Vector3f();
	Quat4f rot = new Quat4f();
	Vector4f desiredRot = new Vector4f();
	Vector3f cameraVel = new Vector3f();
	Vector3f angle_vel = new Vector3f();
	javax.vecmath.Vector3f desiredOffSet = new javax.vecmath.Vector3f(0f, 10f, -5f);
	
	public AirboatPlayer(Camera camera, AirBoat airboat){
		this.camera = camera;
		this.airboat = airboat;
	}
	
	
	/**
	 * Sets desiredPos field to proper spot given position of the airboat
	 * @param airboatPos
	 */
	public void setDesiredPosition(Vector3f vec, Vector4f angle){
		airboat.getBody().getMotionState().getWorldTransform(desiredPositionTransform);
		desiredPositionTransform.transform(desiredOffSet);
		desiredPositionTransform.getRotation(rot);
		
		vec.x = desiredOffSet.x; vec.y = desiredOffSet.y;vec.z = desiredOffSet.z;
//		desiredOffSet.angle(desiredRot);
		angle.x = rot.getX(); angle.y = rot.getY(); angle.z = rot.getZ(); 
		angle.w = rot.getW();
		//set back for next time
		desiredOffSet.x = 0f; desiredOffSet.y = 27f; desiredOffSet.z = -40f;
	}
	
	//move camera by hooke's law to a position above and behind the boat....
	public void shiftCamera(){
		//camera position
		Vector3f camPos = camera.getPosition();
		setDesiredPosition(desiredPos, desiredRot);
//		System.out.println(desiredRot.getX());// z
//		System.out.println(desiredRot.getY());// z
//		System.out.println(desiredRot.getZ());// y
		float y = desiredRot.getY();
		float x = desiredRot.getX();
		float z = desiredRot.getZ();
		float w = desiredRot.getW();
		//definitely find a speedup
//		 float roll  = (float) Math.atan2(2*y*w - 2*x*z, 1 - 2*y*y - 2*z*z);
		 float pitch = (float) Math.atan2(2*x*w - 2*y*z, 1 - 2*x*x - 2*z*z);
//		 float yaw   =  (float) Math.asin(2*x*y + 2*z*w);
		
		 float yaw  = (float) Math.atan2(2*y*w - 2*x*z, 1 - 2*y*y - 2*z*z);
		 float roll   =  (float) Math.asin(2*x*y + 2*z*w);
		 //Direction of motion
		Vector3f.sub(desiredPos, camPos, dr);
		dr.scale(.3f);
		//undamped harmonic oscillation without resistance, so add resistance
		cameraVel.scale(.1f); //dampening coeff
		Vector3f.sub(dr, cameraVel, dr); //drag?
		
		//update the camera vel
		Vector3f.add(cameraVel, dr, cameraVel);
		
		
		//integrate into the position
		camera.getPosition().x += cameraVel.x;
		camera.getPosition().y += cameraVel.y;
		camera.getPosition().z += cameraVel.z;
		
		Vector3f desiredRot = new Vector3f((float) (pitch*57f + 23.0f), roll*57f, -yaw * 57f +180f);
		Vector3f rotPos = new Vector3f(camera.getPitch(), camera.getRoll(), camera.getYaw());
		Vector3f.sub(desiredRot, rotPos, delta_angle);
		delta_angle.scale(.02f); //force constant
		angle_vel.scale(.1f); //inverse drag
		//Vector3f.sub(delta_an1le, angle_vel, delta_angle);
		Vector3f.add(angle_vel, delta_angle, angle_vel);
		
		
		
		//Pull camera rotation to match pt of player
//		if(pitch*57 > 25){
//			pitch =( pitch*57 -12)/57;
//		}
		camera.setPitch(pitch*57 + 25);
//		camera.setRoll(roll*57);
		camera.setRoll(0);
		camera.setYaw(-yaw*57 +180);
		
		camera.getPosition().x = desiredPos.x;
		camera.getPosition().y = desiredPos.y;
		camera.getPosition().z = desiredPos.z;
//		camera.setPitch((camera.getPitch() + angle_vel.x * 57));
//		camera.setRoll((camera.getRoll() + angle_vel.y * 57));
//		camera.setYaw((camera.getYaw() + angle_vel.z * 57.12f));
		
		
		
		
		//System.out.println(camera.getYaw());
//		System.out.println("Yaw: " + Float.toString(camera.getYaw()));
//		System.out.println("Roll: " + Float.toString(camera.getRoll()));
//		System.out.println("Pitch: " + Float.toString(camera.getPitch()));
		
	}
	
	@Override
	public void checkInputs() {
		//incorrect want direction not position
		airboat.getBody().getMotionState().getWorldTransform(forceTransform);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			//translate z
			javax.vecmath.Vector3f w = new javax.vecmath.Vector3f(0,0,1);
			javax.vecmath.Vector3f origin = new javax.vecmath.Vector3f(0,0,0);
			//rot.
			//w.normalize();
			forceTransform.transform(w);
			forceTransform.transform(origin);
			//w.scale(1000);
			//System.out.println(w);
			w.sub(origin);
			//System.out.println(w);
			w.scale(100000f);
			airboat.applyForce(w,
					new javax.vecmath.Vector3f(0,0,0));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			javax.vecmath.Vector3f w = new javax.vecmath.Vector3f(0,0,-1);
			javax.vecmath.Vector3f origin = new javax.vecmath.Vector3f(0,0,0);
			//w.normalize();
			forceTransform.transform(w);
			forceTransform.transform(origin);
			w.sub(origin);
			w.scale(200000);
			airboat.applyForce(w,
					new javax.vecmath.Vector3f(0,0,0));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			javax.vecmath.Vector3f w = new javax.vecmath.Vector3f(1,0,0);
			javax.vecmath.Vector3f origin = new javax.vecmath.Vector3f(0,0,0);
			//			w.normalize();
			forceTransform.transform(w);
			forceTransform.transform(origin);
			w.sub(origin);
			w.scale(10000);
			
			airboat.applyForce(w,
					new javax.vecmath.Vector3f(0,0,0));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			javax.vecmath.Vector3f w = new javax.vecmath.Vector3f(-1,0,0);
			javax.vecmath.Vector3f origin = new javax.vecmath.Vector3f(0,0,0);
			forceTransform.transform(w);
			forceTransform.transform(origin);
			w.sub(origin);
			w.scale(10000);
			airboat.applyForce(w,
					new javax.vecmath.Vector3f(0,0,0));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			airboat.getBody().applyTorque(new javax.vecmath.Vector3f(0,-50000f,0));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E)){
			airboat.getBody().applyTorque(new javax.vecmath.Vector3f(0,50000f,0));
			
		}
		Quat4f out = new Quat4f();
		airboat.getBody().getOrientation(out);
		
		
//		System.out.println("Airboat position");
//		//System.out.println(airboat.getPosition());
//		System.out.println("Physical box pos");
//		javax.vecmath.Vector3f boxPos = new javax.vecmath.Vector3f();
//		airboat.getBody().getCenterOfMassPosition(boxPos);
//		System.out.println(boxPos);
		
	}

}
