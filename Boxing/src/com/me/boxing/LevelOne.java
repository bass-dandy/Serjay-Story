package com.me.boxing;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;


public class LevelOne extends AbstractLevel {

	private World world;
	private Music song;

	// which map layers to render
	private int[] layer1 = {0};

	// game objects
	private Texture sky;
	Platform platform;
	Pickup pickup;

	// source files
	private String mapSource = "maps/map.tmx";
	private String skySource = "sky.png";

	private float tileWidth;
	private float tileHeight;

	private static final float FRICTION = 0.6f;
	private static final float WALL_FRICTION = 0.1f;

	/****** DEBUG ******/
	Box2DDebugRenderer dr;
	Matrix4 debugMatrix;
	private boolean debug;
	/****** DEBUG ******/


	public LevelOne(Player player, SpriteBatch batch, World world, boolean debug)
	{	
		this.world = world;
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		// load camera
		camera = new OrthographicCamera(w, h);
		camera.translate(w/2, h/2);

		// load SpriteBatch
		this.batch = batch;
		batch.setProjectionMatrix(camera.combined);

		// load map and collision layer
		TmxMapLoader load = new TmxMapLoader();
		map = load.load(mapSource);
		tmr = new OrthogonalTiledMapRenderer(map);
		collision = (TiledMapTileLayer)map.getLayers().get(1);

		tileWidth = collision.getTileWidth();
		tileHeight = collision.getTileHeight();

		// make ground collidable, set friction coefficient
		makeGround(world, tileWidth, tileHeight);
		makeWalls(world, tileWidth, tileHeight);
		for(Body b: bodies) 
		{
			if(b.getUserData().equals("floor"))
				b.getFixtureList().get(0).setFriction(FRICTION);
			else if(b.getUserData().equals("wall"))
				b.getFixtureList().get(0).setFriction(WALL_FRICTION);
			else
				b.getFixtureList().get(0).setFriction(FRICTION);
		}

		// load entities
		this.player = player;
		sky = new Texture(Gdx.files.internal(skySource));
		platform = new Platform(world, 64, 64, 32, 320);
		pickup = new Pickup(35 * tileWidth, 3 * tileHeight);

		// start playing song
		song = Gdx.audio.newMusic(Gdx.files.internal("sound/music/level1.mp3"));
		song.setLooping(true);
		//song.play();

		/****** DEBUG ******/
		this.debug = debug;
		debugMatrix = camera.combined.cpy();
		debugMatrix.scale(Boxing.METERS_TO_PIXELS, Boxing.METERS_TO_PIXELS, 1f);
		dr = new Box2DDebugRenderer();
		/****** DEBUG ******/
	}

	private void update(float delta) {
		updateCamera();

		/****** DEBUG ******/
		debugMatrix = camera.combined.cpy();
		debugMatrix.scale(Boxing.METERS_TO_PIXELS, Boxing.METERS_TO_PIXELS, 1f);
		/****** DEBUG ******/
		
		if(player.isColliding(pickup.getBoundingRectangle()))
			pickup.collected = true;
	}

	@Override
	public void render(float delta)
	{	
		update(delta);

		// update the camera and map view
		camera.update();
		tmr.setView(camera);

		batch.begin();

		if(!debug) 
			batch.draw(sky, camera.position.x - w/2, camera.position.y - h/2);

		player.draw(batch);	
		platform.draw(batch);
		pickup.draw(batch);
		player.draw(batch);	
		
		if(!debug)
			tmr.render(layer1);
		else
			dr.render(world, debugMatrix);
		
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}