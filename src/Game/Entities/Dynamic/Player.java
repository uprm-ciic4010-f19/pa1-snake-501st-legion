package Game.Entities.Dynamic;

import Main.Handler;
import sun.applet.Main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Random;

import Game.GameStates.MenuState;
import Game.GameStates.State;

/**
 * Created by AlexVR on 7/2/2018.
 */
public class Player {

	DecimalFormat df = new DecimalFormat("#.#");
	Color randomColor;

	public int lenght;
	public boolean justAte;
	private Handler handler;

	public int xCoord;
	public int yCoord;

	public int currScore;
	public double scoreResult;
	public int steps;
	int gridSize;

	public String direction;//is your first name one?

	public float moveCounter;

	public float speed;

	public Player(Handler handler){
		this.handler = handler;
		xCoord = 0;
		yCoord = 0;
		moveCounter = 0;
		direction= "Right";
		justAte = false;
		lenght= 1;
		currScore = 0;
		scoreResult = 0;
		steps = 0;
		gridSize = handler.getWorld().GridWidthHeightPixelCount-1;
		speed = 5;


	}

	public void tick(){

		int x = xCoord;
		int y = yCoord;
		moveCounter++;
		if(moveCounter>=this.speed) {
			checkCollisionAndMove();
			moveCounter=0;
		}

		//counts steps for use in calculating rotten apples
		if(this.steps > 420) {
			handler.getWorld().getApple().isGood = false;
		}

		//you can add an extra condition in order to make sure it does not go into itself
		//implemented no-backtracking and grow a tail upon pressing 'N' - JFRM
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_UP) && direction != "Down"){
			direction="Up";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_DOWN) && direction != "Up"){
			direction="Down";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_LEFT) && direction != "Right"){
			direction="Left";
		}if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_RIGHT) && direction != "Left"){
			direction="Right";
		}

		//adds a tail by pressing N
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_N)) {		
			debugEat();
		}

		//makes snake move faster
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_EQUALS)){
			this.increaseSpeed();
		}

		//makes snake move slower
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_MINUS)){
			this.decreaseSpeed();
		}

		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_5)) {
			handler.getWorld().appleLocation[3][0] = true;
			handler.getWorld().appleLocation[0][3] = true;
		}

		//pauses game
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) {
			State.setState(handler.getGame().pauseState);
		}


	}

	public void checkCollisionAndMove(){
		handler.getWorld().playerLocation[xCoord][yCoord]=false;
		int x = xCoord;
		int y = yCoord;
		switch (direction){
		case "Left":
			//this teleports player to other end
			if(xCoord==0){
				xCoord = (gridSize);
			}

			//checks if the next tile is a tail, if so call death() - JFRM
			else{
				if(handler.getWorld().playerLocation[xCoord-1][yCoord] && !handler.getWorld().appleLocation[xCoord-1][yCoord]) {
					death();
				}else
					xCoord--;
			}
			break;

		case "Right":

			//this teleports player to other end - JFRM
			if(xCoord==gridSize){
				xCoord = 0;
			}

			//checks if the next tile is a tail, if so call death() - JFRM  
			else{
				if(handler.getWorld().playerLocation[xCoord+1][yCoord] && !handler.getWorld().appleLocation[xCoord+1][yCoord]) {
					death();
				}else
					xCoord++;
			}
			break;

		case "Up":

			//this teleports player to other end - JFRM
			if(yCoord==0){
				yCoord = (gridSize);
			}

			//checks if the next tile is a tail, if so call death() - JFRM
			else{
				if(handler.getWorld().playerLocation[xCoord][yCoord-1] && !handler.getWorld().appleLocation[xCoord][yCoord-1]) {
					death();
				}else
					yCoord--;
			}
			break;

		case "Down":

			//this teleports player to other end - JFRM
			if(yCoord==gridSize){
				yCoord = 0;
			}

			//checks if the next tile is a tail, if so call death() - JFRM
			else{
				if(handler.getWorld().playerLocation[xCoord][yCoord+1] && !handler.getWorld().appleLocation[xCoord][yCoord+1]) {
					death();
				}else
					yCoord++;
			}
			break;
		}

		//		for(int i=1; i<this.lenght-1; i++)
		//			if(handler.getWorld().body.get(i).x == xCoord && handler.getWorld().body.get(i).y == yCoord)
		//				death();

		//increments the steps
		this.steps++;
		handler.getWorld().playerLocation[xCoord][yCoord]=true;

		if(handler.getWorld().appleLocation[xCoord][yCoord]){

			//if apple is good, then eat as normal, else, check if the player body is empty, if so then trigger death, otherwise remove a tail
			if(handler.getWorld().getApple().isGood()) {
				Eat();
			}else{
				if(handler.getWorld().body.isEmpty())
					death();
				else {
					EatRotten();
				}
			}
		}

		if(!handler.getWorld().body.isEmpty()) {
			handler.getWorld().playerLocation[handler.getWorld().body.getLast().x][handler.getWorld().body.getLast().y] = false;
			handler.getWorld().body.removeLast();
			handler.getWorld().body.addFirst(new Tail(x, y,handler));
		}

	}

	public void render(Graphics g,Boolean[][] playeLocation){

		Random r = new Random();
		float red = r.nextFloat();
		float green = r.nextFloat();
		float blue = r.nextFloat();

		for (int i = 0; i < handler.getWorld().GridWidthHeightPixelCount; i++) {
			for (int j = 0; j < handler.getWorld().GridWidthHeightPixelCount; j++) {
				g.setColor(Color.WHITE);
				g.drawString("Score: " + df.format(this.scoreResult), 10, 10);
				g.drawString("Steps: " + this.steps + "/420", 10,20);

				if(playeLocation[i][j]||handler.getWorld().appleLocation[i][j]){
					g.fillRect((i*handler.getWorld().GridPixelsize),
							(j*handler.getWorld().GridPixelsize),
							handler.getWorld().GridPixelsize,
							handler.getWorld().GridPixelsize);
				}

				//changes good apple color
				if(handler.getWorld().appleLocation[i][j]){
					g.setColor(handler.getWorld().getApple().isGood() ? Color.WHITE : Color.BLACK);
					g.fillRect((i*handler.getWorld().GridPixelsize),
							(j*handler.getWorld().GridPixelsize),
							handler.getWorld().GridPixelsize,
							handler.getWorld().GridPixelsize);
				}

				//changes snake color
				if(playeLocation[i][j]){
					g.setColor(randomColor = new Color(red, green, blue));
					g.fillRect((i*handler.getWorld().GridPixelsize),
							(j*handler.getWorld().GridPixelsize),
							handler.getWorld().GridPixelsize,
							handler.getWorld().GridPixelsize);
				}
			}
		}
	}


	public void Eat(){
		this.calculateScore();
		lenght++;
		Tail tail= null;
		handler.getWorld().appleLocation[xCoord][yCoord]=false;
		handler.getWorld().appleOnBoard=false;
		switch (direction){
		case "Left":
			if(handler.getWorld().body.isEmpty()){
				if(this.xCoord!=handler.getWorld().GridWidthHeightPixelCount-1){
					tail=new Tail(this.xCoord+1,this.yCoord,handler);
				}else{
					if(this.yCoord!=0){
						tail=new Tail(this.xCoord,this.yCoord-1,handler);
					}else{
						tail=new Tail(this.xCoord,this.yCoord+1,handler);

					}
				}
			}else{
				if(handler.getWorld().body.getLast().x!=handler.getWorld().GridWidthHeightPixelCount-1){
					tail=new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler);
				}else{
					if(handler.getWorld().body.getLast().y!=0){
						tail=new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler);
					}else{
						tail=new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler);
					}
				}
			}

			break;
		case "Right":
			if( handler.getWorld().body.isEmpty()){
				if(this.xCoord!=0){
					tail=new Tail(this.xCoord-1,this.yCoord,handler);
				}else{
					if(this.yCoord!=0){
						tail=new Tail(this.xCoord,this.yCoord-1,handler);
					}else{
						tail=new Tail(this.xCoord,this.yCoord+1,handler);
					}
				}
			}else{
				if(handler.getWorld().body.getLast().x!=0){
					tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
				}else{
					if(handler.getWorld().body.getLast().y!=0){
						tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler));
					}else{
						tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler));
					}
				}

			}
			break;
		case "Up":
			if( handler.getWorld().body.isEmpty()){
				if(this.yCoord!=handler.getWorld().GridWidthHeightPixelCount-1){
					tail=(new Tail(this.xCoord,this.yCoord+1,handler));
				}else{
					if(this.xCoord!=0){
						tail=(new Tail(this.xCoord-1,this.yCoord,handler));
					}else{
						tail=(new Tail(this.xCoord+1,this.yCoord,handler));
					}
				}
			}else{
				if(handler.getWorld().body.getLast().y!=handler.getWorld().GridWidthHeightPixelCount-1){
					tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler));
				}else{
					if(handler.getWorld().body.getLast().x!=0){
						tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
					}else{
						tail=(new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler));
					}
				}

			}
			break;
		case "Down":
			if( handler.getWorld().body.isEmpty()){
				if(this.yCoord!=0){
					tail=(new Tail(this.xCoord,this.yCoord-1,handler));
				}else{
					if(this.xCoord!=0){
						tail=(new Tail(this.xCoord-1,this.yCoord,handler));
					}else{
						tail=(new Tail(this.xCoord+1,this.yCoord,handler));
					} System.out.println("Tu biscochito");
				}
			}else{
				if(handler.getWorld().body.getLast().y!=0){
					tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler));
				}else{
					if(handler.getWorld().body.getLast().x!=0){
						tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
					}else{
						tail=(new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler));
					}
				}

			}
			break;
		}

		this.increaseSpeed();
		handler.getWorld().body.addLast(tail);
		handler.getWorld().playerLocation[tail.x][tail.y] = true;
	}

	public void kill(){
		lenght = 0;
		for (int i = 0; i < handler.getWorld().GridWidthHeightPixelCount; i++) {
			for (int j = 0; j < handler.getWorld().GridWidthHeightPixelCount; j++) {

				handler.getWorld().playerLocation[i][j]=false;

			}
		}
	}

	public boolean isJustAte() {
		return justAte;
	}

	public void setJustAte(boolean justAte) {
		this.justAte = justAte;
	}

	//calculates the score of the snake
	public void calculateScore() {
		this.currScore++;
		this.scoreResult = Math.sqrt((2*this.currScore) + 1);
	}

	//reduces score
	public void reduceScore() {
		this.scoreResult -= Math.sqrt((2*this.currScore) + 1);
	}

	//resets score to 0
	public void resetScore() {
		this.currScore = 0;
	}

	//death method changes the current state to the MenuState (to be changed) upon colliding with own tail - JFRM
	public void death() {
		this.resetScore();
		State.setState(handler.getGame().deathState);
	}

	public void debugEat() {
		handler.getWorld().body.addFirst(new Tail(xCoord, yCoord, handler));
		this.calculateScore();
	}

	//call this if player eats a rotten apple
	public void EatRotten() {
		this.reduceScore();
		lenght--;
		handler.getWorld().appleLocation[xCoord][yCoord]=false;
		handler.getWorld().appleOnBoard=false;
		handler.getWorld().playerLocation[handler.getWorld().body.getLast().x][handler.getWorld().body.getLast().y]=false;
		handler.getWorld().body.removeLast();
		handler.getWorld().getApple().isGood = true;
	}

	//increase snake speed
	public void increaseSpeed() {
		if(this.speed > 0)
			this.speed -= 1;
		else
			this.speed = this.speed/2;
	}

	//decrease snake speed
	public void decreaseSpeed() {
		this.speed += 1;
	}

}
