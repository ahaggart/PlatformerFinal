package com.base.entities;
import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

import com.base.engine.CollisionData;
import com.base.engine.GameState;
import com.base.engine.MainComponent;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;


public class Objective extends Tile
{
	private boolean active, complete;
	private int level;
	private int function;
	private Color unlock, checkpoint;
	
	public static final int UNLOCK = 0, CHECKPOINT = 1;
	
	public Objective(double x, double y, double width, double height, int level)
	{
		super(x,y,width,height);
		this.level = level;
		activate();
		function = CHECKPOINT;
		checkpoint = new Color(0,0,200,150);
		unlock = new Color(200,0,0,150);
	}
	public void setFunction(int function)
	{
		this.function = function;
	}
	public void activate()
	{
		active = true;
	}
	public void deactivate()
	{
		active = false;
	}
	public boolean isComplete()
	{
		return complete;
	}
	@Override
	public void drawMe(Graphics b) 
	{
		if(function == UNLOCK)	
			b.setColor(unlock);
		else if(function == CHECKPOINT)
			b.setColor(checkpoint);
		b.drawRect((float)x, (float)y, (float)width, (float)height);
		if(complete)
			b.fillRect((float)x, (float)y, (float)width, (float)height);

	}

	@Override
	public void translate(Vector2d v) 
	{

		
	}

	@Override
	public void addVelocity(Vector2d v) 
	{

		
	}

	@Override
	public void update() 
	{

		
	}

	@Override
	public ArrayList<dPolygon> getObjects() 
	{
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
		if(active && !complete && data.getHitbox().getAttached() instanceof Player)
		{
			complete = true;
			runFunction((Player)data.getHitbox().getAttached());
		}
	}
	public void runFunction(Player p)
	{
		switch(function)
		{
			case CHECKPOINT:
				p.setSpawn(getX()+width/2, getY()+height/2);
				break;
			case UNLOCK:
				if(MainComponent.state.unlockedLevels() < level + 1)
					MainComponent.state.changeData("Unlocked Levels", level + 1);
				MainComponent.state.bindInventory(p.getInventory());
				drop();
				break;
			default:
				break;
		}
	}
	public void drop()
	{
		
	}
}
