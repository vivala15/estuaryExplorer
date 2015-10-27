package bulletphysics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;

import toolbox.Maths;

public interface Physical {

	public CollisionShape getCollisionShape();
	public float getMass();
	public float setMass();
	public Matrix4f getTransformationMatrix();
	public Matrix4f getInitialTransformationMatrix();
	public Vector3f getPosition();
	public void setBody(RigidBody body);
	public RigidBody getBody();
}
