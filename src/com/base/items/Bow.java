package com.base.items;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.base.engine.MainComponent;
import com.base.engine.Vector2d;
import com.base.engine.World;
import com.base.engine.dPolygon;
import com.base.entities.Arrow;
import com.base.entities.Hook;

public class Bow extends Weapon 
{
	protected boolean draw, fired;
	protected int drawTime;
	protected int MAX_DRAW_TIME = 120;
	
	public Bow()
	{
		height = 30;
		width = 10;
		offset = 0;
		posX = 18;
		posY = 218;
		posWidth = 32;
		posHeight = 32;
		
		handle[0] = 16;
		handle[1] = 16;
		
		damage = 20;
		specialDamage = 0;
		knockback = new Vector2d(0,-2);
		type = "bow";
	}
	@Override
	public void init()
	{
		try 
		{
			Image src = new Image(file);
			img = src.getSubImage(posX, posY, posWidth, posHeight);
		} 
		catch (SlickException e) 
		{
			e.printStackTrace();
		}
	}
	@Override
	public Holdable copy()
	{
		Bow b = new Bow();
		return b;
	}
	@Override
	public void position(double x, double y, double angle)
	{
		this.x = x;
		this.y = y;
		this.angle = angle - Math.PI / 4;
	}
	@Override
	public void swing(int ID)
	{
		if(ID == Weapon.SPECIAL)
			draw();
	}
	protected void draw()
	{
		draw = true;
		drawTime = 0;
		fired = false;
	}
	public void fire()
	{
		draw = false;
		fired = true;
		if(MainComponent.CURRENT_SCENE instanceof World)
		{
			World w = (World)MainComponent.CURRENT_SCENE;
			Arrow arrow = new Arrow(x,y,Vector2d.cartesianAngle(new Vector2d(MainComponent.input.getMouseX() - 500, MainComponent.input.getMouseY() - 300)));
			arrow.init();
			arrow.setPower((double)drawTime / MAX_DRAW_TIME * 25 + 5);
			arrow.setDamage(getDamage() * 3 * (double)drawTime / MAX_DRAW_TIME + getDamage());
			arrow.setOwner(owner);
			w.addEntity(arrow);
		}
	}
	public void drawHitbox(Graphics b)
	{
		//this weapon does not have a hitbox
	}
	public dPolygon getHitbox()
	{
		return hitbox;
	}
	@Override
	public boolean retract(int time)
	{
		if(fired)
			return true;
		return false;
	}
	@Override
	public void update()
	{
		if(draw)
		{
			if(drawTime < MAX_DRAW_TIME)	
				drawTime++;
			if(!MainComponent.input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON))
				fire();
		}
		else
			drawTime = 0;
	}
	@Override
	public void drawMe(Graphics b, int left)
	{
		Image temp = img.copy();
		int off = 0;
		if(left < 0)
		{
			temp = img.getFlippedCopy(true, false);
			off = -img.getWidth();
		}
		b.drawImage(temp, (float)x - handle[0] * left + off, (float)y - handle[1]);
	}
	@Override
	public double getPower()
	{
		return (double)drawTime / MAX_DRAW_TIME;
	}

}
