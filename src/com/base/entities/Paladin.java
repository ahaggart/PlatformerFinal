package com.base.entities;

import java.util.ArrayList;

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
import com.base.engine.World;
import com.base.engine.dPolygon;
import com.base.items.HarpoonGun;
import com.base.items.Holdable;
import com.base.items.Item;
import com.base.items.MegaAxe;
import com.base.items.Weapon;

public class Paladin extends Moveable implements Controllable 
{

	private Animation animation;
	private Player target;
	private Weapon hammer, sword, axe;
	private SwordHolder holder;
	private GradientFill healthBar;
	private boolean wall, ground, left, equip, smash;
	private int FRAME, tick, count, rage, CURRENT_ACTION = 0;
	private double REACH = 18;
	
	private Hook hook;
	private Objective objective;
	
	public static final int WALK = 1, STRIKE = 2;
	
	
	public Paladin(double x, double y, Player player)
	{
		super(x,y);
		width = 28;
		height = 68;
		target = player;
		MAX_HEALTH = 1000;
		FRAME = 0;
		tick = 0;
		count = 0;
		rage = 0;
		//set up sword holder dummy
		holder = new SwordHolder(new MegaAxe());
		//build hammer weapon
		hammer = new Weapon();
		hammer.setImage("img/hammer.png", 0, 0, 38, 38);
		int[] handle1 = {8,10};
		hammer.setHandle(handle1);
		hammer.setHeight(23);
		hammer.setWidth(23);
		hammer.setOffset(16);
		hammer.setDamage(10);
		hammer.setKnockback(new Vector2d(10,-5.0));
		//build axe weapon
		axe = new MegaAxe();
		axe.setOwner(this);
		axe.setDamage(25);
		//build sword weapon
		sword = new Weapon();
		sword.setImage("img/excalibur.png", 0, 0, 74, 74);
		int[] handle2 = {6,6};
		sword.setHandle(handle2);
		sword.setHeight(79);
		sword.setWidth(12);
		sword.setOffset(9);
		sword.setDamage(55);
	}
	@Override
	public void init()
	{
		super.init();
		hammer.init();
		axe.init();
		sword.init();
		holder.setItem(hammer);
		try {
			Image base = new Image("img/paladin.png");
			SpriteSheet sheet = new SpriteSheet(base, 66, 68, 0);
			animation = new Animation(sheet, 100);
		
		} catch (SlickException e) {
			e.printStackTrace();
		}
		if(objective != null)
			objective.deactivate();
	}
	public void bind(Objective o)
	{
		this.objective = o;
	}
	@Override
	public void drawMe(Graphics b)
	{
		//image
		b.drawImage(animation.getImage(FRAME).getFlippedCopy(!left, false), (float)(x - 33), (float)(y - height/2));
		//health bar
		b.setColor(Color.black);
		b.fillRect((float)(x - 52), (float)(y - height/2 - 20), 104, 12);
		healthBar = new GradientFill((float)(x - 50), (float)(y - height/2 - 19), Color.transparent, (float)(x - 50), (float)(y - height/2 + 1), Color.black);
		b.setColor(Color.red);
		b.fillRect((float)(x - 50), (float)(y - height/2 - 19), (float)(100 * health / MAX_HEALTH), 10);
		b.fill(new Rectangle((float)(x - 50), (float)(y - height/2 - 19), (float)(100 * health / MAX_HEALTH), 10), healthBar);
		b.setColor(Color.black);
		for(int i = 0; i < 5; i++)
			b.drawRoundRect((float)(x - 50) + i * 20, (float)(y - height/2 - 19), 20, 10, 2);
		//weapon
		int left = 1;
		if(this.left)
			left = -1;
		if(equip)
		{
			holder.getItem().drawMe(b, left);
//			holder.getItem().drawHitbox(b);
		}
//		drawHitbox(b);
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
		holder.passID(getID());
		if(mark)
		{
			sword.setDamage(100);
			drop(hammer);
			drop(sword);
			drop(axe);
			drop(new HarpoonGun());
			
			objective.activate();
		}
	}
	public void drop(Holdable drop)
	{
		if(MainComponent.CURRENT_SCENE instanceof World)
		{
			World w = (World)MainComponent.CURRENT_SCENE;
			Item drop1 = new Item(x,y);
			drop1.init();
			Weapon p1 = (Weapon)drop;				
			drop1.insert(p1, 1);
			w.addEntity(drop1);
			
		}
	}
	@Override
	public void act() 
	{
		if(health < 300 && !holder.getItem().equals(sword))
			holder.setItem(sword);
		else if(health < 600  && health >= 300 &&!holder.getItem().equals(axe))
			holder.setItem(axe);
		if(target.getX() < x)
			left = true;
		else
			left = false;
		if(getAttack())
		{
			
		}
		else if(CURRENT_ACTION == 0)
			CURRENT_ACTION = WALK;
		switch(CURRENT_ACTION)
		{
			case WALK:
				if(left)
					moveLeft();
				else
					moveRight();
				break;
			case STRIKE:
				strike();
				break;
			default:
				clearAnimation();
				break;
		}

	}
	@Override
	public void addCollisionData(CollisionData data) 
	{
	//	data.setKnockback(new Vector2d(data.getColliding().getX() - getX(), data.getColliding().getY() - getY()).mul(0.3));
	}
	@Override
	public void processCollisionData(CollisionData data)
	{
		if(!data.isExempt(this))
		{	
			Entity e = data.getHitbox().getAttached() ;
			if(data.getSurfaceNormal().getY() < 0 && getVelocity().getY() > 0)
			{
				ground();
			}
			if(Math.abs( data.getSurfaceNormal().getY() ) == 0 && e instanceof Tile)
			{
				wall(data.getSurfaceNormal().getX() );
			}
			if(e instanceof Arrow)
				rage += 400;
			if(e instanceof SwordHolder)
				if(((SwordHolder)e).getItem() instanceof Weapon)
					rage += data.getDamage();
			if(e instanceof Player)
				rage += 100;
			CollisionDetection.collisionPhysics(getVelocity(), data.getProjection(), data.getSurfaceNormal(), this);
			if(alive && data.getDamage() != 0)
				health -= data.getDamage() * ((health + 100) / MAX_HEALTH) + 1;
			if(data.getDamage() < 0)
				MainComponent.sound.playSound(Moveable.healID);
			translate(data.getKnockback().div(10));
			addVelocity(data.getKnockback().div(100));
		}
	}
	@Override
	public ArrayList<dPolygon> getObjects()
	{
		ArrayList<dPolygon> p = new ArrayList<dPolygon>();
		if(equip)
		{
			dPolygon d = holder.getItem().getHitbox();
			d.bind(holder);
			holder.passID(getID());
			p.add(d);
		}
		return p;
	}
	private boolean getAttack()
	{	
		Vector2d toTarget = new Vector2d(getX() - target.getX(), getY() - target.getY());
		double distance = toTarget.length();
		
		rage += distance / 100;
		if(count > 0)
		{
			count--;
			return true;
		}
		else if(CURRENT_ACTION == 0 || CURRENT_ACTION == WALK)
		{
			Weapon item = (Weapon)holder.getItem();
			if(ground && item.getOffset() + REACH < distance && distance < item.getHeight() + item.getOffset() + REACH)
			{	
				CURRENT_ACTION = STRIKE; 
				tick = 0;
				count = 120;
				equip = true;
				int left = 1;
				if(this.left)
					left = -1;
				item.setKnockback(item.getKnockback().mul(new Vector2d(left, 1)));
				holder.getItem().swing(Weapon.NORMAL);
				return true;
			}
			else if(item.getOffset() + REACH > distance && rage > 100)
			{
				pulse();
			}
			else if(rage > 400)
			{
				pull();
				return true;
			}
		}
		return false;
	}
	public void clearAnimation()
	{
		CURRENT_ACTION = 0;
		tick = 0;
		equip = false;
		holder.getItem().end();
	}
	private void walk()
	{
		tick = (tick) % 60;
		FRAME = tick/10 + 3;
		tick++;
	}
	private void strike()
	{
		int STRIKE_SPEED = 20;
		FRAME = tick/STRIKE_SPEED + 9;
		int left = 1;
		if(this.left)
			left = -1;
		double angle = (Math.PI / 2 * tick / (1.7 * STRIKE_SPEED) -  Math.PI / 8) * left;
		Holdable held = holder.getItem();
		held.position((float)x-3-5*left+Math.sin(angle)*REACH, (float)y-3-Math.cos(angle) * REACH, angle);
		
		
		tick++;
		if(tick > STRIKE_SPEED * 3 - 2)
		{
			clearAnimation();
		}
	}
	private void pull()
	{
		if(MainComponent.CURRENT_SCENE instanceof World)
		{
			World w = (World)MainComponent.CURRENT_SCENE;
			int power = 20;
			double angle = correctAim(new Vector2d(target.getX() - x, target.getY() - y),0,power);
			if(left)
				angle = Math.PI + angle;
			Hook arrow = new Hook(x,y,angle,false);
			arrow.init();
			arrow.setPower(power);
			arrow.setDamage(25);
			arrow.setOwner(this);
			w.addEntity(arrow);
			hook = arrow;
			rage = 0;
			count = 0;
		}
	}
	public double correctAim(Vector2d t, int aim, int power)
	{
		//pass in aim = 0
		if(aim == 1)
			return Math.atan( (power * power + Math.sqrt(power * power * power * power + GRAVITY.getY() * (-GRAVITY.getY() * t.getX() * t.getX() + 2 * t.getY() * power * power))) / (-GRAVITY.getY() * t.getX()) );
		else
			return Math.atan( (power * power - Math.sqrt(power * power * power * power + GRAVITY.getY() * (-GRAVITY.getY() * t.getX() * t.getX() + 2 * t.getY() * power * power))) / (-GRAVITY.getY() * t.getX()) );
	}
	private void pulse()
	{
		if(MainComponent.CURRENT_SCENE instanceof World)
		{
			World w = (World)MainComponent.CURRENT_SCENE;
			Projectile pulse1 = new Projectile(x,y,16,48,new Vector2d(5.0,0), 500);
			pulse1.setImage("img/pulse.png", 0, 0, 34, 34, Math.PI / 4, false);
			pulse1.init();
			pulse1.setDamage(15);
			pulse1.exempt(this);
			pulse1.setOwner(this);
			w.addEntity(pulse1);
			Projectile pulse2 = new Projectile(x,y,16,48,new Vector2d(-5.0,0), 500);
			pulse2.setImage("img/pulse.png", 0, 0, 34, 34, Math.PI / 4, true);
			pulse2.init();
			pulse2.setDamage(15);
			pulse2.exempt(this);
			pulse2.setOwner(this);
			w.addEntity(pulse2);
			rage = 0;
		}
	}
}
