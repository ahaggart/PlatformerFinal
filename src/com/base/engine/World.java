package com.base.engine;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;

import org.newdawn.slick.tiled.TiledMap;

import com.base.engine.GameState;
import com.base.UI.PauseMenu;
import com.base.UI.HUD;
import com.base.UI.ScreenObject;
import com.base.entities.*;
import com.base.items.Item;


public class World implements ScreenObject
{
	protected Player player;
	protected Camera camera;
	protected Objective finish;
	protected int width, height, level;
	protected ArrayList<Entity> entities = new ArrayList<Entity>();
	protected ArrayList<Integer> lastPressed = new ArrayList<Integer>();
	protected ArrayList<Integer> currentPressed = new ArrayList<Integer>();	
	protected String collision = "";
	protected dPolygon square;
	protected Image background1, background2;
	protected TiledMap map;
	protected boolean paused, showQuadtree = false;
	protected static boolean[] played;
	protected Color back = new Color(92, 185, 203);
	//Collision Detection
	protected CollisionDetection collisionManager;
	//tile map arrays
	protected Image mapImage;
	//UI
	protected ScreenObject pause, CURRENT_SCENE;
	protected HUD HUD;
	//music
	protected static int[] musicID;
	
	public World(int width, int height, int level)//screen dimensions
	{
		//TODO: add more map backgrounds
		this.width = width;
		this.height = height;
		this.level = level;
		camera = new Camera(0,0, width, height);
		if(played == null)
			played = new boolean[7];
		if(musicID == null)
		{
			musicID = new int[7];
			for(int i = 0; i < musicID.length; i++)
				musicID[i] = -1;
		}
	}
	public void init(ScreenObject holder)
	{

		if(musicID[level] == -1)
			musicID[level] = MainComponent.sound.loadMusic("sound/level" + (level) + ".ogg");
		//TODO: complete background image set
		try {
			map = new TiledMap("map/map" + level + ".tmx",false);
			mapImage = new Image("map/map" + level + ".png");
			background1 = new Image("img/backgrounds/back" + level + "_1.png");
			background2 = new Image("img/backgrounds/back" + level + "_2.png");

		} catch (SlickException e) {
	//		e.printStackTrace();
		}
		Paladin p = null;
		collisionManager = new CollisionDetection();
		
		int objectGroupCount = map.getObjectGroupCount();
		for( int gi = 0; gi < objectGroupCount; gi++ ) // gi = object group index
		{
		    int objectCount = map.getObjectCount(gi);
		    for( int oi = 0; oi < objectCount; oi++ ) // oi = object index
		    {
		    	String type = map.getObjectProperty(gi, oi, "Type", "tile");
	//	    	System.out.println(type);
		    	if(type.equals("Player"))
		    	{
		    		player = new Player(map.getObjectX(gi,oi), map.getObjectY(gi,oi));
		    		entities.add(0, player);
		    		player.setInventory(MainComponent.state.getInventory());
		    	}
		    	else if(type.equals("Objective"))
		    	{
		    		Objective o = new Objective(map.getObjectX(gi, oi),map.getObjectY(gi, oi),26,44, level);
		    		if(map.getObjectProperty(gi, oi, "Check", "no").equals("final"))
		    		{
		    			o.setFunction(Objective.UNLOCK);
		    			finish = o;
		    		}
		    		entities.add(o);
		    	
		    	}
		    	else if(type.equals("Item"))
		    	{
		    		Item o = new Item(map.getObjectX(gi, oi),map.getObjectY(gi, oi));
		    		o.insert(MainComponent.state.getContents(map.getObjectProperty(gi, oi, "Contained", "")),Integer.parseInt(map.getObjectProperty(gi, oi, "Quantity", "1")));
		    		entities.add(o);
		    	
		    	}
		    	else if(type.equals("Zombie"))
		    	{
		    		entities.add(new Zombie(map.getObjectX(gi,oi), map.getObjectY(gi,oi)));
		    	}
		    	else if(type.equals("Paladin"))
		    	{
		    		p = new Paladin(map.getObjectX(gi,oi), map.getObjectY(gi,oi), player);
		    		entities.add(p);
		    	}
		    	else if(type.equals("Skeleton"))
		    	{
		    		entities.add(new Skeleton(map.getObjectX(gi,oi), map.getObjectY(gi,oi)));
		    	}
		    	else if(type.equals("Pumpkin"))
		    	{
		    		entities.add(new Pumpkin(map.getObjectX(gi,oi), map.getObjectY(gi,oi)));
		    	}
		    	else
		    		entities.add(new Tile(map.getObjectX(gi, oi),map.getObjectY(gi, oi),map.getObjectWidth(gi, oi),map.getObjectHeight(gi, oi), false));
		    }
		}
		
		if(p != null)
    		p.bind(finish);
		for(int i = 0; i < entities.size(); i++)
			entities.get(i).init();
		//testing quadtree collision detection
		ArrayList<Entity> temp = new ArrayList<Entity>();
		for(int i = 0; i < entities.size(); i++)
			temp.add(entities.get(i));
		//get width the program
	//	collisionManager.createQuadtree(temp, map.getWidth() * map.getTileWidth(), map.getHeight()*map.getTileHeight());
		pause = new PauseMenu(1000,600);
		pause.init(holder);
		HUD = new HUD(1000,600, player);
		HUD.init(holder);
		if(!played[level])
		{
			HUD.createDialog(MainComponent.state.getDialog(level), "img/Merchant.png");
			played[level] = true;
		}
		CURRENT_SCENE = HUD;
	}
	public void click(int mouseX, int mouseY)
	{
		
		CURRENT_SCENE.click(mouseX, mouseY);
		if(player.isMarked())
			HUD.click(mouseX, mouseY);
		else
			player.click(camera.getRealX(mouseX), camera.getRealY(mouseY));
	}
	public void rightClick(int mouseX, int mouseY)
	{
		player.rightClick(camera.getRealX(mouseX), camera.getRealY(mouseY));
	}
	public void key(int keyCode)
	{
		if(keyCode == Input.KEY_ESCAPE)
		{
			if(paused)
			{
				paused = false;
				changeScene(HUD);
			}
			else
			{
				paused = true;
				changeScene(pause);
			}
		}
		else if(keyCode == Input.KEY_1)
		{
			player.selectSlot(0);
		}
		else if(keyCode == Input.KEY_2)
		{
			player.selectSlot(1);
		}
		else if(keyCode == Input.KEY_3)
		{
			player.selectSlot(2);
		}		
		else if(keyCode == Input.KEY_4)
		{
			player.selectSlot(3);
		}
		else if(keyCode == Input.KEY_5)
		{
			player.selectSlot(4);
		}
		else if(keyCode == Input.KEY_6)
		{
			player.selectSlot(5);
		}
		else if(!paused)
		{
			currentPressed.add(keyCode);
			lastPressed.add(keyCode);
		}
	}
	public void release(int keyCode)
	{
		currentPressed.remove((Integer)keyCode);
	}
	public void scroll(int change)
	{
		player.cycleEquipment(change);
	}
	private void runKeys()
	{
		for(int i = 0; i < currentPressed.size();i++)
		{
			switch(currentPressed.get(i))
			{
				case Input.KEY_SPACE :
					pressSpace();
					break;
				case Input.KEY_A :
					pressA();
					break;
				case Input.KEY_D :
					pressD();
					break;	
				case Input.KEY_F :
					pressF();
					break;
				case Input.KEY_Q : 
					pressQ();
					break;
				case Input.KEY_R :
					pressR();
					break;
				case Input.KEY_S :
					pressS();
					break;
				case Input.KEY_W :
					pressW();
					break;
				case Input.KEY_ENTER :
					pressEnter();
					break;
				default :
					break;
			}			
		}
		for(int i = 0; i < lastPressed.size(); i++)
			if(!currentPressed.contains(lastPressed.get(i)))
				currentPressed.add(lastPressed.get(i));
		lastPressed.clear();

	}
	public void show(Graphics b)
	{
		//draw background (parallax scrolling?)
		b.setColor( back );
		if(level == 3 || level == 4)
			b.setColor(Color.black);
		b.fillRect(0, 0, width, height);
		b.drawImage(background1, 0, 120, 0, 0, background1.getWidth(), background1.getHeight());
		b.drawImage(background2, 0, 220, 0, 0, background2.getWidth(), background2.getHeight());
		//draw objects in world (to be translated)
		camera.translate(b);
		if(showQuadtree)
		{
			Color[] color = {Color.green, Color.red, Color.blue, Color.yellow};
			ArrayList<Tile> tree = collisionManager.getTree();
			for(int i = 0; i < tree.size(); i++)
			{
				if(collisionManager.projectionCollisionResponse(tree.get(i).getHitbox().get(), player.getHitbox().get()) != null)
					tree.get(i).drawMe(b, Color.gray, "" + i);
				else
					tree.get(i).drawMe(b, color[i%4], "" + i);
			}
			ArrayList<Tile[]> branches = collisionManager.getBranches();
			for(int i = 0; i < branches.size(); i++)
			{
				for(int j = 0; j < branches.get(i).length; j++)
					branches.get(i)[j].drawMe(b);
			}
		}
		for(int i = 0; i < entities.size(); i++)
			entities.get(i).drawMe(b);
	//	map.render(0, 0, 0, 0, 1000, 600, false);
		b.drawImage(mapImage, 0, 0);
		b.resetTransform();
		//draw objects over world (HUD)
		b.setColor(Color.red);
		CURRENT_SCENE.show(b);
//		b.drawString("Player coordinates: ( " + player.getX() + ", " + player.getY() + " )" + "    Player velocity: " + player.getVelocity(), 50, 100 );
//		b.drawString("Mouse: (" + camera.getRealX(MainComponent.input.getMouseX()) + "," + camera.getRealY(MainComponent.input.getMouseY()) + ")", MainComponent.input.getMouseX(), MainComponent.input.getMouseY());
		b.setColor(Color.black);
		if(showQuadtree)
		{
			String str = "Tree size: " + collisionManager.getTree().size() + "     Branch Size: " + collisionManager.getBranches().size();
			b.drawString(str, 50, 300);
		}
	}
	public void resetPlayer()
	{
		entities.remove(player);
		player.respawn();
		entities.add(0,player);
	}
	public void clean()
	{
		for(int i = entities.size() - 1; i >= 0; i--)
		{
			if(entities.get(i).isMarked() || !inBounds(entities.get(i)))
			{
				entities.remove(i);
			}
		}
	}
	private boolean inBounds(Entity entity)
	{
		double x = entity.getX();
		double y = entity.getY();
		if(x < 0 || x > map.getWidth() * map.getTileWidth() || y > map.getHeight() * map.getTileHeight())
		{
			entity.mark();
			return false;
		}
		return true;
	}
	public void update()
	{
		CURRENT_SCENE.update();
		if(player.isMarked())
			if(HUD.shouldRespawn())
				resetPlayer();
		if(!paused)
		{
			clean();
			runKeys();
			for(int i = 0; i < entities.size(); i++)
				entities.get(i).update();
			ArrayList<dPolygon> temp = new ArrayList<dPolygon>();//must be bound to Entities
			for(int i = 0 ; i < entities.size(); i++)
			{
				temp.add(entities.get(i).getHitbox());
				ArrayList<dPolygon> newObjs = entities.get(i).getObjects();
				if(newObjs != null)
					for(int j = 0; j < newObjs.size(); j++)
						temp.add(newObjs.get(j));
			}
			for(int i = 0; i < temp.size(); i++)
				temp.get(i).getAttached().setID(i);
			collisionManager.runCollisionDetection(temp);
			//set camera to player
			camera.setLocation(player.getX(), player.getY());
		}
	}
	public void exit()
	{
		if(finish != null)
			if(finish.isComplete())
				MainComponent.state.bindInventory(player.getInventory());
	}
	public void enter()
	{
		MainComponent.sound.playMusic(musicID[level], true);
	}
//keys
	public void pressSpace()
	{
		player.addActionToQueue(Player.BLOCK);
	}
	public void pressR()
	{
		player.warp();
	}
	public void pressW()
	{
		if(lastPressed.contains((Integer)Input.KEY_W))//check if key was just pressed(not held)
		{	
			player.addActionToQueue(Player.JUMP);
		//	System.out.println("jump!");
		}
	}
	public void pressS()
	{
		player.pickup(); 
	}
	public void pressQ()
	{
	}
	public void pressA()
	{
		player.addActionToQueue(Player.MOVE_LEFT);
	}
	public void pressD()
	{
		player.addActionToQueue(Player.MOVE_RIGHT);
	}
	public void pressF()
	{
		player.drop();
	}
	public void pressEnter()
	{
	}
//end keys
	public void changeScene(ScreenObject change)
	{
		CURRENT_SCENE = change;
	}
	public void addEntity(Entity e)//for adding more permanent/independent entities
	{
		//temporary or highly dependent entities should be added through the getObjects() method of the Entity class
		//getObjects() allows for collision detection on a frame-by-frame basis as determined by the owner
		entities.add(e);
	}
}









