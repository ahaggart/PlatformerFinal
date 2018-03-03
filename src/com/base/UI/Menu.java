package com.base.UI;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.UnicodeFont;

import java.util.ArrayList;

public class Menu implements ScreenObject
{
	protected Color backgroundColor;
	protected int width, height;
	protected ArrayList<Button> buttons;
	protected ScreenObject current;
	protected String title = "";
	protected UnicodeFont font;
	
	public Menu(int width, int height)
	{
		this.width = width;
		this.height = height;
				
		backgroundColor = new Color( 255, 255, 255 );
		
		buttons = new ArrayList<Button>();
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public void setFont(UnicodeFont font)
	{
		this.font = font;
	}
	public void setColor(Color color)
	{
		backgroundColor = color;
	}
	public void changeScene(ScreenObject change)
	{
		current = change;
	}
	public void init(ScreenObject holder)
	{
		for(Button each : buttons)
			each.init();
	}
	public void click(int mouseX, int mouseY)
	{
		for(Button each : buttons)
		{
			if( each.checkClick(mouseX,mouseY) )
			{
				each.runFunction();
			}
		}
	}
	public void rightClick(int mouseX, int mouseY)
	{
		//does not take right clicks
	}
	public void key(int keyCode)
	{
		//does not take key inputs
	}
	public void release(int keyCode)
	{
		//does not take input
	}
	public void scroll(int change)
	{
		
	}
	public void addButton(Button button)
	{
		buttons.add(button);
	}
	public ArrayList<Button> getButtons()
	{
		return buttons;
	}
	public void show(Graphics b)
	{
		for(Button each : buttons)
			each.drawMe(b);
		b.resetFont();
		if(font != null)
			b.setFont(font);
		b.setColor(Color.white);
		b.drawString(title,100,75);
	}
	public void drawBackground(Graphics b)
	{
		b.setColor(backgroundColor);
		b.fillRect(0, 0, width, height);
	}
	public void update()
	{
		for(Button each : buttons)
			each.update();
	}
	public void enter()
	{
		
	}
	public void exit()
	{
		
	}
}