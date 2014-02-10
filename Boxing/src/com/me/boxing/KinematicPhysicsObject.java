package com.me.boxing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;


/**
 * @author Christian
 * An image attached to a rectangular Kinematic B2D body
 */
public class KinematicPhysicsObject {

	// game object
	public Body body;
	private Sprite sprite;

	// sizing and position info
	public Vector2 pos;
	private int w;
	private int h;
	private float offsetX;
	private float offsetY;

	public KinematicPhysicsObject(World world, TextureRegion img, int x, int y, int w, int h)
	{
		pos = new Vector2(x, y);
		this.w = w;
		this.h = h;
		offsetX = w/2;
		offsetY = h/2;

		body = BodyFarm.createKinematicBody(world, pos.x, pos.y, w, h);

		// create and place sprite over its body
		sprite = new Sprite(img);
		sprite.setSize(w, h);
		sprite.setPosition(pos.x, pos.y);
	}

	private void update(float dt)
	{
		pos.x = body.getPosition().x * Boxing.METERS_TO_PIXELS - offsetX;
		pos.y = body.getPosition().y * Boxing.METERS_TO_PIXELS - offsetY;
		sprite.setPosition(pos.x, pos.y);
	}

	public void draw(SpriteBatch batch)
	{
		update(Gdx.graphics.getDeltaTime());
		sprite.draw(batch);
	}

	/*******************************************/
	/****** Mutate/access underlying Body ******/
	/*******************************************/

	public void setFriction(float friction) { body.getFixtureList().get(0).setFriction(friction); }

	public Vector2 getLinearVelocity() { return body.getLinearVelocity(); }

	public void setLinearVelocity(Vector2 v) { body.setLinearVelocity(v); }

	public float getX() { return sprite.getX(); }

	public float getY() { return sprite.getY(); }

	public int getWidth() { return w; }

	public int getHeight() { return h; }

	public Vector2 getCenter() { return new Vector2(sprite.getX() + w/2, sprite.getY() + h/2); }
}