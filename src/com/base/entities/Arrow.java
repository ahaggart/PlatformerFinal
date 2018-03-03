package com.base.entities;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.CollisionData;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;

public class Arrow extends Moveable 
{
	protected double angle, damage, arrowAngle, pen;
	protected Entity owner;
	protected int count;
	protected static int impactID = -1;
	protected Image img;
	protected ArrayList<Entity> exempt;
	
	public Arrow(double x, double y, double angle)
	{
		super(x,y);
		this.angle = angle;
		width = 4;
		height = 4;
		count = 0;
		exempt = new ArrayList<Entity>();
		MAX_HEALTH = 1;
	}
	@Override
	public void init() 
	{
		super.init();
		try {
			img = new Image("img/arrow.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	public void setPower(double power)
	{
		setVelocity(new Vector2d(Math.cos(angle) * power, Math.sin(angle) * power));
		pen = power / 10;
	}
	public void setDamage(double damage)
	{
		this.damage = damage;
	}
	public void setOwner(Entity owner)
	{
		this.owner = owner;
	}
	@Override
	public void drawMe(Graphics b)
	{
		Image temp = img.copy();
		temp.rotate((float)Math.toDegrees(arrowAngle));
		b.drawImage(temp, (float)x - 7, (float)y - 16);
	}
	@Override
	public void update()
	{
		super.update();
		arrowAngle = Vector2d.cartesianAngle(getVelocity()) + Math.PI / 2;	
	}

	@Override
	public void addCollisionData(CollisionData data) 
	{
		if(owner != null)	
			data.exempt(owner.getID());
		for(int i = 0; i < exempt.size(); i++)
			data.exempt(exempt.get(i).getID());
		data.setDamage(damage);
		data.setKnockback(v.div(3));
	}

	@Override
	public void processCollisionData(CollisionData data)
	{
		Entity e = data.getHitbox().getAttached();
		if(!data.isExempt(this) && e.getID() != owner.getID() && !exempt.contains(e))
		{
			if(e instanceof Tile)
				stick(e);
			else
			{
				exempt.add(e);
				count++;
				if(count > pen - 1)
					stick(data.getHitbox().getAttached());
			}
		}
	}
	public void stick(Entity e)
	{
		mark = true;
	}

}
