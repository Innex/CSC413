package tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Observable;

import wingman.GameWorld;
import wingman.game.BigExplosion;
import wingman.game.PlayerShip;
import wingman.game.SmallExplosion;
import wingman.modifiers.AbstractGameModifier;
import wingman.modifiers.motions.InputController;
import wingman.modifiers.weapons.SimpleWeapon;
/**
 *
 * @author Inex
 */
public class Tank extends PlayerShip {
	int direction;
	
    public Tank(Point location, Image img, int[] controls, String name) {
        super(location, new Point(0,0), img, controls, name);
        resetPoint = new Point(location);
        this.gunLocation = new Point(32,32);
        
        this.name = name;
        weapon = new TankWeapon();
        motion = new InputController(this, controls, TankWorld.getInstance());
        lives = 3;
        health = 100;
        strength = 100;
        score = 0;
        respawnCounter=0;
        height=64;
        width=64;
        direction=180;
        this.location = new Rectangle(location.x,location.y,width,height);
    }
  //You need to fill in here    
    //angle for turning
        public void turn(int angle) {
        this.direction = this.direction + angle;

        if (this.direction < 0) {
            this.direction = 359;
        } // end if

        if (this.direction >= 360) {
            this.direction = 0;
        } // end if
    }
  //You need to fill in here  
    public void update(int w, int h) {
        
        //keypressed to update the tanks position based on up
        //and down key pressed for turning
        //Multiplies this direcitonal movement by 3
    	if(up==1 || down==1)
        {
            int dy = (int)(4*(double)Math.cos(Math.toRadians(direction+90)));
            int dx = (int)(4*(double)Math.sin(Math.toRadians(this.direction+90)));
            this.location.x = this.location.x + dx * (up - down);
            this.location.y = this.location.y + dy * (up - down);
        }
    	//keypresses to update the tanks postions based on left 
        //and right keys pressed for turning.
        // Multiplies this turning by 4.
    	if(right==1 || left==1)
        {
    		this.turn(4*(left-right));
    	}

        if (location.x < 0) 
        {
            this.location.x = 0;
        } 
        
        if (this.location.y < 0)
        {
            this.location.y = 0;
        } 
        
        if (location.x > w - this.width)
        {
            this.location.x = w - this.width;
        } 
        
        if (this.location.y > h - this.height) 
        {
            this.location.y = h - this.height;
        } 
        
        if (isFiring) {
            int bulletFrame = TankWorld.getInstance().getFrameNumber();

            if (bulletFrame >= lastFired + weapon.reload) {
                fire();
                lastFired = bulletFrame;
            } 
        } 
    }
    
    
    public void draw(Graphics g, ImageObserver obs) {
    	if(respawnCounter<=0)
    		g.drawImage(img,  //the image
    				location.x,location.y,  //destination top left
    				location.x+this.getSizeX(), location.y+this.getSizeY(),  //destination lower right
    				(direction/6)*this.getSizeX(), 0,  //source top left
    				((direction/6)*this.getSizeX())+this.getSizeX(),this.getSizeY(),  //source lower right
    				obs);
    	else if(respawnCounter==80){
    		TankWorld.getInstance().addClockObserver(this.motion);
    		respawnCounter -=1;
        	System.out.println(Integer.toString(respawnCounter));
    	}
    	else if(respawnCounter<80){
    		if(respawnCounter%2==0)
        		g.drawImage(img,  //the image
        				location.x,location.y,  //destination top left
        				location.x+this.getSizeX(), location.y+this.getSizeY(),  //destination lower right
        				(direction/6)*this.getSizeX(), 0,  //source top left
        				((direction/6)*this.getSizeX())+this.getSizeX(),this.getSizeY(),  //source lower right
        				obs);
    		respawnCounter -= 1;
    	}
    	else
    		respawnCounter -= 1;
    }
    
    public void die(){
    	this.show=false;
    	GameWorld.setSpeed(new Point(0,0));
    	BigExplosion explosion = new BigExplosion(new Point(location.x,location.y));
    	TankWorld.getInstance().addBackground(explosion);
    	lives-=1;
    	if(lives>=0){
        	TankWorld.getInstance().removeClockObserver(this.motion);
    		reset();
    	}
    	else{
    		this.motion.delete(this);
    	}
    }
    
    public void reset(){
    	this.setLocation(resetPoint);
    	health=strength;
    	respawnCounter=160;
    	this.weapon = new TankWeapon();
    }

}
