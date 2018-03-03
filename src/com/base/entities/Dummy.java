package com.base.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.base.engine.CollisionData;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;

public class Dummy extends Tile 
{
	public Dummy(double x, double y, double width, double height)
	{
		super(x,y,width,height);
	}
	@Override
	public void init()
	{
		double[][] p = {{x-width/2,y-height/2},{x+width/2,y-height/2},{x+width/2,y+height/2},{x-width/2,y+height/2}};
		hitbox = new dPolygon(p);
		hitbox.bind(this);
	}
	@Override
	public void drawMe(Graphics b)
	{
		b.setColor(Color.red);
		b.draw(hitbox.makePolygon());
	}
	@Override
	public void addCollisionData(CollisionData data)
	{
		data.exempt(data.getColliding().getID());
	}
	@Override
	public void translate(Vector2d v) 
	{
		x += v.getX();
		y += v.getY();
		hitbox.translate(v.getX(), v.getY());
	}
	
}
