package com.base.UI;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.Font;

import com.base.engine.MainComponent;
import com.base.entities.Player;
import com.base.items.Item;

public class HUD implements ScreenObject 
{
	private int width, height;
	private Player player;
	private GradientFill gradHealth, gradBorder, gradPower;
	private Color itemBack, selectedItemBack;
	private Menu death;
	private boolean dead, shouldRespawn, dialog = true, printing = true;
	private Image avatar;
	private ArrayList<String> text;
	private int shown, hide, dialogPosition, MAX_CHARACTERS_ACROSS, MAX_LINES, tick;
	private Button respawn;
	private ScreenObject world;
	
	public HUD(int width, int height, Player player)
	{
		this.width = width;
		this.height = height;
		this.player = player;
		
		shown = 0;
		hide = 0;
		dialogPosition = 0;
		tick = 0;
		text = new ArrayList<String>();
		
		death = new Menu(width, height);
		death.setTitle("You died!");
		death.setColor(new Color(200,0,0,150));
		
		respawn = new Button(200, 300, 600, 50, "Respawn!");
		
		itemBack = new Color(24,184,201,200);
		selectedItemBack = new Color(247,244,40,200);
		
		gradHealth = new GradientFill(500, 15, Color.transparent, 500, 115, Color.black);
		gradBorder = new GradientFill(500, 15, Color.gray, 500, 135, Color.black);
		gradPower = new GradientFill(500, 70, Color.transparent, 500, 100, Color.black);

	}
	@Override
	public void init(ScreenObject holder) 
	{
		world = holder;
		
		death.init(this);
		
		Button back = new Button(width - 275, height - 75, 200, 50, "Back to Menu");
		back.setFunction(Button.CHANGE_SCENE);
		MainMenu main = new MainMenu(width,height);
		main.init(holder);
		back.setTargetScene(holder, main);
		
		death.addButton(back);
		
		try {
			avatar = new Image("img/default.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	public void bind(Player player)
	{
		this.player = player;
	}
	@Override
	public void click(int mouseX, int mouseY) 
	{
		if(dead)
		{
			death.click(mouseX, mouseY);
			if(respawn.checkClick(mouseX,mouseY))
			{
				shouldRespawn = true;
			}
		}
		else if(dialog)
		{
			if(!printing && mouseY > height - 150)
			{
				if(dialogPosition < text.size() - 1)
					advanceDialog();
				else
					dialog = false;
			}
		}
	}

	@Override
	public void rightClick(int mouseX, int mouseY) 
	{
		//does not take input
	}

	@Override
	public void key(int keyCode) 
	{
		//does not take input
	}

	@Override
	public void release(int keyCode) 
	{
		//does not take input
	}
	@Override
	public void scroll(int change) 
	{
		//does not take input
	}
	@Override
	public void show(Graphics b) 
	{
		
		if(!dead)
		{
			b.setColor(Color.black);
			b.drawString("FPS: " + MainComponent.app.getFPS(), 15, 15);
			b.drawString("Lives: " + player.getLives(), width - 100, 15);
			//health bar
			b.setColor(Color.red);
			b.fillRoundRect(200, 15, (int)(600 * player.getHealth() / player.getMaxHealth()), 50, 2);
			b.fill(new Rectangle(200, 15, (int)(600 * player.getHealth() / player.getMaxHealth()), 50), gradHealth );
			b.setColor(Color.black);
			b.drawRoundRect(200, 15, 600, 50, 2);
			String str = "Health";
			b.drawString(str, 470, 35);
			//power bar
			b.setColor(Color.blue);
			if(player.getHeldItem() != null)
			{
				b.fillRoundRect(200, 70, (int)(600 * player.getHeldItem().getPower() ), 15, 2);
				b.fill(new Rectangle(200, 70, (int)(600 * player.getHeldItem().getPower() ), 15), gradPower);
			}
			
			b.setColor(Color.black);
			b.drawRoundRect(200, 70, 600, 15, 2);
			for(int i = 0; i < 5; i++)
				b.drawRoundRect(200 + 120 * i, 70, 120, 15, 2);
			b.drawString("Power", 475, 70);
	
			//border
			b.fill(new Rectangle(190,10,20,80), gradBorder);
			b.fill(new Rectangle(790,10,20,80), gradBorder);
			
			//item sidebar
			Item[] inventory =  player.getInventory();
			for(int i = 0; i < inventory.length; i++)
			{
				b.setColor(itemBack);
				if(i == player.getSelected())
					b.setColor(selectedItemBack);
				b.fillRoundRect(15, 140 + 65 * i, 60, 60, 5);
				if(inventory[i] != null)
				{
					Image img = inventory[i].getItemImage();
					if(img != null)
					{
						if(img.getWidth() > 50 || img.getHeight() > 50)
							b.drawImage(img, 20, 145 + i * 65, 70, 195 + i * 65, 0, 0, img.getWidth(), img.getHeight());
						else
							b.drawImage(img, 20 + 25 - img.getWidth() / 2, 145 + i * 65 + 25 - img.getHeight() / 2, 0, 0, img.getWidth(), img.getHeight());
					}	
					b.setColor(Color.black);
					if(inventory[i].get().stackable())
					b.drawString("" + inventory[i].getQuantity(), 60, 180 + i * 65);
				}
	
			}
			
			//item interaction
			ArrayList<Item> interact = player.getAccessible();
			for(int i = 0; i < interact.size(); i++)
			{
				Image img = interact.get(i).getItemImage();
				b.setColor(itemBack);
				float x = (float)(MainComponent.app.getWidth()/2 + interact.get(i).getX() - player.getX());
				float y = (float)(MainComponent.app.getHeight()/2 + interact.get(i).getY() - player.getY());
				if(img != null)
				{
	
					b.fillRoundRect(x, y, img.getWidth()+10, img.getHeight()+10, 5);
					b.drawImage(img, x + 5,y + 5);
					b.setColor(Color.black);
					b.drawString("" + interact.get(i).getQuantity(), x + img.getWidth(), y + img.getHeight() - 5);
				}	
				else
				{
					b.fillRoundRect(x, y, 30, 30, 5);
					b.setColor(Color.black);
					b.drawString("" + interact.get(i).getQuantity(), x + 20, y + 15);
				}
	
			}
			//NPC dialog
			if(dialog)
			{
				if(text.size() < 1)
				{
					advanceDialog();
					dialog = false;
					printing = false;
				}
				else
				{
					int top = height - 150;
					b.setColor(Color.white);
					b.fillRoundRect(0, top, width, 150, 10);
					b.setColor(Color.black);
					b.drawRoundRect(0, top, width, 150, 10);
				//	b.drawRect(20, top + 60, 30, 30);
					b.drawImage(avatar, 20 + (int)((30 - avatar.getWidth())/2), top + 60 + (int)((30 - avatar.getHeight())/2));
					Font current = b.getFont();
					MAX_CHARACTERS_ACROSS = (width - 90) / current.getWidth("a");//~91
					MAX_LINES = 110 / (current.getLineHeight() + 5);//5
			//		System.out.println("max characters across: " + MAX_CHARACTERS_ACROSS + " max lines: " + MAX_LINES);
					
					ArrayList<String> lines = new ArrayList<String>();
					for(int i = 0; i < text.get(dialogPosition).length() / MAX_CHARACTERS_ACROSS + 1; i++)
					{
						if(text.get(dialogPosition).length() > i * MAX_CHARACTERS_ACROSS + MAX_CHARACTERS_ACROSS)
						{
							String line = text.get(dialogPosition).substring(i*MAX_CHARACTERS_ACROSS,i*MAX_CHARACTERS_ACROSS + MAX_CHARACTERS_ACROSS);
							int end =  i*MAX_CHARACTERS_ACROSS + MAX_CHARACTERS_ACROSS;
							if(text.get(dialogPosition).charAt(end) != ' ' && text.get(dialogPosition).charAt(end) != '.' && text.get(dialogPosition).charAt(end) != '?' && text.get(dialogPosition).charAt(end) != '!')
								if(text.get(dialogPosition).charAt(end-1) != ' ' && text.get(dialogPosition).charAt(end-1) != '.' && text.get(dialogPosition).charAt(end-1) != '?' && text.get(dialogPosition).charAt(end-1) != '!')
									line += "-";
							lines.add(line);
						}
						else if(text.get(dialogPosition).length() > i * MAX_CHARACTERS_ACROSS)
							lines.add(text.get(dialogPosition).substring(i * MAX_CHARACTERS_ACROSS));
					}
					
					for(int j = hide; j < shown; j++)
						b.drawString(lines.get(j), 70, top + 20 + (j-hide) * (current.getLineHeight() + 5));
					String show = "";
					if(printing)
					{
						for(int j = hide; j < tick / 2; j++)
						{
							if(j < lines.get(shown).length())
							{
									show += "" + lines.get(shown).charAt(j);
							}
							else
							{
								tick = 0;
								if(shown < lines.size() - 1)
								{
									shown++;
								}
								else
								{
									shown++;
									printing = false;
								}	
								if(shown-hide > MAX_LINES)
									hide++;
							}
						}
					}
					b.drawString(show, 70, top + 20 + (shown-hide) * (current.getLineHeight() + 5));
				}
			}
		}
		else
		{
			death.drawBackground(b);
			death.show(b);
			respawn.drawMe(b);
		}
	}

	@Override
	public void update() 
	{
		if(player.isMarked())
		{
			dead = true;
		}
		else
			dead = false;
		if(dead)
		{
			if(player.getLives() <= 0)
				respawn.deactivate();
			death.update();
			respawn.update();
		}
		else
			shouldRespawn = false;
		if(printing)
			tick++;
	}
	public boolean shouldRespawn()
	{
		return shouldRespawn;
	}
	@Override
	public void changeScene(ScreenObject change) 
	{
		//no child scenes
	}
	public void enter()
	{
		
	}
	public void exit()
	{
		
	}
	public void createDialog(ArrayList<String> dialog, String avatar)
	{
		this.dialog = true;
		try {
			this.avatar = new Image(avatar);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		text = dialog;
		printing = true;
	}
	private void advanceDialog()
	{
		dialogPosition++;
		hide = 0;
		tick = 0;
		shown = 0;
		printing = true;
	}

}
