//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JFrame;

import wingman.GameClock;
import wingman.GameSounds;
import wingman.GameWorld;
import wingman.game.BackgroundObject;
import wingman.game.BigExplosion;
import wingman.game.Bullet;
import wingman.game.SmallExplosion;
import wingman.modifiers.AbstractGameModifier;
import wingman.modifiers.motions.MotionController;
import wingman.ui.InfoBar;
import wingman.ui.InterfaceObject;

public class LazarusWorld extends GameWorld {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Thread thread;
    
    // GameWorld is a singleton class!
    private static final LazarusWorld game = new LazarusWorld();
    public static final GameSounds sound = new GameSounds();
    public static final GameClock clock = new GameClock();
    LazarusMenu menu;
    public LazarusLevel level;

	public static HashMap<String,Image> sprites = GameWorld.sprites;
    private BufferedImage bimg;
    int score = 0;
    Random generator = new Random();
    int sizeX, sizeY;
    Point mapSize;
    boolean stopFallingBox = false;
	long startTime=0;
	long endTime=0;
    
    /*Some ArrayLists to keep track of game things*/
    private ArrayList<Lazarus> players;
    private ArrayList<InterfaceObject> ui;
    private ArrayList<Box> boxes;
    private ArrayList<ArrayList<Wall>> walls;
    private ArrayList<Box> fallingboxes;
    private ArrayList<StopButton> stopButtons;
    private ArrayList<ArrayList<Box>> restedboxes;
    
    public static HashMap<String, MotionController> motions = new HashMap<String, MotionController>();

    // is player still playing, did they win, and should we exit
    boolean gameOver, gameWon, gameFinished;
    ImageObserver observer;
        

	private LazarusWorld() {
		this.setFocusable(true);
		background = new ArrayList<BackgroundObject>();
		players = new ArrayList<Lazarus>();
		ui = new ArrayList<InterfaceObject>();
		boxes = new ArrayList<Box>();
		walls = new ArrayList<ArrayList<Wall>>(16);
		for(int i=0;i<16;i++){
			walls.add(new ArrayList<Wall>());
		}
		fallingboxes = new ArrayList<Box>();
		stopButtons = new ArrayList<StopButton>();
		restedboxes = new ArrayList<ArrayList<Box>>(16);	/* max 16 columns of rested boxes:: 640/40 = 16 */
		for(int i=0;i<16;i++){
			restedboxes.add(new ArrayList<Box>());
		}

	}
	//Couldnt get function working in reasonable amount of time
	public void resetWorld() {
		/* TODO: Take game to next Level */
	}


    public static LazarusWorld getInstance(){
    	return game;
    }

    /*Game Initialization*/
    public void init() {
        setBackground(Color.white);
        loadSprites();
         
        gameOver = false;
        observer = this;
        
        
        level = new LazarusLevel(0);
        level.addObserver(this);
        clock.addObserver(level);
        
        mapSize = new Point(level.w*40,level.h*40);
        GameWorld.setSpeed(new Point(0,0));

        addBackground(new LazarusBackground(mapSize.x,mapSize.y,GameWorld.getSpeed(), sprites.get("background")));
        menu = new LazarusMenu(); 	/* XXX: Comment this to remove menu */
	//	level.load(); /* XXX: Un-Comment this to remove menu */
		startTime = System.currentTimeMillis();
		
    }
    
    /********************************
     * 	These functions GET things	*
     * 		from the game world		*
     ********************************/
    
    public int getFrameNumber(){
    	return clock.getFrame();
    }
    
    public int getTime(){
    	return clock.getTime();
    }
    
    public void removeClockObserver(Observer theObject){
    	clock.deleteObserver(theObject);
    }
    
    public ListIterator<BackgroundObject> getBackgroundObjects(){
    	return background.listIterator();
    }

    public ListIterator<StopButton> getStopButtons(){
    	return stopButtons.listIterator();
    }

    public ListIterator<Lazarus> getPlayers(){
    	return players.listIterator();
    }
    
    
    public int countPlayers(){
    	return players.size();
    }
    
    public ListIterator<Box> getFallingboxes(){
    	return fallingboxes.listIterator();
    }
    
    public int countFallingboxes(){
    	return fallingboxes.size();
    }    
    
    public void setDimensions(int w, int h){
    	this.sizeX = w;
    	this.sizeY = h;
    }
    
    public boolean isStopFallingBox()
    {
    	return stopFallingBox;
    }
    public void setStopFallingBox(boolean value)
    {
    	stopFallingBox = value;
    }
    /********************************
     * 	These functions ADD things	*
     * 		to the game world		*
     ********************************/
    
    public void addFallingbox(Box...newObjects){
    	for(Box box : newObjects){
    		fallingboxes.add(box);
    	}
    }    

