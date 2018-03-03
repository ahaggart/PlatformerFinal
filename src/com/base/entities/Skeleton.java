package com.base.entities;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import com.base.engine.CollisionData;
import com.base.engine.CollisionDetection;
import com.base.engine.Controllable;
import com.base.engine.MainComponent;
import com.base.engine.Vector2d;
import com.base.engine.World;
import com.base.engine.dPolygon;

public class Skeleton extends Moveable implements Controllable 
{
	private Dummy sight;
	private Tile standing;
	private Animation left;
	private boolean right, ground, wall;
	private Entity target;
	private int RANGE = 400, SIGHT = 300, POWER = 20, SKILL = 30, FRAME;
	private int tick, count;
	
	public Skeleton(double x, double y)
	{
		super(x,y);
		sight = new Dummy(x,y,SIGHT*2,SIGHT*2);
		left = new Animation();
		width = 22;
		height = 52;
		tick = 0;
		count = 0;
	}
	public void init()//#inittowinit
	{
		super.init();
		sight.init();
		try {
			Image base = new Image("img/skeleton.png");
			SpriteSheet sheet = new SpriteSheet(base, 60, 60, 0);
			left = new Animation(sheet, 100);
		
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void update()
	{
		act();
		super.update();
		sight.update();
		if(target != null)
			if(target.isMarked())
				target = null;
		standing = null;
	}
	@Override
	public void translate(Vector2d v)
	{
		super.translate(v);
		sight.translate(v);
	}
	@Override
	public void drawMe(Graphics b)
	{
		Image img = left.getImage(FRAME).getFlippedCopy(right, false);
		Color color = Color.white;
		if(protect > 0)
			color = Color.red;
		b.drawImage(img, (float)(x-30), (float)(y-30), color);
		b.setColor(Color.black);
		if(target != null)
		{
	//		b.drawLine((float)x, (float)y, (float)target.getX(), (float)target.getY());
			b.setColor(Color.red);
		}
		b.drawString("Skeleton", (float)x - 40, (float)y - 35);
	//	sight.drawMe(b);
		b.setColor(Color.blue);
		//b.drawOval((float)x - RANGE, (float)y - RANGE, 2*RANGE, 2*RANGE );
	}
	public void moveLeft()
	{
		right = false;
		if(ground)
		{
			walk();
			if(v.getX() > -1.0)
			{
				addVelocity(new Vector2d(-0.5,0.0) );
			}

		}

	}
	public void moveRight()
	{
		right = true;
		if(ground)
		{
			walk();
			if(v.getX() < 1.0)
			{
				addVelocity(new Vector2d(0.5,0.0) );
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
	public void face(boolean right)
	{
		this.right = right;
	}
	@Override
	public void act() 
	{
		ArrayList<Entity> nearby = sight.getStandingEntities();
		for(Entity e : nearby)
			if(e instanceof Player)
				target = e;
		if(target != null)//target acquired
		{
			Vector2d t = new Vector2d(target.getX() - x, target.getY() - y);
			if(target.getX() > x)
			{
				right = true;
				if(t.dot(t) > RANGE*RANGE)//out of range
				{
					count = 0;
					if(standing != null)
					{
						if(x < standing.getX() + standing.getWidth() - 5)//near right edge
							moveRight();
					}
					else
						moveRight();
				}
				else
					aim(t);
			}
			else
			{
				right = false;
				if(t.dot(t) > RANGE*RANGE)//out of range
				{
					count = 0;
					if(standing != null)
					{
						if(x > standing.getX() + 5)//near left edge
							moveLeft();
					}
					else
						moveLeft();				
				}
				else
					aim(t);
			}
		}
		else
		{
			FRAME = 6;
			tick = 0;
		}
	}
	@Override
	public ArrayList<dPolygon> getObjects()
	{
		ArrayList<dPolygon> p = new ArrayList<dPolygon>();
		p.add(sight.getHitbox());
		return p;
	}
	@Override
	public void processCollisionData(CollisionData data)
	{
		if(!data.isExempt(this))
		{	
			if(data.getHitbox().getAttached() instanceof Tile)
				standing = (Tile)data.getHitbox().getAttached();
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
			addVelocity(data.getKnockback());
		}
	}
	public void walk()
	{
		tick = (tick) % 130;
		FRAME = tick/10 + 7;
		tick++;
	}
	public void aim(Vector2d t)
	{
		count++;
		double angle = Vector2d.cartesianAngle(new Vector2d(Math.abs(t.getX()), t.getY()));
		if(-Math.PI / 2 < angle && angle <= -3 * Math.PI / 8)
			FRAME = 5;
		else if(-3 * Math.PI / 8 < angle && angle <= -Math.PI / 8)
			FRAME = 4;
		else if(-Math.PI / 8 < angle && angle <= Math.PI / 8)
			FRAME = 3;
		else if(Math.PI / 8 < angle && angle <= Math.PI / 4)
			FRAME = 2;
		else if(Math.PI / 4 < angle && angle <= 3 * Math.PI / 8)
			FRAME = 1;
		if(count > 180)
		{
			shoot(t);
			count = 0;
		}
	}
	public void shoot(Vector2d t)
	{
		if(MainComponent.CURRENT_SCENE instanceof World)
		{
			World w = (World)MainComponent.CURRENT_SCENE;
			double angle = correctAim(t,0);
			if(!right)
				angle = Math.PI + angle;
			Arrow arrow = new Arrow(x,y,angle + (Math.random() - 0.5) * 2 * Math.PI/SKILL);
			arrow.init();
			arrow.setPower(POWER);
			arrow.setDamage(25);
			arrow.setOwner(this);
			w.addEntity(arrow);
		}
	}
	public double correctAim(Vector2d t, int aim)
	{
		//pass in aim = 0
		if(aim == 1)
			return Math.atan( (POWER * POWER + Math.sqrt(POWER * POWER * POWER * POWER + GRAVITY.getY() * (-GRAVITY.getY() * t.getX() * t.getX() + 2 * t.getY() * POWER * POWER))) / (-GRAVITY.getY() * t.getX()) );
		else
			return Math.atan( (POWER * POWER - Math.sqrt(POWER * POWER * POWER * POWER + GRAVITY.getY() * (-GRAVITY.getY() * t.getX() * t.getX() + 2 * t.getY() * POWER * POWER))) / (-GRAVITY.getY() * t.getX()) );
	}
}
