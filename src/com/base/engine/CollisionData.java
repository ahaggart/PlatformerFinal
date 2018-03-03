package com.base.engine;
import java.util.ArrayList;

import com.base.entities.Entity;

public class CollisionData
{
	
	private Vector2d projection, surfaceNormal, knockback = new Vector2d(0,0);
	private dPolygon hitbox;
	private ArrayList<Integer> exemptions = new ArrayList<Integer>();
	private int ID = -1;
	private Entity colliding = null;
	private double damage;
	
	public CollisionData(Vector2d projection, Vector2d surfaceNormal, dPolygon hitbox, Entity colliding)//surface data and hitbox of overlap
	{
		this.projection = projection;
		this.surfaceNormal = surfaceNormal;
		this.hitbox = hitbox;
		this.colliding = colliding;
		damage = 0;
	}

	public dPolygon getHitbox()
	{
		return hitbox;
	}

	public Vector2d getProjection() 
	{
		return projection;
	}
	public void setProjection(Vector2d v) 
	{
		projection = v;
	}
	public Vector2d getSurfaceNormal() 
	{
		return surfaceNormal;
	}
	public void setSurfaceNormal(Vector2d v) 
	{
		surfaceNormal = v;
	}
	public Vector2d getKnockback()
	{
		return knockback;
	}
	public void setKnockback(Vector2d v) 
	{
		knockback = v;
	}
	public void exempt(Integer ID)
	{
		exemptions.add(ID);
	}
	public boolean isExempt(Entity check)
	{
		for(int i = 0; i < exemptions.size(); i++)
		{
			if(check.getID() == exemptions.get(i) )
				return true;
		}
		return false;
	}
	public void setDamage(double damage)
	{
		this.damage = damage;
	}
	public double getDamage()
	{
		return damage;
	}
	public void addID(int ID)
	{
		this.ID = ID;
	}
	public int getID()
	{
		return ID;
	}

	public Entity getColliding() 
	{
		return colliding;
	}

}
