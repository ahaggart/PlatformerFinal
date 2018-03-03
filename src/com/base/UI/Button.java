package com.base.UI;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import com.base.engine.GameState;
import com.base.engine.MainComponent;

public class Button
{
	public static final int CHANGE_SCENE = 0, CHANGE_GAME_DATA = 1;
	
	protected int x, y, width, height, spring, down = 0;
	protected boolean isPressed, active;
	protected String text, data;
	protected Color color, off;
	protected GradientFill fill;
	protected int function;
	protected ScreenObject change, target;
	protected float arg;
	protected static int clickID = -1;
	
	public Button(int x, int y, int width, int height, String text)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		active = true;
		
		fill = new GradientFill(x + width / 2, y, Color.white, x + width / 2, y + height, Color.gray);
		off = new Color(155,155,155,0.5f);
		
		spring = 2;
		
		color = new Color(255,255,255);
	}
	public void init()
	{
		if(clickID == -1)
		{
			clickID = MainComponent.sound.loadSound("sound/button.wav");
		}
	}
	public void activate()
	{
		active = true;
	}
	public void deactivate()
	{
		active = false;
	}
	public void setFunction(int function)
	{
		this.function = function;
	}
	public void setTargetScene(ScreenObject target, ScreenObject change)
	{
		this.target = target;
		this.change = change;
	}
	public void setTargetData(String target, float arg)
	{
		data = target;
		this.arg = arg;
	}
	public void drawMe(Graphics b)
	{
		b.resetFont();
		if(isPressed)
			b.fill(new Rectangle(x,y,width,height), fill.getInvertedCopy());
		else
			b.fill(new Rectangle(x,y,width,height), fill);
		b.setColor(Color.gray);
		b.drawRect(x,y,width,height);
		b.setColor(Color.black);
		Font current = b.getFont();
		b.drawString(text,x+width/2-current.getWidth(text)/2,y+height/2);
		if(!active)
		{
			b.setColor(off);
			b.fillRect(x, y, width, height);
		}
	}
	public boolean checkClick(int mouseX, int mouseY)
	{
		if(x < mouseX && mouseX < x + width && y < mouseY && mouseY < height + y && active)
		{
			return true;
		}
		return false;
	}
	public void runFunction()
	{
		MainComponent.sound.playSound(clickID);
		switch(function)
		{
			case CHANGE_SCENE:
				target.changeScene(change);
				break;
			case CHANGE_GAME_DATA:
				MainComponent.state.changeData(data, arg);
				if(target != null && change != null)
					target.changeScene(change);
			//	System.out.println("Attempted to set data: " + data + " to value " + arg);
				break;
			default:
				break;
		}
	}
	public void update()
	{
		if(checkClick(MainComponent.input.getMouseX(), MainComponent.input.getMouseY()))
		{
			isPressed = true;
			down = 0;
		}
		if(isPressed)
			down++;
		if(down >= spring)
		{
			down = 0;
			isPressed = false;
		}
	}
}



