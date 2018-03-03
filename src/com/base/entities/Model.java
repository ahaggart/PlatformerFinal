package com.base.entities;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.base.engine.MainComponent;
import com.base.engine.PlaySound;
import com.base.engine.Vector2d;
import com.base.engine.dPolygon;
import com.base.items.Holdable;
import com.base.items.Weapon;


public class Model
{
	public static final int IDLE = 0;//universal animation constant
	public static final int WALK_FORWARD = 1, WALK_BACKWARD = 2;//lower body animation constants
	public static final int SWING = 1, THRUST = 2, FALL = 3;//upper body animation constants
	public static final int FRONT_FLIP = 1, BACK_FLIP = 2, JUMP = 3;//full body animation constants
	
	private final int WALK_MAX = 20, WALK_MIN = 6, WALK_SPEED = 10;//walk animation constants
	private final int SWING_MAX = 4, SWING_MIN = 1, SWING_SPEED = 15, REACH_BASE = 12;
	private int REACH = 12;
	
	private double x, y, holdAngle, rotation;
	private int tick, tickleg, left;
	private int frame1, frame2;
	private boolean action, actionleg, actionUpper;
	private Holdable item;
	private Animation helmet, hands, chestplate, legs, head;
		
	private int CURRENT_ACTION = 0;//full body animations
	private int CURRENT_ACTION_UPPER = 0;//upper body animations
	private int CURRENT_ACTION_LOWER = 0;//lower body animations
	
