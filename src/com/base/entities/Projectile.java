package com.base.entities;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.CollisionData;
import com.base.engine.Vector2d;

public class Projectile extends Moveable 
{	
	protected Image sprite;
	protected String file;
	protected int posX, posY, posWidth, posHeight, lifetime;
	protected boolean flip = false;
	protected double angle, damage;
	protected ArrayList<Entity> exempt;
	protected Entity owner;
	
	public Projectile(double x, double y, double width, double height, Vector2d v, int lifetime)
	{
		super(x,y);
		setVelocity(v);
		this.width = width;
		this.height = height;
		this.lifetime = lifetime;
		exempt = new ArrayList<Entity>();
	}
	public void setImage(String file, int x, int y, int width, int height, double angle,boolean flip)
	{
		this.file = file;
		posX = x;
		posY = y;
		posWidth = width;
		posHeight = height;
		this.angle = angle;
		this.flip = flip;
	}
	@Override
	public void init()
	{
		super.init();
		try {
			Image src = new Image(file);
			sprite = src.getSubImage(posX, posY, posWidth, posHeight);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	public void setOwner(Entity owner)
	{
		this.owner = owner;
	}
	public void setDamage(double damage)
	{
		this.damage = damage;
	}
	public void exempt(Entity e)
	{
		exempt.add(e);
	}
	@Override
	public void update()
	{
		translate(v);
		if(lifetime > 0)
			lifetime--;
		else
			mark = true;
	}
	@Override
	public void drawMe(Graphics b)
	{
		sprite.rotate((float)Math.toDegrees(angle));
		b.drawImage(sprite.getFlippedCopy(flip, false), (float)(x - posWidth / 2), (float)(y - posHeight/2));
	}
	@Override
	public void addCollisionData(CollisionData data) 
	{
		if(owner != null)	
			data.exempt(owner.getID());
		if(!exempt.contains(data.getColliding()))
			data.setDamage(damage);
	}
	@Override
	public void processCollisionData(CollisionData data)
	{
		if(!data.isExempt(this))
		{	
			exempt(data.getHitbox().getAttached());
		}		
	}
}
