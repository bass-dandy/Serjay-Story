package com.me.boxing;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Boxing implements ApplicationListener {

	// rendering and modeling tools
	private SpriteBatch batch;
	private World world;

	// game objects
	private Player player;

	// maps
	private LevelOne level1;

	// Box2D constants
	public static final float PIXELS_TO_METERS = 0.01f;
	public static final float METERS_TO_PIXELS = 100f;
	private static final float TIMESTEP = 1.0f/60.0f;
	private static final int VEL_IT = 6;
	private static final int POS_IT = 2;

	// physics constants
	private static final int GRAVITY = -15;

	// is debug enabled?
	private boolean debug = false;


	@Override
	public void create() 
	{		
		world = new World(new Vector2(0, GRAVITY), true);
		batch = new SpriteBatch();
		player = new Player(world, 64, 200);
		level1 = new LevelOne(player, batch, world, debug);
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

		// update
		world.step(TIMESTEP, VEL_IT, POS_IT);

		// draw
		level1.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() 
	{
		batch.dispose();
		world.dispose();
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
