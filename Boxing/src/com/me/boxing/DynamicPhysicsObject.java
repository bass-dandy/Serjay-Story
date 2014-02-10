package com.me.boxing;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;


/**
 * @author Christian
 * A sprite attached to a rectangular dynamic B2D body
 */
public class DynamicPhysicsObject {

	// game object
	public Body body;
	public Sprite sprite;

	// sizing and position info
	public Vector2 pos;
	private int w;
	private int h;
	private float offsetX;
	private float offsetY;

	public DynamicPhysicsObject(World world, TextureRegion img, int x, int y, int w, int h)
	{
		pos = new Vector2(x, y);
		this.w = w;
		this.h = h;
		offsetX = w/2;
		offsetY = h/2;

		body = BodyFarm.createDynamicBody(world, pos.x, pos.y, w, h);

		// create and place sprite over its body
		sprite = new Sprite(img);
		sprite.setSize(w, h);
		sprite.setPosition(pos.x, pos.y);
	}

	private void update()
	{
		pos.x = body.getPosition().x * Boxing.METERS_TO_PIXELS - offsetX;
		pos.y = body.getPosition().y * Boxing.METERS_TO_PIXELS - offsetY;
		sprite.setPosition(pos.x, pos.y);
	}

	public void draw(SpriteBatch batch)
	{
		update();
		sprite.draw(batch);
	}

	/*******************************************/
	/****** Mutate/access underlying Body ******/
	/*******************************************/

	public void setFixedRotation(boolean flag) { body.setFixedRotation(flag); }

	public void setFriction(float friction) { body.getFixtureList().get(0).setFriction(friction); }

	public Vector2 getLinearVelocity() { return body.getLinearVelocity(); }

	public float getX() { return sprite.getX(); }

	public float getY() { return sprite.getY(); }

	public int getWidth() { return w; }

	public int getHeight() { return h; }

	public Vector2 getCenter() { return new Vector2(sprite.getX() + w/2, sprite.getY() + h/2); }
}
