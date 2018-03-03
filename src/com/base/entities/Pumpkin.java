package com.base.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Pumpkin extends Moveable 
{
	private SpriteSheet texture;
	private int hit;
	private int rand;
	
	public Pumpkin(double x,double y)
	{
		super(x,y);
		width = 32;
		height = 24;
		health = 5;
		MAX_HEALTH = 5;
		
		rand = (int)(Math.random() * 9) * 2;
	}
	@Override
	public void init()
	{
		super.init();
		try {
			Image src = new Image("img/pumpkin.png");
			texture = new SpriteSheet(src,16,16,2);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void update()
	{
		if(oldHealth > health)
			hit = 20;
		if(hit > 0)
			hit--;
		super.update();
	}
	@Override
	public void drawMe(Graphics b)
	{
		int off = 2;
		if(hit > 0)
			off = 0;
		b.drawImage(texture.getSprite(0+off, rand), (float)x-16, (float)y-20);
		b.drawImage(texture.getSprite(1+off, rand), (float)x, (float)y-20);
		b.drawImage(texture.getSprite(0+off, rand+1), (float)x-16, (float)y-4);
		b.drawImage(texture.getSprite(1+off, rand+1), (float)x, (float)y-4);

	}
}
