package com.base.items;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.CollisionData;
import com.base.engine.CollisionDetection;
import com.base.entities.Entity;
import com.base.entities.Moveable;
import com.base.entities.Player;
import com.base.entities.Tile;

public class Item extends Moveable 
{
	private Image img;
	private Holdable contained;
	private int quantity;
	private String type;
	
	public static final int MAX_ITEMS_PER_SLOT = 99;
	
	public Item(double x, double y)
	{
		super(x,y);
		width = 22;
		height = 26;
		health = 1;
		MAX_HEALTH = 1;
	}
	public Item copy()
	{
		Item copy = new Item(x,y);
		copy.init();
		copy.mark();
		copy.insert(contained.copy(), quantity);
		return copy;
	}
	public void insert(String type)//pass in a type which will determine possible random drops
	{
		//TODO: add random item drops
	}
	public void insert(Holdable h, int quantity)
	{
		contained = h;
		h.init();
		type = h.getType();
		this.quantity = quantity;
	}
	public void init()
	{
//		System.out.println("got here " + type);
		super.init();
		try {
			img = new Image("img/item.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	public String getType()
	{
		return type;
	}
	public Holdable get()
	{
		return contained;
	}
	public int getQuantity()
	{
		return quantity;
	}
	public void addQuantity(int q)
	{
		quantity += q;
	}
	public void mark()
	{
		mark = true;
	}
	@Override
	public void update()
	{
		if(mark)//in inventory if it is marked and still has references
		{
			contained.update();
			if(contained.used() && quantity > 0)
			{
				quantity--;
				contained.resetUse();
			}
		}
		else
			super.update();
	}
	public void drawMe(Graphics b)
	{
		b.drawImage(img, (float)(x - width / 2), (float)(y - height / 2));
	}
	public Image getItemImage()
	{
		if(contained != null)
			return contained.getImage();
		return null;
	}
	@Override
	public void addCollisionData(CollisionData data)
	{
		data.exempt(data.getColliding().getID());
	}
	@Override
	public void processCollisionData(CollisionData data)
	{
		if(!data.isExempt(this))
		{
			Entity e = data.getHitbox().getAttached();
			if(e instanceof Tile)
				CollisionDetection.collisionPhysics(getVelocity(), data.getProjection(), data.getSurfaceNormal(), this);
			else if(e instanceof Player)
			{
				Player p = (Player)e;
				p.look(this);
			}
		}
	}
}
