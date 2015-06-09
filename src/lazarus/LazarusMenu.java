//Hossein Niazmandi
//9130464794
//Final Version 
//Lazarus Game CSC 413
package lazarus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Observable;

import wingman.modifiers.AbstractGameModifier;
import wingman.modifiers.motions.MenuController;
import wingman.ui.GameMenu;

public class LazarusMenu extends GameMenu {

	int selection;
	MenuController controller;
	boolean waiting;
	
	public LazarusMenu(){
		selection = 0;
		controller = new MenuController(this);
		waiting = true;
		 LazarusWorld.getInstance().addKeyListener(controller);
	}
	public void draw(Graphics g2, int x, int y){
		g2.setFont(new Font("Calibri", Font.PLAIN, 24));
		if(selection==0)
			g2.setColor(Color.RED);
		else
			g2.setColor(Color.WHITE);
		g2.drawString("Level 1", 200,150);
		if(selection==1)
			g2.setColor(Color.RED);
		else
			g2.setColor(Color.WHITE);
		g2.drawString("Level 2", 200, 250);
		if(selection==2)
			g2.setColor(Color.RED);
		else
			g2.setColor(Color.WHITE);
		g2.drawString("Quit", 200, 350);
	}
	
	public void down(){
		if(selection<2)
			selection++;
	}
	
	public void up(){
		if(selection>0)
			selection--;
	}
	
	public void applySelection(){
		LazarusWorld world = LazarusWorld.getInstance();
		if(selection == 2){
			System.exit(0);
		}
			
		//WingmanWorld.sound.playmp3("Resources/strobe.mp3");
		
		controller.deleteObservers();
		world.removeKeyListener(controller);
		world.loadnewLevel(selection);
		waiting=false;
	}
	
	public void update(Observable o, Object arg) {
		AbstractGameModifier modifier = (AbstractGameModifier) o;
		modifier.read(this);
	}
	
	public boolean isWaiting(){
		return this.waiting;
	}
}
