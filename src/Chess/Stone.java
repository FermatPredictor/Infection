package Chess;

import processing.core.PApplet;

public class Stone {
	
	public int step;
	char color;
	boolean isDead=false;
	int x,y;
	int boardSize;
	protected PApplet parent;
	protected ChessBoard board;
	
	public Stone(int x, int y, int step, char color, PApplet parent, ChessBoard board){

		this.x=x;
		this.y=y;
		this.step=step;
		this.color=color;
		this.parent=parent;
		this.board=board;
	}
	

}
