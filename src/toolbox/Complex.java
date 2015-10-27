package toolbox;

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

/**
 * a simple implementation of a complex number
 * @author sascha hartleb
 *
 */
public class Complex
{
	
	public static Random r = new Random();
	public static int _additions = 0;
	public static int _multiplications = 0;

	/**
	 * real part
	 */
	public float _a = 0;

	/**
	 * imaginary part
	 */
	public float _b = 0;


	public Complex()
	{

	}


	public Complex(float a, float b)
	{
		_a = a;
		_b = b;
	}


	public Complex mult(Complex c)
	{
		_multiplications++;
		return new Complex(_a * c._a - _b * c._b, _a * c._b + _b * c._a);
	}


	public Complex mult(float c)
	{
		return new Complex(_a * c, _b * c);
	}


	public Complex add(Complex c)
	{
		_additions++;
		return new Complex(_a + c._a, _b + c._b);
	}


	public Complex sub(Complex c)
	{
		_additions++;
		return new Complex(_a - c._a, _b - c._b);
	}


	public Complex negate()
	{
		return new Complex(-_a, -_b);
	}


	public void set(Complex c)
	{
		_a = c._a;
		_b = c._b;
	}


	public Complex clone()
	{
		return new Complex(_a, _b);
	}


	public void resetCounter()
	{
		_additions = 0;
		_multiplications = 0;
	}

	
	public static Complex gaussianRandomVariable()
	{
		float x1, x2, w;
		do
		{
			
			
//			x1 = 2.f * FastMath.nextRandomFloat() - 1.f;
//			x2 = 2.f * FastMath.nextRandomFloat() - 1.f;
			
			x1 = 2.f * r.nextFloat() - 1.f;
			x2 = 2.f * r.nextFloat() - 1.f;
			w = x1 * x1 + x2 * x2;
		} while (w >= 1.f);
		w = (float) FastMath.sqrt((-2.f * FastMath.log(w)) / w);

		return new Complex(x1 * w, x2 * w);
	}


}
