package com.base.UI;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;

import com.base.engine.GameState;
import com.base.engine.MainComponent;
import com.base.engine.PlaySound;
import com.base.engine.World;

public class MainMenu extends Menu
{
	protected Image back;
	private final int MAIN = 0, OPTIONS = 1, LEVELS = 2;
	private Menu[] menus;
	protected static int musicID = -1;
	
	public MainMenu(int width, int height)
	{
		super(width, height);
		backgroundColor = new Color( 119, 249, 232 );
	}
	@Override
	public void click(int mx, int my)
	{
		current.click(mx, my);
	}
	public void init(ScreenObject holder)
	{
		if(musicID == -1)	
			musicID = MainComponent.sound.loadMusic("sound/Wings.ogg");
		try {
			back = new Image("img/menu.png");	
		} catch (SlickException e) {
			e.printStackTrace();
		}
		if(menus == null)
		{
			menus = new Menu[3];
			menus[MAIN] = new Menu(width, height);
			menus[OPTIONS] = new Menu(width, height);
			menus[LEVELS] = new Menu(width, height);
			//central menu buttons
			Button playButton = new Button(200,300,600,50,"Play");
			playButton.setFunction(Button.CHANGE_SCENE);
			playButton.setTargetScene(this,menus[LEVELS]);
			menus[MAIN].addButton(playButton);
	
			Button options = new Button(200,375,600,50,"Options");
			options.setFunction(Button.CHANGE_SCENE);
			options.setTargetScene(this, menus[OPTIONS]);
			
			menus[MAIN].addButton(options);
			
			//level select menu buttons
			for(int i = 0; i < 6; i++)
			{
				Button b = new Button(200 + 305 * (i / 3),125 + 75 * (i % 3),295,50,"Level " + (i + 1));
				b.setFunction(Button.CHANGE_SCENE);
				b.setTargetScene(holder, new World(width,height, (i + 1)));
				menus[LEVELS].addButton(b);
			}
			Button b = new Button(200,350,600,50,"Test Drive");
			b.setFunction(Button.CHANGE_SCENE);
			b.setTargetScene(holder, new World(width,height, 0));
			menus[LEVELS].addButton(b);
			
			Button cheat = new Button(200,150,600,50,"Unlock All Levels");
			cheat.setFunction(Button.CHANGE_GAME_DATA);
			cheat.setTargetData("Unlocked Levels", 6);
			cheat.setTargetScene(this, menus[MAIN]);
			menus[OPTIONS].addButton(cheat);
			
			Button dialog = new Button(200,225,600,50,"Reset NPC Dialogs");
			dialog.setFunction(Button.CHANGE_GAME_DATA);
			dialog.setTargetData("Reset Dialogs", 0);
			dialog.setTargetScene(this, menus[MAIN]);
			menus[OPTIONS].addButton(dialog);
			
			Button dialog2 = new Button(200,300,600,50,"Turn Off NPC Dialogs");
			dialog2.setFunction(Button.CHANGE_GAME_DATA);
			dialog2.setTargetData("Disable Dialogs", 0);
			dialog2.setTargetScene(this, menus[MAIN]);
			menus[OPTIONS].addButton(dialog2);
			
			Button sword = new Button(200,375,600,50,"Give Me a Weapon!");
			sword.setFunction(Button.CHANGE_GAME_DATA);
			sword.setTargetData("Add Weapon To Slot", 1);
			menus[OPTIONS].addButton(sword);
			
			Button back = new Button(width - 275, height - 75, 200, 50, "Back");
			back.setFunction(Button.CHANGE_SCENE);
			back.setTargetScene(this,menus[MAIN]);
			menus[OPTIONS].addButton(back);
			menus[LEVELS].addButton(back);
			
			menus[MAIN].setTitle("Platform Warrior");
			menus[LEVELS].setTitle("Level Select");
			menus[OPTIONS].setTitle("Options");
			
		}
		ArrayList<Button> b = menus[LEVELS].getButtons();
		for(int i = 0; i < 6; i++ )
		{
			if(i + 1 > MainComponent.state.unlockedLevels())
				b.get(i).deactivate();
			else
				b.get(i).activate();
		}
		for(int i = 0; i < 3; i++)
			menus[i].init(this);
		current = menus[MAIN];
	}
	public void show(Graphics b)
	{
		b.setColor(backgroundColor);
		b.fillRect(0,0,width,height);
		b.drawImage(back, 0, 0, width, height, 0, 0, back.getWidth(), back.getHeight());
		current.show(b);
	}
	@Override
	public void update()
	{
		current.update();
		ArrayList<Button> b = menus[LEVELS].getButtons();
		for(int i = 0; i < 6; i++ )
		{
			if(i + 1 > MainComponent.state.unlockedLevels())
				b.get(i).deactivate();
			else
				b.get(i).activate();
		}
	}
	@Override
	public void enter()
	{
		MainComponent.sound.playMusic(musicID, true);
	}
	@Override
	public void exit()
	{
//		MainComponent.sound.transition(250, 0.3);
	}
}