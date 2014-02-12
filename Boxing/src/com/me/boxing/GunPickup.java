package com.me.boxing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GunPickup extends Pickup {
	
	private String pickupSource = "gun.png";
	
	private Sound sound;
	private boolean soundPlayed = false;
	private String soundSource = "sound/fx/gunpickup.wav";

	private static final int SIZE = 96;

	public GunPickup(float x, float y)
	{
		isGun = true;
		
		// create and size sprite
		sprite = new Sprite(new Texture(Gdx.files.internal(pickupSource)));
		sprite.setSize(SIZE, SIZE/3);
		sprite.setOrigin(sprite.getX() + sprite.getWidth()/2, sprite.getY() + sprite.getHeight()/2);

		// place sprite
		sprite.setPosition(x + sprite.getWidth()/2 - 90, y - 15);
		sprite.setRotation(135);
		
		// load sound
		sound = Gdx.audio.newSound(Gdx.files.internal(soundSource));
	}

	public void draw(SpriteBatch batch)
	{
		if(!collected)
			sprite.draw(batch);
		
		else if(!soundPlayed) 
		{
			sound.play();
			soundPlayed = true;
		}
	}

}
