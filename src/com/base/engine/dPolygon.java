package com.base.engine;
import org.newdawn.slick.geom.Polygon;

import com.base.entities.Entity;

public class dPolygon
{
	private double[][] points;
	private double angle;
	private Entity bound = null;//bind to an Entity if using for a hitbox, used to send collision data packets back to "owner" object
	private String name = "";
	
	public dPolygon()
	{
	}
	public dPolygon(double[][] points)
	{
		this.points = points;
	}
	public dPolygon(double x, double y, double width, double height)
	{
		double[][] temp = {{x,y},{x+width,y},{x+width,y+height},{x,y+height}};
		points = temp;
	}
	public void set(double[][] points)
	{
		this.points = points;
	}
	public void set(double x, double y, double width, double height)
	{
		double[][] temp = {{x,y},{x+width,y},{x+width,y+height},{x,y+height}};
		points = temp;
	}
	public double[][] get()
	{
		return points;
	}
	public int getNumPoints()
	{
		return points.length;
	}
	public double[] averagePoint()
	{
		double x = 0;
		double y = 0;
		for(int i = 0; i < points.length; i++)
		{
			x += points[i][0];
			y += points[i][1];
		}
		x /= points.length;
		y /= points.length;
		double[] temp = {x,y};
		return temp;
	}
	public void translate(double x, double y)
	{
		for(int i = 0; i < getNumPoints(); i++)
		{
			points[i][0] += x;
			points[i][1] += y;
		}
	}
	public int[] getX()
	{
		int[] x = new int[points.length];
		for(int i = 0; i < points.length; i++)
			x[i] = (int)points[i][0];
		return x;
	}
	public int[] getY()
	{
		int[] y = new int[points.length];
		for(int i = 0; i < points.length; i++)
			y[i] = (int)points[i][1];
		return y;
	}
	public Polygon makePolygon()
	{
		Polygon p = new Polygon();
		for(int i = 0; i < getNumPoints(); i++)
			p.addPoint((float)points[i][0],(float)points[i][1]);
		return p;
	}
	public double getAngle()
	{
		return angle;
	}
	public void rotate(double angle)//radians
	{
		double[] point = averagePoint();
		rotate(angle, point[0], point[1]);
	}
	public void rotate(double angle, double x, double y)//radians
	{
		this.angle += angle;
		
		for(int i = 0; i < points.length; i++)
		{
			double xDif = points[i][0] - x;
			double yDif = points[i][1] - y;
			Vector2d point = new Vector2d(xDif, yDif);
			double length = point.length();
			double current = Vector2d.cartesianAngle(point);
			current += angle;
			points[i][0] = Math.cos(current) * length + x;
			points[i][1] = Math.sin(current) * length + y;
//			System.out.println("Polygon point " + i + ":\t(" + points[i][0] + "," + points[i][1] + ")");
		}
	}
	public void flip(boolean h, boolean v)
	{
		for(int i = 0; i < getNumPoints(); i++)
		{
			if(h)
				points[i][0] *= -1;
			if(v)
				points[i][1] *= -1;
		}
	}
	public void bind(Entity bind)
	{
		bound = bind;
	}
	public Entity getAttached()
	{
		return bound;
	}
	public void name(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
}