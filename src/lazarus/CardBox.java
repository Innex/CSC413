//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Point;

public class CardBox extends Box {

	public CardBox(int x, int y) {
		super(new Point(x,y), new Point(0,0), 1, LazarusWorld.sprites.get("CardBox"));
	}

}
