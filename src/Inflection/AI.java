package Inflection;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import processing.core.PApplet;

public class AI {
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	private boolean isFullBoard=true;
	private int simulateNum=500;
	private int simulateStepNum=10;
	
	private int[][] AIBoard;//used in simulate, noted the points that ai can choose.
	private int[][] randomJumpBoard;
	private int[][] chooseBoard;//noted the point that ai can choose, 0:initial, 1:breed, 2:jump
	private int[][] allowedMove;
	private int[] allowedMoveValue;//the number of win times while ai simulate games many times 
	private int allowedMoveNum=0;
	private char[][] simulateBoard; //b:black; w:white; n:null
	


	public AI(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		AIBoard=new int[size+1][size+1];
		randomJumpBoard=new int[size+1][size+1];
		chooseBoard=new int[size+1][size+1];
		allowedMove=new int[30][4];
		allowedMoveValue=new int[30];
		simulateBoard=new char[size+1][size+1];
	}
	 
	 
	//if the stone place or move at a place, infect all the stones near the place
	private void simulateInfection(int x, int y, char c){

		 char d=' ';
		 if(c=='b')d='w';
		 else if(c=='w')d='b';
		 if(x+1<=size){
			 if(simulateBoard[x+1][y]==d)simulateBoard[x+1][y]=c;
		 }
		 if(x-1>0){
			 if(simulateBoard[x-1][y]==d)simulateBoard[x-1][y]=c;
		 }
		 if(y+1<=size){
			 if(simulateBoard[x][y+1]==d)simulateBoard[x][y+1]=c;
		 }
		 if(y-1>0){
			 if(simulateBoard[x][y-1]==d)simulateBoard[x][y-1]=c;
		 }
		 if(x+1<=size && y+1<=size){
			 if(simulateBoard[x+1][y+1]==d)simulateBoard[x+1][y+1]=c;
		 }
		 if(x+1<=size && y-1>0){
			 if(simulateBoard[x+1][y-1]==d)simulateBoard[x+1][y-1]=c;
		 }
		 if(x-1>0 && y+1<=size){
			 if(simulateBoard[x-1][y+1]==d)simulateBoard[x-1][y+1]=c;
		 }
		 if(x-1>0 && y-1>0){
			 if(simulateBoard[x-1][y-1]==d)simulateBoard[x-1][y-1]=c;
		 }	 
		 
	 }
	
	

	
	 private void simulateDoAction(int rx, int ry, int x, int y, char color){
		 
		 if(rx>0 && ry>0 && rx<=size && ry<=size)
			 simulateBoard[rx][ry]='n';
		 if(x>0 && y>0 && x<=size && y<=size && simulateBoard[x][y]=='n'){
			 simulateBoard[x][y]=color;
			 simulateInfection(x,y,color);
		 }
	}
	 

	 private int[] simulateGame(char color){
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


	 private void copyBoard(){
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++)
						simulateBoard[i][j]=board.points[i][j];
		 }
	
	 
        //true for ending
	 	private boolean simulateCheckEnding(char c){

			boolean canMove=false;
			isFullBoard=true;
			int blackStones=0;
			int whiteStones=0;

			for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(simulateBoard[i][j]=='b')
						blackStones++;
					else if(simulateBoard[i][j]=='w')
						whiteStones++;
				}
			
			if(blackStones==0 || whiteStones==0){
	    		return true;
	    	}

			for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					if(simulateBoard[i][j]=='n')
						isFullBoard=false;
			
