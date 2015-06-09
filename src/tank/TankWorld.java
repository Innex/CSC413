package tank;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.*;

import wingman.game.*;
import wingman.*;
import wingman.ui.*;
import wingman.modifiers.*;
import wingman.modifiers.motions.*;
import wingman.modifiers.weapons.*;

/**
 *
 * @author Inex
 */
public class TankWorld extends GameWorld {

    private Thread thread;

    // GameWorld is a singleton class!
    private static final TankWorld game = new TankWorld();

    /**
     *
     */
    public static final GameSounds sound = new GameSounds();

    /**
     *
     */
    public static final GameClock clock = new GameClock();
    GameMenu menu;

    /**
     *
     */
    public TankLevel level;

    /**
     *
     */
    public static HashMap<String, Image> sprites = GameWorld.sprites;

    private BufferedImage bimg, player1view, player2view;
    int score = 0, life = 1;
    Random generator = new Random();
    int sizeX, sizeY;
    Point mapSize;

    /*Some ArrayLists to keep track of game things*/
    private ArrayList<Bullet> bullets;
    private ArrayList<PlayerShip> players;
    private ArrayList<InterfaceObject> ui;
    //array List for Powerup
    private ArrayList<Ship> powerups;

    /**
     *
     */
    public static HashMap<String, MotionController> motions = new HashMap<String, MotionController>();

    // is player still playing, did they win, and should we exit
    boolean gameOver, gameWon, gameFinished;
    ImageObserver observer;

    // constructors makes sure the game is focusable, then
    // initializes a bunch of ArrayLists
    private TankWorld() {
        this.setFocusable(true);
        background = new ArrayList<BackgroundObject>();
        players = new ArrayList<PlayerShip>();
        ui = new ArrayList<InterfaceObject>();
        //Initialize Bullets
        bullets = new ArrayList<Bullet>();
        //Initialize Powerups
        powerups = new ArrayList<Ship>();
    }

    /* This returns a reference to the currently running game*/

    /**
     *
     * @return
     */
    
    public static TankWorld getInstance() {
        return game;
    }

    /*Game Initialization*/

    /**
     *
     */
    
    public void init() {
        setBackground(Color.white);
        loadSprites();
        gameOver = false;
        observer = this;
        level = new TankLevel("Resources/level.txt");
        level.addObserver(this);
        mapSize = new Point(level.w * 32, level.h * 32);
        GameWorld.setSpeed(new Point(0, 0));
        addBackground(new Background(mapSize.x, mapSize.y, GameWorld.getSpeed(), sprites.get("background")));
        level.load();
        clock.addObserver(level);
    }

    /*Functions for loading image resources*/

    /**
     *
     */
    
    protected void loadSprites() {
        sprites.put("background", getSprite("Resources/Background.png"));
        sprites.put("wall", getSprite("Resources/Blue_wall1.png"));
        sprites.put("wall2", getSprite("Resources/Blue_wall2.png"));
        sprites.put("bullet", getSprite("Resources/bullet.png"));
        sprites.put("powerup", getSprite("Resources/powerup.png"));
        sprites.put("explosion1_1", getSprite("Resources/explosion1_1.png"));
        sprites.put("explosion1_2", getSprite("Resources/explosion1_2.png"));
        sprites.put("explosion1_3", getSprite("Resources/explosion1_3.png"));
        sprites.put("explosion1_4", getSprite("Resources/explosion1_4.png"));
        sprites.put("explosion1_5", getSprite("Resources/explosion1_5.png"));
        sprites.put("explosion1_6", getSprite("Resources/explosion1_6.png"));
        sprites.put("explosion2_1", getSprite("Resources/explosion2_1.png"));
        sprites.put("explosion2_2", getSprite("Resources/explosion2_2.png"));
        sprites.put("explosion2_3", getSprite("Resources/explosion2_3.png"));
        sprites.put("explosion2_4", getSprite("Resources/explosion2_4.png"));
        sprites.put("explosion2_5", getSprite("Resources/explosion2_5.png"));
        sprites.put("explosion2_6", getSprite("Resources/explosion2_6.png"));
        sprites.put("explosion2_7", getSprite("Resources/explosion2_7.png"));
        sprites.put("player1", getSprite("Resources/Tank_blue_basic_strip60.png"));
        sprites.put("player2", getSprite("Resources/Tank_blue_basic_strip60.png"));
    }

