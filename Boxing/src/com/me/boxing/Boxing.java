package com.me.boxing;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Boxing implements ApplicationListener {

	// rendering and modeling tools
	private SpriteBatch batch;

	// maps
	private LevelOne level1;

	// Box2D constants
	public static final float PIXELS_TO_METERS = 0.01f;
	public static final float METERS_TO_PIXELS = 100f;

	// is debug enabled?
	private boolean debug = false;


	@Override
	public void create() 
	{		
		batch = new SpriteBatch();
		level1 = new LevelOne(batch, debug);
	}

	@Override
	public void render() 
	{		
		if(debug) 
		{
			// clear render buffer
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
		
		// draw
		level1.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() 
	{
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) 
	{
		level1.resize(width, height);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}
}
