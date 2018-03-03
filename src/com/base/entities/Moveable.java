package com.base.entities;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.base.engine.CollisionData;
import com.base.engine.CollisionDetection;
import com.base.engine.MainComponent;
import com.base.engine.PlaySound;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;


public class Moveable extends Entity
{
	protected dPolygon hitbox;
	protected double health, oldHealth, MAX_HEALTH;
	protected int protect;
	protected double width, height;
	protected boolean alive = true;
	protected static int healID = -1;
	
	public Moveable(double x, double y)
	{
		this.x = x;
		this.y = y;
		width = 30;
		height = 50;
		MAX_HEALTH = 100;

	}
	public void init()
	{
		if(healID == -1)	
			healID = MainComponent.sound.loadSound("sound/heal.wav");
		double[][] hitbox = {{x-width/2,y-height/2},{x+width/2,y-height/2},{x+width/2,y+height/2},{x-width/2,y+height/2},};
		this.hitbox = new dPolygon(hitbox);
		this.hitbox.bind(this);
		health = MAX_HEALTH;
		oldHealth = health;
		alive = true;
		mark = false;
	}
	@Override
	public void drawMe(Graphics b)
	{
		b.setColor(Color.green);
		if(!alive)
			b.setColor(Color.red);
		b.drawString("Health: " + health, (float)(x-width/2), (float)(y-height/2) - 20);
		b.setColor(Color.red);
		b.fill(hitbox.makePolygon());
	}

	@Override
	public void translate(Vector2d v) 
	{
		x += v.getX();
		y += v.getY();
		hitbox.translate(v.getX(),v.getY());	
	}
	public void damage(double damage)
	{
		health -= damage;
	}
	@Override
	public void addVelocity(Vector2d v) 
	{
		setVelocity(this.v.add(v));		
	}

	@Override
	public void update() 
	{
		if(health <= 0)
		{
			alive = false;
			health = 0;
		}
		if(oldHealth < health && alive)
		{
//			System.out.println("Health: " + health + " old health: " + oldHealth);
			MainComponent.sound.playSound(healID);
		}
		if(health > MAX_HEALTH)
			health = MAX_HEALTH;
		if(protect > 0)
			protect--;
		else if(health < oldHealth)
		{
			protect = 20;
		}
		if(!alive)
		{
			mark = true;
	//		System.out.println("marked entity as dead");
		}
		if(v.getY() < TERMINAL_VELOCITY)
		{
			addVelocity(GRAVITY);
		}
		translate(v);
		oldHealth = health;
	}

	@Override
	public ArrayList<dPolygon> getObjects() 
	{
		return new ArrayList<dPolygon>();
	}
	@Override
	public dPolygon getHitbox()
	{
		return hitbox;
	}
	@Override
	public void addCollisionData(CollisionData data) 
	{
		//use default collision physics
	}
	public double getHealth()
	{
		return health;
	}
	public double getMaxHealth()
	{
		return MAX_HEALTH;
	}
	@Override
	public void processCollisionData(CollisionData data)
	{
		if(!data.isExempt(this))
		{	
			CollisionDetection.collisionPhysics(getVelocity(), data.getProjection(), data.getSurfaceNormal(), this);
			if(protect == 0)
			{
				health -= data.getDamage();
				if(data.getDamage() > 0)
					protect = 20;
			}
			translate(data.getKnockback().div(10));
			addVelocity(data.getKnockback());
		}		
	}
}
