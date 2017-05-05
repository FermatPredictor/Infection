package Inflection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;

public class GammaCat extends AI{
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	private char[][] simulateBoard; //b:black; w:white; n:null

	public GammaCat(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		simulateBoard=new char[size+1][size+1];
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
					if(i > 1 && j < size && board[i-1][j+1] == color){
						a[length][0] = i-1;
						a[length][1] = j+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(j < size-1 && board[i][j+2] == color){
						a[length][0] = i;
						a[length][1] = j+2;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(i < size && j < size && board[i+1][j+1] == color){
						a[length][0] = i+1;
						a[length][1] = j+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(i < size-1 && board[i+2][j] == color){
						a[length][0] = i+2;
						a[length][1] = j;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(i < size && j > 1 && board[i+1][j-1] == color){
						a[length][0] = i+1;
						a[length][1] = j-1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(j > 2 && board[i][j-2] == color){
						a[length][0] = i;
						a[length][1] = j-2;
						a[length][2] = i;
						a[length][3] = j;
						length++;
						
					}
					if(i > 1 && j > 1 && board[i-1][j-1] == color){
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
					if(i > 1 && j < size && board[i-1][j+1] == color){
						a[length][0] = i-1;
						a[length][1] = j+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(j < size-1 && board[i][j+2] == color){
						a[length][0] = i;
						a[length][1] = j+2;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(i < size && j < size && board[i+1][j+1] == color){
						a[length][0] = i+1;
						a[length][1] = j+1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(i < size-1 && board[i+2][j] == color){
						a[length][0] = i+2;
						a[length][1] = j;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(i < size && j > 1 && board[i+1][j-1] == color){
						a[length][0] = i+1;
						a[length][1] = j-1;
						a[length][2] = i;
						a[length][3] = j;
						length++;
					}
					if(j > 2 && board[i][j-2] == color){
						a[length][0] = i;
						a[length][1] = j-2;
						a[length][2] = i;
						a[length][3] = j;
						length++;
						
					}
					if(i > 1 && j > 1 && board[i-1][j-1] == color){
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
 	
	//for a ending game, count the number of how many stone that we win
 	//cannotMoveColor: enemyStones
	private int countWinNum(char[][] board, char cannotMoveColor){
		 int selfStones=0;
	     int enemyStones=0;
	     char selfColor=changeColor(cannotMoveColor);
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(board[i][j]==selfColor || board[i][j]=='n')
						selfStones++;
					else if(board[i][j]==cannotMoveColor)
						enemyStones++;
				}
		 
		 return selfStones-enemyStones;	
	}
	
	//d:subnode color，設定在node之下的subnode(敵方)的起始資訊(perfect, infectNum…)
		private void setGoodSteps(char[][] board, GammaNode node, char subNodeColor){
			
			boolean existAttackBreed=false;
			for (GammaNode each : node.getChildren()){
				boolean isGood = false;
				boolean isBreed=true;
				boolean canInfect = false;
				int infectNum=0;
				int[] move = each.getMove();
				int r = move[0], s = move[1], x = move[2], y = move[3];
				if(0<r && r<=size && 0<s && s<=size)
					isBreed=false;
				int selfStones=0;//subnode color
				int enemyStones=0;//node color
				for(int i=1; i<=size; i++)
					for(int j=1; j<=size; j++){
						if(Math.abs(x-i)>1 || Math.abs(y-j)>1){
							if(board[i][j] == subNodeColor){
								selfStones++;
							}
							else if(board[i][j] == changeColor(subNodeColor)){
								enemyStones++;
							}
						}
						else{
							if(board[i][j] !='n'){
								if(board[i][j] ==changeColor(subNodeColor)){
									canInfect=true;
									infectNum++;
									existAttackBreed=true;
								}
								selfStones++;
							}		
						}
					}
				if(isBreed)
					selfStones++;
				if(enemyStones==0)
					each.setWinStones(25);
				each.setInfectNum(infectNum);
				each.setWinStones(selfStones-enemyStones);
				
				if(canInfect){
					if(x-1>0 && y-1>0){
						if(board[x-1][y-1]!='n' && board[x-1][y]!='n' && board[x][y-1]!='n')
							if(!(r==x-1&&s==y-1)){
								isGood = true;
							}
					}
					if(x+1<=size && y+1<=size){
						if(board[x+1][y+1]!='n' && board[x+1][y]!='n' && board[x][y+1]!='n')
							if(!(r==x+1&&s==y+1)){
								isGood = true;
							}
					}
					if(x-1>0 && y+1<=size){
						if(board[x-1][y]!='n' && board[x-1][y+1]!='n' && board[x][y+1]!='n')
							if(!(r==x-1&&s==y+1)){
								isGood = true;
							}
					}
					if(x+1<=size && y-1>0){
						if(board[x][y-1]!='n' && board[x+1][y-1]!='n' && board[x+1][y]!='n')
							if(!(r==x+1&&s==y-1)){
								isGood = true;
							}
					}
					if(isGood)
						each.markPerfectStep();
				}
			}
			if(existAttackBreed){
				for (GammaNode each : node.getChildren()){
					if(each.getInfectNum()==0)
						each.markNonUseful();
				}
			}
		}
	 
	private void expandNode(GammaNode node, char[][] board, char nextColor){
		if(!node.isVisit){
			int[][] a = setAllBreedMove(board, nextColor);
			int[][] b = setAllJumpMove(board, nextColor);
			if(a != null){
				for(int[] y:a)
					node.addChild(y,nextColor);		
			}
			if(b != null){
				for(int[] y:b)
					node.addChild(y,nextColor);
			}
			node.isVisit = true;
			setGoodSteps(board, node , nextColor);
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
     
     private void printTheBoard(char board[][]){
    	 
       	for(int i=1; i<=size ;i++){
   			for(int j=1; j<=size ;j++){
   				System.out.print(board[j][i]+" ");
   			}	 
   			System.out.println();
       	}
       	System.out.println();
      }
     
 	private GammaNode chooseHighUCBSubNode(GammaNode node){
		double max = -1;
		GammaNode best=null;
		for(GammaNode each : node.getChildren()){
			double ucb=each.getUCB(node.getVisitNum());
			if(ucb>max){
				max=ucb;
				best=each;
			}
		}
		return best;
	}
 	
     private boolean RandomPlaytheGame(char board[][], char nextColor){
        
    	int[][] a = setAllBreedMove(board, nextColor);
  		int[][] b = setAllJumpMove(board, nextColor);
  		int a_len=0,b_len=0;
  		if(a!=null)a_len=a.length;
  		if(b!=null)b_len=b.length;
  		int len=a_len+b_len;
  		if(len==0)
  			return !(countWinNum(board, nextColor)>=0);
  		else{
  			simulateBoard=board;
  			Random random = new Random();
  			int[] point;
  			int rand_num = random.nextInt(len);
  			if(rand_num<a_len)
  				point=a[rand_num];
  			else
  				point=b[rand_num-a_len];
  			simulateDoAction(point,nextColor);
  			return !RandomPlaytheGame(simulateBoard, changeColor(nextColor));
  		}
     }
     
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){

    	 int simulateNum=20000;
    	 copyBoard();
    	 GammaNode root = new GammaNode(null);
    	 int point[]=new int[4];
    	 expandNode(root,simulateBoard,color);
    	 
    	 while(simulateNum>0){
    		 copyBoard();
    		 GammaNode play=chooseHighUCBSubNode(root);
    		 if(play.getResult()==-1)//if the game cannot win, break;
    			 break;
    		 simulateDoAction(play.getMove(), color);
    		 while(play.getSubNodeNum()!=0){
    			 play=chooseHighUCBSubNode(play);
    			 simulateDoAction(play.getMove(), play.getColor());
    		 }
    		 expandNode(play,simulateBoard,changeColor(play.getColor()));
    		 if(play.getSubNodeNum()==0){
    			 char winColor='n';
    			 int winNum=countWinNum(simulateBoard , changeColor(play.getColor()));
    			 if(winNum>0){
    				 play.setResult(1);
    				 winColor=play.getColor();
    			 }
    			 else if(winNum<0){
    				 play.setResult(-1);
    				 winColor=changeColor(play.getColor());
    			 }
    			 play.refresh(winColor,true);
    		 }
    		 else{
    			 play=chooseHighUCBSubNode(play);
    			 simulateDoAction(play.getMove(), play.getColor());
	    		 boolean value=RandomPlaytheGame(simulateBoard, changeColor(play.getColor()));
	    		 char winColor;
	    		 if(!value)
	    			 winColor=play.getColor();
	    		 else 
	    			 winColor=changeColor(play.getColor());
	    		 play.refresh(winColor,false);
    		 }
    		 simulateNum--;
    	 }
    	 
    	 //print the result
    	 int max =0;
    	 GammaNode best = null;
    	 for(GammaNode each : root.getChildren()){
    		 int[] a=each.getMove();
    		 for(int x:a)
    			 System.out.print(x+" ");
    		 System.out.print(each.getVisitNum()+" "+each.getProb());
    		 for(GammaNode sub : each.getChildren()){
    			 System.out.println();
    			 System.out.print("  ");
    			 int[] b=sub.getMove();
        		 for(int y:b)
        			 System.out.print(y+" ");
    			 System.out.print(sub.getVisitNum()+" "+sub.getProb());
    		 }
    		 System.out.println();
    		 if(each.getVisitNum()> max){
    			 max = each.getVisitNum();
    			 best = each;
    		 }
    	 }
    	 System.out.println();
    	 point=best.getMove();
    	 return point;
    }

}
