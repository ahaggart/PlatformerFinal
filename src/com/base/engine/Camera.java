package com.base.engine;
import org.newdawn.slick.Graphics;

public class Camera 
{
	private double x, y;
	private int width, height;
	
	public Camera(double x, double y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public void setLocation(double x,double y)
	{
		this.x = x;
		this.y = y;
	}
	public void translate(Graphics b)
	{
		b.translate((int)(width/2.0-x), (int)(height/2.0-y));
	}
	public void reverse(Graphics b)
	{
		b.translate((int)(x-width/2.0), (int)(y-height/2.0));
	}
	public double getX()
	{
		return x;
	}
	public double getY()
	{
		return y;
	}
	public int getWidth()
	{
		return width;
	}
	public int getHeight()
	{
		return height;
	}
	public double getRealX(double screenX)
	{
		return this.x + screenX - width / 2;
	}
	public double getRealY(double screenY)
	{
		return this.y + screenY - height / 2;
	}
}