	public Model(double x, double y)
	{
		this.x = x;
		this.y = y;
		left = 1;
		tick = 0;
		holdAngle = 0;
	}
	public void init()
	{
		try {
			//head
			Image headBase = new Image("img/Player_Head.png");
			SpriteSheet headSheet = new SpriteSheet(headBase,40,56,0);
			head = new Animation(headSheet, 100);
			//chestplate
			Image chest = new Image("img/chestplate.png");
			SpriteSheet chestSheet = new SpriteSheet(chest, 40, 56, 0);
			chestplate = new Animation(chestSheet, 100);
			//hands and arms
			Image hand = new Image("img/gauntlets.png");
			SpriteSheet handSheet = new SpriteSheet(hand, 40, 56, 0);
			hands = new Animation(handSheet, 100);
			//helmet
			Image helm = new Image("img/helmet.png");
			SpriteSheet helmSheet = new SpriteSheet(helm, 40, 56, 0);
			helmet = new Animation(helmSheet, 100);
			//legs
			Image leg = new Image("img/legs.png");
			SpriteSheet legSheet = new SpriteSheet(leg, 40, 56, 0);
			legs = new Animation(legSheet, 100);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	public void hold(Holdable item)
	{
		this.item = item;
	}
	public void update(double x, double y)
	{
		this.x = x;
		this.y = y;
		tick();
		build();
	}
	private void tick()
	{
		if(actionUpper || action)	
			tick++;
		else
			tick = 0;
		if(actionleg)	
			tickleg++;
		else
			tickleg = 0;
	}
	public int getTick()
	{
		return tick;
	}
	public void build()
	{
		if(action)
		{
			switch(CURRENT_ACTION)
			{
				case JUMP:
					jumpA();
					action = false;
					break;
				case FRONT_FLIP:
					frontFlip();
					break;
				case BACK_FLIP:
					backFlip();
					break;
				default:
					clearAnimation();
					break;
			}
		}
		else
		{
			if(actionleg)
			{
				switch(CURRENT_ACTION_LOWER)
				{
					case WALK_FORWARD:
						walkForward();
						break;
					case WALK_BACKWARD:
						walkBackward();
						break;
					default:
						clearLowerAnimation();
						break;
				}
			}
			if(actionUpper)
			{
				switch(CURRENT_ACTION_UPPER)
				{
					case FALL:
						fall();
						break;
					case SWING:
						swing();
						break;
					case THRUST:
						stab();
						break;
					default:
						clearUpperAnimation();
						break;
				}
			}
		}	
		if(item != null)
			item.position(x - 8 * left + Math.sin(holdAngle * left) * REACH, y - Math.cos(holdAngle * left) * REACH, holdAngle * left);
	}
	public void show(Graphics b)
	{		
		//TODO: put into an array?
		Image legs = this.legs.getImage(frame2).copy();
		Image head = this.head.getImage(frame1).copy();
		Image chestplate = this.chestplate.getImage(frame1).copy();
		Image helmet = this.helmet.getImage(frame1).copy();
		Image hands = this.hands.getImage(frame1).copy();
		if(left < 0)
		{
			legs = legs.getFlippedCopy(true, false);
			head = head.getFlippedCopy(true, false);
			chestplate = chestplate.getFlippedCopy(true, false);
			helmet = helmet.getFlippedCopy(true, false);
			hands = hands.getFlippedCopy(true, false);		
		}
		legs.setRotation((float)Math.toDegrees(rotation)*left);
		head.setRotation((float)Math.toDegrees(rotation)*left);
		chestplate.setRotation((float)Math.toDegrees(rotation)*left);
		helmet.setRotation((float)Math.toDegrees(rotation)*left);
		hands.setRotation((float)Math.toDegrees(rotation)*left);

		b.drawImage(legs, (float)x - 11 - 10, (float)y - 22 - 10);
		b.drawImage(head,(float)x - 21, (float)y - 32);
		b.drawImage(chestplate, (float)x - 11 - 10, (float)y - 22 - 10);
		b.drawImage(helmet, (float)x - 11 - 10, (float)y - 24 - 10);
		//draw weapon
		if(CURRENT_ACTION_UPPER == SWING || CURRENT_ACTION_UPPER == THRUST)
		{
			if(item != null)	
				item.drawMe(b,left);
	//		item.drawHitbox(b);
		}
		b.drawImage(hands, (float)x - 11 - 10, (float)y - 22 - 10);
		b.setColor( Color.black);
	//	b.drawString("Animation: Full: " + CURRENT_ACTION + " Upper: " + CURRENT_ACTION_UPPER + " Lower: " + CURRENT_ACTION_LOWER, (float)x - 40, (float)y - 80);
	}
	public void cancelAnimation()
	{
		clearAnimation();
	}
	public void flip(double x)
	{
		if(facingForward(x) && CURRENT_ACTION != FRONT_FLIP)
		{		
			clearAnimation();
			action = true;
			CURRENT_ACTION = FRONT_FLIP;
		}
		else if(!facingForward(x) && CURRENT_ACTION != BACK_FLIP)
		{		
			clearAnimation();
			action = true;
			CURRENT_ACTION = BACK_FLIP;
		}
	}
	public void land()
	{
		if(CURRENT_ACTION_UPPER == FALL)
		{
			clearAnimation();
		}
	}
	public void walk(double x)
	{
		
		if(facingForward(x) && CURRENT_ACTION_LOWER != WALK_FORWARD)
		{
			clearLowerAnimation();
			tickleg = WALK_MIN * WALK_SPEED;
			CURRENT_ACTION_LOWER = WALK_FORWARD;
			actionleg = true;
		}
		else if(!facingForward(x) && CURRENT_ACTION_LOWER != WALK_BACKWARD)
		{
			clearLowerAnimation();
			tickleg = WALK_MIN * WALK_SPEED;
			CURRENT_ACTION_LOWER = WALK_BACKWARD;
			actionleg = true;
		}
	}
	public void jump()
	{
		clearAnimation();
		action = true;
		CURRENT_ACTION = JUMP;

	}
	public void face(boolean left)
	{
		if(CURRENT_ACTION_LOWER == 0 || CURRENT_ACTION_LOWER == 1 || CURRENT_ACTION_LOWER == 2 )
		{	
			if(left)
				this.left = -1;
			else
				this.left = 1;
		}
	}
	public void strike(double x, double y)
	{
		if(CURRENT_ACTION_UPPER != SWING)
		{
			item.swing(SWING);
			clearUpperAnimation();
			actionUpper = true;
			CURRENT_ACTION_UPPER = SWING;
			tick = SWING_MIN;
		}
	}
	public void thrust(double x, double y)
	{
		if(CURRENT_ACTION_UPPER != THRUST)
		{
			clearUpperAnimation();
			item.swing(THRUST);
			actionUpper = true;
			CURRENT_ACTION_UPPER = THRUST;
		}
	}
	public void look(int x, int y)
	{

	}
	public int getAction()
	{
		if(action)
			return CURRENT_ACTION;
		return 0;
	}
/** ANIMATIONS FOR MODEL */
	private void clearAnimation()
	{
		rotation = 0;
		CURRENT_ACTION = IDLE;
		action = false;
		clearUpperAnimation();
		clearLowerAnimation();
	}
	public void clearUpperAnimation()
	{
		tick = 0;
		frame1 = 0;
		holdAngle = 0;
		REACH = REACH_BASE;
		CURRENT_ACTION_UPPER = IDLE;
		actionUpper = false;
	}
	public void clearLowerAnimation()
	{
		tickleg = 0;
		frame2 = 0;
		actionleg = false;
		CURRENT_ACTION_LOWER = IDLE;
	}
	public boolean facingForward(double x)
	{
		if(x > 0)
		{
			if(left < 0)
				return false;
			else
				return true;
		}
		else
		{
			if(left > 0)
				return false;
			else
				return true;
		}
	}
	private void walkForward()
	{
		if(tickleg >= (WALK_MAX) * WALK_SPEED)
			clearLowerAnimation();
		else
			frame1 = frame2 = tickleg / WALK_SPEED;
	
	}
	private void walkBackward()
	{
		if(tickleg >= (WALK_MAX) * WALK_SPEED)
			clearLowerAnimation();
		else
			frame1 = frame2 = WALK_MAX - 1 - tickleg / WALK_SPEED + WALK_MIN;
	}
	private void frontFlip()
	{
		frame1 = frame2 = 5;
		holdAngle = Math.PI/24 * (tick-1);
		rotation = Math.PI/24 * (tick-1);
		if(tick > 48)
		{
			clearAnimation();
			fall();
		}
	}
	private void backFlip()
	{
		frame1 = frame2 = 5;
		holdAngle = -Math.PI/24 * (tick-1);
		rotation = -Math.PI/24 * (tick-1);
		if(tick > 48)
		{
			clearAnimation();
			fall();
		}
	}
	private void jumpA()
	{
		fall();
	}
	private void fall()
	{
		actionUpper = true;
		CURRENT_ACTION_UPPER = FALL;
		CURRENT_ACTION_LOWER = FALL;
		frame1 = 5;
		frame2 = 5;
	}
	private void swing()
	{	
		if(tick > SWING_MAX * SWING_SPEED )
		{
			clearUpperAnimation();
			item.end();
		}
		else
		{
			holdAngle = Math.PI / 2 * tick / (2.5 * SWING_SPEED) - Math.PI / 8;
			frame1 = tick / SWING_SPEED;
		}
	}
	private void stab()
	{
		holdAngle = Math.PI / 2;
		frame1 = 3;
		if(item.retract(tick))
		{
			clearUpperAnimation();
			item.end();
		}
	}
}



