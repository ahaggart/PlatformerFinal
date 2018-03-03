package com.base.engine;

public class Vector2f
{
	private float x;
	private float y;
	
	public Vector2f(float x, float y)
	{
		this.setX(x);
		this.setY(y);
	}
	
	public Vector2f copy()
	{
		return new Vector2f(x, y);
	}
	
	public float length()
	{
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public float lengthSquared()
	{
		return x * x + y * y;
	}
	
	public float max()
	{
		return Math.max(x, y);
	}
	
	public float dot(Vector2f r)
	{
		return x * r.getX() + y * r.getY();
	}
	public float cross(Vector2f r)
	{
		return x * r.getY() - y * r.getX();
	}
	public Vector2f normalized()
	{
		float length = length();
		
		float x_ = x / length;
		float y_ = y / length;
		
		return new Vector2f(x_, y_);
	}
	public Vector2f normL()
	{
		return new Vector2f(y, -x);
	}
	public Vector2f normR()
	{
		return new Vector2f(-y, x);
	}
	public Vector2f rotate(float angle)
	{
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new Vector2f((float)(x * cos - y * sin), (float)(x * sin + y * cos));
	}
	
	public Vector2f project(Vector2f r)//projection of this vector onto vector r
	{
		if(r.dot(r) == 0)//uh oh, zero vector
			return this;//should work
		return new Vector2f( ( ( dot(r) / (r.getX() * r.getX() + r.getY() * r.getY()) ) * r.getX() ), ( ( dot(r) / (r.getX() * r.getX() + r.getY() * r.getY()) ) * r.getY() ) );
	}
	
	public Vector2f lerp(Vector2f dest, float lerpFactor)
	{
		return dest.sub(this).mul(lerpFactor).add(this);
	}
	
	public Vector2f add(Vector2f r)
	{
		return new Vector2f(x + r.getX(), y + r.getY() );
	}
	public Vector2f add(float r)
	{
		return new Vector2f(x + r, y + r);
	}
	public Vector2f sub(Vector2f r)
	{
		return new Vector2f(x - r.getX(), y - r.getY() );
	}
	public Vector2f sub(float r)
	{
		return new Vector2f(x - r, y - r);
	}	
	public Vector2f mul(Vector2f r)
	{
		return new Vector2f(x * r.getX(), y * r.getY() );
	}
	public Vector2f mul(float r)
	{
		return new Vector2f(x * r, y * r);
	}	
	public Vector2f div(Vector2f r)
	{
		return new Vector2f(x / r.getX(), y / r.getY() );
	}
	public Vector2f div(float r)
	{
		return new Vector2f(x / r, y / r);
	}
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	public float getX()
	{
		return x;
	}

	public void setX(float x) 
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}
	
	public boolean equals(Vector2f r)
	{
		return x == r.getX() && y == r.getY();
	}
	
	public Vector2f set(float x, float y)
	{
		this.x = x;
		this.y = y;
		
		return this;
	}
	
	public Vector2f set(Vector2f r)
	{
		return set(r.getX(), r.getY());
	}
}
