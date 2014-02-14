package com.me.boxing;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;

public class Player {

	private DynamicPhysicsObject player;
	private String playerSource = "serjay.png";

	// taunts
	private ArrayList<Sound> taunts;
	private static final int NUM_TAUNTS = 15;
	private Random r;
	private long lastSound = 0L;

	// sizing info
	private static final int W = 50;
	private static final int H = 60;

	// movement constants
	private static final float JUMP = 7f;
	private static final float WALK_ACCEL = 10f;
	private static final float WALK_MAX = 2.5f;
	private static final float FRICTION = 0.4f;

	// player state tracking
	private boolean canJump = true;
	private int numJumps = 2;
	private boolean canTaunt = true;

	private boolean accelerometer;

	public Player(World world, int x, int y, boolean accelerometer)
	{
		// make player
		Texture playerTex = new Texture(Gdx.files.internal(playerSource));
		TextureRegion playerIMG = new TextureRegion(playerTex, 304, 336);
		player = new DynamicPhysicsObject(world, playerIMG, x, y, (float)W/2);
		player.setFixedRotation(true);
		player.setFriction(FRICTION);
		player.body.setUserData("player");

		// load taunts
		r = new Random();
		taunts = new ArrayList<Sound>();
		for(int i = 0; i < NUM_TAUNTS; i++)
			taunts.add(Gdx.audio.newSound(Gdx.files.internal("sound/taunts/taunt" + i + ".wav") ));

		this.accelerometer = accelerometer;
	}

	private void update()
	{
		Vector2 vel = player.getLinearVelocity();

		if(!accelerometer)
		{
			// horizontal movement; accelerate and keep below speed limit, flip sprite if necessary
			if(Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && vel.x > -WALK_MAX) {
				player.body.applyForceToCenter(new Vector2(-WALK_ACCEL, 0), true);
				if(player.sprite.isFlipX())
					player.sprite.flip(true, false);
			}
			else if(Gdx.input.isKeyPressed(Keys.D) && !Gdx.input.isKeyPressed(Keys.A) && vel.x < WALK_MAX) {
				player.body.applyForceToCenter(new Vector2(WALK_ACCEL, 0), true);
				if(!player.sprite.isFlipX())
					player.sprite.flip(true, false);
			}
		}
		else
		{
			float force = Gdx.input.getAccelerometerY() * 2;
			player.body.applyForceToCenter(new Vector2(force, 0), true);

			if(vel.x > WALK_MAX * 1.2f)
				player.body.setLinearVelocity(WALK_MAX * 1.2f, vel.y);
			else if(vel.x < -WALK_MAX * 1.2f)
				player.body.setLinearVelocity(-WALK_MAX * 1.2f, vel.y);

			if(player.sprite.isFlipX() && vel.x < -0.2)
				player.sprite.flip(true, false);
			else if(!player.sprite.isFlipX() && vel.x > 0.2)
				player.sprite.flip(true, false);
		}

		if(vel.y == 0)
		{
			// check contacts
			for(Contact c : player.body.getWorld().getContactList())
			{
				Object userDataA = c.getFixtureA().getBody().getUserData();
				Object userDataB = c.getFixtureB().getBody().getUserData();
				float verticalA = c.getFixtureA().getBody().getPosition().y;
				float verticalB = c.getFixtureB().getBody().getPosition().y;

				// if player is colliding with a floor and player is above the floor
				if( (userDataA.equals("floor") && userDataB.equals("player") && verticalA < verticalB )
						|| (userDataA.equals("player") && userDataB.equals("floor") && verticalA > verticalB) ) 
				{
					canJump = true;
					numJumps = 2;
				}
				else if( (userDataA.equals("platform") && userDataB.equals("player")) || (userDataA.equals("player") && userDataB.equals("platform")) ) {
					canJump = true;
					numJumps = 2;
				}
			}
		}
		if(!accelerometer)
		{
			// jump if able
			if(Gdx.input.isKeyPressed(Keys.W) && numJumps > 0 && canJump) {
				player.body.setLinearVelocity(player.body.getLinearVelocity().x, JUMP);
				numJumps--;
				canJump = false;
			}
			// player can only air jump after they release jump button
			if(!Gdx.input.isKeyPressed(Keys.W))
				canJump = true;
		}
		else
		{
			// jump if able
			if(Gdx.input.isButtonPressed(Buttons.LEFT) && numJumps > 0 && canJump) {
				player.body.setLinearVelocity(player.body.getLinearVelocity().x, JUMP);
				numJumps--;
				canJump = false;
			}
			// player can only air jump after they release jump button
			if(!Gdx.input.isButtonPressed(Buttons.LEFT))
				canJump = true;
		}

		// play taunt if able
		if(Gdx.input.isKeyPressed(Keys.Q) && canTaunt) {
			for(Sound s: taunts)
				s.stop(lastSound);
			lastSound = taunts.get(r.nextInt(NUM_TAUNTS)).play();
			canTaunt = false;
		}
		else if(!Gdx.input.isKeyPressed(Keys.Q))
			canTaunt = true;
	}

	public void draw(SpriteBatch batch)
	{
		update();
		player.draw(batch);
	}


	/***********************/
	/****** ACCESSORS ******/
	/***********************/

	public float getX() { return player.getX(); }

	public float getY() { return player.getY(); }

	public void flip(boolean x, boolean y) { player.sprite.flip(x, y); }

	public boolean isFlipX() { return player.sprite.isFlipX(); }

	public boolean isFlipY() { return player.sprite.isFlipY(); }

	public Vector2 getCenter() { return player.getCenter(); }

	public float getWidth() { return W; }

	public float getHeight() { return H; }

	public boolean isColliding(Rectangle other)
	{
		Rectangle playerBounds = player.sprite.getBoundingRectangle();
		return Intersector.overlaps(playerBounds, other);
	}

	public void applyForceToCenter(Vector2 force) { player.body.applyForceToCenter(force, true); }

	public Vector2 getLinearVelocity() { return player.body.getLinearVelocity(); }
	
	public Body getBody() { return player.body; }
}
