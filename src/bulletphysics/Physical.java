package bulletphysics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


import toolbox.Maths;

public interface Physical {

	public float getMass();
	public void setMass(float mass);
	public Matrix4f getTransformationMatrix();
	public Matrix4f getInitialTransformationMatrix();
	public Vector3f getPosition();
}
