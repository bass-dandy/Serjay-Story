package com.me.boxing;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Gun {

	private Sprite gun;
	private Player player;
	private Sound shot;
	private OrthographicCamera camera;
	
	// source files
	private String gunSrc = "gun.png";
	private String shotSrc = "sound/fx/shot.wav";
	
	// for centering gun on player
	private float offsetX;
	private float offsetY;
	
	// for shooting
	private ArrayList<Bullet> bullets;
	private Vector2 aim;
	private Vector3 playerPos;
	private Vector2 mousePos;
	
	// for timing shots
	private float time = 0f;
	private float lastShot = 0f;
	private float shootDelay = 0.1f;
	
	private static final int SIZE = 100;
	

	public Gun(Player player, OrthographicCamera camera)
	{
		this.player = player;
		this.camera = camera;
		
		Texture gunImg = new Texture(Gdx.files.internal(gunSrc));
		gun = new Sprite(gunImg);
		gun.setSize(SIZE, SIZE/3);

		offsetX = gun.getWidth()/2;
		offsetY = gun.getHeight()/2;

		gun.setOrigin(offsetX, offsetY);
		gun.setPosition(player.getCenter().x - offsetX, player.getCenter().y - offsetY);
		
		// set up shooting
		bullets = new ArrayList<Bullet>();
		playerPos = new Vector3();
		mousePos = new Vector2();
		aim = new Vector2();
		shot = Gdx.audio.newSound(Gdx.files.internal(shotSrc));
	}

	private void aimGun()
	{
		// get mouse screen position, convert from y-down to y-up coordinates
		mousePos.x = Gdx.input.getX();
		mousePos.y = Gdx.graphics.getHeight() - Gdx.input.getY();

		// get player world position and convert to screen position
		playerPos.x = player.getCenter().x;
		playerPos.y = player.getCenter().y;
		camera.project(playerPos);

		// aim the gun from player to mouse
		aim.x = mousePos.x - playerPos.x;
		aim.y = mousePos.y - playerPos.y;		
		rotate(aim);
		
		// return playerPos to world coordinates
		camera.unproject(playerPos);
	}

	private void cleanUp()
	{
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		
		Iterator<Bullet> it = bullets.iterator();
		Bullet b;
		
		// remove offscreen bullets from game
		while(it.hasNext())
		{
			b = it.next();
			if(b.getPos().x < camera.position.x - w/2 || b.getPos().x > camera.position.x + w/2 
					|| b.getPos().y < camera.position.y - h/2 || b.getPos().y > camera.position.y + h/2) 
			{
				b.dispose();
				it.remove();
			}	
		}
	}
	
	private void update(float dt)
	{
		aimGun();
		
		if(Gdx.input.isButtonPressed(Buttons.LEFT) && (time - lastShot) > shootDelay) 
		{
			// shoot bullets
			bullets.add(new Bullet(player.getCenter(), aim));
			shot.play(0.1f);
			lastShot = time;
			
			// let the player fly, cancel horizontal forces
			Vector2 force = aim.cpy().rotate(180).scl(0.2f);
			force.x = 0;
			
			// flying down really fast is anti-fun
			if(force.y < 0)
				force.y = 0;
			
			// keep player below speed limit
			if(player.getLinearVelocity().y < 4)
				player.applyForceToCenter(force);
		}
		time += dt;
		cleanUp();
		
		move(player.getCenter());
	}
	
	public void draw(SpriteBatch batch) 
	{ 
		update(Gdx.graphics.getDeltaTime());
		gun.draw(batch);
		
		for(Bullet b: bullets) 
			b.draw(batch);
	}

	private void rotate(Vector2 dir) 
	{ 
		gun.setRotation(dir.angle()); 
		if(gun.getRotation() > 90 && gun.getRotation() < 270 && !gun.isFlipY())
			gun.flip(false, true);
		else if((gun.getRotation() < 90 || gun.getRotation() > 270) && gun.isFlipY())
			gun.flip(false, true);
	}

	private void move(Vector2 pos) { gun.setPosition(pos.x - offsetX, pos.y - offsetY); }

}
