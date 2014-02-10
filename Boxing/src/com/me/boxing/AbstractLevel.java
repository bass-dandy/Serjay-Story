package com.me.boxing;


import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Abstract class for 2D tiled metroidvania/sidescroller type games
 * @author Christian
 *
 */
public class AbstractLevel implements Screen {

	// rendering tools
	protected OrthographicCamera camera;
	protected SpriteBatch batch;
	protected OrthogonalTiledMapRenderer tmr;

	// graphics objects
	protected Player player;
	protected TiledMap map;
	protected TiledMapTileLayer collision;
	
	// physics objects
	protected ArrayList<Body> bodies = new ArrayList<Body>();

	// game window attributes
	protected float w;
	protected float h;
	public boolean loaded = false;


	protected void updateCamera()
	{
		// center camera on player
		float newX = player.getCenter().x;
		float newY = player.getY() + 200;

		// make sure camera doesn't leave the level
		if(newX < w/2)
			newX = w/2;
		else if(newX > collision.getWidth() * collision.getTileWidth() - w/2)
			newX = collision.getWidth() * collision.getTileWidth() - w/2;
		if(newY < h/2)
			newY = h/2;
		else if(newY > collision.getHeight() * collision.getTileHeight() - h/2)
			newY = collision.getHeight() * collision.getTileHeight() - h/2;

		// set new viewport size
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportHeight = Gdx.graphics.getHeight();
		
		// set new camera position
		camera.position.set(newX, newY, 0);
		batch.setProjectionMatrix(camera.combined);
	}

	protected void makeGround(World world, float tileWidth, float tileHeight)
	{
		for(int y = 0; y < collision.getHeight(); y++)
		{
			for(int x = 0; x < collision.getWidth(); x++)
			{
				if( collision.getCell(x, y).getTile().getProperties().containsKey("floor") ) {
					int bodyWidth = helpGround(x, y);
					bodies.add( BodyFarm.createStaticBody(world, x * tileWidth, y * tileHeight, tileWidth * bodyWidth, tileHeight) );
					bodies.get(bodies.size() - 1).setUserData("floor");
					x += bodyWidth;
				}
			}
		}
	}
	
	private int helpGround(int x, int y)
	{
		if( x >= collision.getWidth() )
			return 0;
		else if( !collision.getCell(x, y).getTile().getProperties().containsKey("floor") )
			return 0;
		
		else return 1 + helpGround(x + 1, y);
	}
	
	protected void makeWalls(World world, float tileWidth, float tileHeight)
	{
		for(int x = 0; x < collision.getWidth(); x++)
		{
			for(int y = 0; y < collision.getHeight(); y++)
			{
				if( collision.getCell(x, y).getTile().getProperties().containsKey("wall") ) {
					int bodyHeight = helpWalls(x, y);
					bodies.add( BodyFarm.createStaticBody(world, x * tileWidth, y * tileHeight, tileWidth, tileHeight * bodyHeight) );
					bodies.get(bodies.size() - 1).setUserData("wall");
					y += bodyHeight;
				}
			}
		}
	}
	
	private int helpWalls(int x, int y)
	{
		if( y >= collision.getHeight() )
			return 0;
		else if( !collision.getCell(x, y).getTile().getProperties().containsKey("wall") )
			return 0;
		
		else return 1 + helpWalls(x, y + 1);
	}
	
	/***********************************/
	/****** IMPLEMENTED ELSEWHERE ******/
	/***********************************/
	
	@Override
	public void render(float delta) {}

	@Override
	public void resize(int width, int height) 
	{
		w = width;
		h = height;
	}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}
}