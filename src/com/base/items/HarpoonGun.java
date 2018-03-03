package com.base.items;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.MainComponent;
import com.base.engine.Vector2d;
import com.base.engine.World;
import com.base.entities.Arrow;
import com.base.entities.Hook;

public class HarpoonGun extends Bow 
{
	public static final int MAX_HOOKS = 1;
	private ArrayList<Hook> hooks;
	
	public HarpoonGun()
	{
		file = "img/crossbow.png";
		posX = 0;
		posY = 0;
		posWidth = 58;
		posHeight = 28;
	}
	
	@Override
	public void init()
	{
		hooks = new ArrayList<Hook>();
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
		HarpoonGun g = new HarpoonGun();
		return g;
	}
	@Override
	public void swing(int ID)
	{
		if(ID == Weapon.SPECIAL)
			draw();
		else
		{
			for(int i = 0; i < hooks.size(); i++)
				hooks.get(i).remove();
			hooks.clear();
		}
	}
	@Override
	public void fire()
	{
		draw = false;
		fired = true;
		if(MainComponent.CURRENT_SCENE instanceof World)
		{
			World w = (World)MainComponent.CURRENT_SCENE;
			Arrow arrow = new Hook(x,y,Vector2d.cartesianAngle(new Vector2d(MainComponent.input.getMouseX() - 500, MainComponent.input.getMouseY() - 300)), false);
			arrow.init();
			arrow.setPower((double)drawTime / MAX_DRAW_TIME * 25 + 5);
			arrow.setDamage(getDamage() * 3 * (double)drawTime / MAX_DRAW_TIME + getDamage());
			arrow.setOwner(owner);
			w.addEntity(arrow);
			if(arrow instanceof Hook)
				hooks.add((Hook)arrow);
		}
	}
	@Override
	public void update()
	{
		super.update();
		if(hooks.size() > MAX_HOOKS)
		{
			hooks.get(0).remove();
			hooks.remove(0);
		}
		for(int i = hooks.size() - 1; i >= 0; i--)
			if(hooks.get(i).isMarked())
				hooks.remove(i);
	}
}
