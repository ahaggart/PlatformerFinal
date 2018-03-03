package com.base.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.CollisionData;
import com.base.engine.Vector2d;

public class Hook extends Arrow 
{
	private Image chain;
	private Entity grab;
	private boolean lift;
	
	public Hook(double x, double y, double angle, boolean lift)
	{
		super(x,y,angle);
		this.lift = lift;
	}
	@Override
	public void init()
	{
		super.init();
		try {
			chain = new Image("img/chain.png");
			img = new Image("img/spear.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void update()
	{
		if(grab != null)
		{
			translate(new Vector2d(grab.getX() - getX(), grab.getY() - getY()));
			if(lift)
				lift();
			else
				pull();
		}
		else
		{
			super.update();
		}
	}
	@Override
	public void drawMe(Graphics b)
	{
		//draw chain
		Vector2d chain = new Vector2d(x - owner.getX(), y - owner.getY());
		int height = this.chain.getHeight();
		int links = (int)(chain.length() / height + 1);
		Image temp = this.chain.copy();
		temp.rotate((float)Math.toDegrees(Vector2d.cartesianAngle(chain)) + 90);
		chain.normalize();
		for(int i = 0; i < links; i++)
		{
			b.drawImage(temp, (float)(x - chain.getX() * height * i), (float)( y - chain.getY() * height * i));
		}
		//draw arrow
		super.drawMe(b);
	}
	@Override
	public void addCollisionData(CollisionData data)
	{
		if(count > 0)
			data.exempt(data.getColliding().getID());
		data.exempt(owner.getID());
	}
	@Override
	public void processCollisionData(CollisionData data)
	{
		Entity e = data.getHitbox().getAttached();
		if(!data.isExempt(this) && e.getID() != owner.getID() && grab == null)
		{
			if(!(e instanceof Tile) && e instanceof Moveable)
			{
				grab = e;
				count++;
			}
			else if(lift && e instanceof Tile)
			{
				grab = new Moveable(x,y);
				count++;
			}
			else
				remove();
		}
	}
	public int getCount()
	{
		return count;
	}
	public void remove()
	{
		mark = true;
	}
	private void pull()
	{
		Vector2d toTarget = new Vector2d(owner.getX() - grab.getX(), owner.getY() - grab.getY());
		Vector2d temp = toTarget.add(0).normalize().mul(3);
		grab.translate(temp);
		grab.setVelocity(temp);
		if(toTarget.length() < 40)
			remove();
	}
	private void lift()
	{
		Vector2d toTarget = new Vector2d(owner.getX() - grab.getX(), owner.getY() - grab.getY());
		Vector2d temp = toTarget.add(0).normalize().mul(-3);
		owner.translate(temp);
		owner.setVelocity(temp);
		if(toTarget.length() < 20)
			remove();
	}
}
