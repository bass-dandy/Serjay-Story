package com.me.boxing;

import java.util.ArrayList;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;


public class LevelTwo extends AbstractLevel {

	private Music song;

	// tiled map info
	private int[] layer1 = {0};
	private float tileWidth;
	private float tileHeight;

	// game objects
	private Gun gun;
	private ArrayList<Pickup> pickups;

	// source files
	private String mapSrc = "maps/level2/map.tmx";
	private String songSrc = "sound/music/cave.ogg";

	// physics constants
	private static final int GRAVITY = -15;
	private static final float FRICTION = 0.1f;
	private static final float WALL_FRICTION = 0.1f;
	
	// are we on Android?
	public boolean accelerometer = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);
	
	// LIGHTING
	private RayHandler rh;
	private PointLight p;
	private ConeLight c;


	public LevelTwo(SpriteBatch batch, boolean debug)
	{	
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		// create world and place player in it
		world = new World(new Vector2(0, GRAVITY), true);
		world.setContactListener(new PlayerContactListener());
		player = new Player(world, 64, 49 * 32, accelerometer);
		
		// set up camera
		camera = new OrthographicCamera(w, h);
		camera.translate(w/2, h/2);

		// set up SpriteBatch
		this.batch = batch;
		batch.setProjectionMatrix(camera.combined);

		// load map and collision layer
		TmxMapLoader load = new TmxMapLoader();
		map = load.load(mapSrc);
		tmr = new OrthogonalTiledMapRenderer(map);
		collision = (TiledMapTileLayer)map.getLayers().get(1);

		tileWidth = collision.getTileWidth();
		tileHeight = collision.getTileHeight();

		// make ground and walls collidable, set friction coefficients accordingly
		makeGround(world, tileWidth, tileHeight);
		makeWalls(world, tileWidth, tileHeight);

		for(Body b: surfaces) 
		{
			if(b.getUserData().equals("floor"))
				b.getFixtureList().get(0).setFriction(FRICTION);
			else if(b.getUserData().equals("wall"))
				b.getFixtureList().get(0).setFriction(WALL_FRICTION);
		}

		// load entities
		if(debug)
			gun = new Gun(player, camera);

		// place pickups
		pickups = new ArrayList<Pickup>();
		pickups.add(new GunPickup(128, 49 * tileHeight));

		// start playing song
		if(!debug)
		{
			song = Gdx.audio.newMusic(Gdx.files.internal(songSrc));
			song.setLooping(true);
			song.play();
		}

		// set up debug rendering
		if(debug) 
		{
			this.debug = debug;
			debugMatrix = camera.combined.cpy();
			debugMatrix.scale(Boxing.METERS_TO_PIXELS, Boxing.METERS_TO_PIXELS, 1f);
			dr = new Box2DDebugRenderer();
		}
		
		//LIGHTING
		rh = new RayHandler(world);
		rh.setCombinedMatrix(camera.combined.cpy().scl(Boxing.PIXELS_TO_METERS));
		p = new PointLight(rh, 64, new Color(1, 1, 1, 0.8f), 2, player.getCenter().x, player.getCenter().y);
		p.attachToBody(player.getBody(), 0, 0);
		c = new ConeLight(rh, 128, new Color(1, 1, 1,0.8f), 8, player.getCenter().x, player.getCenter().y, 0, 30);
	}
	
	private void update(float delta) 
	{
		// update physics
		world.step(TIMESTEP, VEL_IT, POS_IT);
		
		// update the camera and map view
		updateCamera();
		camera.update();
		tmr.setView(camera);

		// update debug matrix
		if(debug) 
		{
			debugMatrix = camera.combined.cpy();
			debugMatrix.scale(Boxing.METERS_TO_PIXELS, Boxing.METERS_TO_PIXELS, 1f);
		}

		// check if player has picked up an item
		for(Pickup p: pickups)
		{
			if(player.isColliding(p.getBoundingRectangle()))
				p.collected = true;
			if(p.isGun && p.collected && gun == null)
				gun = new Gun(player, camera);
		}
		
		//LIGHTING
		rh.setCombinedMatrix(camera.combined.cpy().scl(Boxing.METERS_TO_PIXELS));
		c.setPosition(player.getCenter().scl(Boxing.PIXELS_TO_METERS));
		if(gun != null && c != null) 
			c.setDirection(gun.getRotation());
	}

	@Override
	public void render(float delta)
	{	
		// clear render buffer
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		update(delta);

		batch.begin();

		for(Pickup p: pickups)
			p.draw(batch);

		player.draw(batch);	
		if(gun != null)
			gun.draw(batch);

		batch.end();
		
		// render either tiled map or debug world
		if(!debug)
			tmr.render(layer1);
		else
			dr.render(world, debugMatrix);
		
		//LIGHTING
		rh.updateAndRender();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
	
}
