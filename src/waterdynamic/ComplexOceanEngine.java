package waterdynamic;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.apache.commons.math3.util.FastMath;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import toolbox.Complex;

/**
 * A class that can calculate the vertex positions of an ocean-like plane.
 * Does not provide a mesh
 *  
 * @author sascha
 *
 */
public class ComplexOceanEngine implements OceanEngine
{
	private final float GRAVITY = 9.81f;
	
	/**
	 * count of edges on one axis
	 */
	public int N; 
	
	/**
	 * count of vertices on one axis
	 */
	public int Nplus1;

	private float amplitude;
	private Vector2f windVector;

	private float length;

	private Complex[] h_tilde, h_tilde_slopex, h_tilde_slopez, h_tilde_dx, h_tilde_dz;

	private VertexOcean[] _vertices;
	private int[] _indices;

	FloatFFT_2D fft2d;

	public Vector2f _currentLocation;
	

	/**
	 * @param N
	 *            count of edges on one axis
	 * @param A
	 *            Amplitude
	 * @param windVector
	 * @param length
	 */
	public ComplexOceanEngine(int N, float A, Vector2f windVector, float length)
	{
		this.N = N;
		this.Nplus1 = N + 1;
		this.amplitude = A;
		this.windVector = windVector;
		this.length = length;

		initVertices();

		int index;
		int indicesCount = 0;
		for (int m_prime = 0; m_prime < N; m_prime++)
		{
			for (int n_prime = 0; n_prime < N; n_prime++)
			{
				index = m_prime * Nplus1 + n_prime;

				_indices[indicesCount++] = index; // two triangles
				_indices[indicesCount++] = index + Nplus1;
				_indices[indicesCount++] = index + Nplus1 + 1;
				_indices[indicesCount++] = index;
				_indices[indicesCount++] = index + Nplus1 + 1;
				_indices[indicesCount++] = index + 1;
			}
		}
		
		fft2d = new FloatFFT_2D(N, N);
		
		_currentLocation = new Vector2f();
	}
	
	@Override
	public void initVertices(){
		
		h_tilde = new Complex[N * N];
		h_tilde_slopex = new Complex[N * N];
		h_tilde_slopez = new Complex[N * N];
		h_tilde_dx = new Complex[N * N];
		h_tilde_dz = new Complex[N * N];

		_vertices = new VertexOcean[Nplus1 * Nplus1];
		_indices = new int[Nplus1 * Nplus1 * 10];
		
		Complex htilde0, htilde0mk_conj;

		int index;
		for (int m_prime = 0; m_prime < Nplus1; m_prime++)
		{
			for (int n_prime = 0; n_prime < Nplus1; n_prime++)
			{
				index = m_prime * Nplus1 + n_prime;

				htilde0 = hTilde_0(n_prime, m_prime);
				htilde0mk_conj = hTilde_0(n_prime, m_prime).clone();

				_vertices[index] = new VertexOcean();
				_vertices[index].a = htilde0._a;
				_vertices[index].b = htilde0._b;
				_vertices[index]._a = htilde0mk_conj._a;
				_vertices[index]._b = htilde0mk_conj._b;

				_vertices[index]._ox = _vertices[index]._x = (n_prime ) * length / N;
				_vertices[index]._oy = _vertices[index]._y = 0.0f;
				_vertices[index]._oz = _vertices[index]._z = (m_prime ) * length / N;

				_vertices[index]._nx = 0.0f;
				_vertices[index]._ny = 1.0f;
				_vertices[index]._nz = 0.0f;
			}
		}
	}


	/**
	 * dispersion = Zerstreuung
	 * @param n_prime
	 * @param m_prime
	 * @return
	 */
	public float dispersion(int n_prime, int m_prime)
	{
		float w_0 = (float) (2.0f * FastMath.PI / 200.0f);
		float kx = (float) (FastMath.PI * (2 * n_prime - N) / length);
		float kz = (float) (FastMath.PI * (2 * m_prime - N) / length);
		return (float) (FastMath.floor(FastMath.sqrt(GRAVITY * FastMath.sqrt(kx * kx + kz * kz)) / w_0)* w_0);
	}


