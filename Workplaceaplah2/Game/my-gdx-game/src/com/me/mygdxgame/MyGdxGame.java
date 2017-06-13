package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.me.mygdxgame.objects.Brick;
import com.me.mygdxgame.objects.Coin;
import com.me.mygdxgame.objects.GameObject;
import com.me.mygdxgame.objects.GaryOak;
import com.me.mygdxgame.objects.Pole;
import com.me.mygdxgame.objects.Spikes;

public class MyGdxGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private GaryOak player1;
	private ArrayList<GameObject> list = new ArrayList<GameObject>();
	private ArrayList<GameObject> pleaseDelete = new ArrayList<GameObject>();
	private int level = 1;
	private Rectangle menuButton1;
	private Sprite menuCongrats, menuNext, menuStart, menuGameOver, menuRestart ;
	private Texture menuButtonTex, startTex;
	
	private int gameState = 1; //1 = Main Menu, 2 = Main Game, 3 = Next Level Screen, 4 = Game Over Screen
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		
		player1 = new GaryOak();
		player1.setPosition(200, 100);
		
		menuButtonTex = new Texture(Gdx.files.internal("sprite/cont.png"));
		startTex = new Texture(Gdx.files.internal("sprite/startbutton.png"));
		menuCongrats = new Sprite(menuButtonTex, 0, 0, 256, 64);
		menuNext = new Sprite(menuButtonTex,0, 0, 256, 64);
		menuButton1 = new Rectangle(0, 0, 256, 64);
		menuStart = new Sprite(startTex, 0, 0, 256, 64);
		menuStart.setPosition(275, 300);
		this.menuGameOver = new Sprite(menuButtonTex, 0, 0, 256, 64);
		this.menuGameOver.setPosition(275, 300);
		this.menuRestart = new Sprite(menuButtonTex, 0, 0, 256, 64);
		this.menuRestart.setPosition(275, 200);
		
		
		
		level = 1;
		loadLevel("sprite/level1.txt");
	
	}	

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {		
		switch (this.gameState){
		case 1:
			this.mainMenu();
			break;
		case 2:
			this.mainGame();
			break;
		case 3:
			this.nextLevel();
			break;
		case 4:
			this.gameOver();
			break;
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
	
	public void updateCamera(){
		camera.position.x = player1.getHitBox().x;
		camera.update();
	}
	
	public void loadLevel(String level){
		list.clear();
		FileHandle file = Gdx.files.internal(level);
		StringTokenizer tokens = new StringTokenizer(file.readString());
		while (tokens.hasMoreTokens()){
			String type = tokens.nextToken();
			if (type.equals("Block")){
				list.add(new Brick(
						Integer.parseInt(tokens.nextToken()),
						Integer.parseInt(tokens.nextToken())));
			}else if (type.equals("Spike")){
				list.add(new Spikes(
						Integer.parseInt(tokens.nextToken()),
						Integer.parseInt(tokens.nextToken())));
			}else if (type.equals("Coin")){
				list.add(new Coin(
						Integer.parseInt(tokens.nextToken()),
						Integer.parseInt(tokens.nextToken())));
			}else if (type.equals("Pole")){
				list.add(new Pole(
						Integer.parseInt(tokens.nextToken()),
						Integer.parseInt(tokens.nextToken())));
			}
		}
	}
	
	public void mainMenu() {
		Gdx.gl.glClearColor(1f, 1f, 1, 1);//clears background (white)
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);//clears everything
		
		batch.setProjectionMatrix(camera.combined);//camera and canvas match screen
		batch.begin();
		menuStart.draw(batch);
		batch.end();
		
		camera.position.x = 400;
		camera.position.y = 240;
		camera.update();
		
		this.menuButton1.x = 275;
		this.menuButton1.y = 300;
		
		if (Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			Rectangle touch = new Rectangle(touchPos.x -16, touchPos.y - 16, 32, 32);
			if(touch.overlaps(menuButton1)){
			
			gameState = 2;
			}
		}
	}
	
	public void mainGame() {
		Gdx.gl.glClearColor(0.8f, 0.8f, 1, 1);//clears background (white)
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);//clears everything
		
		batch.setProjectionMatrix(camera.combined);//camera and canvas match screen
		batch.begin();
		player1.draw(batch);//draw sprite to the batch
		for (GameObject t : list){
			t.draw(batch);
		}
		
		
		batch.end();
		
		//Updates
		player1.update(Gdx.graphics.getDeltaTime());
		Rectangle temp = new Rectangle(0, 0, 800, 10);
		if (player1.hits(temp) != -1){
			player1.action(1, 0, 10);
		}
		boolean changeLevel = false;
		for (GameObject t : list){
			switch (player1.hits(t.getHitBox())){
			case 1:
				switch(t.hitAction(1)){
				case 1:
					player1.action(1, 0, t.getHitBox().y + t.getHitBox().height);
					break;
				case 2:	
					player1.setPosition(0, 400);
					this.gameState = 4;
					break;
				case 3:
					pleaseDelete.add(t);
					break;
				case 4:
					level++;
					changeLevel = true;
					break;
				}
			break;
			case 2:
				switch(t.hitAction(2)){
				case 1:
					player1.action(2, t.getHitBox().x + t.getHitBox().width+1,0);
					break;
				case 2:	
					player1.setPosition(0, 400);
					this.gameState = 4;
					break;
				case 3:
					pleaseDelete.add(t);
					break;
				case 4:
					level++;
					changeLevel = true;
					break;
				}
				break;
			case 3:
				switch(t.hitAction(3)){
				case 1:
					player1.action(3, t.getHitBox().x - player1.getHitBox().width - 1, 0);
					break;
				case 2:	
					player1.setPosition(0, 400);
					this.gameState = 4;
					break;	
				case 3:
					pleaseDelete.add(t);
					break;
				case 4:
					level++;
					changeLevel = true;
					break;
				}
				break;
			case 4:
				switch(t.hitAction(4)){
				case 1:
					player1.action(4, 0, t.getHitBox().y - player1.getHitBox().height);
					break;
				case 2:	
					player1.setPosition(0, 400);
					this.gameState = 4;
					break;
				case 3:
					pleaseDelete.add(t);
					break;
				case 4:
					level++;
					changeLevel = true;
					break;
			}	
				break;
		}
	
	
	}
		
		while (!pleaseDelete.isEmpty()){
			list.remove(pleaseDelete.get(0));
			pleaseDelete.remove(0);
		
		}
		if(changeLevel){
			this.gameState = 3;
			this.menuButton1.x = player1.getHitBox().x - 50;
			this.menuButton1.y = 200;
			this.menuCongrats.setPosition(player1.getHitBox().x - 100, 250);
			this.menuNext.setPosition(player1.getHitBox().x - 50, 200);
			
		}
		
		
		
		updateCamera();
		
		
		//Controls
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			player1.moveLeft(Gdx.graphics.getDeltaTime());
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			player1.moveRight(Gdx.graphics.getDeltaTime());
		}
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
			player1.jump();
		}
		
	}

	public void nextLevel(){
		Gdx.gl.glClearColor(0.8f, 0.8f, 1, 1);//clears background (white)
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);//clears everything
		
		batch.setProjectionMatrix(camera.combined);//camera and canvas match screen
		batch.begin();
		player1.draw(batch);//draw sprite to the batch
		for (GameObject t : list){
			t.draw(batch);
		}
		
		//this.menuCongrats.draw(batch);
		this.menuNext.draw(batch);
		
		batch.end();
		
		
		
		//Controls
		for (int i=0; i<5; i++){
			if (Gdx.input.isTouched(i)){
				Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				camera.unproject(touchPos);
				Rectangle touch = new Rectangle(touchPos.x -16, touchPos.y - 16, 32, 32);
		
		if(touch.overlaps(menuButton1)){
			player1.setPosition(0, 400);
					if(level == 2){
					loadLevel("sprite/level2.txt");
					gameState = 2;
					}
					else if (level == 3){
						loadLevel("sprite/level3.txt");
						gameState = 2; 
								
					}
		}
			}
		}
				
		
		
	}
	
	public void gameOver(){
		Gdx.gl.glClearColor(1f, 1f, 1, 1);//clears background (white)
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);//clears everything
		
		batch.setProjectionMatrix(camera.combined);//camera and canvas match screen
		batch.begin();
		this.menuGameOver.draw(batch);
		this.menuRestart.draw(batch);
		batch.end();
		
		camera.position.x = 400;
		camera.position.y = 240;
		camera.update();
		
		this.menuButton1.x = 275;//350
		this.menuButton1.y = 200;//250
		
		if (Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			Rectangle touch = new Rectangle(touchPos.x -16, touchPos.y - 16, 32, 32);
			if(touch.overlaps(menuButton1)){
			
			gameState = 2;
			}
		}
		
	}
}
