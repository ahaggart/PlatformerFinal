package com.base.engine;

import com.base.engine.Vector2d;

import com.base.engine.Vector2f;

public class VectorCollisionEngine 
{
	public Vector2f detectCollision(Polygon2f poly1, Polygon2f poly2)
	{
		//return a vector2f that will solve the collision by translating poly1 by the vector
		//shortest possible solution to collision will be returned
		//translate both polygons by half of returned to solve collision
		
		//create arrays of polygon normals
		Vector2f[] norm1 = poly1.getNormals();
		Vector2f[] norm2 = poly2.getNormals();
		//create arrays of polygon vertices
		Vector2f[] vert1 = poly1.getVertices();
		Vector2f[] vert2 = poly2.getVertices();
		//run through normal arrays testing possible separating axis		
		Vector2f[] proj = new Vector2f[norm1.length + norm2.length];
		for(int j = 0; j < norm1.length; j++)
		{
			int min1 = 0;
			int max1 = 0;
			for(int i = 1; i < norm1.length; i++)//get index of corner with smallest projection vector
			{
				if( vert1[i].dot(norm1[j]) < vert1[min1].dot(norm1[j]) )
					min1 = i;
				if( vert1[i].dot(norm1[j]) > vert1[max1].dot(norm1[j]) )
					max1 = i;
			}
			//repeat for hitbox2
			int min2 = 0;
			int max2 = 0;
			for(int i = 1; i < norm2.length; i++)//get index of corner with largest projection vector (using dot product)
			{
				if( vert2[i].dot(norm1[j]) < vert2[min2].dot(norm1[j]) )
					min2 = i;
				if( vert2[i].dot(norm1[j]) > vert2[max2].dot(norm1[j]) )
					max2 = i;
			}
			float dotMax1 = vert1[max1].dot(norm1[j]);
			float dotMin1 = vert1[min1].dot(norm1[j]);
			float dotMax2 = vert2[max2].dot(norm1[j]);
			float dotMin2 = vert2[min2].dot(norm1[j]);
			if(dotMax1 < dotMin2 || dotMax2 < dotMin1)//no overlap
			{
				return null;
			}
			else
			{
//					System.out.println("overlap detected on side: " + j);
				if(dotMax1 - dotMin2 < dotMax2 - dotMin1)
				{
					proj[j] = vert1[max1].project(norm1[j]).sub( vert2[min2].project(norm1[j]) ).mul(-1);
				}
				else
				{
					proj[j] = vert2[max2].project(norm1[j]).sub( vert1[min1].project(norm1[j]) );
				}
			}
		}
		for(int j = 0; j < norm2.length; j++)
		{
			int min1 = 0;
			int max1 = 0;
			for(int i = 1; i < norm1.length; i++)//get index of corner with smallest projection vector
			{
				if( vert1[i].dot(norm2[j]) < vert1[min1].dot(norm2[j]) )
					min1 = i;
				if( vert1[i].dot(norm2[j]) > vert1[max1].dot(norm2[j]) )
					max1 = i;
			}
			//repeat for hitbox2
			int min2 = 0;
			int max2 = 0;
			for(int i = 1; i < norm2.length; i++)//get index of corner with largest projection vector (using dot product)
			{
				if( vert2[i].dot(norm2[j]) < vert2[min2].dot(norm2[j]) )
					min2 = i;
				if( vert2[i].dot(norm2[j]) > vert2[max2].dot(norm2[j]) )
					max2 = i;
			}
			float dotMax1 = vert1[max1].dot(norm2[j]);
			float dotMin1 = vert1[min1].dot(norm2[j]);
			float dotMax2 = vert2[max2].dot(norm2[j]);
			float dotMin2 = vert2[min2].dot(norm2[j]);
			if(dotMax1 < dotMin2 || dotMax2 < dotMin1)//no overlap
			{
				return null;
			}
			else
			{
//					System.out.println("overlap detected on side: " + j);
				if(dotMax1 - dotMin2 < dotMax2 - dotMin1)
				{
					proj[j + norm1.length] = vert1[max1].project(norm2[j]).sub( vert2[min2].project(norm2[j]) ).mul(-1);
				}
				else
				{
					proj[j + norm1.length] = vert2[max2].project(norm2[j]).sub( vert1[min1].project(norm2[j]) );
				}
			}
		}
		Vector2f lowest = proj[0];
		for(Vector2f vector : proj)
			if(vector.lengthSquared() < lowest.lengthSquared())
				lowest = vector;
	//	System.out.println("Collision solved with vector: " + lowest);
		return lowest;
	}
	
	public Vector2d detectCollisionD(dPolygon poly1, dPolygon poly2)
	{
		//convert dPolygons to Polygon2f and use detectCollision() above
		Vector2f[] vertices1 = new Vector2f[poly1.getNumPoints()];
		double[][] orig1 = poly1.get();
		for(int i = 0; i < poly1.getNumPoints(); i++)
			vertices1[i] = new Vector2f((float)orig1[i][0], (float)orig1[i][1]);
		Polygon2f polyF1 = new Polygon2f(vertices1);
		//repeat for second polygon
		Vector2f[] vertices2 = new Vector2f[poly2.getNumPoints()];
		double[][] orig2 = poly2.get();
		for(int i = 0; i < poly2.getNumPoints(); i++)
			vertices2[i] = new Vector2f((float)orig2[i][0], (float)orig2[i][1]);
		Polygon2f polyF2 = new Polygon2f(vertices2);
		
		Vector2f solved = detectCollision(polyF1, polyF2);
		if(solved == null)
			return null;
		return new Vector2d(solved.getX(), solved.getY());
	}
}
