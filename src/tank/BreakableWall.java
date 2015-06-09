package tank;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.ImageObserver;

import wingman.game.BackgroundObject;
import wingman.game.GameObject;
/**
 *
 * @author Inex
 */
public class BreakableWall extends BackgroundObject 
{

    int timer = 400;

    public BreakableWall(int x, int y) {
        super(new Point(x * 32, y * 32), new Point(0, 0), TankWorld.sprites.get("wall2"));
    }

    //You need to fill in here  	
    /*
    @param otherObject
    @return
    */

    public boolean collision(GameObject otherObject) //this function should check to see if Tank/
    {
        if (location.intersects(otherObject.getLocation())) 
        {
            if (otherObject instanceof TankBullet)
            {
                this.show = false;
            }
            return true;
        }
        return false;
    } // end collision

    //You need to fill in here     
    /*
    @param g
    @param obs
    */
    public void draw(Graphics g, ImageObserver obs) 
    {
        if (!show) 
        {
            this.timer--;
            if (this.timer < 0)
            {
                this.timer = 400;
                this.show = true;
            } // end if
        } else {
            super.draw(g, obs);
        } // end else
    }
}
