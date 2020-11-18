package com.v_kolomiichuk.drop_catch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

	final Drop game;
	OrthographicCamera camera;
	SpriteBatch batch;
	Texture dropImage, bucketImage;
	Sound dropSound;
	Music rainMusic;

	Rectangle bucket;
	Vector3 touchPos;

	Array<Rectangle> drops;
	long lastDropTime;

	int dropsGatchered;
	
	public GameScreen (final Drop gam) {

		this.game = gam;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		batch = new SpriteBatch();
		dropImage = new Texture("droplet.png");
		bucketImage = new Texture("bucket.png");

		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"));

		rainMusic.setLooping(true);
		rainMusic.play();

		bucket = new Rectangle();
		bucket.x = 800/2 - 64/2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height  = 64;

		touchPos = new Vector3();

		drops = new Array<>();
		spawnRaindrops();
	}

	private void spawnRaindrops(){
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		drops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "Drops: " + dropsGatchered, 0, 480);
		game.batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle rectangle : drops){
			game.batch.draw(dropImage, rectangle.x, rectangle.y);
		}
		game.batch.end();

		if (Gdx.input.isTouched()){
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = (int) (touchPos.x - 64/2);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			bucket.x += 200 * Gdx.graphics.getDeltaTime();

		if (bucket.x < 0)
			bucket.x = 0;
		if (bucket.x > Gdx.graphics.getWidth()-64)
			bucket.x = Gdx.graphics.getWidth()-64;

		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
			spawnRaindrops();

		Iterator<Rectangle> iterator = drops.iterator();
		while (iterator.hasNext()){
			Rectangle raindrop = iterator.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0)
				iterator.remove();

			if (raindrop.overlaps(bucket)){
				dropsGatchered++;
				dropSound.play();
				iterator.remove();
			}
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

	@Override
	public void show() {
		rainMusic.play();
	}
}
