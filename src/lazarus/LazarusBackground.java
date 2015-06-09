//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Image;
import java.awt.Point;

import wingman.game.BackgroundObject;
import wingman.game.GameObject;

public class LazarusBackground extends BackgroundObject {
	int w, h;
	
	public LazarusBackground(int w, int h, Point speed, Image img) {
		super(new Point(0, 0), speed, img);
		this.setImage(img);
		this.img = img;
		this.w = w;
		this.h = h;
		//System.out.println("Lazarus XX " + w + " : " + h);
		//System.out.println("LazarusBackground " + getSizeX() + " : " + getSizeY());
	}
		
    public void update(int w, int h) {
    }

    public boolean collision(GameObject otherObject) {
        return false;
    }	
}
