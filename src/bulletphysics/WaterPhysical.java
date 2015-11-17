package bulletphysics;

public interface WaterPhysical extends Physical{

	public float getMassPerDensityPoint();
	public float getVolumePerDensityPoint();
	public javax.vecmath.Vector3f[] getDensityPositions();
}
