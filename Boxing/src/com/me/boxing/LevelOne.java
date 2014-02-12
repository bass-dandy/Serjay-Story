package com.me.boxing;


import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;


public class LevelOne extends AbstractLevel {

	private World world;
	private Music song;

	// tiled map info
	private int[] layer1 = {0};
	private float tileWidth;
	private float tileHeight;

	// game objects
	private Texture sky;
	private Platform platform;
	private Gun gun;
	private ArrayList<Pickup> pickups;

	// source files
	private String mapSrc = "maps/map.tmx";
	private String skySrc = "sky.png";
	private String shotSrc = "sound/fx/shot.wav";

	// physics constants
	private static final float FRICTION = 0.1f;
	private static final float WALL_FRICTION = 0.1f;

	// for debug rendering
	private boolean debug;
	private Box2DDebugRenderer dr;
	private Matrix4 debugMatrix;

	// for shooting
	private Vector2 aim;
	private Vector3 playerPos;
	private Vector2 mousePos;
	private ArrayList<Bullet> bullets;
	private float time = 0f;
	private float lastShot = 0f;
	private float shootDelay = 0.1f;
	private Sound shot;


	public LevelOne(Player player, SpriteBatch batch, World world, boolean debug)
	{	
		this.world = world;
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

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

		for(Body b: bodies) 
		{
			if(b.getUserData().equals("floor"))
				b.getFixtureList().get(0).setFriction(FRICTION);
			else if(b.getUserData().equals("wall"))
				b.getFixtureList().get(0).setFriction(WALL_FRICTION);
		}

		// load entities
		this.player = player;
		sky = new Texture(Gdx.files.internal(skySrc));
		platform = new Platform(world, 64, 64, 32, 320);
		gun = new Gun(player.getCenter());

		// set up shooting
		bullets = new ArrayList<Bullet>();
		playerPos = new Vector3();
		mousePos = new Vector2();
		aim = new Vector2();
		shot = Gdx.audio.newSound(Gdx.files.internal(shotSrc));

		// place pickups
		pickups = new ArrayList<Pickup>();
		pickups.add( new Pickup(35 * tileWidth, 3 * tileHeight) );
		pickups.add( new Pickup(2 * tileWidth, 36 * tileHeight) );
		pickups.add( new Pickup(41 * tileWidth, 42 * tileHeight) );
		pickups.add( new Pickup(52 * tileWidth, 13 * tileHeight) );

		// start playing song
		if(!debug)
		{
			song = Gdx.audio.newMusic(Gdx.files.internal("sound/music/level1.mp3"));
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
	}

	private void aimGun()
	{
		// get mouse screen position, convert from y-down to y-up coordinates
		mousePos.x = Gdx.input.getX();
		mousePos.y = h - Gdx.input.getY();

		// get player screen position
		playerPos.x = player.getCenter().x;
		playerPos.y = player.getCenter().y;
		camera.project(playerPos);

		// aim the gun
		aim.x = mousePos.x - playerPos.x;
		aim.y = mousePos.y - playerPos.y;		
		gun.rotate(aim);
		
		// return playerPos to world coordinates
		camera.unproject(playerPos);
		
	}

	private void cleanUp()
	{
		Iterator<Bullet> it = bullets.iterator();
		Bullet b;
		while(it.hasNext())
		{
			b = it.next();
			if(b.getPos().x < camera.position.x - w/2 || b.getPos().x > camera.position.x + w/2 
					|| b.getPos().y < camera.position.y - h/2 || b.getPos().y > camera.position.y + h/2) {
				b.dispose();
				it.remove();
			}	
		}
	}
	
	private void update(float delta) 
	{
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

		// shoot
		aimGun();
		if(Gdx.input.isButtonPressed(Buttons.LEFT) && (time - lastShot) > shootDelay) {
			bullets.add(new Bullet(player.getCenter(), aim));
			shot.play(0.1f);
			lastShot = time;
			
			// let the player fly
			Vector2 force = aim.cpy().rotate(180).scl(0.2f);
			force.x = 0;
			
			if(player.getLinearVelocity().y < 4)
			player.applyForceToCenter(force);
		}

		// check if player has picked up vodka
		for(Pickup p: pickups)
		{
			if(player.isColliding(p.getBoundingRectangle()))
				p.collected = true;
		}

		gun.move(player.getCenter());
		time += delta;
		
		cleanUp();
	}

	@Override
	public void render(float delta)
	{	
		update(delta);

		batch.begin();

		if(!debug) 
			batch.draw(sky, camera.position.x - w/2, camera.position.y - h/2, w, h);

		platform.draw(batch);

		for(Pickup p: pickups)
			p.draw(batch);

		player.draw(batch);	
		gun.draw(batch);

		for(Bullet b: bullets) {
			b.draw(batch);
		}


		batch.end();
		
		if(!debug)
			tmr.render(layer1);

		else
			dr.render(world, debugMatrix);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}