package com.me.boxing;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class StaticPhysicsObject {

	// game object
	public Body body;
	private Sprite sprite;

	// sizing and position info
	public Vector2 pos;
	private int w;
	private int h;

	public StaticPhysicsObject(World world, TextureRegion img, int x, int y, int w, int h)
	{
		pos = new Vector2(x, y);
		this.w = w;
		this.h = h;

		body = BodyFarm.createStaticBody(world, pos.x, pos.y, w, h);

		// create and place sprite over its body
		sprite = new Sprite(img);
		sprite.setSize(w, h);
		sprite.setPosition(pos.x, pos.y);
	}

	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public void setFriction(float friction) { body.getFixtureList().get(0).setFriction(friction); }
	
	public int getWidth() { return w; }
	
	public int getHeight() { return h; }
}