    /**
     *
     * @param name
     * @return
     */
    public Image getSprite(String name) {
        URL url = TankWorld.class.getResource(name);
        Image img = java.awt.Toolkit.getDefaultToolkit().getImage(url);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }

    /**
     * ******************************
     * These functions GET things	* from the game world	*
     * ******************************
     * @return 
     */
    public int getFrameNumber() {
        return clock.getFrame();
    }

    /**
     *
     * @return
     */
    public int getTime() {
        return clock.getTime();
    }

    /**
     *
     * @param theObject
     */
    public void removeClockObserver(Observer theObject) {
        clock.deleteObserver(theObject);
    }

    /**
     *
     * @return
     */
    public ListIterator<BackgroundObject> getBackgroundObjects() {
        return background.listIterator();
    }

    /**
     *
     * @return
     */
    public ListIterator<PlayerShip> getPlayers() {
        return players.listIterator();
    }

    /**
     *
     * @return
     */
    public ListIterator<Bullet> getBullets() {
        return bullets.listIterator();
    }

    /**
     *
     * @return
     */
    public int countPlayers() {
        return players.size();
    }

    /**
     *
     * @param w
     * @param h
     */
    public void setDimensions(int w, int h) {
        this.sizeX = w;
        this.sizeY = h;
    }

    /**
     * ******************************
     * These functions ADD things	* to the game world	*
     * ******************************
     * @param newObjects
     */
    public void addBullet(Bullet... newObjects) {
        for (Bullet bullet : newObjects) {
            bullets.add(bullet);
        }
    }

    /**
     *
     * @param newObjects
     */
    public void addPlayer(PlayerShip... newObjects) {
        for (PlayerShip player : newObjects) {
            ui.add(new InfoBar(player, Integer.toString(players.size())));
            players.add(player);
        }
    }

    // add background items (islands)

    /**
     *
     * @param newObjects
     */
        public void addBackground(BackgroundObject... newObjects) {
        for (BackgroundObject object : newObjects) {
            background.add(object);
        }
    }

    // add power ups to the game world

    /**
     *
     * @param powerup
     */
        public void addPowerUp(Ship powerup) {
        powerups.add(powerup);
    }

    /**
     *
     */
    public void addRandomPowerUp() {
        // rapid fire weapon or pulse weapon
        if (generator.nextInt(10) % 2 == 0) {
            powerups.add(new PowerUp(generator.nextInt(sizeX), 1, new SimpleWeapon(5)));
        } else {
            powerups.add(new PowerUp(generator.nextInt(sizeX), 1, new PulseWeapon()));
        }
    }

    /**
     *
     * @param theObject
     */
    public void addClockObserver(Observer theObject) 
    {
        clock.addObserver(theObject);
    }

    // this is the main function where game stuff happens!
    // each frame is also drawn here

