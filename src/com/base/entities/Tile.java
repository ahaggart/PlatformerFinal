package com.base.entities;
import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.CollisionData;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;

public class Tile extends Entity
{
	protected double x, y;
	protected dPolygon hitbox;
	protected ArrayList<Entity> standing = new ArrayList<Entity>(), temp = new ArrayList<Entity>();
	protected Image texture1, texture2;
	protected Image[] dirt = new Image[6];
	protected Image[] grass = new Image[6];
	protected double width, height;
	private boolean render = true;
	public static final int TOP = 0, RIGHT = 1, LEFT = 2, BOTTOM = 3, CORNER = 4, MIDDLE = 5; 
	
	public Tile(double x, double y)
	{
		this.x = x;
		this.y = y;
		width = 16;
		height = 16;
	}
	public Tile(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public Tile(double x, double y, double width, double height, boolean render)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.render = render;
	}
	public void init()
	{
		double[][] p = {{x,y},{x+width,y},{x+width,y+height},{x,y+height}};
		hitbox = new dPolygon(p);
		hitbox.bind(this);
		/*	
		try {
			texture1 = new Image("img/dirt.png");
			texture2 = new Image("img/grass.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//dirt
		dirt[TOP] = texture1.getSubImage(18,0,16,16);
		dirt[RIGHT] = texture1.getSubImage(72,18,16,16);
		dirt[LEFT] = texture1.getSubImage(0,18,16,16);
		dirt[BOTTOM] = texture1.getSubImage(36,36,16,16);
		dirt[CORNER] = texture1.getSubImage(0,54,16,16);
		dirt[MIDDLE] = texture1.getSubImage(36,18,16,16);
		//grass
		grass[TOP] = texture2.getSubImage(18,0,16,16);
		grass[RIGHT] = texture2.getSubImage(72,18,16,16);
		grass[LEFT] = texture2.getSubImage(0,18,16,16);
		grass[BOTTOM] = texture2.getSubImage(36,36,16,16);
		grass[CORNER] = texture2.getSubImage(0,54,16,16);
		grass[MIDDLE] = texture2.getSubImage(36,18,16,16);
		*/	
	}
	public Tile(dPolygon p)
	{
		hitbox = p;
	}
	public void drawMe(Graphics b)
	{
		/*
		if(render)
		{
			for(int i = 0; i < width / 16; i++)
			{
				for(int j = 0; j < height / 16; j++)
				{
					if(i == 0 && j == 0)
						b.drawImage(grass[CORNER], (float)x + 16 * i, (float)y + 16 * j);
					else if(i == 0 && j == height / 16 - 1)
						b.drawImage(dirt[CORNER].getFlippedCopy(false, true), (float)x + 16 * i, (float)y + 16 * j);
					else if(i == width / 16 - 1 && j == height / 16 - 1)
						b.drawImage(dirt[CORNER].getFlippedCopy(true,true), (float)x + 16 * i, (float)y + 16 * j);
					else if(i == width / 16 - 1 && j == 0)
						b.drawImage(grass[CORNER].getFlippedCopy(true,false), (float)x + 16 * i, (float)y + 16 * j);
					else if(i == 0)
						b.drawImage(dirt[LEFT], (float)x + 16 * i, (float)y + 16 * j);
					else if(i == width / 16 -1)
						b.drawImage(dirt[RIGHT], (float)x + 16 * i, (float)y + 16 * j);
					else if(j == 0)
						b.drawImage(grass[TOP], (float)x + 16 * i, (float)y + 16 * j);
					else if(j == height / 16 - 1)
						b.drawImage(dirt[BOTTOM], (float)x + 16 * i, (float)y + 16 * j);
					else
						b.drawImage(dirt[MIDDLE], (float)x + 16 * i, (float)y + 16 * j);
				}
			}
		}
		*/
		if(render)
		{
			b.setColor(Color.green);
			b.fillRect((float)x, (float)y, (float)width, (float)height);
			b.setColor(Color.black);
			b.drawRect((float)x, (float)y, (float)width, (float)height);
		}
	}
	public void drawMe(Graphics b, Color color, String info)
	{
		if(render)
		{
			b.setColor(color);
			b.fillRect((float)x, (float)y, (float)width, (float)height);
			b.setColor(Color.black);
			b.drawString(info, (float)x, (float)y);
			b.drawRect((float)x, (float)y, (float)width, (float)height);
		}
	}
	public dPolygon getHitbox()
	{
		return hitbox;
	}
	public void update()
	{
		temp.clear();
		for(int i = 0; i < standing.size(); i++)
			temp.add(standing.get(i));
		standing.clear();
	}
	public double getX()
	{
		return x;
	}	
	public void setX(double x)
	{
		this.x = x;
	}
	public double getWidth()
	{
		return width;
	}
	public double getY()
	{
		return y;
	}	
	public void setY(double y)
	{
		this.y = y;
	}
	@Override
	public void translate(Vector2d v) 
	{
		//these shouldn't move
	}
	@Override
	public void addVelocity(Vector2d v) 
	{
		//these shouldn't move
	}
	@Override
	public ArrayList<dPolygon> getObjects() 
	{
		return new ArrayList<dPolygon>();
	}
	@Override
	public void addCollisionData(CollisionData data) 
	{
//		data.setKnockback(new Vector2d(0,-1));
		
	}
	@Override
	public void processCollisionData(CollisionData data) 
	{
		standing.add(data.getHitbox().getAttached());
	}
	public ArrayList<Entity> getStandingEntities()
	{
		return temp;
	}
	
}


