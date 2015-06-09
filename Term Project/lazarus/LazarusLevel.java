//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import java.util.ListIterator;
import wingman.game.PlayerShip;
import wingman.modifiers.AbstractGameModifier;

public class LazarusLevel extends AbstractGameModifier implements Observer {

	int start;
	Integer position;
	String filename;
	BufferedReader level;
	Box currentBox;
	Box nextBox;
	int w, h;
	int endgameDelay = 1;	// don't immediately end on game end
	int gameLevel = 0;
	static int MAX_LEVELS=2;
	
	Random generator = new Random();
	
	/*Constructor sets up arrays of enemies in a LinkedHashMap*/
	public LazarusLevel(int GameLevel){
		super();
		setLevel(GameLevel);
		String line;
		try {
			level = new BufferedReader(new InputStreamReader(LazarusWorld.class.getResource(filename).openStream()));
			line = level.readLine();
			w = line.length();
			h=0;
			while(line!=null){
				h++;
				line = level.readLine();
			}
			level.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void setLevel(int Level){
		this.gameLevel = Level;
		if(this.gameLevel>0){
			this.filename = "Resources/level2.txt";
		}else{
			this.filename = "Resources/level.txt";
		}
	}
	public int getGameLevel()
	{
		return (this.gameLevel+1);
	}
	public int getRemainingGameLevel()
	{
		return MAX_LEVELS-this.gameLevel-1;
	}
	public void read(Object theObject){
	}
	
	public void load(){
		LazarusWorld world = LazarusWorld.getInstance();
		
		try {
			level = new BufferedReader(new InputStreamReader(LazarusWorld.class.getResource(filename).openStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	
		String line;
		try {
			line = level.readLine();
			w = line.length();
			h=0;
			while(line!=null){
				for(int i = 0, n = line.length() ; i < n ; i++) { 
				    char c = line.charAt(i); 
				    
				    if(c=='1'){
				    	Wall wall = new Wall(i,h);
				    	world.addBackground(wall);
				    }
				    
				    if(c=='2'){
				    	/// Stop button
				    	StopButton stopbutton = new StopButton(i,h);
				    	world.addStopButton(stopbutton);
				    }
				    
				    if(c=='3'){
				    	/// lazarus initial position
				    	int[] controls = new int[] {KeyEvent.VK_LEFT,KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_ENTER};
						world.addPlayer(new Lazarus(new Point(i*40, h*40),world.sprites.get("player1"), controls, "2"));
				    }
				    
				    if(c=='4'){
				    	// next box indicator
				    	CardBox cardbox = new CardBox(i*40, h*40);
				    	world.addBackground(cardbox);
				    	currentBox = cardbox;
				    	nextBox = cardbox;
				    	
				    }
				    
				}
				h++;
				line = level.readLine();
			}
			
			level.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*Level observes GameClock and updates on every tick*/
	@Override
	public void update(Observable o, Object arg) {
		LazarusWorld world = LazarusWorld.getInstance();
		
		// Testing the box following
		if( (world.isStopFallingBox() == false) && (world.countFallingboxes()<1) ){
			Rectangle playerloc = new Rectangle(160,0,40,40);
			Box box = getRandomBox(0, (11*40));
			currentBox = nextBox;
			 ListIterator<Lazarus> players = world.getPlayers();
			 while(players.hasNext()){
	            	Lazarus player = (Lazarus) players.next();
	            	playerloc = player.getLocation();
	            } 
			currentBox.setLocation(new Point(playerloc.x, 0));
			//System.out.println("Game level is "+gameLevel+" Setting game speed to "+((gameLevel+1)*2));
			currentBox.setSpeed(new Point(0, (gameLevel+1)*2));
			world.addFallingbox(currentBox);
			nextBox = box;
			world.addBackground(nextBox);
	        setChanged();
	        notifyObservers();			
		}else{
			/* check whether this box lands on any other box */
		/*	 
	           
		// stop falling and update restboxes
			if(currentBox.getY() > 360) {
				currentBox.setLocation(new Point(currentBox.getX(), 360));
				currentBox.setSpeed(new Point(0, 0));
				// add in rested
				world.addRestedBox(currentBox);
				// remove from following
				 world.removeFallingbox(currentBox);
				
			}
			*/ 
		}
		
		if(world.isGameOver()){
			if(endgameDelay<=0){
				world.removeClockObserver(this);
				world.finishGame();
			} else endgameDelay--;
		}
	}

	private Box getRandomBox(int x, int y) {
		// TODO Auto-generated method stub
		// get rendom of 4
		Box nbox = null;
		int i = generator.nextInt(4) + 1;
		if(i == 1) {
			nbox = new CardBox(x, y);
		} else if(i == 2) {
			nbox = new WoodBox(x, y);
		} else if(i == 3) {
			nbox = new MetalBox(x, y);
		} else if(i == 4) {
			nbox = new StoneBox(x, y);
		} 
		return nbox;
	}
}
