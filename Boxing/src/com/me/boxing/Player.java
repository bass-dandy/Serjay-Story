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
	private static final float WALK_ACCEL = 10f;
	private static final float WALK_MAX = 2.5f;
	private static final float FRICTION = 0.4f;
	private static final float JUMP = 7f;
	private static final float WALL_JUMP_PAUSE = 0.35f;
	private static final float WALL_JUMP_VEL_X = 2.5f;
	private static final float 	WALL_JUMP_VEL_Y = 7.5f;

	// jump logic tracking
	private boolean canJump = true;
	public boolean canWallJumpRight = false;
	public boolean canWallJumpLeft = false;
	public boolean airborne = true;
	private int numJumps = 2;
	private boolean canAirControl = true;
	private float time = 0f;
	private float lastWallJump = 0f;

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

		// attach sensors
		player.attachSensor(W/4, 2, new Vector2(0, -H/2 * Boxing.PIXELS_TO_METERS), "foot");
		player.attachSensor(2, H/4, new Vector2(-W/2 * Boxing.PIXELS_TO_METERS, 0), "left");
		player.attachSensor(2, H/4, new Vector2(W/2 * Boxing.PIXELS_TO_METERS, 0), "right");

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
		time += Gdx.graphics.getDeltaTime();

		/*******************************/
		/***** HORIZONTAL MOVEMENT *****/
		/*******************************/

		if(!accelerometer) // MOVEMENT CONTROLS FOR DESKTOP
		{
			if(!airborne) // MOVE NORMALLY IF PLAYER IS GROUNDED
			{
				if(Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && vel.x > -WALK_MAX)
					player.body.applyForceToCenter(new Vector2(-WALK_ACCEL, 0), true);
				else if(Gdx.input.isKeyPressed(Keys.D) && !Gdx.input.isKeyPressed(Keys.A) && vel.x < WALK_MAX)
					player.body.applyForceToCenter(new Vector2(WALK_ACCEL, 0), true);
			}
			else if(canAirControl) // REDUCE MOBILITY IN THE AIR
			{
				if(Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && vel.x > -WALK_MAX)
					player.body.applyForceToCenter(new Vector2(-WALK_ACCEL/2, 0), true);
				else if(Gdx.input.isKeyPressed(Keys.D) && !Gdx.input.isKeyPressed(Keys.A) && vel.x < WALK_MAX)
					player.body.applyForceToCenter(new Vector2(WALK_ACCEL/2, 0), true);
			}
		}
		else // MOVEMENT CONTROLS FOR ANDROID
		{
			float force = Gdx.input.getAccelerometerY() * 2;
			player.body.applyForceToCenter(new Vector2(force, 0), true);
		}

		// flip player sprite if he changes direction
		if(player.sprite.isFlipX() && vel.x < -0.2)
			player.sprite.flip(true, false);
		else if(!player.sprite.isFlipX() && vel.x > 0.2)
			player.sprite.flip(true, false);

		/**********************/
		/***** JUMP LOGIC *****/
		/**********************/

		if(!airborne)
		{
			canJump = true;
			canAirControl = true;
			numJumps = 2;
		}
		else
		{
			if(numJumps > 1)
				numJumps = 1;
			if(time - lastWallJump > WALL_JUMP_PAUSE)
				canAirControl = true;
		}

		if(!accelerometer) // JUMP CONTROLS FOR DESKTOP
		{
			// jump if able and not attached to wall
			if(Gdx.input.isKeyPressed(Keys.W) && numJumps > 0 && canJump && !canWallJumpRight && !canWallJumpLeft) {
				player.body.setLinearVelocity(player.body.getLinearVelocity().x, JUMP);
				numJumps--;
				canJump = false;
			}
			// wall jump right if able
			else if(Gdx.input.isKeyPressed(Keys.W) && canWallJumpRight && canJump) {
				player.body.setLinearVelocity(WALL_JUMP_VEL_X, WALL_JUMP_VEL_Y);
				canJump = false;
				canAirControl = false;
				lastWallJump = time;
			}
			// wall jump left if able
			else if(Gdx.input.isKeyPressed(Keys.W) && canWallJumpLeft && canJump) {
				player.body.setLinearVelocity(-WALL_JUMP_VEL_X, WALL_JUMP_VEL_Y);
				canJump = false;
				canAirControl = false;
				lastWallJump = time;
			}
			// player can only air jump after they release jump button
			if(!Gdx.input.isKeyPressed(Keys.W))
				canJump = true;
		}
		else // JUMP CONTROLS FOR ANDROID
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

		/***************************/
		/***** PLAYER TAUNTING *****/
		/***************************/

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
