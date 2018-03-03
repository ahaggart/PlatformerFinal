package com.base.items;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.CollisionData;
import com.base.engine.MainComponent;
import com.base.engine.PlaySound;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;
import com.base.entities.Entity;
import com.base.entities.Tile;

public class Weapon implements Holdable
{
	protected double height, width, x, y, angle;
	protected dPolygon hitbox;
	protected double[][] template;
	protected double offset, damage, specialDamage;
	protected int posX, posY, posWidth, posHeight;
	protected Image img;
	protected int[] handle = new int[2];
	protected String file = "img/sprites_base.png";
	protected int tick = 0;
	private boolean hasHit = true;
	private int damageType = 0;
	protected Vector2d knockback;
	public static final int NORMAL = 1, SPECIAL = 2;
	protected int THRUST_TIME = 80;
	protected Entity owner;
	protected String type = "";
	
	protected ArrayList<Entity> hitObjects;
	
	protected static int hitID = -1, swingID = -1, thrustID = -1;
	
	
	public Weapon()//set hitbox width, height, offset, and file path
	{
		height = 30;
		width = 10;
		offset = 0;
		posX = 177;
		posY = 37;
		posWidth = posHeight = 32;
		
		hitObjects = new ArrayList<Entity>();
		
		handle[0] = 6;
		handle[1] = 6;
		
		damage = 20;
		specialDamage = 10;
		knockback = new Vector2d(0,-2);
		
		type = "weapon";
	}
	public void init()
	{
		if(hitID == -1)
			hitID = MainComponent.sound.loadSound("sound/blade_impact.wav");
		if(swingID == -1)	
			swingID = MainComponent.sound.loadSound("sound/sword_swing.wav");
		if(thrustID == -1)
			thrustID = MainComponent.sound.loadSound("sound/sword_thrust.wav");
		
		template = new double[4][2];
		template[0][0] = -width / 2;
		template[0][1] = -offset-height - 5;
		template[1][0] = width / 2;
		template[1][1] = -offset-height - 5;
		template[2][0] = width / 2;
		template[2][1] = -offset-5;
		template[3][0] = -width / 2;
		template[3][1] = -offset-5;

		hitbox = new dPolygon(template);
		hitbox.name("weapon");
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
	public Holdable copy()
	{
		Weapon w = new Weapon();
		w.setDamage(damage);
		w.setSpecialDamage(specialDamage);
		w.setWidth(width);
		w.setHeight(height);
		int[] newHandle = {handle[0],handle[1]}; 
		w.setHandle(newHandle);
		w.setOffset(offset);
		w.setKnockback(knockback);
		w.setOwner(owner);
		w.setImage(file, posX, posY, posWidth, posHeight);
		return w;
	}
	public void setOwner(Entity owner)
	{
		this.owner = owner;
	}
	@Override
	public String getType()
	{
		return type;
	}
	public void setImage(String file, int x, int y, int width, int height)
	{
		this.file = file;
		posX = x;
		posY = y;
		posWidth = width;
		posHeight = height;
	}
	public void setWidth(double width)
	{
		this.width = width;
	}
	public void setOffset(double offset)
	{
		this.offset = offset;
	}
	public void setHeight(double height)
	{
		this.height = height;
	}
	public void setHandle(int[] handle)
	{
		this.handle = handle;
	}
	public void setDamage(double damage)
	{
		this.damage = damage;
	}
	public void setSpecialDamage(double specialDamage)
	{
		this.specialDamage = specialDamage;
	}
	public void setKnockback(Vector2d knockback)
	{
		this.knockback = knockback;
	}
	public Vector2d getKnockback()
	{
		return knockback;
	}
	public double getDamage()
	{
		return damage;
	}
	public double getSpecialDamage()
	{
		return specialDamage;
	}
	public void addCollisionData(CollisionData data)
	{
		if(hitObjects.contains(data.getColliding()))
			data.exempt(data.getColliding().getID());
		data.setProjection(new Vector2d(0,0));
		if(damageType == 1)
		{
			data.setDamage(damage);
			data.setKnockback(knockback);
		}
		else if(damageType == 2)
		{
			data.setDamage(specialDamage);
		}
	}
	public void hit(Entity e)
	{
		if(!hasHit && !(e instanceof Tile))	
		{
			MainComponent.sound.playSound(hitID);
			hasHit = true;
			hitObjects.add(e);
		}
	}
	public void swing(int ID)
	{
		hasHit = false;
		damageType = ID;
		if(ID == NORMAL)
			MainComponent.sound.playSound(swingID);	
		else if(ID == SPECIAL)
			MainComponent.sound.playSound(thrustID);	

	}
	public void use()
	{
		//needed to uphold Holdable implementation
		//this method should not be fired
	}
	public boolean used()
	{
		return false;
	}
	public void resetUse()
	{
		
	}
	public boolean consumable()
	{
		return false;
	}
	public boolean stackable()
	{
		return false;
	}
	public void end()//fired when the weapon is done swinging
	{
		hitObjects.clear();
		hasHit = true;
	}
	public void position(double x, double y, double angle)
	{
		this.x = x;
		this.y = y;
		this.angle = angle;
		double[][] t = new double[template.length][2];
		for(int i = 0 ; i < template.length; i++)
		{
			t[i][0] = template[i][0];
			t[i][1] = template[i][1];
		}
		hitbox.set(t);
		hitbox.rotate(angle,0,0);
		hitbox.translate(x,y);
	}
	public void drawMe(Graphics b, int left)
	{
		Image sub = img.copy();
		int off = -5;
		if(left > 0)
		{
			sub.setCenterOfRotation(handle[0],posHeight - handle[1]);
			sub.setRotation((float)Math.toDegrees(angle)-45.0f);
		}
		else
		{
			sub = sub.getFlippedCopy(true, false);
			sub.setCenterOfRotation(posWidth - handle[0],posHeight - handle[1]);
			sub.setRotation((float)Math.toDegrees(angle)+45.0f);
			off = sub.getWidth() - 5;
		}
		b.drawImage(sub, (int)(x-handle[0]-off), (int)(y-posHeight+handle[1]), Color.white);
	}
	public void drawHitbox(Graphics b)
	{
		b.setColor(Color.red);
		b.draw(hitbox.makePolygon());
	}
	@Override
	public Image getImage()
	{
		return img;
	}
	public boolean canHit()
	{
		return !hasHit;
	}
	public dPolygon getHitbox()
	{
		return hitbox;
	}
	public double getHeight()
	{
		return height;
	}
	public double getWidth()
	{
		return width;
	}
	public double getOffset()
	{
		return offset;
	}
	public void update()
	{
		
	}
	public boolean retract(int time)
	{
		return time > THRUST_TIME;
	}
	public double getPower()
	{
		return 0;
	}

}