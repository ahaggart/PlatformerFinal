package com.base.engine;
public interface Controllable 
{
	public void moveLeft();
	public void moveRight();
	public void ground();
	public void wall(double dir);
	public void jump();
	public void act();
}
