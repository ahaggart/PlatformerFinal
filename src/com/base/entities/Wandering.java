package com.base.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;

import com.base.engine.CollisionData;
import com.base.engine.CollisionDetection;
import com.base.engine.Controllable;
import com.base.engine.MainComponent;
import com.base.engine.Vector2d;

public class Wandering extends Moveable implements Controllable 
{

	private Dummy sight;
	private Animation animation;
	private Player target;
	private GradientFill healthBar;
	private boolean wall, ground, left;
	private int FRAME, tick;
	
	public Wandering(double x, double y, Player player)
	{
		super(x,y);
		width = 28;
		height = 68;
		target = player;
		MAX_HEALTH = 1000;
		FRAME = 0;
		tick = 0;
	}
	@Override
	public void init()
	{
		super.init();
		try {
			Image base = new Image("img/paladin.png");
			SpriteSheet sheet = new SpriteSheet(base, 66, 68, 0);
			animation = new Animation(sheet, 100);
		
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void drawMe(Graphics b)
	{
		b.drawImage(animation.getImage(FRAME).getFlippedCopy(!left, false), (float)(x - 40), (float)(y - height/2));
		b.setColor(Color.black);
		b.fillRect((float)(x - 52), (float)(y - height/2 - 20), 104, 12);
		healthBar = new GradientFill((float)(x - 50), (float)(y - height/2 - 19), Color.transparent, (float)(x - 50), (float)(y - height/2 + 1), Color.black);
		b.setColor(Color.red);
		b.fillRect((float)(x - 50), (float)(y - height/2 - 19), (float)(100 * health / MAX_HEALTH), 10);
		b.fill(new Rectangle((float)(x - 50), (float)(y - height/2 - 19), (float)(100 * health / MAX_HEALTH), 10), healthBar);
		b.setColor(Color.black);
		for(int i = 0; i < 5; i++)
			b.drawRoundRect((float)(x - 50) + i * 20, (float)(y - height/2 - 19), 20, 10, 2);
		drawHitbox(b);
	}
	public void drawHitbox(Graphics b)
	{
		b.setColor(Color.red);
		b.draw(hitbox.makePolygon());
	}
	@Override
	public void moveLeft()
	{
		left = true;
		if(ground)
		{
			if(wall)
			{
				wall = false;
				moveRight();
			}
			else
			{
				walk();
				if(v.getX() > -1.0)
				{
					addVelocity(new Vector2d(-0.5,0.0) );
				}
			}

		}

	}
	@Override
	public void moveRight()
	{
		left = false;
		if(ground)
		{
			if(wall)
			{
				wall = false;
				moveLeft();
			}
			else
			{
				walk();
				if(v.getX() < 1.0)
				{
					addVelocity(new Vector2d(0.5,0.0) );
				}
			}
		}
	}

	@Override
	public void ground() 
	{
		ground = true;
	}

	@Override
	public void wall(double dir) 
	{
		wall = true;
	}

	@Override
	public void jump() 
	{


	}
	@Override
	public void update()
	{
		act();
		super.update();
		ground = false;
		wall = false;
	}
	@Override
	public void act() 
	{
		if(left)
			moveLeft();
		else
			moveRight();
	}
	@Override
	public void addCollisionData(CollisionData data) 
	{
		data.setKnockback(new Vector2d(data.getColliding().getX() - getX(), data.getColliding().getY() - getY()).mul(0.3));
	}
	@Override
	public void processCollisionData(CollisionData data)
	{
		if(!data.isExempt(this))
		{	
			if(data.getSurfaceNormal().getY() == 1 && getVelocity().getY() > 0)
			{
				ground();
			}
			if(Math.abs( data.getSurfaceNormal().getY() ) == 0 && data.getHitbox().getAttached() instanceof Tile)
			{
				wall(data.getSurfaceNormal().getX() );
			}
			CollisionDetection.collisionPhysics(getVelocity(), data.getProjection(), data.getSurfaceNormal(), this);
			if(alive)
				health -= data.getDamage();
			if(data.getDamage() < 0)
				MainComponent.sound.playSound(Moveable.healID);
			translate(data.getKnockback().div(10));
			addVelocity(data.getKnockback().div(100));
		}
	}
	private void walk()
	{
		tick = (tick) % 60;
		FRAME = tick/10 + 3;
		tick++;
	}
}
