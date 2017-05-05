package Inflection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import processing.core.PApplet;

public class Tree extends AI{
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	private char[][] simulateBoard; //b:black; w:white; n:null
    int count=0;
    private ZobristHash zTable;
    

	public Tree(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		simulateBoard=new char[size+1][size+1];
		zTable=new ZobristHash(size);
	}
	
	 private boolean isSymmetric(char board[][]){
		 boolean isSymmetric=true;
		 for(int i=2; i<=size ;i++)
				for(int j=1; j<i ;j++){
					if(board[i][j]!=board[j][i])
						isSymmetric=false;
				}
		 return isSymmetric;				
	 }
	 
	private int[][] setAllBreedMove(char[][] board, char color){
		int [][] a = new int [40][4];
		int [][] temp = null;
		int length = 0;
		boolean b;
		boolean isSymmetric=isSymmetric(board);
		for (int i=1; i<=size; i++)
			for (int j=1; j<=size; j++){
				if(!isSymmetric || j<=i){
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
							a[length][0] = size+1;
							a[length][1] = size+1;
							a[length][2] = i;
							a[length][3] = j;
							length++;
						}
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
		return temp;
			
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
		boolean isSymmetric=isSymmetric(board);
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++){
				if(!isSymmetric || j<=i){
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
		boolean isSymmetric=isSymmetric(board);
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++){
				if(!isSymmetric || j<=i){
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
     private int checkTheResult(char board[][], int height, char color, TreeNode node){
  
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
    		 node.setResult(-result);
    		 return result;
    	 }	 
    	 else if(height==0){
    		 if(color=='b')
    			 result=simpleCountWinNum(board);
    		 else 
    			 result=-simpleCountWinNum(board);
    		 node.setResult(-result);
    		 return result;
    	 }
    		 
    	 TreeNode root = new TreeNode(null);
    	 result=-9999;
    	 expandNode(node,board,color);
    	 for(TreeNode child:node.getChildren()){
    		 copyBoard(memoryBoard);
    		 int[] p=child.getMove();
    		 simulateDoAction(p, color);
    		 int x=-checkTheResult(simulateBoard,height-1,changeColor(color),child);
    		 if(x>result)
    			 result=x;
    	 }
    	 node.setResult(-result);
    	 return result;
     }
     
     
     private int alpha_betaCheckTheResult(TreeNode node, int alpha, int beta, char board[][], int height , boolean isMaxizingPlayer){
     
    	 count++;
    	 char nextColor=changeColor(node.getColor());
    	 char[][] memoryBoard=new char[size+1][size+1];
    	 for(int i=1; i<=size ;i++)
 	  		for(int j=1; j<=size ;j++)
 	  			memoryBoard[i][j]=board[i][j];
    	 int result;
    	 if(simulateCheckEnding(board, nextColor)){
    		 char MinizingPlayerColor=node.getColor();
    		 if(isMaxizingPlayer)
    			 MinizingPlayerColor=changeColor(node.getColor());		 
    		 result=countWinNum(board, nextColor);
             if(MinizingPlayerColor=='w')
            	 result=-result;
             if(!isMaxizingPlayer)
            	 node.setResult(result);
             else
            	 node.setResult(-result);
        	 return result;
    	 }	 
    	 else if(height==0){
    		 char MinizingPlayerColor=node.getColor();
    		 if(isMaxizingPlayer)
    			 MinizingPlayerColor=changeColor(node.getColor());	 
    		 result=simpleCountWinNum(board);
    		 if(MinizingPlayerColor=='w')
            	 result=-result;
    		 if(!isMaxizingPlayer)
            	 node.setResult(result);
             else
            	 node.setResult(-result);
    		 return result;
    	 }
    		 
    	 if(isMaxizingPlayer){
    		 result=-9999;
    		 expandNode(node,board,nextColor);
    		 for(TreeNode child:node.getChildren()){
    			 copyBoard(memoryBoard);
        		 int[] p=child.getMove();
        		 simulateDoAction(p, nextColor);
    			 result=Math.max(result, alpha_betaCheckTheResult(child, alpha, beta, simulateBoard,height-1,false));
    			 alpha= Math.max(alpha, result);
    	         if(beta < alpha)
    	        	 break;// (* β cut-off *)
    		 }
    		 if(!isMaxizingPlayer)
            	 node.setResult(result);
             else
            	 node.setResult(-result);
    		 return result;
    	 }
         else{
        	 result=9999;
        	 expandNode(node,board,nextColor);
        	 for(TreeNode child:node.getChildren()){
        		 copyBoard(memoryBoard);
        		 int[] p=child.getMove();
        		 simulateDoAction(p, nextColor);
        		 result=Math.min(result, alpha_betaCheckTheResult(child, alpha, beta, simulateBoard,height-1,true));
        		 beta= Math.min(beta, result);
        		 if(beta < alpha)
        			 break;// (* α cut-off *)
        	 }
        	 if(!isMaxizingPlayer)
            	 node.setResult(result);
             else
            	 node.setResult(-result);
    	     return result;
         }
     }
     
     //improved alpha-beta method
     private int PVScheckTheResult(TreeNode node, int alpha, int beta, char board[][], int height , boolean isMaxizingPlayer){
         
    	 count++;
    	 char nextColor=changeColor(node.getColor());
    	 char[][] memoryBoard=new char[size+1][size+1];
    	 for(int i=1; i<=size ;i++)
 	  		for(int j=1; j<=size ;j++)
 	  			memoryBoard[i][j]=board[i][j];
    	 int result;
    	 if(simulateCheckEnding(board, nextColor)){
    		 char MinizingPlayerColor=node.getColor();
    		 if(isMaxizingPlayer)
    			 MinizingPlayerColor=changeColor(node.getColor());		 
    		 result=countWinNum(board, nextColor);
             if(MinizingPlayerColor=='w')
            	 result=-result;
             if(!isMaxizingPlayer)
            	 node.setResult(result);
             else
            	 node.setResult(-result);
        	 return result;
    	 }	 
    	 else if(height==0){
    		 char MinizingPlayerColor=node.getColor();
    		 if(isMaxizingPlayer)
    			 MinizingPlayerColor=changeColor(node.getColor());	 
    		 result=simpleCountWinNum(board);
    		 if(MinizingPlayerColor=='w')
            	 result=-result;
    		 if(!isMaxizingPlayer)
            	 node.setResult(result);
             else
            	 node.setResult(-result);
    		 return result;
    	 }
    		 
    	 if(isMaxizingPlayer){
    		 result=-9999;
    		 expandNode(node,board,nextColor);
    		 for(TreeNode child:node.getChildren()){
    			 copyBoard(memoryBoard);
        		 simulateDoAction(child.getMove(), nextColor);
        		 if(result!=-9999){
        			 int eva=PVScheckTheResult(child, alpha, alpha, simulateBoard,height-1,false);
        			 if(eva>alpha && eva<beta){
        				 copyBoard(memoryBoard);
                		 simulateDoAction(child.getMove(), nextColor);
        				 result=Math.max(result, PVScheckTheResult(child, alpha, beta, simulateBoard,height-1,false));
        			 }
        			 else result=Math.max(result,eva);
        		 }
        		 else result=Math.max(result, PVScheckTheResult(child, alpha, beta, simulateBoard,height-1,false));
    			 alpha= Math.max(alpha, result);
    	         if(beta < alpha)
    	        	 break;// (* β cut-off *)
    		 }
    		 if(!isMaxizingPlayer)
            	 node.setResult(result);
             else
            	 node.setResult(-result);
    		 return result;
    	 }
         else{
        	 result=9999;
        	 expandNode(node,board,nextColor);
        	 for(TreeNode child:node.getChildren()){
        		 copyBoard(memoryBoard);
        		 simulateDoAction(child.getMove(), nextColor);
        		 if(result!=9999){
        			 int eva=PVScheckTheResult(child, beta, beta, simulateBoard,height-1,true);
        			 if(eva>alpha && eva<beta){
        				 copyBoard(memoryBoard);
                		 simulateDoAction(child.getMove(), nextColor);
        				 result=Math.min(result, PVScheckTheResult(child, alpha, beta, simulateBoard,height-1,true));
        			 }
        			 else result=Math.min(result,eva);
        		 }
        		 else result=Math.min(result, PVScheckTheResult(child, alpha, beta, simulateBoard,height-1,true));
        		 beta= Math.min(beta, result);
        		 if(beta < alpha)
        			 break;// (* α cut-off *)
        	 }
        	 if(!isMaxizingPlayer)
            	 node.setResult(result);
             else
            	 node.setResult(-result);
    	     return result;
         }
     }
	 
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){
         count=0;
    	 copyBoard();
    	 TreeNode root = new TreeNode(null);
    	 root.setColor(changeColor(color));
    	 int point[]=new int[4];
    	 expandNode(root,simulateBoard,color);
    	 int result = -9999;
    	 TreeNode best = null;
    	 boolean endMode=countNullPointsNum(simulateBoard)<=4;
    	 if(!endMode)
				 result=PVScheckTheResult(root,-24,24,simulateBoard,6,true);
    	 else
    		 result=PVScheckTheResult(root,-3,3,simulateBoard,12,true);
    	 
    	 root.setColor('n');
    	 System.out.println("advice plays:");
    	 root.printAdviceTree(5);
    	 System.out.println();
    	 //print the result
    	 for(TreeNode each : root.getChildren()){
    		 int[] a=each.getMove();
    		 for(int x:a)
    			 System.out.print(x+" ");
    		 System.out.print(each.getResult());
      		 /*for(TreeNode sub : each.getChildren()){
    			 System.out.println();
    			 System.out.print("  ");
    			 int[] b=sub.getMove();
        		 for(int y:b)
        			 System.out.print(y+" ");
    			 System.out.print(sub.getResult());
    		 }*/
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
    	 copyBoard();
    	 if(isSymmetric(simulateBoard) && random.nextInt(2)<1){
    		 int temp=point[0];
    		 point[0]=point[1];
    		 point[1]=temp;
    		 temp=point[2];
    		 point[2]=point[3];
    		 point[3]=temp;
    	 }
    	 return point;
    }

}
