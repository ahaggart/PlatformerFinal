package com.base.items;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.dPolygon;
import com.base.entities.Entity;
import com.base.entities.Moveable;

public class Potion implements Holdable 
{
	private double x, y;
	private String type;
	private Image img;
	private Entity owner;
	private boolean used;
	
	public Potion(String type)
	{
		this.type = type;
	}
	@Override
	public void init() 
	{
		try {
			img = new Image("img/" + type + ".png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
	}
	public Holdable copy()
	{
		Potion p = new Potion(type);
		return p;
	}
	@Override
	public void update()
	{

	}

	@Override
	public void position(double x, double y, double angle)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public void setOwner(Entity e) 
	{
		this.owner = e;
	}

	@Override
	public String getType() 
	{
		return type + "potion";
	}

	@Override
	public Image getImage() 
	{
		return img;
	}

	@Override
	public dPolygon getHitbox() 
	{
		return null;
	}

	@Override
	public double getPower() 
	{
		return 0;
	}

	@Override
	public void drawMe(Graphics b, int left)
	{
		b.drawImage(img, (float)(x-img.getWidth()/2), (float)(y-img.getHeight()/2));
	}

	@Override
	public void drawHitbox(Graphics b) 
	{

	}

	@Override
	public boolean canHit() 
	{
		return false;
	}

	@Override
	public void swing(int type) 
	{

	}

	@Override
	public void use() 
	{
//		System.out.println("used a potion!");
		if(owner instanceof Moveable)
		{
			Moveable m = (Moveable)owner;
			if(m.getHealth() < m.getMaxHealth())
			{
				m.damage(-75.0);
				used = true;
			}
		}
	}
	public boolean used()
	{
		return used;
	}
	public void resetUse()
	{
		used = false;
	}
	public boolean consumable()
	{
		return true;
	}
	public boolean stackable()
	{
		return true;
	}
	@Override
	public void end() 
	{

	}

	@Override
	public boolean retract(int time)
	{
		return (time > 80);
	}

}
