package com.base.entities;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

import com.base.UI.ScreenObject;
import com.base.engine.CollisionData;
import com.base.engine.CollisionDetection;
import com.base.engine.Controllable;
import com.base.engine.MainComponent;
import com.base.engine.Vector2d;
import com.base.engine.World;
import com.base.engine.dPolygon;
import com.base.items.Bow;
import com.base.items.Holdable;
import com.base.items.Item;
import com.base.items.Weapon;

import java.util.ArrayList;

public class Player extends Moveable implements Controllable
{
	private Color color;
	private double wall = 0;
	private double JUMP_HEIGHT = 7;
	private double AIR_JUMP_HEIGHT = 7;
	private double SHIELD_HEALTH = 200, SHIELD_HEALTH_MAX = 200;
	public static double spawnX, spawnY;
	protected int hop = 0, spin = 0, lives; 
	private boolean ground, doubleJump, wallJump, left, walk, cartwheelRight, cartwheelLeft, dodge, shield, canShield = true;
	private Model rig;
	private SwordHolder holder;
	private ArrayList<Integer> ACTION_QUEUE = new ArrayList<Integer>();
	private ArrayList<Item> contact;
	
	private Item[] inventory;
	private int SELECTED = 0;
	
	public static final int MOVE_RIGHT = 0, MOVE_LEFT = 1, JUMP = 2, ATTACK = 3, USE = 4, BLOCK = 5;
	
