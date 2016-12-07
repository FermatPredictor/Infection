package Inflection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;

public class BetaCat extends AI{
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	private boolean canMove=true;
	private char cannotMoveColor=' ';
	private int simulateNum=20000;
	private int simulateStepNum=30;
	private int sourceLevel=4;
	private int[] levelWinProb;
	private int nowPosition=0;
	
	
	private int winStoneNum=0; //def:blackStone-whiteStone for ending
	private int endSourceLevel=11;
	private int[] levelWinStone;//record most stoneNum of black win in current level.
	
	private String information="";
	
	private int[][] allowedMove;
	private int[] allowedMoveValue;//0~100, the win probability.  
	private int[] realAllowedMoveValue;//the ending win number(count as stone)
	private int value;//the number of win times while ai simulate games many times 
	private int allowedMoveNum;
	private char[][] simulateBoard; //b:black; w:white; n:null
	
	private int exStepNum=0;
	private int exStep[][]=new int[40][4];
    boolean useSpecial=false;


	public BetaCat(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		allowedMove=new int[40][4];
		allowedMoveValue=new int[40];
		realAllowedMoveValue=new int[40];
		simulateBoard=new char[size+1][size+1];
		levelWinProb=new int[sourceLevel];
		levelWinStone=new int[endSourceLevel];
	}
	
