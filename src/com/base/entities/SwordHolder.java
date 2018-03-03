package com.base.entities;
import java.util.ArrayList;

import org.newdawn.slick.Graphics;

import com.base.engine.CollisionData;
import com.base.engine.MainComponent;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;
import com.base.items.Holdable;
import com.base.items.Weapon;


public class SwordHolder extends Entity
{
	private int ownerID = -1;
	private Holdable held;
	
	public SwordHolder(Holdable hold)
	{
		held = hold;
	}
	public void init()
	{
		held.init();
	}
	public void setItem(Holdable hold)
	{
		this.held = hold;
	}
	public Holdable getItem()
	{
		return held;
	}
	@Override
	public void drawMe(Graphics b) 
	{
		
	}
	public void passID(int ID)
	{
		ownerID = ID;
	}
	@Override
	public void translate(Vector2d v)
	{
		
	}

	@Override
	public void addVelocity(Vector2d v) 
	{
		
	}

	@Override
	public void update()
	{
	}

	@Override
	public ArrayList<dPolygon> getObjects()
	{
		return null;
	}

	@Override
	public void addCollisionData(CollisionData data)
	{
		data.exempt(ownerID);
		if(held instanceof Weapon)
		{
			Weapon w = (Weapon)held;
			w.addCollisionData(data);
		}
	}

	@Override
	public void processCollisionData(CollisionData data)
	{
		if(!data.isExempt(this) && held instanceof Weapon)
		{
			Weapon w = (Weapon)held;
			w.hit(data.getHitbox().getAttached());
		}
	}

}
