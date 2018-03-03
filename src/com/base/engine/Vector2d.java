package com.base.engine;
public class Vector2d
{
	private double x;
	private double y;
	
	public Vector2d(double x, double y)
	{
		this.setX(x);
		this.setY(y);
	}
	public double length()
	{
		return Math.sqrt(x * x + y * y);
	}
	public double dot(Vector2d r)
	{
		return x * r.getX() + y * r.getY();
	}
	public Vector2d normalize()
	{
		double length = length();
		
		x /= length;
		y /= length;
		
		return this;
	}
	public Vector2d normalized()
	{
		double length = length();
		
		if(length == 0)
			return this;
		
		return new Vector2d(x / length, y / length);
	}
	public Vector2d rotate(double angle)
	{
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new Vector2d((x * cos - y * sin), (x * sin + y * cos));
	}
	public static double cartesianAngle(Vector2d v)
	{
		int mod = 1;
		if(v.getY() < 0) 
			mod = -1;
		Vector2d origin = new Vector2d(1,0);
		double length = v.length();
		double angle =  Math.acos(v.dot(origin) / length ) * mod;
		return angle;
	}
	public Vector2d project(Vector2d r)//projection of this vector onto vector r
	{
		if(r.dot(r) == 0)//uh oh, zero vector
			return this;//should work
		return new Vector2d( ( ( dot(r) / (r.getX() * r.getX() + r.getY() * r.getY()) ) * r.getX() ), ( ( dot(r) / (r.getX() * r.getX() + r.getY() * r.getY()) ) * r.getY() ) );
	}
	public Vector2d normL()
	{
		return new Vector2d(y,-x);
	}
	public Vector2d add(Vector2d r)
	{
		return new Vector2d(x + r.getX(), y + r.getY() );
	}
	public Vector2d add(double r)
	{
		return new Vector2d(x + r, y + r);
	}
	public Vector2d sub(Vector2d r)
	{
		return new Vector2d(x - r.getX(), y - r.getY() );
	}
	public Vector2d sub(double r)
	{
		return new Vector2d(x - r, y - r);
	}	
	public Vector2d mul(Vector2d r)
	{
		return new Vector2d(x * r.getX(), y * r.getY() );
	}
	public Vector2d mul(double r)
	{
		return new Vector2d(x * r, y * r);
	}	
	public Vector2d div(Vector2d r)
	{
		return new Vector2d(x / r.getX(), y / r.getY() );
	}
	public Vector2d div(double r)
	{
		return new Vector2d(x / r, y / r);
	}
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
	public double getX()
	{
		return x;
	}

	public void setX(double x) 
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}
}




