package com.base.engine;

import com.base.items.*;

public class Util//use for random game stuff (memory loading and such)
{
	public static Item[] deriveInventory(String[] save)
	{
		Item[] v = new Item[save.length];
		for(int i = 0; i < save.length; i++)
		{
			int length = save[i].length();
			String str = save[i].substring(0,length - 2);
			String num = save[i].substring(length - 2, length);
			int q = Integer.parseInt(num);
			Item n = new Item(0,0);
			n.init();
			switch(str)
			{
				case "weapon" :
					n.insert(new Weapon(),q);
					break;
				case "megasword" :
					n.insert(new MegaSword(),q);
					break;
				case "rageblade" : 
					n.insert(new RageBlade(), q);
					break;
				case "bow" :
					n.insert(new Bow(),q);
					break;
				case "healthpotion" :
					n.insert(new Potion("health"),q);
					break;
				default :
					n = null;
					break;
			}
			if(n != null)
				n.mark();
			v[i] = n;
		}
		return v;
	}
}