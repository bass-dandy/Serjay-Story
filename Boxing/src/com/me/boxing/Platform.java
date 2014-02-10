package com.me.boxing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Platform {
	
	KinematicPhysicsObject platform;
	
	String platformSource = "platform.png";
	
	// points between which the platform will move
	private Vector2 start;
	private Vector2 end;
	
	// sizing constants
	private static final int W = 96;
	private static final int H = 32;
	private static final float SPEED = 1f;
	
	private static final float FRICTION = 0.6f;
	
	public Platform(World world, int x, int y, int x2, int y2)
	{
		TextureRegion platformIMG = new TextureRegion(new Texture(Gdx.files.internal(platformSource)), W, H);
		platform = new KinematicPhysicsObject(world, platformIMG, x, y, W, H);
		platform.setFriction(FRICTION);
		platform.body.setUserData("platform");
		
		start = new Vector2(x, y);
		end = new Vector2(x2, y2);
		
		setLinearVelocity(new Vector2(0, SPEED));
	}
	
	private void update() 
	{
		if(platform.pos.y < start.y)
			setLinearVelocity(new Vector2(0, SPEED));
		else if(platform.pos.y > end.y)
			setLinearVelocity(new Vector2(0, -SPEED));
	}
	
	public void draw(SpriteBatch batch) {
		update();
		platform.draw(batch);
	}
	
	public void setLinearVelocity(Vector2 v) { platform.setLinearVelocity(v); }

}
