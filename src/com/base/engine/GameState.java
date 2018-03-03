package com.base.engine;

import java.util.ArrayList;

import com.base.items.*;

public class GameState 
{
	private float effectVolume = 0.5f, musicVolume;
	private int unlocked = 1;
	private Item[] inventory;
	private String[] perm;
	
	public GameState()
	{
		inventory = new Item[6];
		String[] test = {"00","00","00","00","00","00"};
		perm = test;
		inventory = Util.deriveInventory(test);
	}
	public Item[] getInventory()
	{
		//TODO: create a duplicate inventory
		Item[] copy = new Item[inventory.length];
		for(int i = 0; i < inventory.length; i++)
			if(inventory[i] != null)
				copy[i] = inventory[i].copy();
		return copy;
	}
	public void bindInventory(Item[] inventory)
	{
		this.inventory = inventory;
	}
	public ArrayList<String> getDialog(int level)
	{
		ArrayList<String> d = new ArrayList<String>();
		switch(level)
		{
			case 1:
				d.add("Hello!");
				d.add("You look confused. Do you need help? Click your \"mouse\" on this box to move through my dialog.");
				d.add("I will be your GUIDE. Let me help you get yourself oriented.");
				d.add("Try pushing the \"a\" key. You should move left.");
				d.add("Now try pushing the \"d\" key. You should move right.");
				d.add("Alright, now lets try the \"w\" key.");
				d.add("Ooh, I liked that one. Push it again while you're in the air this time.");
				d.add("Sick! DOUBLE JUMP!");
				d.add("Now try pushing the \"SPACE\" key. I don't actually know what will happen.");
				d.add("An energy shield. I've only someone powerful enough to do that once. I do know a bit about the things though. While you are shielded, the shield will absorb all damage taken. Be careful, though, because the shield can break, leaving you vulnerable.");
				d.add("Your shield will slowly shrink while you use it. Don't worry, your shield recharges over time, but it will recharge much more slowly if it breaks before you stop using it.");
				d.add("You are much more powerful than I thought. Let's put your prowess to the test. Go over to that bag over there.");
				d.add("Now try pushing the \"s\" key to pick up the mallet inside the bag. If you don't want it, you can push the \"f\" key to toss it back on the ground.");
				d.add("Okay, now go over to one of those pumpkins and smash it with the mallet by clicking.");
				d.add("Very good.");
				d.add("I know someone who could use a sturdy fellow like you. He's got a real problem on his hands.");
				d.add("The King of the city across the mountain range is being held hostage. An unbeatable champion has taken over the throne and is terrorizing the King's subjects in the city. No warrior that has faced him has been able to defeat this rogue knight, but we must defeat him or the fate of the kingdom is at risk.");
				d.add("I would go help them myself. I mean, I used to be an adventurer like you...but then I took an arrow to my knee.");
				d.add("I implore to journey to the city and save the King and his people. I will provide you with a weapon if you so desire it. It is in that bag past the pumpkins I had you smash.");
				d.add("Be careful, the way to the city is treacherous, and you will likely encounter a few unsavory beasts along the way. Your first obstacle will be the mountain range. You must climb the mountain until you reach the nearest gap between the peaks.");
				d.add("Next, you will have to venture into the caves. That labyrinth of stone has swallowed many adventurers and explorers alike.");
				d.add("From there, it is a pretty straightforward trek across the plains, until you reach the city.");
				d.add("Once you are at the city wall, declare your challenge to the champion. He will emerge to battle you.");
				d.add("I wish you the best of luck. The fate of the kingdom may rest on your shoulders, warrior.");
				d.add("Oh, and, by the way, if you see a floating red box, you want to jump into that. That allows you to unlock the next part of your adventure.");
				break;
			case 2:
				d.add("This climb is going to be harder than I thought. The place is absolutely crawling with skeletons. I hate those little buggers.");
				d.add("Skeletons specialize in long-range combat. They will try to snipe you with arrows, and they are quite accurate. If you can get up close, you can probably beat them in a fight.");
				d.add("I would have given you a bow if I had one. Hopefully you'll find one out there on the mountain somewhere.");
				d.add("If you do happen to find one, you can use it by \"right clicking.\" The bow will gain range and damage as you hold the button, and will fire an arrow when you release the button. You can also stab with your sword by \"right clicking.\"");
				d.add("Similar to the red boxes, the blue boxes are important for you to touch. They allow you to reappear there instantly should you die or press \"r\".");
				break;
			case 3:
				d.add("Ahh, the great caverns. No one has yet explored the deepest reaches of this vast tunnel network, and for good reason.");
				d.add("Beware of the zombies wandering around down here. They can be quite menacing, but keep in mind they are pretty dumb.");
				d.add("Really dumb, actually. I wouldn't be surprised if you found them walking into walls or not moving at all...");
				d.add("Also watch out for the Spider Queen. I hear she is lurking around in these parts. You'd best try to avoid running into her.");
				d.add("Happy travels!");
				break;
			case 4:
				d.add("Meh. More caves. These caverns look pretty peaceful, at least, compared to those last ones.");
				d.add("Be prepared for some intense climbing, though.");
				d.add("You should emerge from the caves right near the plains outside the city. It shouldn't be too hard to find your way there once you get out.");
				break;
			case 5:
				d.add("This gathering of undead is quite strange. I'm beginning to think there might be more to this rogue knight that's taken over the city. Oh well.");
				d.add("I can see the city walls off in the distance. Our final destination is very close, my friend!");
				d.add("Just, slog through this mass of zombies and skeletons first. Nothing you can't handle.");
				d.add("Oh, and before I go, I should tell you more about the rogue knight. I cannot accompany you into the city.");
				d.add("Rumor has it he wields three weapons: a hammer, an axe, and a sword.");
				d.add("The hammer is his favorite weapon. It doesn't hurt as much as his other weapons, but it does more damage than your mallet, if you still have that.");
				d.add("If he doesn't think he can beat you with the hammer, he'll probably pull out his axe. You need to be careful if he does that.");
				d.add("The axe has a lot more reach than the hammer, and a lot more reach than your weapons as well. However, he can't hit you if you are too close to him. Try to use that to your advantage.");
				d.add("The sword is the most powerful of his three weapons. He prefers not to use it if he can avoid doing so. It is said that he aquired a sword of such power from a king that he deposed in the years before today.");
				d.add("His three weapons are not what makes him truly dangerous though. He can send out waves of energy if you enrage him, knocking you back and dealing moderate damage.");
				d.add("He also wields a harpoon which he very much enjoys empaling his foes with. Don't get hit by it or he will pull you into range and cut you down with his other weapons.");
				d.add("I have faith in you, warrior. Bring back light to this dark time.");
				break;
			default:
				break;
		}
		return d;
	}
	public Holdable getContents(String data)
	{
		Holdable h;
		switch(data)
		{
			case "sword":
				h = new Weapon();
				break;
			case "mallet":
				Weapon w = new Weapon();
				w.setImage("img/mallet.png",0,0,32,32);
				int[] handle1 = {4,4};
				w.setHandle(handle1);
				w.setWidth(16);
				w.setHeight(10);
				w.setOffset(28);
				w.setDamage(1);
				w.setSpecialDamage(0);
				w.setKnockback(new Vector2d(0,0));
				h = w;
				break;
			case "bow":
				h = new Bow();
				break;
			case "rageblade":
				h = new RageBlade();
				break;
			case "broadsword":
				Weapon b = new Weapon();
				b.setImage("img/broadsword.png",0,0,48,48);
				int[] handle2 = {9,9};
				b.setHandle(handle2);
				b.setWidth(12);
				b.setHeight(51);
				b.setOffset(4);
				b.setDamage(75);
				b.setSpecialDamage(40);
				b.setKnockback(new Vector2d(0,-3.0));
				h = b;
				break;
			default:
				h = new Potion("health");
				break;
		}
		
		return h;
	}
	public float getMusicVolume() 
	{
		return musicVolume;
	}

	private void setMusicVolume(float musicVolume) 
	{
		this.musicVolume = musicVolume;
	}

	public float getEffectVolume() 
	{
		return effectVolume;
	}

	private void setEffectVolume(float effectVolume) 
	{
		this.effectVolume = effectVolume;
	}
	public int unlockedLevels()
	{
		return unlocked;
	}
	public void changeData(String name, float arg)
	{
		if(name.equals("Effect Volume"))
			setEffectVolume(arg);
		else if(name.equals("Music Volume"))
			setMusicVolume(arg);
		else if(name.equals("Unlocked Levels"))
			unlocked = (int)arg;
		else if(name.equals("Reset Dialogs"))
			World.played = new boolean[World.played.length];
		else if(name.equals("Disable Dialogs"))
		{
			boolean[] reset = new boolean[World.played.length];
			for(int i = 0; i < reset.length; i++)
				reset[i] = true;
			World.played = reset;
		}
		else if(name.equals("Add Weapon To Slot"))
		{
			Holdable h = new RageBlade();
			int slot = (int)arg % 6;
			inventory[slot] = new Item(0,0);
			inventory[slot].insert(h,1);;
		}
	}
	
}
