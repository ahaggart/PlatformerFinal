package com.base.UI;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.base.engine.MainComponent;

public class PauseMenu extends Menu 
{
	private Menu pause;
	private Menu CURRENT_SCENE;
	
	public PauseMenu(int width, int height) 
	{
		super(width, height);
		pause = new Menu(width, height);
		pause.setTitle("Paused");
		pause.setColor(new Color(50,50,50,200));
	}
	@Override
	public void init( ScreenObject holder)
	{
//		int swaggity = 420;
//		int sweg = swaggity;
		
		Button back = new Button(width - 275, height - 75, 200, 50, "Back to Menu");
		back.setFunction(Button.CHANGE_SCENE);
		MainMenu main = new MainMenu(width,height);
		main.init(holder);
		back.setTargetScene(holder, main);
		pause.addButton(back);
		
		CURRENT_SCENE = pause;
	}
	@Override
	public void update()
	{
		CURRENT_SCENE.update();
	}
	@Override
	public void show(Graphics b)
	{
		CURRENT_SCENE.drawBackground(b);
		CURRENT_SCENE.show(b);
	}
	@Override
	public void click(int mouseX, int mouseY)
	{
		CURRENT_SCENE.click(mouseX, mouseY);
	}
}
