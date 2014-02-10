package com.me.boxing;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class BodyFarm {
	
	private static final float PIXELS_TO_METERS = Boxing.PIXELS_TO_METERS;
	
	public static Body createStaticBody(World world, float x, float y, float w, float h)
	{
		Body tmp;
		
		// define body position
		BodyDef bd = new BodyDef();
		bd.position.set((x + w/2) * PIXELS_TO_METERS, (y + h/2) * PIXELS_TO_METERS);
		
		// set shape and size
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(w/2 * PIXELS_TO_METERS, h/2 * PIXELS_TO_METERS);
		
		// finished
		tmp = world.createBody(bd);
		tmp.createFixture(shape, 0.0f);
		return tmp;
	}
	
	public static Body createDynamicBody(World world, float x, float y, float w, float h)
	{
		Body tmp;
		
		// define body position, make it dynamic
		BodyDef bd = new BodyDef();
		bd.position.set((x + w/2) * PIXELS_TO_METERS, (y + h/2) * PIXELS_TO_METERS);
		bd.type = BodyType.DynamicBody;
		
		// define shape and size
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(w/2 * PIXELS_TO_METERS, h/2 * PIXELS_TO_METERS);
		
		// create and define fixture
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1;
		fd.friction = 0.5f;
		
		// finished, create body and attach fixture to it
		tmp = world.createBody(bd);
		tmp.createFixture(fd);
		return tmp;
	}
	
	public static Body createKinematicBody(World world, float x, float y, float w, float h)
	{
		Body tmp;
		
		// define body position, make it dynamic
		BodyDef bd = new BodyDef();
		bd.position.set((x + w/2) * PIXELS_TO_METERS, (y + h/2) * PIXELS_TO_METERS);
		bd.type = BodyType.KinematicBody;
		
		// define shape and size
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(w/2 * PIXELS_TO_METERS, h/2 * PIXELS_TO_METERS);
		
		// create and define fixture
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1;
		fd.friction = 0.5f;
		
		// finished, create body and attach fixture to it
		tmp = world.createBody(bd);
		tmp.createFixture(fd);
		return tmp;
	}
}
