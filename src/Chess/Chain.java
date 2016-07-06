package Chess;

import java.util.ArrayList;

//this class is used for AI, which can judge the stone that connected

public class Chain {
	public char color;//b:black, w:white
	public int safety;//0~64, 0 for dead;
	public int freedom;//1~4,and if >4, set 5.
	public int stoneNum;
	public int controlClosedAreaNum;//record the area that points<8 (if the controlClosedAreaNum>1, it is alive.)
	public int controlClosedAreapoints;
	public int[] points=new int[2];
	
	public Chain(char color, int freedom, int stoneNum, int controlClosedAreaNum){
		this.color=color;
		this.freedom=freedom;
		this.stoneNum=stoneNum;
		this.controlClosedAreaNum=controlClosedAreaNum;
	}

}