			if(isFullBoard){
				return true;
			}
			else {
				for(int i=1; i<=size ;i++)
					 for(int j=1; j<=size ;j++)
						 if(simulateBoard[i][j]=='n' && !canMove){
							 if(i+1<=size){
								 if(simulateBoard[i+1][j]==c)canMove=true;
							 }
				    		 if(i-1>0){
				    			 if(simulateBoard[i-1][j]==c)canMove=true;
				    		 }
				    		 if(j+1<=size){
				    			 if(simulateBoard[i][j+1]==c)canMove=true;
				    		 }
				    		 if(j-1>0){
				    			 if(simulateBoard[i][j-1]==c)canMove=true;
				    		 }
				    		 
				    		 if(i+2<=size){
				    			 if(simulateBoard[i+2][j]==c)canMove=true;
				    		 }
				    		 if(i-2>0){
				    			 if(simulateBoard[i-2][j]==c)canMove=true;
				    		 }
				    		 if(j+2<=size){
				    			 if(simulateBoard[i][j+2]==c)canMove=true;
				    		 }
				    		 if(j-2>0){
				    			 if(simulateBoard[i][j-2]==c)canMove=true;
				    		 }
				    			 
				    	     if(i+1<=size && j+1<=size){
				    			 if(simulateBoard[i+1][j+1]==c)canMove=true;
				    	     }
				    		 if(i-1>0 && j-1>0){
				    			 if(simulateBoard[i-1][j-1]==c)canMove=true;
				    		 }
				    		 if(i+1<=size && j-1>0){
				    			 if(simulateBoard[i+1][j-1]==c)canMove=true;
				    		 }
				    		 if(i-1>0 && j+1<=size){
				    			 if(simulateBoard[i-1][j+1]==c)canMove=true;
				    		 }
				   }
				if(!canMove)
					return true;
				else
					return false;
			}
		}
	 	
	 //if ending, make a simple count, "true" for win
	 private boolean simpleCount(char c){
		 int blackStones=0;
		 int whiteStones=0;
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(simulateBoard[i][j]=='b')
						blackStones++;
					else if(simulateBoard[i][j]=='w')
						whiteStones++;
				}
		 if(c=='b' && blackStones>=whiteStones)
			 return true;
		 else if(c=='w' && whiteStones>=blackStones)
			 return true;
		 else return false;
			
	}
	 
	 private void setAllowedMove(int rx, int ry,int x, int y){
		 allowedMove[allowedMoveNum][0]=rx;
		 allowedMove[allowedMoveNum][1]=ry;
		 allowedMove[allowedMoveNum][2]=x;
		 allowedMove[allowedMoveNum][3]=y;
		 allowedMoveNum++;
	 }
	 
	 private void setChoosePoint(int x, int y, char c){
		 
		 chooseBoard[x][y]=0;
		 char d=' ';
		 if(c=='b')d='w';
		 else if(c=='w')d='b';
		 
		 if(simulateBoard[x][y]=='n'){
			 
			 if(x+1<=size){
				 if(simulateBoard[x+1][y]==c){
					 chooseBoard[x][y]=1;
				 }
			 }
			 if(x-1>0){
				 if(simulateBoard[x-1][y]==c){
					 chooseBoard[x][y]=1;
				 }
			 }
			 if(y+1<=size){
				 if(simulateBoard[x][y+1]==c){
					 chooseBoard[x][y]=1;
				 }
			 }
			 if(y-1>0){
				 if(simulateBoard[x][y-1]==c){
					 chooseBoard[x][y]=1;
				 }
			 }
		 }
		 
		 boolean valueJump=false;
		 boolean canJump=false;
		 
		 if(simulateBoard[x][y]=='n' && chooseBoard[x][y]!=1){
			 
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++){
						if(Math.abs(i-x)<=1 && Math.abs(j-y)<=1 && simulateBoard[i][j]==d)
							valueJump=true;
			 }
			 
			 if(valueJump){
				 for(int i=1; i<=size ;i++)
						for(int j=1; j<=size ;j++){
							int distance=Math.abs(i-x)+Math.abs(j-y);
							if(distance==2 && simulateBoard[i][j]==c)
								canJump=true;
				 }
			 }
			 
			 if(canJump && valueJump)
				 chooseBoard[x][y]=2;
		}
	 }
	 
     private void setAllowedMove(int x, int y, char c){
		 
		 char d=' ';
		 if(c=='b')d='w';
		 else if(c=='w')d='b';
		 
		 if(simulateBoard[x][y]=='n'){
			 
			 boolean set=false;
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++){
						int distance=Math.abs(i-x)+Math.abs(j-y);
						if(distance==1 && simulateBoard[i][j]==c && !set){
							setAllowedMove(size+1,size+1,x,y);
							set=true;
						}
					}
			 boolean valueJump=false;

			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++){
						if(Math.abs(i-x)<=1 && Math.abs(j-y)<=1 && simulateBoard[i][j]==d){
							valueJump=true;
						}
					}
			 if(valueJump){
				 for(int i=1; i<=size ;i++)
						for(int j=1; j<=size ;j++){
							int distance=Math.abs(i-x)+Math.abs(j-y);
							if(distance==2 && simulateBoard[i][j]==c){
								setAllowedMove(i,j,x,y);
							}
						}
			 }
		 }
		 
	 }
	 
	 
     private int[] randomJump(int x, int y , char c){
		 
		 int jump[]=new int[2];
		 int branch=1;
	     int choose;
	     
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					randomJumpBoard[i][j]=0;
				}
		 
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(Math.abs(i-x)+Math.abs(j-y)==2 && simulateBoard[i][j]==c){
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
	    allowedMoveNum=0;
	    boolean isEnding=false;
	    
	    char d=' ';
		if(color=='b')d='w';
	    else if(color=='w')d='b';	
		
		copyBoard();

    	for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++){
				setAllowedMove(i,j,color);
			}
    	
    	for(int i=0; i<30;i++)
    		allowedMoveValue[i]=0;
    	int simulatePoint[]=new int[4];
    	int rx,ry,x,y;
    	
    	for(int i=0; i<allowedMoveNum ;i++){
				for(int j=1; j<=simulateNum ;j++){
					copyBoard();
					simulateDoAction(allowedMove[i][0],allowedMove[i][1],allowedMove[i][2],allowedMove[i][3],color);
					for(int k=1; k<=simulateStepNum ;k++){
						if(k%2==1)
							isEnding=simulateCheckEnding(d);
						else if(k%2==0)
							isEnding=simulateCheckEnding(color);
						if(isEnding || k==simulateStepNum){
							if(simpleCount(color))
								allowedMoveValue[i]++;
							if(!isFullBoard){
								if(k==1)
									allowedMoveValue[i]+=simulateNum;
								else if(k==2)allowedMoveValue[i]-=simulateNum;
							}
							break;
						}
						if(k%2==1)
							simulatePoint=simulateGame(d);
						else if(k%2==0)
							simulatePoint=simulateGame(color);
						rx=simulatePoint[0];
						ry=simulatePoint[1];
						x=simulatePoint[2];
						y=simulatePoint[3];
						if(k%2==1)
							simulateDoAction(rx,ry,x,y,d);
						else if(k%2==0)
							simulateDoAction(rx,ry,x,y,color);
					}
				}
		    }
    	
    	int choose=0;
    	point[0]=allowedMove[choose][0];
		point[1]=allowedMove[choose][1];
		point[2]=allowedMove[choose][2];
		point[3]=allowedMove[choose][3];
		
		for(int i=0; i<allowedMoveNum ;i++){
				if(allowedMoveValue[i]>allowedMoveValue[choose]){
					choose=i;
					point[0]=allowedMove[i][0];
					point[1]=allowedMove[i][1];
					point[2]=allowedMove[i][2];
					point[3]=allowedMove[i][3];
				}
				System.out.println(i+" "+allowedMove[i][0]+" "+allowedMove[i][1]+" "+allowedMove[i][2]+" "+allowedMove[i][3]+" "+allowedMoveValue[i]);
			}
		System.out.println("");

    	return point;
    }

}
