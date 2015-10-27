package toolbox;

import java.nio.ByteBuffer;


import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;

public class Maths {


	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	
	/**
	 * Find the height given a point in a triangle and the three vertex points
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param pos
	 * @return
	 */
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static void convertBulletToOpenGL(javax.vecmath.Matrix4f input, Matrix4f output){
		output.m00 = input.m00; output.m01 = input.m10;output.m02 = input.m20;output.m03 = input.m30;
		output.m10 = input.m01; output.m11 = input.m11;output.m12 = input.m21;output.m13 = input.m31;
		output.m20 = input.m02; output.m21 = input.m12;output.m22 = input.m22;output.m23 = input.m32;
		output.m30 = input.m03; output.m31 = input.m13;output.m32 = input.m23;output.m33 = input.m33;
	}
	
	public static void convertOpenGLToBullet(Matrix4f input, javax.vecmath.Matrix4f output){
		output.m00 = input.m00; output.m01 = input.m10;output.m02 = input.m20;output.m03 = input.m30;
		output.m10 = input.m01; output.m11 = input.m11;output.m12 = input.m21;output.m13 = input.m31;
		output.m20 = input.m02; output.m21 = input.m12;output.m22 = input.m22;output.m23 = input.m32;
		output.m30 = input.m03; output.m31 = input.m13;output.m32 = input.m23;output.m33 = input.m33;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry,
			float rz, float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera){
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0),
				viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0),
				viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0,0,1),
				viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static byte [] long2ByteArray (long value)
	{
	    return ByteBuffer.allocate(8).putLong(value).array();
	}

	public static byte [] float2ByteArray (float value)
	{  
	     return ByteBuffer.allocate(4).putFloat(value).array();
	}
	
	public static byte[] floatArray2ByteArray(float[] values){
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values){
            buffer.putFloat(value);
        }

        return buffer.array();
    }
	
	public static byte[] intArray2ByteArray(int[] values){
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (int value : values){
            buffer.putInt(value);
        }

        return buffer.array();
    }
	
	public static void vector3fjavaxTovector3flwjgl(javax.vecmath.Vector3f vecJavax, Vector3f vecLWJGL){
		vecLWJGL.x = vecJavax.x;
		vecLWJGL.y = vecJavax.y;
		vecLWJGL.z = vecJavax.z;
	}
	
	public static void vector4fjavaxTovector4flwjgl(javax.vecmath.Vector4f vecJavax, Vector4f vecLWJGL){
		vecLWJGL.x = vecJavax.x;
		vecLWJGL.y = vecJavax.y;
		vecLWJGL.z = vecJavax.z;
		vecLWJGL.w = vecJavax.w;
	}
	
	public static void vector3flwjglTovector4flwjgl(Vector3f vecJavax, Vector4f vecLWJGL){
		vecLWJGL.x = vecJavax.x;
		vecLWJGL.y = vecJavax.y;
		vecLWJGL.z = vecJavax.z;
		vecLWJGL.w = 1.0f;
	}
	
	public static void vector4flwjglTovector3flwjgl(Vector4f vecJavax, Vector3f vecLWJGL){
		vecLWJGL.x = vecJavax.x;
		vecLWJGL.y = vecJavax.y;
		vecLWJGL.z = vecJavax.z;
	
	}
}
