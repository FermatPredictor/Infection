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
	private int[][] randomJumpBoard;
	private int[][] chooseBoard;//noted the point that ai can choose, 0:initial, 1:breed, 2:jump
	private char[][] simulateBoard; //b:black; w:white; n:null


	public AI(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		AIBoard=new int[size+1][size+1];
		randomJumpBoard=new int[size+1][size+1];
		chooseBoard=new int[size+1][size+1];
		simulateBoard=new char[size+1][size+1];
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
	 
	 private void setChoosePoint(int x, int y, char c){
		 
		 chooseBoard[x][y]=0;
		 char d=' ';
		 if(c=='b')d='w';
		 else if(c=='w')d='b';
		 
		 if(board.points[x][y]=='n'){
			 
			 if(x+1<=size){
				 if(board.points[x+1][y]==c)chooseBoard[x][y]=1;
			 }
			 if(x-1>0){
				 if(board.points[x-1][y]==c)chooseBoard[x][y]=1;
			 }
			 if(y+1<=size){
				 if(board.points[x][y+1]==c)chooseBoard[x][y]=1;
			 }
			 if(y-1>0){
				 if(board.points[x][y-1]==c)chooseBoard[x][y]=1;
			 }
		 }
		 
		 boolean valueJump=false;
		 boolean canJump=false;
		 if(board.points[x][y]=='n' && chooseBoard[x][y]!=1){
			 
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++){
						int distance=Math.abs(i-x)+Math.abs(j-y);
						if(distance==2 && board.points[i][j]==c)
							canJump=true;
						if(Math.abs(i-x)<=1 && Math.abs(j-y)<=1 && board.points[i][j]==d){
							valueJump=true;
						}
					}
			 if(canJump && valueJump)
				 chooseBoard[x][y]=2;
		}
	 }
	 
	 
	 public int[] randomJump(int x, int y , char c){
		 
		 int jump[]=new int[2];
		 int branch=1;
	     int choose;
	     
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					randomJumpBoard[i][j]=0;
				}
		 
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(Math.abs(i-x)+Math.abs(j-y)==2 && board.points[i][j]==c){
						randomJumpBoard[i][j]=branch;
						branch++;
					}
				}

		 Random random=new Random();
		 choose=random.nextInt(branch-1)+1;
		 for(int i=1; i<=size ;i++)
 			for(int j=1; j<=size ;j++)
 				if(randomJumpBoard[i][j]==choose){
 					jump[0]=i;
 					jump[1]=j;
 				}	 
		 return jump;
		 
	 }
	 
	 
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){
    	
    	int point[]=new int[4];
    	int jump[]=new int[2];
    	int branch=1;
    	int choose;
    	
    	point[0]=size+1;
		point[1]=size+1;

    	for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++){
				AIBoard[i][j]=0;
				setChoosePoint(i,j,color);
			}
    	
    	for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				if(chooseBoard[i][j]==1 || chooseBoard[i][j]==2){
					AIBoard[i][j]=branch;
					branch++;
				}
    	Random random=new Random();
    	if(branch>1){
    		choose=random.nextInt(branch-1)+1;
    		for(int i=1; i<=size ;i++)
    			for(int j=1; j<=size ;j++)
    				if(AIBoard[i][j]==choose){
    					point[2]=i;
    					point[3]=j;
    					if(chooseBoard[i][j]==2){
    						jump=randomJump(i,j,color);
    						point[0]=jump[0];
    						point[1]=jump[1];
    					}
    				}
    	}
    	else{
    		point[2]=size+1;
			point[3]=size+1;
    	}
    	
    	return point;
    }

}
