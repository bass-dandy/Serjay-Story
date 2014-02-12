package com.me.boxing;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Pickup {

	protected Sprite sprite;
	public boolean collected = false;
	public boolean isGun = false;
	
	public void draw(SpriteBatch batch) {}
	
	public Rectangle getBoundingRectangle() { return sprite.getBoundingRectangle(); }
}
