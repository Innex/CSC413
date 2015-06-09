//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.ListIterator;

import tank.TankWeapon;
import tank.TankWorld;
import wingman.GameWorld;
import wingman.WingmanWorld;
import wingman.game.BigExplosion;
import wingman.game.PlayerShip;
import wingman.modifiers.motions.InputController;

public class Lazarus extends PlayerShip {
	int prevRight=0;
	int prevLeft=0;
	int prevUp=0;
	int prevDown=0;
	int lastX = 0;
	int lastY = 0;
	int initialX=0;
	int initialY=0;
	static int MAX_LIVES= 3;
	public Lazarus(Point location, Image img, int[] controls,
			String name) {
		super(location, new Point(0,0), img, controls, name);
		resetPoint = new Point(location);
		lastX = location.x;
		lastY = location.y;
	    initialX = location.x;
	    initialY = location.y;
	    this.name = name;
	    weapon = new TankWeapon();
	    motion = new InputController(this, controls, LazarusWorld.getInstance());
	    lives = MAX_LIVES-1;
	    health = 100;
	    strength = 100;
	    score = 0;
	    respawnCounter=0;
	    height=40;
	    width=40;
	    this.location = new Rectangle(location.x,location.y,width,height);
	}

	private boolean stopOnNextWallOrBox(int Col)
	{
		Wall nWall;
		int PrevLocY = location.y;
		Box nBox;
		int itemsAbove = 0;
		LazarusWorld	world = LazarusWorld.getInstance(); 
		
		ListIterator<Wall> Walllist =world.getRestedwallsAtCol(Col);
		ListIterator<Box> boxlist = world.getRestedboxesAtCol(Col);
    	while(true){
    		if(Walllist.hasNext()){
    			nWall = Walllist.next();
    		}else{
    			nWall = null;
    		}
    		if(boxlist.hasNext()) {
    			nBox = boxlist.next();
    		}else{
    			nBox = null;
    		}
    		if(nBox == null && nWall == null){
    			break;
    		}
    		if(nBox != null) {
    			location.y = PrevLocY;
	    		while(location.y < 480){
		    		if(nBox.collision(this)) {
		    			/* Now search for how many boxes are stacked upon this box */
		    			itemsAbove = world.getNumberOfRestedBoxesAbove(Col,nBox.getY());
		    			if(itemsAbove > 0){
		    				break;
		    			}else{
			    			//System.out.println("##5::1 Returning location.Y "+location.y+" PrevLoc "+PrevLocY);
							location.y-=40;
			    			//System.out.println("##5 Returning location.Y "+location.y+" PrevLoc "+PrevLocY);
			    			return false;
		    			}
		    		}
		    		location.y+=40;
	    		}
    		}
    		location.y = PrevLocY;
    		if(nWall != null){
	    		while(location.y < 480){
		    		location.y+=40;
		    		itemsAbove = world.getNumberOfRestedWallsAbove(Col,nWall.getY());
	    			itemsAbove += world.getNumberOfRestedBoxesAbove(Col,nWall.getY());
	    			if(itemsAbove > 0) {
	    				break;
	    			}else{
			    		if(nWall != null && nWall.collision(this)) {
			    			/* Now search for how many boxes are stacked upon this box */
			    			location.y-=40;
			    		//	System.out.println("##6 Returning location.Y "+location.y);
			    			return false;
			    		}
	    			}
	    		}
    		}
    	}
    	location.y = PrevLocY;
    	//System.out.println("##7 Returning location.Y "+location.y);
		return true;
	}
	
	private boolean isPlayerWallOrBoxCollision()
	{
		int Col = location.x/40;
		int itemsAbove = 0;
		LazarusWorld	world = LazarusWorld.getInstance(); 
		ListIterator<Wall> Walllist = world.getRestedwallsAtCol(Col);
		
		ListIterator<Box> boxlist = world.getRestedboxesAtCol(Col);
    	
		while(boxlist.hasNext()){
    		Box nBox = boxlist.next();
    		if(nBox.collision(this)) {
    			/* Now search for how many boxes are stacked upon this box */
    			itemsAbove = world.getNumberOfRestedBoxesAbove(Col,nBox.getY());
    			//itemsAbove += world.getNumberOfRestedWallsAbove(Col,nBox.getY());
    			if(itemsAbove > 0) {
    				//System.out.println("##1 Returning location.Y "+location.y);
    				return true;
    			}
				//System.out.println("##2 Returning location.Y "+location.y);
    			location.y-=40;
   				return false;
    		}
    	}
    	while(Walllist.hasNext()){
    		Wall nWall = Walllist.next();
    		if(nWall.collision(this)) {
    			/* Now search for how many boxes are stacked upon this box */
    			itemsAbove = world.getNumberOfRestedWallsAbove(Col,nWall.getY());
    			itemsAbove += world.getNumberOfRestedBoxesAbove(Col,nWall.getY());
    			if(itemsAbove > 0) {
    				//System.out.println("##3 Returning location.Y "+location.y);
    				return true;
    			}
    			//System.out.println("##4 Returning location.Y "+location.y);
    			location.y-=40;
   				return false;
    		}
    	}
    	return stopOnNextWallOrBox(Col);
	}
	