    public void addStopButton(StopButton...newObjects){
    	for(StopButton stopb : newObjects){
    		stopButtons.add(stopb);
    	}
    }    

    public void removeFallingbox(Box...newObjects){
    	for(Box box : newObjects){
    		fallingboxes.remove(box);
    	}
    }
    
    public void addBackground(BackgroundObject...newObjects){
    	for(BackgroundObject object : newObjects){
    		background.add(object);
    		//System.out.println("Adding background " + object.getSizeX() + " " + object.getSizeY());
    		if (object instanceof LazarusBackground) {
    			System.out.println("Lazarus background added"); 
			}
    	}
    }

    public void addPlayer(Lazarus...newObjects){
    	for(Lazarus player : newObjects){
    		players.add(player);
    		ui.add(new InfoBar(player,Integer.toString(players.size())));
    	}    	
    }

	public void addBackground(Box...cardbox) {
    	for(Box object : cardbox){
    		boxes.add(object);
    	}
	}
	public void addBackground(Wall...newObjects){
    	for(Wall object : newObjects){
    		Rectangle wallLocation = object.getLocation();
			int wallCol = wallLocation.x/40;
    		walls.get(wallCol).add(object);
    	}
	}
	public void addRestedWall(int Col,Wall...restedWall){
		for(Wall object : restedWall){
			walls.get(Col).add(object);
		}
		//Collections.sort(restedboxes.get(Col);
	}
	public void removeRestedWall(Wall...restedWall){
		for(Wall object : restedWall){
			Rectangle WallLocation = object.getLocation();
			int WallCol = WallLocation.x/40;
			walls.get(WallCol).remove(object);
		}
	}
	public ListIterator<Wall> getRestedwallsAtCol(int Col){
    	return walls.get(Col).listIterator();
    }
	public void removeRestedWallInColAtIndex(int Col,int index){
		walls.get(Col).remove(index);
	}
	public int getNumberOfRestedWallsInCol(int Col){
		return walls.get(Col).size();
	}
	public int getNumberOfRestedWallsAbove(int Col,int LocationY)
	{
		int count=0;
		ListIterator<Wall> Walllist = getRestedwallsAtCol(Col);
		while(Walllist.hasNext()){
			Wall nWall = Walllist.next();
			if(nWall.getY() < LocationY){
				count++;
			}
		}
		return count;
	}


	public void addRestedBox(Box...restedBox){
		for(Box object : restedBox){
			Rectangle boxLocation = object.getLocation();
			int boxCol = boxLocation.x/40;
			restedboxes.get(boxCol).add(object);
		}
	}
	public void addRestedBox(int Col,Box...restedBox){
		for(Box object : restedBox){
			restedboxes.get(Col).add(object);
		}
		//Collections.sort(restedboxes.get(Col);
	}
	public void removeRestedBox(Box...restedBox){
		for(Box object : restedBox){
			Rectangle boxLocation = object.getLocation();
			int boxCol = boxLocation.x/40;
			restedboxes.get(boxCol).remove(object);
		}
	}
	public ListIterator<Box> getRestedboxesAtCol(int Col){
    	return restedboxes.get(Col).listIterator();
    }
	public void removeRestedBoxInColAtIndex(int Col,int index){
		restedboxes.get(Col).remove(index);
	}
	public int getNumberOfRestedBoxesInCol(int Col){
		return restedboxes.get(Col).size();
	}
	
	public int getRemainingGameLevel()
	{
		return level.getRemainingGameLevel();
	}
	public void loadnewLevel(int Level)
	{
		level.setLevel(Level);
		level.load();
	}

	public int getNumberOfRestedBoxesAbove(int Col,int LocationY)
	{
		int count=0;
		ListIterator<Box> boxlist = getRestedboxesAtCol(Col);
		while(boxlist.hasNext()){
			Box nBox = boxlist.next();
			if(nBox.getY() < LocationY){
				count++;
			}
		}
		return count;
	}

	
	
