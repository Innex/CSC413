//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Point;

public class MetalBox extends Box {

	public MetalBox(int x, int y) {
		super(new Point(x,y), new Point(0,0), 3, LazarusWorld.sprites.get("MetalBox"));
	}

}
