package com.base.UI;
import org.newdawn.slick.Graphics;

public interface ScreenObject
{
	public void click(int mouseX, int mouseY);	
	public void rightClick(int mouseX, int mouseY);	
	public void key(int keyCode);
	public void release(int keyCode);
	public void scroll(int change);
	public void show(Graphics b);
	public void update();
	public void init(ScreenObject holder);
	public void changeScene(ScreenObject change);
	public void enter();
	public void exit();
}