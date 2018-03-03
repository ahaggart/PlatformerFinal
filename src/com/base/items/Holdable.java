package com.base.items;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.base.engine.dPolygon;
import com.base.entities.Entity;

public interface Holdable 
{
	public void init();
	public Holdable copy();
	public void update();
	public void position(double x, double y, double angle);
	public void setOwner(Entity e);
	public String getType();
	public Image getImage();
	public dPolygon getHitbox();
	public double getPower();
	public void drawMe(Graphics b, int left);
	public void drawHitbox(Graphics b);
	public boolean canHit();
	public void swing(int type);
	public void use();
	public void end();
	public boolean retract(int time);
	public boolean used();
	public void resetUse();
	public boolean consumable();
	public boolean stackable();
}
