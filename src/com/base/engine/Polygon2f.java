package com.base.engine;

import com.base.engine.Vector2f;
import com.base.entities.Entity;

public class Polygon2f
{
	private Vector2f[] vertices;
	private Vector2f[] normals;
	private float bounce;
	private float friction;

	
	public Polygon2f()
	{
		vertices = new Vector2f[0];
		normals = new Vector2f[vertices.length];
		bounce = 0;
		friction = 0;
	}
	public Polygon2f(Vector2f[] points)
	{
		this.vertices = points;
		calcNormals();
		bounce = 0;
		friction = 0;
	}
	
	public void calcNormals()
	{
		normals = new Vector2f[vertices.length];
		for(int i = 0; i < vertices.length - 1; i++)
			normals[i] = vertices[i+1].sub(vertices[i]).normL().normalized();
		normals[vertices.length-1] = vertices[0].sub(vertices[vertices.length-1]).normL().normalized();
	}
	public Vector2f getCenter()
	{
		Vector2f total = new Vector2f(0,0);
		for(int i = 0; i < vertices.length; i++)
			total = total.add(vertices[i]);
		return total.div(vertices.length);
	}
	public Polygon2f rotate(float angle)
	{
		Vector2f center = getCenter();
		for(Vector2f point : vertices)
			point = point.sub(center).rotate(angle).add(center);
		for(Vector2f normal : normals)
			normal = normal.rotate(angle);
		return this;
			
	}
	public Polygon2f translate(Vector2f translation)
	{
		for(int i = 0; i < vertices.length; i++)
			vertices[i] = vertices[i].add(translation);
		
		return this;
	}
	public float getMaxPointProjection(Vector2f projection)
	{
		int maxIndex = 0;
		for(int j = 1; j < vertices.length; j++)
		{
			float dot = vertices[j].dot(projection);
			if(dot > vertices[maxIndex].dot(projection))
			{
				maxIndex = j;
			}
		}
		
		return vertices[maxIndex].project(projection).lengthSquared();
	}
	
	public float getMinPointProjection(Vector2f projection)
	{
		int minIndex = 0;
		for(int j = 1; j < vertices.length; j++)
		{
			float dot = vertices[j].dot(projection);
			if(dot < vertices[minIndex].dot(projection))
			{
				minIndex = j;
			}
		}
		
		return vertices[minIndex].project(projection).lengthSquared();
	}
	
	public boolean testPointNormal(Vector2f point)
	{
		for(int i = 0; i < vertices.length; i++)
			if(normals[i].mul(-1).dot(point.sub(vertices[i])) < 0)
				return false;
		return true;
	}
	public boolean testPointQuadrant(Vector2f point)
	{
		float x = point.getX();
		float y = point.getY();
		
		int count = 0;
		
		Vector2f[] points = new Vector2f[this.vertices.length + 1];
		
		for(int i = 0; i < this.vertices.length; i++)
			points[i] = this.vertices[i];
		points[points.length-1] = this.vertices[0];
		
		for(int i = 0; i < points.length-1; i++)
		{
			int q1 = getQuad(points[i], x, y);
			int q2 = getQuad(points[i+1], x, y);
			int dif = q2 - q1;
			
			switch(dif)
			{
				case -1 :
					count--;
					break;
				case 3 :
					count--;
					break;
				case 2 :
					if(xIntercept(points[i],points[i+1],y) > x)
						count -= 2;
					else
						count += 2;
					break;
				case -2 :
					if(xIntercept(points[i],points[i+1],y) > x)
						count -= 2;
					else
						count += 2;					
					break;
				default :
					count++;
					break;
			}

		}
		
	//	System.out.println(count);
		
		if(count == 4 || count == -4)
			return true;
		return false;
	}
	
	private int getQuad(Vector2f point, float x, float y)
	{
		if(point.getY() >= y)
		{
			if(point.getX() >= x)
				return 0;
			else
				return 3;
		}
		else
		{
			if(point.getX() >= x)
				return 1;
			else
				return 2;
		}
	}
	
	private float xIntercept(Vector2f one, Vector2f two, float hitY)
	{
		return ((two.getX() - (two.getY() - hitY)) * (one.getX() - two.getX())) / (one.getY() - two.getY());
	}
	
	public int[] getXi()//use for drawing this polygon with java.awt
	{
		int[] array = new int[vertices.length];
		for(int i = 0; i < array.length; i++)
			array[i] = (int)vertices[i].getX();
		return array;
	}
	
	public int[] getYi()//use for drawing this polygon with java.awt
	{
		int[] array = new int[vertices.length];
		for(int i = 0; i < array.length; i++)
			array[i] = (int)vertices[i].getY();
		return array;
	}
	
	public float[] getXf()//use for drawing this polygon with slick
	{
		float[] array = new float[vertices.length];
		for(int i = 0; i < array.length; i++)
			array[i] = (float)vertices[i].getX();
		return array;
	}
	
	public float[] getYf()//use for drawing this polygon with slick
	{
		float[] array = new float[vertices.length];
		for(int i = 0; i < array.length; i++)
			array[i] = (float)vertices[i].getY();
		return array;
	}
	
	public int getSize()
	{
		return vertices.length;
	}
	
	public Vector2f[] getVertices() 
	{
		return vertices;
	}
	public void setPoints(Vector2f[] points) 
	{
		this.vertices = points;
	}
	public Vector2f[] getNormals() 
	{
		return normals;
	}
	public void setNormals(Vector2f[] normals) 
	{
		this.normals = normals;
	}
	public float getBounce() {
		return bounce;
	}
	public void setBounce(float bounce) {
		this.bounce = bounce;
	}
	public float getFriction() {
		return friction;
	}
	public void setFriction(float friction) {
		this.friction = friction;
	}
	
}