	public float phillips(int n_prime, int m_prime)
	{
		Vector2f k = new Vector2f((float)FastMath.PI * (2 * n_prime - N) / length,(float) FastMath.PI
				* (2 * m_prime - N) / length);
		float k_length = k.length();
		if (k_length < 0.000001)
			return 0;

		float k_length2 = k_length * k_length;
		float k_length4 = k_length2 * k_length2;

		windVector.normalize();
		k.normalize();
		float k_dot_w = k.dot(windVector);
		float k_dot_w2 = k_dot_w * k_dot_w;

		float windLength = windVector.length();
		float L = windLength * windLength / GRAVITY;
		float L2 = L * L;

		float damping = 0.001f;
		float l2 = L2 * damping * damping;

		return (float) (amplitude * FastMath.exp(-1.0f / (k_length2 * L2)) / k_length4 * k_dot_w2
				* FastMath.exp(-k_length2 * l2));
	}


	public Complex hTilde_0(int n_prime, int m_prime)
	{
		Complex r = Complex.gaussianRandomVariable();
		return r.mult((float)FastMath.sqrt(phillips(n_prime, m_prime) / 2.0f));
	}


	public Complex hTilde(float t, int n_prime, int m_prime)
	{
		int index = m_prime * Nplus1 + n_prime;

		Complex htile0 = new Complex(_vertices[index].a, _vertices[index].b);
		Complex htilde0mkconj = new Complex(_vertices[index]._a, _vertices[index]._b);

		float omegat = dispersion(n_prime, m_prime) * t;

		float cos = (float) FastMath.cos(omegat);
		float sin = (float) FastMath.sin(omegat);

		Complex c0 = new Complex(cos, sin);
		Complex c1 = new Complex(cos, -sin);

		Complex res = htile0.mult(c0).add(htilde0mkconj.mult(c1));

		return res;
	}



