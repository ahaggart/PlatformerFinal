package com.base.engine;
import java.util.ArrayList;

import com.base.entities.Entity;
import com.base.entities.Tile;

///reference
/* http://www.metanetsoftware.com/technique/tutorialA.html */
/* http://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169 */
public class CollisionDetection
{
	///TEMPORARY HOME FOR COLLISION PHYSICS VARIABLES---RETRIEVE FROM ENTITY CLASSES IN FUTURE
	public static double BOUNCE = 0.00;//0.3//must be in [0,1], where 1 means full bounce. but 1 seems to incite "the flubber effect" so use 0.9 as a practical upper bound
	public static double GRAV = 0.2;//currently not used o_0//.3 is a bit much, .1 is a bit "on the moon"..
	private static double FRICTION = 0.05;//0.05//probably set near max (0)
	
	
	private static int MAX_OBJECTS = 10;
	private static int MAX_LEVELS = 5;
	
	private ArrayList<Tile> tree;
	private ArrayList<Tile[]> branch;
	
	public void createQuadtree(ArrayList<Entity> e, double width, double height)//passing in the base entities list causes some of the saved tiles to be destroyed
	{
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for(int i = e.size() - 1; i >= 0; i--)
		{
			if(e.get(i) instanceof Tile)
			{
				tiles.add((Tile)e.get(i));
				e.remove(i);
			}
		}
		branch = new ArrayList<Tile[]>();
		tree = createBranch(tiles, 0, 0, width, height, 0);
	}
	private ArrayList<Tile> createBranch(ArrayList<Tile> tiles, double x, double y, double width, double height, int level)
	{
		Tile[] col = new Tile[4];
		double secWidth = width/2;
		double secHeight = height/2;
		col[0] = new Tile(x,y,secWidth, secHeight);
		col[1] = new Tile(x+secWidth,y,secWidth, secHeight);
		col[2] = new Tile(x+secHeight,y+secHeight,secWidth, secHeight);
		col[3] = new Tile(x,y+secHeight,secWidth, secHeight);
		ArrayList<Tile> branches = new ArrayList<Tile>();
		for(int i = 0; i < 4; i++)
		{
			col[i].init();
			ArrayList<Tile> contains = new ArrayList<Tile>();
			for(int j = 0; j < tiles.size(); j++)
				if(projectionCollisionResponse(col[i].getHitbox().get(), tiles.get(j).getHitbox().get()) != null)
					contains.add(tiles.get(j));
			if(contains.size() > MAX_OBJECTS && level <= MAX_LEVELS)
			{
				if(i == 0)	
					branches.addAll(createBranch(contains,x,y,secWidth,secHeight,level + 1));
				else if(i == 1)
					branches.addAll(createBranch(contains,x+secWidth,y,secWidth,secHeight,level + 1));
				else if(i == 2)
					branches.addAll(createBranch(contains,x+secWidth,y+secHeight,secWidth,secHeight,level + 1));
				else
					branches.addAll(createBranch(contains,x,y+secHeight,secWidth,secHeight,level + 1));
			}
			else
			{
				if(i == 0)
					branches.add(new Tile(x,y,secWidth,secHeight));
				else if(i == 1)
					branches.add(new Tile(x+secWidth,y,secWidth,secHeight));
				else if(i == 2)
					branches.add(new Tile(x+secWidth,y+secHeight,secWidth,secHeight));
				else if(i == 3)
					branches.add(new Tile(x,y+secHeight,secWidth,secHeight));
				Tile[] temp = new Tile[contains.size()];
				for(int k = 0; k < temp.length; k++)
					temp[k] = contains.get(k);
				branch.add(temp);
			}
		}
		for(int i = 0; i < branches.size(); i++)
			branches.get(i).init();
		return branches;
	}
	public ArrayList<Tile[]> getBranches()
	{
		return branch;
	}
	public ArrayList<Tile> getTree()
	{
		return tree;
	}
	public void runCollisionDetection(ArrayList<dPolygon> temp)
	{
		//run collision detection algorithm for entity-entity collisions
		for(int j = 0; j < temp.size() - 1; j++)
		{
			for(int i = j+1; i < temp.size(); i++)
			{
				if(!(temp.get(i).getAttached() instanceof Tile && temp.get(j).getAttached() instanceof Tile))//ignore tile-tile collisions
					collide(temp.get(j), temp.get(i));
			}
		}
	}
	public void runQuadtreeCollisionDetection(ArrayList<dPolygon> temp)
	{
		for(int i = 0; i < temp.size(); i++)
		{
			ArrayList<Integer> regions = new ArrayList<Integer>();
			for(int j = 0; j < tree.size(); j++)
			{
				if(projectionCollisionResponse(temp.get(i).get(), tree.get(j).getHitbox().get()) != null)
				{
					regions.add(j);
				}
			}
			for(int j = 0; j < regions.size(); j++)
					collideWithRegion(temp.get(i), regions.get(j));
			
		}
		
		//run collision detection algorithm for entity-entity collisions
		for(int j = 0; j < temp.size() - 1; j++)
		{
			for(int i = j+1; i < temp.size(); i++)
			{
				collide(temp.get(j), temp.get(i));
			}
		}
	}
	private void collideWithRegion(dPolygon a, int treeIndex)
	{
		Tile[] tileset = branch.get(treeIndex);
		for(int i = 0; i < tileset.length; i++)
		{
			collide(a, tileset[i].getHitbox());
		}
	}
	private void collide(dPolygon a, dPolygon b)
	{
		Vector2d[] collisionData = projectionCollisionResponse2( a, b );
		
		if(collisionData != null)
		{
//				collision += "collision detected with shape " + j + " and shape " + i + "    ";
			//create data packets from the collision for each object
			CollisionData packet1 = new CollisionData(collisionData[0].mul(-1),collisionData[1].mul(-1),a,b.getAttached());//hitbox from j
			CollisionData packet2 = new CollisionData(collisionData[0],collisionData[1],b,a.getAttached());//hitbox from i
			//send each packet to its hitbox owner for modifying
			a.getAttached().addCollisionData(packet1);
			b.getAttached().addCollisionData(packet2);				
			//send each packet to the colliding hitbox owner for processing
			b.getAttached().processCollisionData(packet1);				
			a.getAttached().processCollisionData(packet2);					
		}
	}
	public Vector2d[] projectionCollisionResponse(double[][] hitbox1, double[][] hitbox2)
	{	
		//hitbox[number of vertices][x,y]; hitbox1 "collides" with hitbox2; 
		//returns array[2] of vectors with [0] projection vector; [1] surface normal
		//
		int len1 = hitbox1.length;
		int len2 = hitbox2.length;
		Vector2d[] hitbox2_SIDES = new Vector2d[len2];
		//generate vectors for hitbox2 in clockwise direction--store normals of side vector
		Vector2d sMax = new Vector2d(hitbox2[0][0] - hitbox2[len2 - 1][0], hitbox2[0][1] - hitbox2[len2 - 1][1]);
		hitbox2_SIDES[len2 - 1] = sMax.normL();
		
		for(int i = 0; i < len2 - 1; i++)
		{
			Vector2d tan = new Vector2d(hitbox2[i+1][0] - hitbox2[i][0], hitbox2[i+1][1] - hitbox2[i][1]);
			hitbox2_SIDES[i] = tan.normL();
		}
		
		for(int i = 0; i < len2; i++)//normalize hitbox2_SIDE vectors to unit vectors
		{
			hitbox2_SIDES[i].normalize();
		}	
		//sort origin-to-corner vectors by max/min
		Vector2d[] oc1 = new Vector2d[len1];//hitbox1 origin-to-corner vectors
		for(int i = 0; i < len1; i++)
		{
			oc1[i] = new Vector2d( hitbox1[i][0], hitbox1[i][1] );
		}
		
		Vector2d[] oc2 = new Vector2d[len2];//hitbox2 origin-to-corner vectors
		
		for(int i = 0; i < len2; i++)
		{
			oc2[i] = new Vector2d( hitbox2[i][0], hitbox2[i][1] );
		}
		///loop through hitbox2 axis list, look for overlap at each axis
		Vector2d[] proj = new Vector2d[len2];
		for(int j = 0; j < len2; j++)
		{
			int min1 = 0;
			int max1 = 0;
			for(int i = 1; i < len1; i++)//get index of corner with smallest projection vector
			{
				if( oc1[i].dot(hitbox2_SIDES[j]) < oc1[min1].dot(hitbox2_SIDES[j]) )
					min1 = i;
				if( oc1[i].dot(hitbox2_SIDES[j]) > oc1[max1].dot(hitbox2_SIDES[j]) )
					max1 = i;
			}
			//repeat for hitbox2
			int min2 = 0;
			int max2 = 0;
			for(int i = 1; i < len2; i++)//get index of corner with largest projection vector (using dot product)
			{
				if( oc2[i].dot(hitbox2_SIDES[j]) < oc2[min2].dot(hitbox2_SIDES[j]) )
					min2 = i;
				if( oc2[i].dot(hitbox2_SIDES[j]) > oc2[max2].dot(hitbox2_SIDES[j]) )
					max2 = i;
			}
			double dotMax1 = oc1[max1].dot(hitbox2_SIDES[j]);
			double dotMin1 = oc1[min1].dot(hitbox2_SIDES[j]);
			double dotMax2 = oc2[max2].dot(hitbox2_SIDES[j]);
			double dotMin2 = oc2[min2].dot(hitbox2_SIDES[j]);
			if(dotMax1 < dotMin2 || dotMax2 < dotMin1)//no overlap
			{
				return null;
			}
			else
			{
//				System.out.println("overlap detected on side: " + j);
				if(dotMax1 - dotMin2 < dotMax2 - dotMin1)
				{
					proj[j] = oc1[max1].project(hitbox2_SIDES[j]).sub( oc2[min2].project(hitbox2_SIDES[j]) ).mul(-1);
				}
				else
				{
					proj[j] = oc2[max2].project(hitbox2_SIDES[j]).sub( oc1[min1].project(hitbox2_SIDES[j]) );
				}
			}
		}///if it makes it through the whole loop, we have a collision
//		System.out.println("collision detected");
		///find the smallest projection vector
		int min = 0;
		for(int i = 1; i < len2; i++)
			if(proj[i].length() < proj[min].length())
				min = i;
		Vector2d[] colVecs = {proj[min],hitbox2_SIDES[min]};
		return colVecs;
	}
//4868
	public static void collisionPhysics(Vector2d velocity, Vector2d projection, Vector2d surfaceNormal, Entity obj)//velocity is velocity of colliding object, projection is projection vector of colliding surfaces, surfaceNormal is normal of surface collision, 
	{
		
		//find component of velocity parallel to collision normal
		double dot = velocity.dot(surfaceNormal);///dot product
		
		Vector2d normalVelocity = velocity.project(surfaceNormal);//project velocity onto surface normal
		
		Vector2d tangentVelocity = new Vector2d(velocity.getX() - normalVelocity.getX(), velocity.getY() - normalVelocity.getY());//subtract normal velocity from velocity to get tangent velocity

		//we only want to apply collision response forces if the object is travelling into, and not out of, the collision
		double b,f;
		Vector2d fric, boun;
		boolean in = true;
		
		if(dot < 0)
		{
			f = FRICTION;
			fric = new Vector2d(tangentVelocity.getX() * f, tangentVelocity.getY() * f);
			if(surfaceNormal.getY() == 0)
				fric = tangentVelocity;
			
			b = -BOUNCE;//this bounce constant should be elsewhere, i.e inside the object/tile/etc..
			boun = new Vector2d(normalVelocity.getX() * b, normalVelocity.getY() * b);
		}
		else
		{
			//moving out of collision, do not apply forces
			fric = tangentVelocity.add(normalVelocity);
			boun = new Vector2d(0,0);
			in = false;

		}
		
	//	System.out.println("Projection vector: " + projection + "\tSurface normal: " + surfaceNormal + "\nObject Impact Velocity: " + velocity + "\tObject new velocity: " + projection.add(fric.add(boun)) );

		obj.translate(projection.mul(1.01));//project object out of collision using velocity
		
		if(in)//TODO: this solves some of the stickiness but creates other problems(infinite ceiling jump)
			obj.setVelocity(fric.add(boun));//apply bounce and friction impulses which alter velocity; need to fix bounce coefficients to fit Entity GRAVITY vector
	//	System.out.println("set velocity to: " + obj.getVelocity());
	}
	public Vector2d[] projectionCollisionResponse2(dPolygon poly1, dPolygon poly2)
	{	
		//hitbox[number of vertices][x,y]; hitbox1 "collides" with hitbox2; 
		//returns array[2] of vectors with [0] projection vector; [1] surface normal
		VectorCollisionEngine engine = new VectorCollisionEngine();
		Vector2d[] data = new Vector2d[2];
		data[0] = engine.detectCollisionD(poly1,  poly2);
		if(data[0] == null)
			return null;
		data[1] = data[0].normalized();
		
		return data;
	}
}