	public Player(double x, double y)
	{
		super(x,y);
		spawnX = x;
		spawnY = y;
		width = 26;
		height = 44;
		lives = 5;
		MAX_HEALTH = 100;
		health = MAX_HEALTH;
		rig = new Model(x,y);
		rig.build();
		
		color = Color.red;		
		contact = new ArrayList<Item>();
		inventory = new Item[6];
	}
	public void init()
	{
		super.init();
		rig.init();
		hitbox.name("player");
	}
	public void respawn()
	{
		translate(new Vector2d(spawnX - x, spawnY - y));
		setVelocity(new Vector2d(0,0));
		health = MAX_HEALTH;
		init();
		lives--;
	}
	public void warp()
	{
		translate(new Vector2d(spawnX - x, spawnY - y));
		setVelocity(new Vector2d(0,0));
	}
	public void equip(Holdable item)
	{
		if(inventory[SELECTED] != null && inventory[SELECTED].get() != null)//redundancies to prevent exceptions
		{
			rig.hold(inventory[SELECTED].get());
			inventory[SELECTED].get().setOwner(this);
			holder = new SwordHolder(inventory[SELECTED].get());
		}
	}
	public void addActionToQueue(int action)
	{
		if(!ACTION_QUEUE.contains(action))
			ACTION_QUEUE.add(action);
	}
	public void act()
	{
		if(alive)
		{
			//TODO: add button combination actions(roll/cartwheel, etc)
			/*
			if(cartwheelRight || cartwheelLeft)
			{
				rig.flip(v.getX());
				spin++;
				if(spin > 48)
				{
					cartwheelRight = false;
					cartwheelLeft = false;
				}
				else
				{
					dodge = true;
					ACTION_QUEUE.clear();
					if(cartwheelRight)
						addVelocity(new Vector2d(5.0,0));
					else if(cartwheelLeft)
						addVelocity(new Vector2d(-5.0,0));

				}
			}
			if(ACTION_QUEUE.contains(BLOCK))
			{
				if(spin > 48)
				{
					spin++;
					if(spin > 96)
						spin = 0;
				}
				else if(ACTION_QUEUE.contains(MOVE_RIGHT))
					cartwheelRight();
				else if(ACTION_QUEUE.contains(MOVE_LEFT))
					cartwheelLeft();
			}
			*/
			for(int i = 0; i < ACTION_QUEUE.size(); i++)
			{
				switch(ACTION_QUEUE.get(i))
				{
					case MOVE_RIGHT:
						moveRight();
						break;
					case MOVE_LEFT:
						moveLeft();
						break;
					case JUMP:
						jump();
						break;
					case ATTACK:
						attack();
						break;
					case USE:
						use();
						break;	
					case BLOCK:
						block();
						break;
					default:
						break;
				}
			}
		}
		ACTION_QUEUE.clear();
	}
	public void drawMe(Graphics b)
	{
		b.setColor(Color.black);
		b.drawString("Player",(int)(x-width/2),(int)(y-height/2)-20);
		b.setColor(color);
		rig.show(b);
		if(shield)
		{
			b.setColor(Color.blue);
			b.setLineWidth(5);
			double size = 50 * SHIELD_HEALTH / SHIELD_HEALTH_MAX;
			b.drawOval((float)(x - size/2), (float)(y - size / 2), (float)(size), (float)(size));
			b.setLineWidth(1);
		}
		//you e big dumn
//		drawHitbox(b);
	}
	public void drawHitbox(Graphics b)
	{
		b.setColor(Color.blue);

		b.draw(hitbox().makePolygon());
	}
	public void jump()
	{
		if(hop > 0)
		{
//			System.out.println("jump!");
			hop = 0;
			rig.jump();
			Vector2d v = new Vector2d(0.0,-JUMP_HEIGHT);
			addVelocity(v);
			ground = false;
		}
		else if(doubleJump || wallJump)
		{
//			System.out.println("double jump!");
			rig.flip(v.getX());
			double dX = v.getX();
			if(wallJump)
				dX += wall / 2;
			Vector2d v = new Vector2d(dX,-AIR_JUMP_HEIGHT);
			setVelocity(v);
			doubleJump = false;
		}
	}
	public void attack()
	{
		if(inventory[SELECTED] != null)	
			rig.strike(MainComponent.input.getMouseX(), MainComponent.input.getMouseY());
	}
	public void use()
	{
		if(inventory[SELECTED] != null)
		{
				rig.thrust(MainComponent.input.getMouseX(), MainComponent.input.getMouseY());
				inventory[SELECTED].get().use();
		}
	}
	public void addVelocity(Vector2d v)//add velocity vector to object velocity vector
	{	
		setVelocity(this.v.add(v));//sets velocity to new vector which is the sum of the current vector and the velocity being added
	}
	public void translate(Vector2d v)//ONLY USE THIS METHOD TO MOVE PLAYER MANUALLY, OR ELSE HITBOX WILL BE LOST
	{	
		x += v.getX();
		y += v.getY();
		hitbox.translate(v.getX(),v.getY());
		rig.update(x,y);
	}
	public void pickup()
	{
		for(int i = 0; i < inventory.length; i++)
		{
			if(contact.size() > 0)
			{
				if(inventory[i] == null && !contact.get(0).isMarked())
				{
					inventory[i] = contact.get(0);
					contact.get(0).mark();
					contact.remove(0);
					if(i == 0)
						equip(inventory[0].get());
				}
				else if(inventory[i] != null && inventory[i].get().getClass() == contact.get(0).getClass() && inventory[i].get().stackable())
				{
					inventory[i].addQuantity(contact.get(0).getQuantity());
					contact.get(0).mark();
					contact.remove(0);
				}
			}
		}
	}
	public void drop()
	{
		if(inventory[SELECTED] != null)
		{
			int left = 1;
			if(this.left)
				left = -1;
			Item drop = inventory[SELECTED];
			inventory[SELECTED] = null;
			drop.setX(x);
			drop.setY(y);
			drop.init();
			drop.setVelocity(new Vector2d(2.0 * left, -2.0));
			if(MainComponent.CURRENT_SCENE instanceof World)
			{
				World w = (World)MainComponent.CURRENT_SCENE;
				w.addEntity(drop);
			}
		}
	}
	public void moveLeft()
	{
		walk = true;
		if(ground)
		{
			if(v.getX() > -4.0)
				addVelocity(new Vector2d(-2.0,0.0) );
			rig.walk(v.getX());

		}
		else
		{
			if(v.getX() > -2.0)
				addVelocity(new Vector2d(-1.0,0.0) );
		}
	}
	public void moveRight()
	{
		walk = true;
		if(ground)
		{
			if(v.getX() < 4.0)
				addVelocity(new Vector2d(2.0,0.0) );	
			rig.walk(v.getX());

		}
		else
		{
			if(v.getX() < 2.0)
				addVelocity(new Vector2d(1.0,0.0) );
		}	
	}
	public void block()
	{
		if(canShield)	
			shield = true;
	}
	public void cartwheelRight()
	{
		ACTION_QUEUE.clear();
		cartwheelRight = true;
		dodge = true;
	}
	public void cartwheelLeft()
	{
		ACTION_QUEUE.clear();
		cartwheelLeft = true;
		dodge = false;
	}
	public void click(double x, double y)
	{
		addActionToQueue(ATTACK);
	}
	public void rightClick(double x, double y)
	{
		addActionToQueue(USE);
	}
	public void cycleEquipment(int change)
	{
		SELECTED -= change/120;
		SELECTED += 6;
		SELECTED = SELECTED % 6;
		if(inventory[SELECTED] != null)
			equip(inventory[SELECTED].get());
	}
	public void selectSlot(int slot)
	{
		SELECTED = slot;
		SELECTED = SELECTED % 6;
		if(inventory[SELECTED] != null)
			equip(inventory[SELECTED].get());
	}
	public void face(double rX, double rY)
	{
		left = (x > rX);
		rig.face(left);
	}
	public void update()
	{
		contact.clear();
		for(int i = 0; i < inventory.length; i++)//clear inventory of used-up consumables
			if(inventory[i] != null)
				if(inventory[i].getQuantity() == 0)
					inventory[i] = null;
		for(int i = 0; i < inventory.length; i++)
			for(int j = i + 1; j < inventory.length; j++)
				if(inventory[i] != null && inventory[j] != null)
					if(inventory[i].get().getClass() == inventory[j].get().getClass() && inventory[i].get().stackable())
					{
						inventory[i].addQuantity(inventory[j].getQuantity());
						inventory[j] = null;
					}
		shield = false;
		dodge = false;
		act();
		if(shield)
			SHIELD_HEALTH -= 0.2;
		else if(SHIELD_HEALTH < SHIELD_HEALTH_MAX)
		{
			if(canShield)
				SHIELD_HEALTH += 0.4;
			else
				SHIELD_HEALTH += 0.1;
			if(SHIELD_HEALTH > SHIELD_HEALTH_MAX)
				SHIELD_HEALTH = SHIELD_HEALTH_MAX;
		}
		if(SHIELD_HEALTH == SHIELD_HEALTH_MAX)
			canShield = true;
		if(!walk )
			rig.clearLowerAnimation();
		walk = false;
		ground = false;
		wallJump = false;
		if(inventory[SELECTED] != null)
			inventory[SELECTED].update();
		if(hop > 0)
			hop--;
		super.update();
		left = (this.x > MainComponent.input.getMouseX() + this.x - MainComponent.app.getWidth() / 2 );
		rig.face(left);
	}
	public String animationData()
	{
		//return "Animation: " + rig.getAction() + " ticks: " + rig.getTick(); 
		return "";
	}
	public void ground()
	{
		ground = true;
		doubleJump = true;
		rig.land();
		hop = 20;
		//setVelocity(new Vector2d(0,v.getY()));
	}
	public void wall(double dir)
	{
//		wallJump = true;
		wall = dir;
	}
	public dPolygon getHitbox()
	{
		return hitbox();
	}
	public dPolygon hitbox()
	{
		return hitbox;
	}
	@Override
	public ArrayList<dPolygon> getObjects() 
	{
		ArrayList<dPolygon> objs =  new ArrayList<dPolygon>();
		if(inventory[SELECTED] != null && inventory[SELECTED].get() != null)
		{
			if(!inventory[SELECTED].get().canHit() || inventory[SELECTED].get().getHitbox() == null)
				return objs;
			dPolygon weapon = inventory[SELECTED].get().getHitbox();
			if(holder == null)
				holder = new SwordHolder(inventory[SELECTED].get());
			weapon.bind(holder);
			holder.passID(getID());
			objs.add(weapon);
		}
		return objs;
	}
	public Item[] getInventory()
	{
		return inventory;
	}
	public void setInventory(Item[] inventory)
	{
		this.inventory = inventory;
		if(inventory[0] != null)
			equip(inventory[SELECTED].get());
	}
	public void addCollisionData(CollisionData data) 
	{
		if(holder != null)	
			data.exempt(holder.getID());
	}
	public void processCollisionData(CollisionData data) 
	{
			if(!data.isExempt(this))
			{
	//			System.out.println("player collided with surface: " + data.getSurfaceNormal());
				if(data.getSurfaceNormal().getY() < 0  && getVelocity().getY() > 0)
				{
					ground();
				}
				if(Math.abs( data.getSurfaceNormal().getY() ) == 0)
				{
					wall(data.getSurfaceNormal().getX() );
				}
				
				CollisionDetection.collisionPhysics(getVelocity(), data.getProjection(), data.getSurfaceNormal(), this);
				
	//			System.out.println("post-collision velocity: " + getVelocity());
				addVelocity(data.getKnockback());
				if(protect == 0)
				{
					if(dodge)
					{
						
					}
					else if(shield)
					{
						SHIELD_HEALTH -= data.getDamage();
						if(SHIELD_HEALTH < 0)
						{
							health += SHIELD_HEALTH;
							SHIELD_HEALTH = 0;
							canShield = false;
						}
					}
					else
					{
						health -= data.getDamage();
						if(data.getDamage() > 0)
							protect = 20;
					}
				}
			}
	}
	public double getHealth()
	{
		return health;
	}
	public double getMaxHealth()
	{
		return MAX_HEALTH;
	}
	public Holdable getHeldItem()
	{
		if(inventory[SELECTED] != null)
			return inventory[SELECTED].get();
		return null;
	}
	public int getSelected()
	{
		return SELECTED;
	}
	@Override
	public void setID(int ID)
	{
		this.ID = ID;
		if(holder != null)
			holder.passID(this.ID);
	}
	public void setSpawn(double x, double y)
	{
		spawnX = x;
		spawnY = y;
	}
	@Override
	public int getID()
	{
		return ID;
	}
	public void look(Item i)
	{
		contact.add(i);
	}
	public ArrayList<Item> getAccessible()
	{
		return contact;
	}
	@Override
	public boolean isMarked()
	{
		return mark;
	}
	public int getLives()
	{
		return lives;
	}
	
}









