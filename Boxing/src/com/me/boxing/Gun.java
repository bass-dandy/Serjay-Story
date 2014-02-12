package com.me.boxing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Gun {

	private Sprite gun;
	private String gunSrc = "gun.png";
	private float offsetX;
	private float offsetY;

	private static final int SIZE = 100;

	public Gun(Vector2 pos)
	{
		Texture gunImg = new Texture(Gdx.files.internal(gunSrc));
		gun = new Sprite(gunImg);
		gun.setSize(SIZE, SIZE/3);

		offsetX = gun.getWidth()/2;
		offsetY = gun.getHeight()/2;

		gun.setOrigin(offsetX, offsetY);
		gun.setPosition(pos.x - offsetX, pos.y - offsetY);
	}

	public void draw(SpriteBatch batch) { gun.draw(batch); }

	public void rotate(Vector2 dir) 
	{ 
		gun.setRotation(dir.angle()); 
		if(gun.getRotation() > 90 && gun.getRotation() < 270 && !gun.isFlipY())
			gun.flip(false, true);
		else if((gun.getRotation() < 90 || gun.getRotation() > 270) && gun.isFlipY())
			gun.flip(false, true);
	}

	public void move(Vector2 pos) { gun.setPosition(pos.x - offsetX, pos.y - offsetY); }

}
