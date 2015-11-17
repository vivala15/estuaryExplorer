package waterdynamic;

/**
 * an ocean vertex that can be mapped on the ocean mesh
 * @author sascha
 *
 */
class VertexOcean
{
	/**
	 * position of the vertex in the mesh
	 */
	public float _x, _y, _z;// vertex
	
	/**
	 * normal value of the vertex
	 */
	public float _nx, _ny, _nz;// normal
	public float a, b, c;// htilde0
	public float _a, _b, _c;// htilde0mk conjugate
	
	public float _ox, _oy, _oz;// original position
}