	private void setPlayerRespawnLocation()
	{
		int Col = (lastX/40)+1;
		/* Find a free spot in next Column */
		lastX+=40;
		if(Col>16){
			Col=0;
			lastX=0;
		}
		ListIterator<Box> boxlist = LazarusWorld.getInstance().getRestedboxesAtCol(Col);
		int PrevY=400;
    	while(boxlist.hasNext()){
    		Box nBox = boxlist.next();
    		if(PrevY > nBox.getY()){
    			PrevY=nBox.getY();
    		}
    	}
    	if(PrevY == 400){
    		/* Couldn't find a box, search for a wall */
    		ListIterator<Wall> walllist = LazarusWorld.getInstance().getRestedwallsAtCol(Col);
    		while(walllist.hasNext()){
        		Wall nWall = walllist.next();
        		if(PrevY > nWall.getY()){
        			PrevY=nWall.getY();
        		}
        	}
    	}
    	lastY = PrevY-40;
    	//System.out.println("Setting Position to "+lastX+":"+lastY);
    	this.setLocation(new Point(lastX, lastY));
		
	}
	private boolean isStopButtonPressed()
	{
		boolean breturn = false;
		ListIterator<StopButton> stoplist = LazarusWorld.getInstance().getStopButtons();
    	while(stoplist.hasNext()){
    		StopButton stopBut = stoplist.next();
    		if(stopBut.collision(this)) {
    			breturn = true;
    			break;
    		}
    	}
    	return breturn;
	}

	public int getLivesRemaining(){
		return MAX_LIVES-lives;
	}
	public int getMaxLives(){
		return MAX_LIVES;
	}
	
	
    public void update(int w, int h) {
    	LazarusWorld world = LazarusWorld.getInstance();
    	/*if(isFiring){
    		int frame = TankWorld.getInstance().getFrameNumber();
    		if(frame>=lastFired+weapon.reload){
    			fire();
    			lastFired= frame;
    		}
    	}*/
    	if(prevRight != right || prevLeft != left){
    		/* This is not the right way to do things. We should install a handler for keypress and keyrelease.
    		 * Handling key events in update is not proper
    		 */
    		prevRight = right;
    		prevLeft = left;
    		if(right == 1 || left == 1){   			
    			
    			location.x+=(right-left)*40;
    			
    			/* Check for Stop press*/
    			if(isStopButtonPressed()) {
    				// Win
    				world.setStopFallingBox(true);
    				this.show=false;
    				world.endGame(true);
    	    		//S
    	    		System.out.println("Game Over");
    	    		this.motion.delete(this);
    	    		GameWorld.sound.playmp3("Resources/lazarus/Music.mp3");    
    			}else {
	    			/* Check collision with wall's */
	    			//if(isPlayerWallCollision() == true || isPlayerBoxCollision() == true) {
	    			if(isPlayerWallOrBoxCollision() == true){
	    				GameWorld.sound.play("Resources/lazarus/Wall.wav");
	    				location.x-=(right-left)*40;
	    			}else{
	    				GameWorld.sound.play("Resources/lazarus/Move.wav");
	    			}
    			}
    		}
    	}
    	if(location.y<0) location.y=0;
    	if(location.y>h-this.height) location.y=h-this.height;
    	if(location.x<0) location.x=0;
    	if(location.x>w-this.width) location.x=w-this.width;
    }
    
    public void draw(Graphics g, ImageObserver observer) {
    	if(respawnCounter<=0){
    		g.drawImage(img, location.x, location.y, observer);
        	LazarusWorld.getInstance().setStopFallingBox(false);
    	}
    	else if(respawnCounter==80){
    		setPlayerRespawnLocation();
    		WingmanWorld.getInstance().addClockObserver(this.motion);
    		respawnCounter -=1;
    	}
    	else if(respawnCounter<80){
    		if(respawnCounter%2==0) g.drawImage(img, location.x, location.y, observer);
    		respawnCounter -= 1;
    	}
    	else{
    		g.drawImage(LazarusWorld.getInstance().sprites.get("Lazarus_squished"), location.x, location.y, null);
    		respawnCounter -= 1;
    	}
    }
    
    public void die(){
    	LazarusWorld world = LazarusWorld.getInstance();
    	if(respawnCounter > 0 || world.isGameOver()){
    		return;	/* You cannot die while you are respawning or while game is over*/
    	}
    	this.show=false;
		GameWorld.sound.play("Resources/lazarus/Squished.wav");
		GameWorld.setSpeed(new Point(0,0));
    	//BigExplosion explosion = new BigExplosion(new Point(location.x,location.y));
    	//LazarusWorld.getInstance().addBackground(explosion);
    	lives-=1;
    	if(lives>=0){
    		world.removeClockObserver(this.motion);
       // 	System.out.println("Die :: " + location.x + "  " + location.y);
        	lastX = location.x;
        	lastY = location.y - 40;
        	//System.out.println("Last :: " + lastX + "  " + lastY);
        	world.setStopFallingBox(true);
        	reset();
    	} else {
    		world.setStopFallingBox(true);
    		world.endGame(false);
    		this.img = LazarusWorld.getInstance().sprites.get("Lazarus_squished");
    		//System.out.println("Game Over");
    		this.motion.delete(this);
    	}
    }	
    
    public void reset(){
    //	System.out.println("Resetted" + location.x + "  " + location.y);
    //	System.out.println("Last :: " + lastX + "  " + lastY);
    	health=strength;
    	respawnCounter=160;
    }
}