	private int[][] setAllBreedMove(char[][] board, char color){
		int [][] a = null;
		int [][] temp = null;
		int length = 0;
		boolean b;
		for (int i=1; i<=size; i++)
			for (int j=1; j<=size; j++){
				b = false;
				if(board[i][j] == 'n'){
					if(i > 1 && board[i-1][j] == color)
						b = true;
					else if(i < size && board[i+1][j] == color)
						b = true;
					else if(j > 1 && board[i][j-1] == color)
						b = true;
					else if(j < size && board[i][j+1] == color)
						b = true;
					if(b == true){
						if(length > 0){
							temp = a;
							a = new int [length+1][4];
							for (int k=0; k<length; k++)
								for (int s=0; s<4; s++){
									a[k][s] = temp[k][s];
								}
						}
						else
							a = new int [1][4];
						a[length][0] = size+1;
						a[length][1] = size+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
				}
			}
		
		return a;
			
	}
	
	//judge whether board[i][j]'s surrounding has enemy color
	private boolean valueJump(char[][] board, char d, int i, int j){
		boolean b=false;
		if(i > 1 && board[i-1][j] == d)
			b = true;
		else if(i < size && board[i+1][j] == d)
			b = true;
		else if(j > 1 && board[i][j-1] == d)
			b = true;
		else if(j < size && board[i][j+1] == d)
			b = true;
		else if(i > 1 && j > 1 && board[i-1][j-1] == d)
			b = true;
		else if(i > 1 && j < size && board[i-1][j+1] == d)
			b = true;
		else if(i < size && j > 1 && board[i+1][j-1] == d)
			b = true;
		else if(i < size && j < size && board[i+1][j+1] == d)
			b = true;
		return b;
	}
	 
	private int[][] setAllJumpMove(char[][] board, char color){
		char d;
		if(color=='b')d='w';
		else d='b';
		int [][] a = new int [40][4];
		int [][] temp = null;
		int length = 0;
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++){
				if(board[i][j]=='n' && valueJump(board,d,i,j)){
					if(i > 2 && board[i-2][j] == color){
						a[length][0] = i-2;
						a[length][1] = j;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(i > 1 && j < size && board[i-1][j+1] == color){
						a[length][0] = i-1;
						a[length][1] = j+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(j < size-1 && board[i][j+2] == color){
						a[length][0] = i;
						a[length][1] = j+2;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(i < size && j < size && board[i+1][j+1] == color){
						a[length][0] = i+1;
						a[length][1] = j+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(i < size-1 && board[i+2][j] == color){
						a[length][0] = i+2;
						a[length][1] = j;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(i < size && j > 1 && board[i+1][j-1] == color){
						a[length][0] = i+1;
						a[length][1] = j-1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(j > 2 && board[i][j-2] == color){
						a[length][0] = i;
						a[length][1] = j-2;
						a[length][2] = i;
						a[length][3] = j;
						length++;
						
					}
					else if(i > 1 && j > 1 && board[i-1][j-1] == color){
						a[length][0] = i-1;
						a[length][1] = j-1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
				}
			}
		if(length > 0){
			temp = new int [length][4];
			for (int k=0; k<length; k++)
				for (int s=0; s<4; s++){
					temp[k][s] = a[k][s];
				}
		}
		//test way
		/*for(int[] x:temp){
			for(int y:x){
				System.out.print(y+" ");
			}
			System.out.println();
		}*/
		return temp;
	}
	
	private int[][] setAllBadJumpMove(char[][] board, char color){
		char d;
		if(color=='b')d='w';
		else d='b';
		int [][] a = new int [40][4];
		int [][] temp = null;
		int length = 0;
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++){
				if(board[i][j]=='n' && !valueJump(board,d,i,j)){
					if(i > 2 && board[i-2][j] == color){
						a[length][0] = i-2;
						a[length][1] = j;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(i > 1 && j < size && board[i-1][j+1] == color){
						a[length][0] = i-1;
						a[length][1] = j+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(j < size-1 && board[i][j+2] == color){
						a[length][0] = i;
						a[length][1] = j+2;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(i < size && j < size && board[i+1][j+1] == color){
						a[length][0] = i+1;
						a[length][1] = j+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(i < size-1 && board[i+2][j] == color){
						a[length][0] = i+2;
						a[length][1] = j;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(i < size && j > 1 && board[i+1][j-1] == color){
						a[length][0] = i+1;
						a[length][1] = j-1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					else if(j > 2 && board[i][j-2] == color){
						a[length][0] = i;
						a[length][1] = j-2;
						a[length][2] = i;
						a[length][3] = j;
						length++;
						
					}
					else if(i > 1 && j > 1 && board[i-1][j-1] == color){
						a[length][0] = i-1;
						a[length][1] = j-1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
				}
			}
		temp = new int [length][4];
		for (int k=0; k<length; k++)
			for (int s=0; s<4; s++){
				temp[k][s] = a[k][s];
			}
		//test way
		/*for(int[] x:temp){
			for(int y:x){
				System.out.print(y+" ");
			}
			System.out.println();
		}*/
		return temp;
	}
	
	//for a ending game, count the number of how many stone that black win
	private int countWinNum(char[][] board, char cannotMoveColor){
		 int blackStones=0;
	     int whiteStones=0;
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(board[i][j]=='b')
						blackStones++;
					else if(board[i][j]=='w')
						whiteStones++;
					else if(board[i][j]=='n'){
						 if(cannotMoveColor=='b')
							 whiteStones++;
						 else if(cannotMoveColor=='w')
							 blackStones++;
					}
				}
		 return blackStones-whiteStones;
		
	}
	
	//for a non-ending game, count the number of how many stone that black win now
	private int simpleCountWinNum(char[][] board){
		 int blackStones=0;
	     int whiteStones=0;
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(board[i][j]=='b')
						blackStones++;
					else if(board[i][j]=='w')
						whiteStones++;
				}
		 return blackStones-whiteStones;
		
	}
	
	private void expandNode(Node node, char[][] board, char nextColor){
		if(!node.isVisit){
			int[][] a = setAllJumpMove(board, nextColor);
			int[][] b = setAllBreedMove(board, nextColor);
			if(a != null){
				for(int[] y:a)
					node.addChild(y,nextColor);
			}
			if(b != null){
				for(int[] y:b)
					node.addChild(y,nextColor);
			}
			node.isVisit = true;
		}
	}
	
	private Node randomChooseSubNode(Node node){
		Random random = new Random();
		int rand_num, sum = 0;
		Node n = null;
		for (Node each : node.getChildren()) {
			 sum += each.getScore();
		 }
		if(sum > 0){
			rand_num = random.nextInt(sum);
			sum = 0;
			for (Node each : node.getChildren()) {
				if(each.getScore()!=0){
					 sum += each.getScore();
					 if(sum > rand_num){
						 n = each;
						 break;
					 }
				}
			 }
		}
		return n;
	}
	
	private Node chooseHighWinProbSubNode(Node node){
		 double max = 0;
   	     Node best = null;
	   	 for(Node each : node.getChildren()){
	   		 if(each.getProb() > max){
	   			 max = each.getProb();
	   			 best = each;
	   		 }
	   	 }
	   	 return best;
	}
	
	
	private char changeColor(char color){
		if (color=='b')return 'w';
		else if(color=='w')return 'b';
		else return 'n';
	}
	
    //true for ending
 	private boolean simulateCheckEnding(char[][] board, char c){
 		boolean test = false;
 		int[][] a = setAllJumpMove(board, c);
 		int[][] b = setAllBreedMove(board, c);
 		if(a==null && b==null)
 			test = true;
 		return test;
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
	
	

	
	 private void simulateDoAction(int[] point, char color){
		 
		 if(point[0]>0 && point[1]>0 && point[0]<=size && point[1]<=size)
			 simulateBoard[point[0]][point[1]]='n';
		 if(point[2]>0 && point[3]>0 && point[2]<=size && point[3]<=size && simulateBoard[point[2]][point[3]]=='n'){
			 simulateBoard[point[2]][point[3]]=color;
			 simulateInfection(point[2],point[3],color);
		 }
	}
	 

	 private void copyBoard(){
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++)
						simulateBoard[i][j]=board.points[i][j];
		 }
	 
	 private void copyBoard(char [][] board){
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					simulateBoard[i][j]=board[i][j];
	 }
	
	 	
	 //if ending, make a simple count, "true" for win
	 private boolean simpleCount(char c){
		 int blackStones=0;
		 int whiteStones=0;
		 
		 char d=' ';
		 if(cannotMoveColor=='b')d='w';
		 else if(cannotMoveColor=='w')d='b';
		 
		 if(!canMove){
			 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					if(simulateBoard[i][j]=='n')
						simulateBoard[i][j]=d;
		 }
		 
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(simulateBoard[i][j]=='b')
						blackStones++;
					else if(simulateBoard[i][j]=='w')
						whiteStones++;
				}
		 
		 winStoneNum=blackStones-whiteStones;
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
     
     
     private void setAllAllowedMove(char color){
		 
     	for(int i=1; i<=size ;i++)
 			for(int j=1; j<=size ;j++){
 				setAllowedMove(i,j,color);
 			}	 
	 }
	 
     
     private void printTheBoard(){
    	 
      	for(int i=1; i<=size ;i++){
  			for(int j=1; j<=size ;j++){
  				System.out.print(simulateBoard[j][i]+" ");
  			}	 
  			System.out.println();
      	}
      	System.out.println();
     }
     
     private int checkTheResult(int position,int rx, int ry,int x, int y, char color){
    	 
    	 char d=' ';
		 if(color=='b')d='w';
		 else if(color=='w')d='b';
		 
		 if(position<nowPosition)
			 for(int i=position+1;i<sourceLevel;i++){
		    		if(color=='b'){
		    			if(i%2==1)
		    				levelWinStone[i]=-999;
		    			if(i%2==0)
		    				levelWinStone[i]=999;
		    		}
		    		else{
		    			if(i%2==1)
		    				levelWinStone[i]=999;
		    			if(i%2==0)
		    				levelWinStone[i]=-999;
		    		}
			 }
		 nowPosition=position;
		 
		 int[][] allowedMove=new int[40][4];
		 int breedMoveNum=0;
		 int jumpMoveNum=0;
		 int allowedMoveNum=0;//breedMoveNum add jumpMoveNum
		 int result;
		 char[][] memoryBoard=new char[size+1][size+1];
		 
    	 simulateDoAction(rx,ry,x,y,color);
    	 //System.out.println("position"+position+":"+rx+" "+ry+" "+x+" "+y);
    	 boolean isEnding=simulateCheckEnding(d);
    	 if(isEnding){
    		 simpleCount(d);
    		 //System.out.println(" result: "+winNum);
    		 return winStoneNum;
    	 }
    	 else if(position==endSourceLevel)
    		 return 0;
    	 else{
    		 for(int i=1; i<=size ;i++)
    	  		for(int j=1; j<=size ;j++)
    	  			memoryBoard[i][j]=simulateBoard[i][j];
    	 }
    	 
    	 //setting the (rival's) allowed move from this situation
    	 for(int i=1; i<=size ;i++)
  			for(int j=1; j<=size ;j++){
  				 if(simulateBoard[i][j]=='n'){
  					 boolean set=false;
  					 for(int k=1; k<=size ;k++)
  							for(int l=1; l<=size ;l++){
  								int distance=Math.abs(k-i)+Math.abs(l-j);
  								if(distance==1 && simulateBoard[k][l]==d && !set){
  									allowedMove[allowedMoveNum][0]=size+1;
  									allowedMove[allowedMoveNum][1]=size+1;
  									allowedMove[allowedMoveNum][2]=i;
  									allowedMove[allowedMoveNum][3]=j;
  									breedMoveNum++;
  									allowedMoveNum++;
  									set=true;
  								}
  							}
  				 }
  			}	
    	 
    	 for(int i=1; i<=size ;i++)
  			for(int j=1; j<=size ;j++){
  				 if(simulateBoard[i][j]=='n'){
  					 boolean valueJump=false;
  					 for(int k=1; k<=size ;k++)
  							for(int l=1; l<=size ;l++){
  								if(Math.abs(k-i)<=1 && Math.abs(l-j)<=1 && simulateBoard[k][l]==color){
  									valueJump=true;
  								}
  							}
  					 if(valueJump){
  						 for(int k=1; k<=size ;k++)
  								for(int l=1; l<=size ;l++){
  									int distance=Math.abs(k-i)+Math.abs(l-j);
  									if(distance==2 && simulateBoard[k][l]==d){
  	  									allowedMove[allowedMoveNum][0]=k;
  	  									allowedMove[allowedMoveNum][1]=l;
  	  									allowedMove[allowedMoveNum][2]=i;
  	  									allowedMove[allowedMoveNum][3]=j;
  	  								    jumpMoveNum++;
  	  									allowedMoveNum++;
  									}
  								}
  					 }
  				 }
  			}
    	 
    	 
    	 if(allowedMoveNum==1)
    		 return checkTheResult(position+1,allowedMove[0][0],allowedMove[0][1],allowedMove[0][2],allowedMove[0][3],d);
    	 else{
    		 result=checkTheResult(position+1,allowedMove[0][0],allowedMove[0][1],allowedMove[0][2],allowedMove[0][3],d);
    		 for(int i=1;i<breedMoveNum;i++){
        		 for(int j=1; j<=size ;j++)
         	  		for(int k=1; k<=size ;k++)
         	  			simulateBoard[j][k]=memoryBoard[j][k];
        		 int r=checkTheResult(position+1,allowedMove[i][0],allowedMove[i][1],allowedMove[i][2],allowedMove[i][3],d);
        		 if(d=='w'){
        			 if(r<result)
            			 result=r;
        		 }
        		 else if(d=='b'){
        			 if(r>result)
            			 result=r;
        		 }
        		 
        		 if(color=='b'){
        			 if(result<levelWinStone[position])
        				 return result;
        		 }
        		 else if(color=='w'){
        			 if(result>levelWinStone[position])
        				 return result;
        		 }
        		 
        	 }
    		 if((d=='w' && result<0)||(d=='b' && result>0)){
    			 if(color=='b'){
    				 if(result>levelWinStone[position])
    					 levelWinStone[position]=result;
    			 }
    			 else if(color=='w'){
    				 if(result<levelWinStone[position])
    					 levelWinStone[position]=result;
    			 }
    			 return result;
    		 }
    		 else{
    			 for(int i=breedMoveNum;i<allowedMoveNum;i++){
            		 for(int j=1; j<=size ;j++)
             	  		for(int k=1; k<=size ;k++)
             	  			simulateBoard[j][k]=memoryBoard[j][k];
            		 int r=checkTheResult(position+1,allowedMove[i][0],allowedMove[i][1],allowedMove[i][2],allowedMove[i][3],d);
            		 if(d=='w'){
            			 if(r<result)
                			 result=r;
            		 }
            		 else if(d=='b'){
            			 if(r>result)
                			 result=r;
            		 }	 
            	 } 
    			 
    			 if(color=='b'){
    				 if(result>levelWinStone[position])
    					 levelWinStone[position]=result;
    			 }
    			 else if(color=='w'){
    				 if(result<levelWinStone[position])
    					 levelWinStone[position]=result;
    			 }
    			 return result;
    		 }
        	 
    	 }
    	 
     }
     
     private int[] endingGameMode(char color){
    	 
    	 char d=' ';
		 if(color=='b')d='w';
		 else if(color=='w')d='b';
		 
    	 int point[]=new int[4];
    	 int choose=0;
    	 
    	 copyBoard();
    	 allowedMoveNum=0;
    	 setAllAllowedMove(color);
    	 for(int i=0; i<40;i++)
    		 realAllowedMoveValue[i]=0;
    	 for(int i=0; i<allowedMoveNum ;i++){
    		 copyBoard();
    		 realAllowedMoveValue[i]=checkTheResult(1,allowedMove[i][0],allowedMove[i][1],allowedMove[i][2],allowedMove[i][3],color);
    		 if(color=='w')
    			 realAllowedMoveValue[i]*=(-1);
    	 }

     	point[0]=allowedMove[choose][0];
 		point[1]=allowedMove[choose][1];
 		point[2]=allowedMove[choose][2];
 		point[3]=allowedMove[choose][3];
 		
 		for(int i=0; i<allowedMoveNum ;i++){
 				if(realAllowedMoveValue[i]>realAllowedMoveValue[choose]){
 					choose=i;
 					point[0]=allowedMove[i][0];
 					point[1]=allowedMove[i][1];
 					point[2]=allowedMove[i][2];
 					point[3]=allowedMove[i][3];
 				}
 				//System.out.println(i+" "+allowedMove[i][0]+" "+allowedMove[i][1]+" "+allowedMove[i][2]+" "+allowedMove[i][3]+" (re)"+realAllowedMoveValue[i]);
 			}
 		//System.out.println("");
 		

 		
 		//if ai can't compute the result precisely, then random choose one
 		if(realAllowedMoveValue[choose]==0){
 			int randomChoose[][]=new int[40][4];
 			int randomChooseNum=0;
 			for(int i=0; i<allowedMoveNum ;i++){
 				if(realAllowedMoveValue[i]==0){
 					randomChoose[randomChooseNum][0]=allowedMove[i][0];
 					randomChoose[randomChooseNum][1]=allowedMove[i][1];
 					randomChoose[randomChooseNum][2]=allowedMove[i][2];
 					randomChoose[randomChooseNum][3]=allowedMove[i][3];
 					randomChooseNum++;
 				}
 			}
 			Random ran=new Random();
 			choose=ran.nextInt(randomChooseNum);
 			point[0]=randomChoose[choose][0];
 	 		point[1]=randomChoose[choose][1];
 	 		point[2]=randomChoose[choose][2];
 	 		point[3]=randomChoose[choose][3];
 		}
 		
 		return point;
     }
     
     //find the good step from human_entered
     private void lookingSpecial(char color){
    	 
    	 exStepNum=0;
    	 
    	 information=board.information.substring(1);
    	 int inflen=information.length();
    	 ArrayList<String> recordList = new ArrayList<String>();

 		try{
			FileReader fr = new FileReader("sz5_special_"+color+".txt");
			BufferedReader br = new BufferedReader(fr);
			while(br.ready()) {
				//System.out.print((char)br.read());
				String record = br.readLine();
				int sign=record.indexOf(';');
				if(sign!=-1)
					record=record.substring(sign);
				if(record.startsWith(information)){
					if(record.substring(inflen).startsWith(";"))
						recordList.add(record.substring(inflen));
				}
					
			}
			fr.close();
			
			if(!recordList.isEmpty()){
				
				//System.out.println(recordList.get(0));
				int sz=recordList.size();
				int now=0;
				
				int rx=recordList.get(0).charAt(3)-'a'+1;
				int ry=recordList.get(0).charAt(4)-'a'+1;
				int x=recordList.get(0).charAt(8)-'a'+1;
				int y=recordList.get(0).charAt(9)-'a'+1;
				exStep[now][0]=rx;
				exStep[now][1]=ry;
				exStep[now][2]=x;
				exStep[now][3]=y;
				
				//System.out.println(0+" "+exStep[0][0]+" "+exStep[0][1]+" "+exStep[0][2]+" "+exStep[0][3]);
				
				exStepNum++;

				
				for(int i=1; i<sz ;i++){
	                
					rx=recordList.get(i).charAt(3)-'a'+1;
				    ry=recordList.get(i).charAt(4)-'a'+1;
					x=recordList.get(i).charAt(8)-'a'+1;
					y=recordList.get(i).charAt(9)-'a'+1;
					if(exStep[now][0]==rx && exStep[now][1]==ry && exStep[now][2]==x && exStep[now][3]==y){
						;
					}
					else{
						now++;
						exStepNum++;
						exStep[now][0]=rx;
						exStep[now][1]=ry;
						exStep[now][2]=x;
						exStep[now][3]=y;
					}
					//System.out.println(recordList.get(i));
					//System.out.println(now+" "+exStep[now][0]+" "+exStep[now][1]+" "+exStep[now][2]+" "+exStep[now][3]);
				}
			}

		} catch (IOException e) {
		} 
     }
     
     
     
     private void RecordTheGame(char winnerColor){
    	 
    	 ArrayList<String> recordList = new ArrayList<String>();
  
     	//record the game was played
 		try{
 			FileReader fr = new FileReader("learned_sz5.txt");
 			BufferedReader br = new BufferedReader(fr);
 			recordList.clear();
 			while(br.ready()) {
 				//System.out.print((char)br.read());
 				String record = br.readLine();
 				int sign=record.indexOf(';');
 				if(sign!=-1)
 					record=record.substring(sign);
 				recordList.add(record);
 			}
 			fr.close();
 			
 			boolean isRepeat=false;
 			information=board.information.substring(1);
 			String s=information.concat("("+winnerColor+")");
 			for(int i=0; i<recordList.size() ;i++){
 				if(s.equals(recordList.get(i)))
 					isRepeat=true;
 			}
 			if(!isRepeat){
 				recordList.add(s);
 				recordList.sort(null);
 			}
 			
 			String symmetryinformation=board.symmetry(s);
 			isRepeat=false;
 			
 			for(int i=0; i<recordList.size() ;i++){
 				if(symmetryinformation.equals(recordList.get(i)))
 					isRepeat=true;
 			}
 			if(!isRepeat){
 				recordList.add(symmetryinformation);
 				recordList.sort(null);
 			}
 			
 			int sz=recordList.size();
 			String record[] = new String [sz];
 			for(int i=0; i<sz ;i++){
 				record[i]=(String)recordList.get(i);
 			}

 			FileWriter fw = new FileWriter("learned_sz5.txt");
 			for(int i=0; i<sz ;i++)
 				fw.write(i+record[i] + "\r\n");
 			fw.flush();
 			fw.close();
 			//System.out.println(information);
 		} catch (IOException e) {
 		}  	
     }
     
	 
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){
    	 Node root = new Node(null);
    	 int point[]=new int[4];
    	 int resignPoint[]={size+1,size+1,size+1,size+1};
    	 copyBoard();
    	 char nextColor=color;
    	 expandNode(root,simulateBoard,color);
    	 for(int i=1;i<=simulateNum;i++){
    		 copyBoard();
	    	 Node play=randomChooseSubNode(root);
	    	 if(play==null)
	    		 return resignPoint;
	    	 play.addVisitNum();
	    	 int[] p=play.getMove();
	    	 simulateDoAction(p,nextColor);
	    	 color=changeColor(nextColor);
	    	 char cannotMoveColor='n';
	    	 boolean isBreak=false;//在做randomChooseSubNode的判斷時，如果回傳null，表示對手的勝率全為0，此時break並不做winNum的判斷
	    	 for(int j=1;j<=simulateStepNum;j++){
	    		 if(simulateCheckEnding(simulateBoard,color)){
	    			 cannotMoveColor=color;
	    			 break;
	    		 }
	    		 expandNode(play,simulateBoard,color);
	    		 Node choose=randomChooseSubNode(play);
	    		 if(choose!= null)
	    			 play=choose;
	    		 else {
	    			 isBreak=true;
	    			 break;
	    		 }
	    		 play.addVisitNum();
		    	 p=play.getMove();
	    		 simulateDoAction(p,color);
	    		 color=changeColor(color);
	    	 }
	    	 int winNum=0;
	    	 if(!isBreak){
		    	 if(cannotMoveColor!='n')
		    		 winNum=countWinNum(simulateBoard, cannotMoveColor);
		    	 else
		    		 winNum=simpleCountWinNum(simulateBoard);
		    	 if((winNum>0 && play.getColor() == 'b') || (winNum<0 && play.getColor() == 'w'))
		    		 play.setProb(1);
		    	 else
		    		 play.setProb(0);
	    	 }
	    	 play = play.getParent();
    		 while(play != null){
    			 if(winNum>0 && play.getColor() == 'b' && !isBreak)
    				 play.addWinNum();
    			 else if(winNum<0 && play.getColor() == 'w' && !isBreak)
    				 play.addWinNum();
    			 play.refreshWinProb();
    			 play = play.getParent();
    		 }

    	 }
    	 double max = -1;
    	 Node best = null;
    	 for(Node each : root.getChildren()){
    		 int[] a=each.getMove();
    		 for(int x:a)
    			 System.out.print(x+" ");
    		 System.out.print(each.getVisitNum()+" "+each.getProb());
    		 for(Node sub : each.getChildren()){
    			 System.out.println();
    			 System.out.print("  ");
    			 int[] b=sub.getMove();
        		 for(int y:b)
        			 System.out.print(y+" ");
    			 System.out.print(sub.getVisitNum()+" "+sub.isRealProb+" "+sub.getProb());
        		 for(Node ssub : sub.getChildren()){
        			 System.out.println();
        			 System.out.print("    ");
        			 int[] c=ssub.getMove();
            		 for(int y:c)
            			 System.out.print(y+" ");
        			 System.out.print(ssub.getVisitNum()+" "+ssub.isRealProb+" "+ssub.getProb());
        		 }
    		 }
    		 System.out.println();
    		 if(each.getProb() > max){
    			 max = each.getProb();
    			 best = each;
    		 }
    	 }
    	 System.out.println();
    	 point=best.getMove();
    	 return point;
 
    }

}
