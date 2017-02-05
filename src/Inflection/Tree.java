package Inflection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;

public class Tree extends AI{
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	private char[][] simulateBoard; //b:black; w:white; n:null
    int count=0;

	public Tree(int size, PApplet parent, ChessBoard board){
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
	
	private int countNullPointsNum(char[][] board){
		int nullPointsNum=0;
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				if(board[i][j]=='n')
					nullPointsNum++;
		return nullPointsNum;
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
	 
	private void expandNode(TreeNode node, char[][] board, char nextColor){
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
     
     //對於一個給定局面，該color走棋，回傳color所能到達的最佳結果
     private int checkTheResult(char board[][], int height, char color){
    	 
    	 char[][] memoryBoard=new char[size+1][size+1];
    	 for(int i=1; i<=size ;i++)
 	  		for(int j=1; j<=size ;j++)
 	  			memoryBoard[i][j]=board[i][j];
    	 
    	 if(simulateCheckEnding(board, color)){
    		 if(color=='b')
    			 return countWinNum(board,color);
    		 else 
    			 return -countWinNum(board,color);
    	 }	 
    	 else if(height==0){
    		 if(color=='b')
    			 return simpleCountWinNum(board);
    		 else 
    			 return -simpleCountWinNum(board);
    	 }
    		 
    	 TreeNode root = new TreeNode(null);
    	 int result=-9999;
    	 expandNode(root,board,color);
    	 for(TreeNode nodes:root.getChildren()){
    		 copyBoard(memoryBoard);
    		 int[] p=nodes.getMove();
    		 simulateDoAction(p, color);
    		 int x=-checkTheResult(simulateBoard,height-1,changeColor(color));
    		 if(x>result)
    			 result=x;
    	 }
    	 return result;
     }
     
     private int ABcheckTheResult(char board[][], int height, char color, TreeNode node){
         count++;
    	 char[][] memoryBoard=new char[size+1][size+1];
    	 for(int i=1; i<=size ;i++)
 	  		for(int j=1; j<=size ;j++)
 	  			memoryBoard[i][j]=board[i][j];
    	 int result;
    	 if(simulateCheckEnding(board, color)){
    		 if(color=='b')
    			 result=countWinNum(board,color);
    		 else 
    			 result=-countWinNum(board,color);
    		 if(node.getParent()!=null && result<node.getParent().getResult())
        		 node.getParent().setResult(result);
    		 node.setResult(-result);
        	 return result;
    	 }	 
    	 else if(height==0){
    		 if(color=='b')
    			 result=simpleCountWinNum(board);
    		 else 
    			 result=-simpleCountWinNum(board);
    		 if(node.getParent()!=null && result<node.getParent().getResult())
        		 node.getParent().setResult(result);
    		 node.setResult(-result);
        	 return result;
    	 }
    		 
    	 result=-9999;
    	 expandNode(node,board,color);
    	 for(TreeNode nodes:node.getChildren()){
    		 copyBoard(memoryBoard);
    		 int[] p=nodes.getMove();
    		 simulateDoAction(p, color);
    		 int x=-ABcheckTheResult(simulateBoard,height-1,changeColor(color),nodes);
    		 if(x>result){
    			 result=x;
    			 TreeNode grandparentnode=nodes.getParent().getParent();
    			 if(grandparentnode!=null && result > grandparentnode.getResult())
    				 break;
    		 }
    	 }
    	 
    	 if(node.getParent()!=null && result<node.getParent().getResult())
    		 node.getParent().setResult(result);
    	 node.setResult(-result);
    	 return result;
     }
	 
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){
         count=0;
    	 copyBoard();
    	 TreeNode root = new TreeNode(null);
    	 int point[]=new int[4];
    	 expandNode(root,simulateBoard,color);
    	 int result = -9999;
    	 TreeNode best = null;
    	 boolean endMode=countNullPointsNum(simulateBoard)<=4;
    	 boolean highComplexity=root.getSubNodeNum()>=9;
    	 if(!endMode){
			 if(!highComplexity)
				 result=ABcheckTheResult(simulateBoard, 7, color,root);
			 else
				 result=ABcheckTheResult(simulateBoard, 6, color,root);
    	 }
    	 else
    		 result=ABcheckTheResult(simulateBoard, 10, color,root);
    	 
    	 System.out.println("advice plays:");
    	 root.printAdviceTree(4);
    	 System.out.println();
    	 //print the result
    	 for(TreeNode each : root.getChildren()){
    		 int[] a=each.getMove();
    		 for(int x:a)
    			 System.out.print(x+" ");
    		 System.out.print(each.getResult());
    		 System.out.println();
    	 }
    	 System.out.println(count);
    	 System.out.println();
    	 
    	//random choose in the best choices
    	 int num = 0, rand_num;
 		 for(TreeNode each : root.getChildren()){
 			if(each.getResult() == result)
 				num++;
 		}
 		Random random = new Random();
		rand_num = random.nextInt(num);	
		for(TreeNode each : root.getChildren()){
			if(each.getResult() == result){
				if(rand_num == 0){
					best=each;
					break;
				}
				else
					rand_num--;	
			}
		}
    	 point=best.getMove();
    	 return point;
    }

}