	/**
	 * evaluates waves with fft
	 * @param elapsedTime
	 */
	public void calcWaves(float elapsedTime)
	{	
		float kx, kz, len, lambda = -1.0f;
		
		int index, index1;

		for (int m_prime = 0; m_prime < N; m_prime++)
		{
			kz = (float) (FastMath.PI * (2.0f * m_prime - N) / length);
			for (int n_prime = 0; n_prime < N; n_prime++)
			{
				kx = (float) (FastMath.PI * (2.0f * n_prime - N) / length);
				len = (float) FastMath.sqrt(kx * kx + kz * kz);
				index = m_prime * N + n_prime;

				h_tilde[index] = hTilde(elapsedTime, n_prime, m_prime);
				h_tilde_slopex[index] = h_tilde[index].mult(new Complex(0, kx));
				h_tilde_slopez[index] = h_tilde[index].mult(new Complex(0, kz));
				if (len < 0.000001f)
				{
					h_tilde_dx[index] = new Complex(0.0f, 0.0f);
					h_tilde_dz[index] = new Complex(0.0f, 0.0f);
				} else
				{
					h_tilde_dx[index] = h_tilde[index].mult(new Complex(0, -kx / len));
					h_tilde_dz[index] = h_tilde[index].mult(new Complex(0, -kz / len));
				}
			}
		}

		//the fast fourier transformation
		fftJTransform(h_tilde);
		fftJTransform(h_tilde_slopex);
		fftJTransform(h_tilde_slopez);
		fftJTransform(h_tilde_dx);
		fftJTransform(h_tilde_dz);

		int sign;
		int signs[] = { 1, -1 };
		Vector3f n;
		for (int m_prime1 = 0; m_prime1 < N; m_prime1++)
		{
			for (int n_prime1 = 0; n_prime1 < N; n_prime1++)
			{				
				index = m_prime1 * N + n_prime1; // index into h_tilde..
				
				float movefactor = N / length; 
				
				int m_prime = (int)(m_prime1 + _currentLocation.y*movefactor)%(N);
				int n_prime = (int)(n_prime1 -_currentLocation.x*movefactor)%(N);

				if(m_prime < 0)
					m_prime += N;

				if(n_prime < 0)
					n_prime += N;

				index1 = m_prime1 * Nplus1 + n_prime1; // index into vertices

				sign = signs[(n_prime1 + m_prime1) & 1];

				h_tilde[index] = h_tilde[index].mult(sign);

				// height
				_vertices[index1]._y = h_tilde[index]._a;

				// displacement
				h_tilde_dx[index] = h_tilde_dx[index].mult(sign);
				h_tilde_dz[index] = h_tilde_dz[index].mult(sign);
				
				_vertices[index1]._x = _vertices[index1]._ox + h_tilde_dx[index]._a * lambda;
				_vertices[index1]._z = _vertices[index1]._oz + h_tilde_dz[index]._a * lambda;

				// normal
				h_tilde_slopex[index] = h_tilde_slopex[index].mult(sign);
				h_tilde_slopez[index] = h_tilde_slopez[index].mult(sign);
				
				n = new Vector3f(0.0f - h_tilde_slopex[index]._a, 1f, 0.0f - h_tilde_slopez[index]._a);

				_vertices[index1]._nx = n.x;
				_vertices[index1]._ny = n.y;
				_vertices[index1]._nz = n.z;

				// for tiling
				if (n_prime == 0 && m_prime == 0)
				{
					_vertices[index1 + N + Nplus1 * N]._y = h_tilde[index]._a;

					_vertices[index1 + N + Nplus1 * N]._x = _vertices[index1 + N + Nplus1 * N]._ox + h_tilde_dx[index]._a * lambda;
					_vertices[index1 + N + Nplus1 * N]._z = _vertices[index1 + N + Nplus1 * N]._oz + h_tilde_dz[index]._a * lambda;

					_vertices[index1 + N + Nplus1 * N]._nx = n.x;
					_vertices[index1 + N + Nplus1 * N]._ny = n.y;
					_vertices[index1 + N + Nplus1 * N]._nz = n.z;
				}
				if (n_prime == 0)
				{
					_vertices[index1 + N]._y = h_tilde[index]._a;
					_vertices[index1 + N]._x = _vertices[index1 + N]._ox + h_tilde_dx[index]._a * lambda;
					_vertices[index1 + N]._z = _vertices[index1 + N]._oz + h_tilde_dz[index]._a * lambda;

					_vertices[index1 + N]._nx = n.x;
					_vertices[index1 + N]._ny = n.y;
					_vertices[index1 + N]._nz = n.z;
				}
				if (m_prime == 0)
				{
					_vertices[index1 + Nplus1 * N]._y = h_tilde[index]._a;

					_vertices[index1 + Nplus1 * N]._x = _vertices[index1 + Nplus1 * N]._ox + h_tilde_dx[index]._a * lambda;
					_vertices[index1 + Nplus1 * N]._z = _vertices[index1 + Nplus1 * N]._oz + h_tilde_dz[index]._a * lambda;

					_vertices[index1 + Nplus1 * N]._nx = n.x;
					_vertices[index1 + Nplus1 * N]._ny = n.y;
					_vertices[index1 + Nplus1 * N]._nz = n.z;
				}
			}
		}
	}


	public float[] values = null;
	
	/**
	 * Execute the fast fourier transformation
	 * 
	 * @param complexArray the source and target array
	 */
	public void fftJTransform(Complex[] complexArray)
	{
		values = new float[complexArray.length * 2];

		for (int x = 0; x < complexArray.length; x++)
		{
			values[x * 2] = complexArray[x]._a;
			values[x * 2 + 1] = complexArray[x]._b;
		}

		fft2d.complexForward(values);
		
		for (int x = 0; x < complexArray.length; x++)
		{
			complexArray[x]._a = values[x * 2];
			complexArray[x]._b = values[x * 2 + 1];
		}
	}

	@Override
	public float getAmplitude()
	{
		return amplitude;
	}
	
	@Override
	public void setAmplitude(float a)
	{
		amplitude = a;
	}
	
	@Override
	public void setWind(Vector2f w)
	{
		windVector = w;
	}
	
	@Override
	public Vector2f getWind()
	{
		return windVector;
	}
	
	@Override
	public VertexOcean[] getVerticesOcean()
	{
		return _vertices;
	}
	
	@Override
	public int getVertexCountPerSide()
	{
		return Nplus1;
	}
	
	@Override
	public float getLength()
	{
		return length;
	}
}

