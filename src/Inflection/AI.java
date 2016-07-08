package Inflection;

import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;

public class AI {
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	
	//some information that we can use
	private int[][] valueBoard;//the number of win times while ai simulate games many times 
	
	private int[][] AIBoard;//used in simulate, noted the points that ai can choose.
	private char[][] simulateBoard; //b:black; w:white; n:null
	private char[][] judgeBoard; // j:has judged


	public AI(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		AIBoard=new int[size+1][size+1];
		simulateBoard=new char[size+1][size+1];
		judgeBoard=new char[size+1][size+1];
		valueBoard=new int[size+1][size+1];
	}
	 
	 
	 //when some chess be eaten, removed it
	private void cleaning(int boardSize, int now_x, int now_y, char c){
		 


		 simulateBoard[now_x][now_y]='n';
		 if(now_x+1<=boardSize){
			 if(simulateBoard[now_x+1][now_y]==c)cleaning(boardSize,now_x+1,now_y,c);
		 }
		 if(now_x-1>0){
			 if(simulateBoard[now_x-1][now_y]==c)cleaning(boardSize,now_x-1,now_y,c);
		 }
		 if(now_y+1<=boardSize){
			 if(simulateBoard[now_x][now_y+1]==c)cleaning(boardSize,now_x,now_y+1,c);
		 }
		 if(now_y-1>0){
			 if(simulateBoard[now_x][now_y-1]==c)cleaning(boardSize,now_x,now_y-1,c);
		 }
	 }
	
	 //reset the judgeBoard
	private void reset(){
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					judgeBoard[i][j]=simulateBoard[i][j];
	 }
	

	
	 private void simulatePlaceChess(char color, int x, int y){
		if(x>0 && y>0 && x<=size && y<=size && simulateBoard[x][y]=='n'){
			if(color=='b'){
				simulateBoard[x][y]='b';
			}
			else if(color=='w'){
				simulateBoard[x][y]='w';
			}
		}
	}
	 

	 private int[] simulateAction(char color){
	    	int point[]=new int[2];
	    	int branch=1;
	    	int choose;

	    	for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					AIBoard[i][j]=0;
	    	for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					if(simulateBoard[i][j]=='n'){
						AIBoard[i][j]=branch;
						branch++;
					}
	    	Random random=new Random();
	    	if(branch>1){
	    		choose=random.nextInt(branch-1)+1;
	    		for(int i=1; i<=size ;i++)
	    			for(int j=1; j<=size ;j++)
	    				if(AIBoard[i][j]==choose){
	    					point[0]=i;
	    					point[1]=j;
	    				}
	    	}
	    	else{
	    		point[0]=size+1;
				point[1]=size+1;
	    	}
	    	
	    	return point;
	    }


	 
	 private void copyBoard(){
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++)
						simulateBoard[i][j]=board.points[i][j];
		 }
	

	 private boolean simpleCount(){
		 int blackPoints=0;
		 int whitePoints=0;
		 if(blackPoints>whitePoints)
			 return true;
		 else return false;
	 }
	 
	 
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){
    	
    	int point[]=new int[2];
    	int branch=1;
    	int choose;

    	for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				AIBoard[i][j]=0;
    	for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				if(simulateBoard[i][j]=='n'){
					AIBoard[i][j]=branch;
					branch++;
				}
    	Random random=new Random();
    	if(branch>1){
    		choose=random.nextInt(branch-1)+1;
    		for(int i=1; i<=size ;i++)
    			for(int j=1; j<=size ;j++)
    				if(AIBoard[i][j]==choose){
    					point[0]=i;
    					point[1]=j;
    				}
    	}
    	else{
    		point[0]=size+1;
			point[1]=size+1;
    	}
    	
    	return point;
    }

}