    public void drawFrame(int w, int h, Graphics2D g2) {
        ListIterator<?> iterator = getBackgroundObjects();
        // iterate through all blocks
        while(iterator.hasNext()){
        	BackgroundObject obj = (BackgroundObject) iterator.next();
            obj.update(w, h);
            obj.draw(g2, this);
            
            if(obj instanceof BigExplosion || obj instanceof SmallExplosion){
            	if(!obj.show) iterator.remove();
            	continue;
            }
            /* Check if any falling box collides with player */
            ListIterator<Box> fboxes = getFallingboxes();
            while(fboxes.hasNext()){
            	Box fallingBox = fboxes.next();
            	Rectangle fallingLoc = fallingBox.getLocation();
            	int	colFalling = fallingLoc.x/40;
            	/* First check if it collides with player. If so game end */
            	ListIterator<Lazarus> players = getPlayers();
                while(players.hasNext()){
                	Lazarus player = (Lazarus) players.next();
                	if(fallingBox.collision(player)){
                		player.die();
                		break;
                	}
                }
            	
            	/* Now check if it collides with any rested boxes */
                ListIterator<Box> rbox = getRestedboxesAtCol(colFalling);
                boolean fallingStopped = false;
                while(rbox.hasNext()){
                	Box restedBox = rbox.next();
                	if(fallingBox.collision(restedBox)){
                		if(restedBox.getStrength() < fallingBox.getStrength()) {
                			/* Destroy the rested box and continue */
                			restedBox.hide();
                			rbox.remove();
                			GameWorld.sound.play("Resources/lazarus/Crush.wav");
                			
                		}else{
                			/* Rest falling box on top of this box */
                			int difference = fallingBox.getY()%40;
                			if( difference != 0) {
                				fallingBox.setLocation(new Point(fallingBox.getX(),fallingBox.getY()-difference));
                			}
                			rbox.add(fallingBox);
                			fboxes.remove();
                			fallingStopped = true;
                		}
                		
                	}
                	//restedBox.draw(g2, this);
                }
                
                if(fallingStopped == false) {
	                /* Now check if it collides with any wall */
	                ListIterator<Wall> rWall = getRestedwallsAtCol(colFalling);
	                while(rWall.hasNext()){
	                	Wall restedWall = rWall.next();
	                	if(fallingBox.collision(restedWall)){
	                		/* Rest falling box on top of this box */
	            			int difference = fallingBox.getY()%40;
	            			if( difference != 0) {
	            				fallingBox.setLocation(new Point(fallingBox.getX(),fallingBox.getY()-difference));
	            			}
	            			addRestedBox(fallingBox);
	            			fboxes.remove();
	            			fallingStopped = true;          	
	                	}
	                	//restedBox.draw(g2, this);
	                }
                }
                /* For now this is hard coded to 360. Logically we should check if it collides with
                 * any of the walls.
                 */
                if(fallingStopped == false && fallingBox.getY() > 360) {
                	/* Set the box to y = 360 */
                	fallingBox.setLocation(new Point(fallingBox.getX(),360));
                	addRestedBox(colFalling,fallingBox);
        			fboxes.remove();
    			} 
            }
        }
        
        if (menu.isWaiting()){ /* XXX: Comment this to remove menu */
    		menu.draw(g2, w, h);/* XXX: Comment this to remove menu */
    	}else	/* XXX: Comment this to remove menu */ 
    		if (!gameFinished) { 
            // update players and draw
            iterator = getPlayers();
            while(iterator.hasNext()){
            	Lazarus player = (Lazarus) iterator.next();
            	
            	if(player.isDead()){
        			gameOver=true;
        			continue;
            	}
            	
                ListIterator<Box> boxList = this.boxes.listIterator();
            	while(boxList.hasNext()){
            		Box nbox = boxList.next();
            		nbox.draw(g2, this);
            	} 
            	
            	ListIterator<StopButton> stopbList = this.stopButtons.listIterator();
            	while(stopbList.hasNext()){
            		StopButton button = stopbList.next();
            		button.draw(g2, this);
            	} 
            	
            	ListIterator<Box> fboxList = this.fallingboxes.listIterator();
            	while(fboxList.hasNext()){
            		Box nbox = fboxList.next();
            		nbox.update(w, h);
            		nbox.draw(g2, this);
	        	}

            	for(int Col=0;Col<16;Col++){
	            	ListIterator<Wall> wallList = getRestedwallsAtCol(Col);
	            	while(wallList.hasNext()){
	            		Wall nWall = wallList.next();
	            		nWall.draw(g2, this);
	            	}
            	}
            	
            	for(int Col=0;Col<16;Col++){
	            	ListIterator<Box> rboxList = getRestedboxesAtCol(Col);
	            	while(rboxList.hasNext()){
	            		Box nbox = rboxList.next();
	            		if(nbox.collision(player)) {
	            			player.die();
	            			break;
	            		}
	            		nbox.draw(g2, this);
	            	}
            	}
            }
            
            Lazarus p1 = players.get(0);
            
            p1.update(w, h);
            
        	p1.draw(g2, this);
        	g2.setFont(new Font("Calibri", Font.PLAIN, 20));
        	
    		g2.setColor(Color.WHITE);
    		g2.drawString("Lives: "+p1.getLivesRemaining()+"/"+p1.getMaxLives(), 10, 20);
    		//g2.drawString("Level: "+level.getGameLevel()+"/"+level.MAX_LEVELS, 10, 40);
        }
    	// end game stuff
        else{
    		g2.setColor(Color.WHITE);
    		if(endTime <= 0){
    			endTime = System.currentTimeMillis();
    		}
    		int LocationY= sizeY/2;
    		if(!gameWon){
    			g2.setFont(new Font("Calibri", Font.PLAIN, 30));
            	
        		
        		g2.drawString("Sucks to Suck!! Better luck next time", sizeX/6, LocationY);
        		g2.drawString("Time Played: "+((endTime-startTime)/1000)+" seconds", sizeX/6, LocationY+40);
        	}
        	else{
        		g2.setFont(new Font("Calibri", Font.PLAIN, 36));
            	g2.drawString("Congrats!!! You Won!!!!", sizeX/4, LocationY);
        		g2.drawString("Time Played: "+((endTime-startTime)/1000)+" seconds", sizeX/4, LocationY+40);
        	}
        	/*
    		g2.drawString("Score", sizeX/3, 400);
    		int i = 1;
        	for(Lazarus player : players){
        		g2.drawString(player.getName() + ": " + Integer.toString(player.getScore()), sizeX/3, 375+50*i);
        		i++;
        	}
        	*/
        }
  
    }
    
    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }

    /* paint each frame */
    public void paint(Graphics g) {
        if(players.size()!=0)
        	clock.tick();
    	Dimension windowSize = getSize();
        Graphics2D g2 = createGraphics2D(windowSize.width, windowSize.height);
        drawFrame(windowSize.width, windowSize.height, g2);
        g2.dispose();
        g.drawImage(bimg, 0, 0, this);
    }

    public void addClockObserver(Observer theObject){
    	clock.addObserver(theObject);
    }

	@Override
	protected void loadSprites() {
	    sprites.put("background", getSprite("Resources/Background.gif"));
	    sprites.put("Lazarus_squished", getSprite("Resources/Lazarus_squished.png"));
		 
	    sprites.put("wall", getSprite("Resources/Wall.gif"));
	    sprites.put("Mesh", getSprite("Resources/Mesh.gif"));
	    
	    sprites.put("CardBox", getSprite("Resources/CardBox.gif"));
	    sprites.put("WoodBox", getSprite("Resources/WoodBox.gif"));
	    sprites.put("MetalBox", getSprite("Resources/MetalBox.gif"));
	    sprites.put("StoneBox", getSprite("Resources/StoneBox.gif"));
	    
	    sprites.put("StopButton", getSprite("Resources/Button.gif"));
		sprites.put("Lazarus_afraid", getSprite("Resources/Lazarus_afraid.gif"));
		sprites.put("Lazarus_jump_left", getSprite("Resources/Lazarus_jump_left.gif"));
		sprites.put("Lazarus_jump_right", getSprite("Resources/Lazarus_jump_right.gif"));
		sprites.put("Lazarus_left", getSprite("Resources/Lazarus_left.gif"));
		sprites.put("Lazarus_right", getSprite("Resources/Lazarus_right.gif"));
	   sprites.put("Lazarus_stand", getSprite("Resources/Lazarus_stand.png"));
		sprites.put("Title", getSprite("Resources/Title.gif"));
		
		sprites.put("LazarusIcon", getSprite("Resources/lazarus.ico"));
		
		
		sprites.put("player1", getSprite("Resources/Lazarus_stand.png"));
		
	}

    public Image getSprite(String name) {
        URL url = LazarusWorld.class.getResource(name);
        Image img = java.awt.Toolkit.getDefaultToolkit().getImage(url);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }
    
    /* start the game thread*/
    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /* run the game */
    public void run() {
    	
        Thread me = Thread.currentThread();
        while (thread == me) {
        	this.requestFocusInWindow();
            repaint();
          
          try {
                thread.sleep(23); // pause a little to slow things down
            } catch (InterruptedException e) {
                break;
            }
            
        }
    }
    
    /* End the game, and signal either a win or loss */
    public void endGame(boolean win){
    	this.gameOver = true;
    	this.gameWon = win;
    }
    
    public boolean isGameOver(){
    	return gameOver;
    }
    
    // signal that we can stop entering the game loop
    public void finishGame(){
    	gameFinished = true;
    }
    

    /*I use the 'read' function to have observables act on their observers.
     */
	@Override
	public void update(Observable o, Object arg) {
		AbstractGameModifier modifier = (AbstractGameModifier) o;
		modifier.read(this);
	}
	
	public static void main(String argv[]) {
		
	    final LazarusWorld game = LazarusWorld.getInstance();
	    JFrame f = new JFrame("Lazarus");
	    f.addWindowListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        game.requestFocusInWindow();
		    }
	    });
	    f.getContentPane().add("Center", game);
	    f.pack();
	    f.setSize(new Dimension(640, 505));
	    game.setDimensions(640, 480);
	    game.init();
	 		
	    f.setVisible(true);
	    f.setResizable(false);
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GameWorld.sound.playmp3("Resources/lazarus/Music.mp3");
	    game.start();
	}

	@Override
	public void addBullet(Bullet... newObjects) {
		// TODO Auto-generated method stub
		
	}
}
