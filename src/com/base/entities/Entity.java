package com.base.entities;
import org.newdawn.slick.Graphics;

import com.base.engine.CollisionData;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;

import java.util.ArrayList;

public abstract class Entity
{
	protected double x, y;
	protected Vector2d v = new Vector2d(0,0);
	protected Vector2d GRAVITY = new Vector2d(0.0,0.2);
	protected double TERMINAL_VELOCITY = 8;
	protected int ID = -1;
	protected boolean mark = false;
	
	public abstract void init();
	public abstract void drawMe(Graphics b);
	public abstract void translate(Vector2d v);
	public abstract void addVelocity(Vector2d v);
	public abstract void update();
	public abstract ArrayList<dPolygon> getObjects();
	public abstract void addCollisionData(CollisionData data);
	public abstract void processCollisionData(CollisionData data);
	
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
	public Vector2d getVelocity()
	{
		return v;
	}
	public void setVelocity(Vector2d v)
	{
		this.v = v;
	}
	public dPolygon getHitbox()
	{
		double[][] b = {{x,y}};
		dPolygon p = new dPolygon(b);
		p.bind(this);
		return p;
	}
	public void setID(int ID)
	{
		this.ID = ID;
	}
	public int getID()
	{
		return ID;
	}
	public void mark()
	{
		mark = true;
	}
	public boolean isMarked()
	{
		return mark;
	}
}

