//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Point;

import wingman.game.BackgroundObject;

public class StopButton extends BackgroundObject {

	
	public StopButton(int x, int y) {
		super(new Point(x*40, y*40), new Point(0,0), LazarusWorld.sprites.get("StopButton"));
	}

}
