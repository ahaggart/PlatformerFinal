package com.base.engine;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;

import com.base.UI.MainMenu;
import com.base.UI.ScreenObject;

public class MainComponent extends BasicGame implements ScreenObject
{
	public static AppGameContainer app;
	public static Input input;
	public static ScreenObject CURRENT_SCENE;
	public static PlaySound sound;
	public static GameState state;
	
    public MainComponent() 
    {
        super("Platformer");
        CURRENT_SCENE = new MainMenu(1000,600);
        sound = new PlaySound();
    }
    
    @Override
    public void init(GameContainer container) throws SlickException 
    {
    	System.out.println("\n\n---Initializing game container---");
        state = new GameState();
    	input = container.getInput();
    	CURRENT_SCENE.init(this);
    	CURRENT_SCENE.enter();
    	sound.init();
    }
    public void changeScene(ScreenObject change)
    {
    	CURRENT_SCENE.exit();
    	CURRENT_SCENE = change;
    	CURRENT_SCENE.init(this);
    	CURRENT_SCENE.enter();
    }
    @Override
    public void update(GameContainer container, int delta)throws SlickException
    {
    	CURRENT_SCENE.update();
    }

    @Override
    public void render(GameContainer container, Graphics g)throws SlickException 
    {
       g.setColor(Color.white);
       g.fillRect(0, 0, app.getWidth(), app.getHeight());
       CURRENT_SCENE.show(g);
    }

    public static void main(String[] args) 
    {
        try 
        {
            app = new AppGameContainer(new MainComponent());
            app.setIcon("img/mallet.png");
            app.setDisplayMode(1000,600,false);
            app.setVSync(false);
            app.setTargetFrameRate(120);
            app.setShowFPS(false);
            app.start();
        } 
        catch (SlickException e) 
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void keyPressed(int key, char c)
    {
    	CURRENT_SCENE.key(key);
    }
    public void keyReleased(int key, char c)
    {
    	CURRENT_SCENE.release(key);
    }
    public void mousePressed(int button, int x, int y)
    {
    	if(button == Input.MOUSE_LEFT_BUTTON)	
    		CURRENT_SCENE.click(x, y);
    	else if(button == Input.MOUSE_RIGHT_BUTTON)
    		CURRENT_SCENE.rightClick(x, y);
    }
    public void mouseWheelMoved(int change)
    {
    	CURRENT_SCENE.scroll(change);
   // 	System.out.println("mouse scrolled: " + change);
    }
//these methods are necessary to support ScreenObject implementation. they do not do anything
	@Override
	public void click(int mouseX, int mouseY) 
	{
		//handled by child ScreenObjects
	}

	@Override
	public void rightClick(int mouseX, int mouseY) 
	{
		//handled by child ScreenObjects
	}

	@Override
	public void key(int keyCode) 
	{
		//handled by child ScreenObjects
	}

	@Override
	public void release(int keyCode) 
	{
		//handled by child ScreenObjects
	}
	@Override
	public void scroll(int change)
	{
		
	}
	@Override
	public void show(Graphics b)
	{
		//shouldn't do anything
	}

	@Override
	public void update()
	{
		//shouldn't do anything
	}

	@Override
	public void init(ScreenObject holder) 
	{
		//shouldn't do anything
		
	}
	public void enter()
	{
		//how are you entering the base ScreenObject container...
	}
	public void exit()
	{
		//why are you exiting the base ScreenObject container...
	}

}