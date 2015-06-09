//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Image;
import java.awt.Point;
import wingman.game.Ship;

public class Box extends Ship {

	public Box(Point position, Point speed, int strength, Image img) {
		super(position, speed, strength, img);
		// TODO Auto-generated constructor stub
	}
	
	public void setSpeed(Point pt) {
		this.speed = pt;
	}

	public int getStrength(){
		return this.strength;
	}
    public void fire()
    {
    	//weapon.fireWeapon(this);
    }
}