    /**
     *
     * @param w
     * @param h
     * @param g2
     */
        public void drawFrame(int w, int h, Graphics2D g2) 
        {
        ListIterator<?> iterator = getBackgroundObjects();
        // iterate through all blocks

        while (iterator.hasNext()) 
        {
            BackgroundObject obj = (BackgroundObject) iterator.next();
            obj.update(w, h);
            obj.draw(g2, this);

            if (obj instanceof BigExplosion || obj instanceof SmallExplosion) 
            {
                if (!obj.show) 
                {
                    iterator.remove();
                }
                continue;
            }

            // check player-block collisions
            ListIterator<PlayerShip> players = getPlayers();
            while (players.hasNext() && obj.show) 
            {
                Tank player = (Tank) players.next();

                //What happens during collision
                if (obj.collision(player)) 
                {
                    // Check player location
                    Rectangle playerLocation = player.getLocation();

                    // Check wat collision is with
                    Rectangle location = obj.getLocation();

                    // If collision happens adjust location by +-2
                    if (playerLocation.x < location.x) 
                    {
                        player.move(-2, 0);
                    } 

                    if (playerLocation.y < location.y) 
                    {
                        player.move(0, -2);
                    }

                    if (playerLocation.x > location.x) 
                    {
                        player.move(2, 0);
                    } 

                    if (playerLocation.y > location.y) 
                    {
                        player.move(0, 2);
                    } 
                } 
            } // end while
        } // end while

        if (!gameFinished) 
        {
            ListIterator<Bullet> bullets = this.getBullets();
            while (bullets.hasNext()) 
            {
                Bullet aBullet = bullets.next();

                //Make sure bullet doesnt pass through object
                if (aBullet.getX() > w || aBullet.getY() > h) 
                {
                    bullets.remove();
                } 
                else 
                {
                    iterator = this.getBackgroundObjects();
                    while (iterator.hasNext()) 
                    {
                        GameObject other = (GameObject) iterator.next();

                        
                        if (other.show && other.collision(aBullet)) 
                        {
                            bullets.remove();
                            addBackground(new SmallExplosion(aBullet.getLocationPoint()));
                            break;
                        }
                    } // end while
                } // end else

                aBullet.draw(g2, this);
            } // end while

            iterator = getPlayers();
            while (iterator.hasNext())
            {
                PlayerShip player = (PlayerShip) iterator.next();

                // if the players is dead
                if (player.isDead()) 
                {
                    gameOver = true;
                    continue;
                } 

                bullets = this.getBullets();
                while (bullets.hasNext()) 
                {
                    Bullet bullet = bullets.next();

                    // Make sure bullet is not from owner
                    //Make sure collides with player
                    //Make sure Player is alive
                    if (bullet.getOwner() != player && bullet.collision(player) && player.respawnCounter <= 0) 
                    {
                        // Do damage to target
                        player.damage(bullet.getStrength());

                       // If Bullet hits target increment score
                        bullet.getOwner().incrementScore(bullet.getStrength());

                        //small explosion is added for every hit
                        addBackground(new SmallExplosion(bullet.getLocationPoint()));
                        //bullet is removed on impact
                        bullets.remove();
                    } 
                } // end while

                ListIterator<Ship> powerups = this.powerups.listIterator();
                while (powerups.hasNext()) 
                {
                    Ship powerup = powerups.next();
                    powerup.draw(g2, this);

                    // If player tank collides with power up
                    if (powerup.collision(player)) 
                    {
                        //Assign Powerup
                        AbstractWeapon weapon = powerup.getWeapon();

                        //Player assigned power 
                        player.setWeapon(weapon);
                        //Remove power up from map
                        powerup.die();
                    } 
                } // end while
            } // end while
            
            PlayerShip p1 = players.get(0);
            PlayerShip p2 = players.get(1);

            // This will check to see if player collides with another player
            if (p1.collision(p2))
            {
                // Get player locations
                Rectangle player1loc = p1.getLocation();
                Rectangle player2loc = p2.getLocation();

                // Set the player location by +-1
                if (player1loc.x < player2loc.x)
                {
                    p1.move(-1, 0);
                } 

                if (player1loc.y < player2loc.y) 
                {
                    p1.move(0, -1);
                } 

                if (player1loc.x > player2loc.x) 
                {
                    p1.move(1, 0);
                } 

                if (player1loc.y > player2loc.y) 
                {
                    p1.move(0, 1);
                } 
            } 

            // Conditional if player 2 collides with player
            if (p2.collision(p1)) {
                // Get player locations 
                Rectangle player1loc = p1.getLocation();
                Rectangle player2loc = p2.getLocation();

                // Set the player location by +- 1
                if (player2loc.x < player1loc.x) 
                {
                    p2.move(-1, 0);
                } 

                if (player2loc.y < player1loc.y) 
                {
                    p2.move(0, -1);
                } 

                if (player2loc.x > player1loc.x) 
                {
                    p2.move(1, 0);
                } 

                if (player2loc.y > player1loc.y)
                {
                    p2.move(0, 1);
                } 
            } 

             // Draw Players
            p1.draw(g2, this);
            p2.draw(g2, this);

            
            // Update Players
            p1.update(w, h);
            p2.update(w, h);

        } else 
        {
            PlayerShip p1 = players.get(1);
            Rectangle p1Location = p1.getLocation();
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            if (p1Location.y < 100)
                p1Location.y = 200;
            g2.drawString("Game Over!", p1Location.x, p1Location.y);
            g2.drawString("Player Score", p1Location.x - 150, p1Location.y - 150);
            int i = 1;
            for (PlayerShip player : players) {
                g2.drawString(player.getName() + ": " + Integer.toString(player.getScore()), p1Location.x - 150, p1Location.y - 150 + (i * 25));
                i++;
            } // end for
        }// end else

    }

