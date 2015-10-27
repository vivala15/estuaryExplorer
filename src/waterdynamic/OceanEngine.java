package waterdynamic;

import javax.vecmath.Vector2f;

/**
 * An interface that provides all method a basic ocean engine needs.
 * 
 * An ocean engine is a class that can process an ocean-like behaviour of a
 * plane (described by objects of type VertexOcean)
 * 
 * An ocean engine is completely decoupled from the ocean mesh so that it should
 * be possible to change the engine on the fly.
 * 
 * @author sascha
 * 
 */
public interface OceanEngine
{
	float getAmplitude();


	void setAmplitude(float a);


	Vector2f getWind();


	void setWind(Vector2f wind);


	void calcWaves(float elapsedTime);


	VertexOcean[] getVerticesOcean();


	void initVertices();


	int getVertexCountPerSide();


	float getLength();
}
