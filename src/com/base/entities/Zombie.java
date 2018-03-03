package com.base.entities;
import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.CollisionData;
import com.base.engine.CollisionDetection;
import com.base.engine.Controllable;
import com.base.engine.MainComponent;
import com.base.engine.PlaySound;
import com.base.engine.Vector2d;
import com.base.engine.World;
import com.base.items.Item;
import com.base.items.Potion;
import com.base.items.Weapon;

public class Zombie extends Moveable implements Controllable
{
	private Animation animation, animation2;
	private Item held;
	private int hit, wallcount;
	private boolean ground, wall, left;
	//AI classes
	private Tile standing;
	private Player target;
	//info string display
	private String info = "";
	
	protected static int hurtID = -1;
	
	public Zombie(double x, double y)
	{
		super(x,y);
		width = 34;
		height = 46;
		oldHealth = health;
		animation = new Animation();
		animation2 = new Animation();
		held = new Item(x,y);
		wallcount = 0;
	}
	public void init()
	{
		super.init();
		try {
			Image base = new Image("img/zombie.png");
			SpriteSheet sheet = new SpriteSheet(base, 34, 46, 2);
			animation = new Animation(sheet, 100);
			SpriteSheet sheet2 = new SpriteSheet(base.getFlippedCopy(true,false), 34, 46, 2);
			animation2 = new Animation(sheet2, 100);
		
		} catch (SlickException e) {
			e.printStackTrace();
		}
		if(hurtID == -1)
			hurtID = MainComponent.sound.loadSound("sound/monster_grunt.wav");
		held.init();
		held.insert(new Potion("health"), 1);
	}
	@Override
	public void drawMe(Graphics b)
	{	
		b.setColor(Color.black);
		if(target != null)
			b.setColor(Color.red);
		b.drawString("Zombie " + wall, (float)(x-width/2), (float)(y-height/2 - 20));
		Color color = Color.white;
		if(hit > 0)
		{
			color = Color.red;
			hit--;
		}
		else if(hit < 0)
		{
			color = Color.green;
			hit++;
		}
		else
			color = Color.white;
		if(left)
			b.drawAnimation(animation, (float)(x - width/2), (float)(y - height/2),color);
		else
			b.drawAnimation(animation2, (float)(x - width/2), (float)(y - height/2),color);


	}
	@Override
	public void update()
	{
		act();
		ground = false;
		wall = false;

		if(oldHealth > health)//has been damaged
		{
			hit = 20;
			if(health > 0)
				MainComponent.sound.playSound(hurtID);
	//		else
	//			PlaySound.play("sound/monster_scream.wav");
		}
		else if(oldHealth < health)//has been healed
		{
			hit = -20;
		}
		super.update();
		if(mark)
		{
			if(MainComponent.CURRENT_SCENE instanceof World)
			{
				World w = (World)MainComponent.CURRENT_SCENE;
				w.addEntity(held);
			}
		}
	}
	@Override
	public void translate(Vector2d v)
	{
		super.translate(v);
		held.translate(v);
	}
	@Override
	public void addCollisionData(CollisionData data) 
	{
		data.setDamage(20);
		data.setKnockback(new Vector2d(v.getX() * 3, -4));
		if(data.getColliding() instanceof Zombie )
		{
	//		System.out.println("got here");
			data.exempt(data.getColliding().getID());
		}
	}
	public void processCollisionData(CollisionData data)
	{
		if(!data.isExempt(this))
		{	
			if(data.getHitbox().getAttached() instanceof Tile)
				standing = (Tile)data.getHitbox().getAttached();
			if(data.getSurfaceNormal().getY() < 0 && getVelocity().getY() > 0)
			{
				ground();
			}
			else
				standing = null;
			if( data.getSurfaceNormal().getY() == 0 && data.getHitbox().getAttached() instanceof Tile)
			{
				if(wallcount >= 0)
				{
					wallcount = 20;
					if(data.getSurfaceNormal().getX() > 0)
						left = true;
					else
						left = false;
				}
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
	@Override
	public void moveLeft()
	{
		left = true;
		if(ground)
		{
			if(wallcount > 0)
				wallcount--;
			if(wall && wallcount >= 0)
			{
				wall = false;
				wallcount = 20;
				moveRight();
			}
			else
			{
				if(v.getX() > -2.0 && target != null)
				{
					addVelocity(new Vector2d(-1.0,0.0) );
					animation.start();
					animation2.stop();
					info += "sprinting left ";
				}
				else if(v.getX() > -1.0)
				{
					addVelocity(new Vector2d(-0.5,0.0) );
					animation.start();
					animation2.stop();
					info += "moving left ";
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
			if(wallcount > 0)
				wallcount--;
			if(wall && wallcount >= 0)
			{
				wall = false;
				wallcount = 20;
				moveLeft();
			}
			else
			{
				if(v.getX() < 2.0 && target != null)
				{
					addVelocity(new Vector2d(1.0,0.0) );
					animation2.start();
					animation.stop();
					info += "sprinting right ";
				}
				else if(v.getX() < 1.0)
				{
					addVelocity(new Vector2d(0.5,0.0) );
					animation2.start();
					animation.stop();
					info += "moving right ";
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
//		left = !left;
	}
	@Override
	public void jump() 
	{
		
	}
	@Override
	public void act() //basic AI kit
	{
		info = ": ";
		if(ground)
		{
			target = null;
			if(standing != null)//check if zombie is standing on a surface
			{
				ArrayList<Entity> search = standing.getStandingEntities();
				for(int i = 0; i < search.size(); i++)
				{
					if(search.get(i) instanceof Player)
					{
						target = (Player)search.get(i);
					}
				}
			}
			
			if(target != null)//check if zombie has found a player object to track
			{
				info += "tracking player ";
				if(target.getX() > getX())
					moveRight();
				else
					moveLeft();
			}
			else if(standing != null)//check if zombie is near edge of tile
			{
				if(getX() < standing.getX() + 10)//near left edge
					moveRight();
				else if(getX() > standing.getX() + standing.getWidth() - 10)//near right edge
					moveLeft();
				else if(left)
					moveLeft();
				else
					moveRight();
			}
			else
				animation.stop();

		}
		else
			animation.stop();
	}
}
