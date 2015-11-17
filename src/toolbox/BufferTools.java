package toolbox;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class BufferTools {

	
	
	public static FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		//knows we are done writing so can flip it for reading
		buffer.flip();
		return buffer;
		
	}
	//is creating vertices array then putting in the buffer faster than 
	//directly writing to buffer???
	//ie
	//buffer.put(index, f)
}
