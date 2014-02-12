package com.me.boxing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
	
	private String bulletSrc = "bullet.png";
	
	private Sprite bullet;
	private Vector2 dir;
	private Vector2 pos;
	
	private static final int SIZE = 8;
	private static final int SPEED = 1200;
	
	public Bullet(Vector2 pos, Vector2 dir)
	{
		this.pos = pos.cpy();
		this.dir = dir.cpy().nor();
		
		this.pos.x += dir.x * 0.2;
		this.pos.y += dir.y * 0.2;
		
		Texture bulletTex = new Texture(Gdx.files.internal(bulletSrc));
		bullet = new Sprite(bulletTex);
		bullet.setSize(SIZE * 2, SIZE);
		bullet.setPosition(pos.x, pos.y);
		bullet.setOrigin(bullet.getWidth()/2, bullet.getHeight()/2);
		bullet.setRotation(dir.angle());
	}
	
	private void update(float dt)
	{
		pos.x += dir.x * SPEED * dt;
		pos.y += dir.y * SPEED * dt;
		bullet.setPosition(pos.x, pos.y);
	}
	
	public void draw(SpriteBatch batch)
	{
		update(Gdx.graphics.getDeltaTime());
		bullet.draw(batch);
	}
	
	public void dispose()
	{
		bullet.getTexture().dispose();
	}
	
	public Vector2 getPos() { return pos.cpy(); }

}