    /**
     *
     * @param w
     * @param h
     * @return
     */
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
        if (players.size() != 0) {
            clock.tick();
        }
        Dimension windowSize = getSize();
        Graphics2D g2 = createGraphics2D(mapSize.x, mapSize.y);
        drawFrame(mapSize.x, mapSize.y, g2);
        g2.dispose();

        int p1x = this.players.get(0).getX() - windowSize.width / 4 > 0 ? this.players.get(0).getX() - windowSize.width / 4 : 0;
        int p1y = this.players.get(0).getY() - windowSize.height / 2 > 0 ? this.players.get(0).getY() - windowSize.height / 2 : 0;

        if (p1x > mapSize.x - windowSize.width / 2) {
            p1x = mapSize.x - windowSize.width / 2;
        }
        if (p1y > mapSize.y - windowSize.height) {
            p1y = mapSize.y - windowSize.height;
        }

        int p2x = this.players.get(1).getX() - windowSize.width / 4 > 0 ? this.players.get(1).getX() - windowSize.width / 4 : 0;
        int p2y = this.players.get(1).getY() - windowSize.height / 2 > 0 ? this.players.get(1).getY() - windowSize.height / 2 : 0;

        if (p2x > mapSize.x - windowSize.width / 2) {
            p2x = mapSize.x - windowSize.width / 2;
        }
        if (p2y > mapSize.y - windowSize.height) {
            p2y = mapSize.y - windowSize.height;
        }
        // Adjust the mini map size according to the window size
        player1view = bimg.getSubimage(p1x, p1y, windowSize.width / 2, windowSize.height);
        player2view = bimg.getSubimage(p2x, p2y, windowSize.width / 2, windowSize.height);
        g.drawImage(player1view, 0, 0, this);
        g.drawImage(player2view, windowSize.width / 2, 0, this);
        g.drawImage(bimg, windowSize.width / 2 - 75, 400, 150, 150, observer);
        g.drawRect(windowSize.width / 2 - 1, 0, 1, windowSize.height);
        g.drawRect(windowSize.width / 2 - 76, 399, 151, 151);

        // interface stuff
        ListIterator<InterfaceObject> objects = ui.listIterator();
        int offset = 0;
        while (objects.hasNext()) {
            InterfaceObject object = objects.next();
            object.draw(g, offset, windowSize.height);
            offset += 500;
        }
    }

    /* start the game thread*/

    /**
     *
     */
    
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

    /**
     *
     * @param win
     */
    
    public void endGame(boolean win) {
        this.gameOver = true;
        this.gameWon = win;
    }

    /**
     *
     * @return
     */
    public boolean isGameOver() {
        return gameOver;
    }

    // signal that we can stop entering the game loop

    /**
     *
     */
        public void finishGame() {
        gameFinished = true;
    }


    /*I use the 'read' function to have observables act on their observers.
     */
    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    /**
     *
     * @param argv
     */
    public static void main(String argv[]) {
        final TankWorld game = TankWorld.getInstance();
        JFrame f = new JFrame("413 Tank Game");
        f.addWindowListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                game.requestFocusInWindow();
            }
        });
        f.getContentPane().add("Center", game);
        f.pack();
        f.setSize(new Dimension(900, 600));
        game.setDimensions(800, 600);
        game.init();
        f.setVisible(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameWorld.sound.play("Resources/castlemusic.mp3");
        game.start();
    }
}