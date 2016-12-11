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
	private int simulateNum=10000;
	private int simulateStepNum=30;

	
	private String information="";
	private char[][] simulateBoard; //b:black; w:white; n:null
	
	private int exStepNum=0;
	private int exStep[][]=new int[40][4];
    boolean useSpecial=false;


	public BetaCat(int size, PApplet parent, ChessBoard board){
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
	
	//c:敵方的棋子，設定在node之下的subnode(敵方)哪些是好步(可形成方塊四(perfect), 可感染(infect), 可全滅敵方(allInfect),nearAllInfect)
	private void setGoodSteps(char[][] board, Node n, char c){
		for (Node each : n.getChildren()){
			boolean isGood = false;
			boolean canInfect = false;
			int infectStones=0;
			int[] move = each.getMove();
			int r = move[0], s = move[1], x = move[2], y = move[3];
			
			for(int i=x-1; i<=x+1; i++)
				for(int j=y-1; j<=y+1; j++){
					if(i>0 && i<=size && j>0 && j<=size){
						if(board[i][j] == changeColor(c)){
							canInfect = true;
							infectStones++;	
						}
					}
				}
			
			if(infectStones>=3)
				each.markManyInfectStep();
			
			if(canInfect){
				
				int liveStones=0;
				int emptyPoints=0;
				for(int i=1; i<=size; i++)
					for(int j=1; j<=size; j++){
						if(board[i][j] =='n')emptyPoints++;
						if(Math.abs(x-i)>1 || Math.abs(y-j)>1){
							if(board[i][j] == changeColor(c)){
								liveStones++;
							}
						}
					}
				
				if(liveStones==0)each.markAllInfectStep();
				else if(emptyPoints<=12 && liveStones<=2)each.markNearAllInfectStep();
				
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
			int[][] a = setAllBreedMove(board, nextColor);
			int[][] b = setAllJumpMove(board, nextColor);
			if(a != null){
				for(int[] y:a)
					node.addChild(y,nextColor);
				for(Node each:node.getChildren())
					each.markBreedStep();
					
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
	
	private Node chooseHighPrioritySubNode(Node node){

		int priorityMax = 1;
		for(Node each : node.getChildren()){
			if(each.priority() > priorityMax)
				if(each.Standard())
					priorityMax = each.priority();
		}
		
		int num = 0, rand_num;
		for(Node each : node.getChildren()){
			if(each.priority() == priorityMax)
				if(each.Standard())
					num++;
		}
		
		if(num==0)return randomChooseSubNode(node);
		
		Random random = new Random();
		rand_num = random.nextInt(num);
		
		for(Node each : node.getChildren()){
			if(each.priority() == priorityMax)
				if(each.Standard()){
					if(rand_num == 0)
						return each;
					else{
						rand_num--;
					}
			}
		}
		return null;
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
	
     
	 
     
     private void printTheBoard(){
    	 
      	for(int i=1; i<=size ;i++){
  			for(int j=1; j<=size ;j++){
  				System.out.print(simulateBoard[j][i]+" ");
  			}	 
  			System.out.println();
      	}
      	System.out.println();
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
    	 setGoodSteps(simulateBoard,root,color);
    	 for(int i=1;i<=simulateNum;i++){
    		 copyBoard();
	    	 Node play=chooseHighPrioritySubNode(root);
	    	 if(play==null)
	    		 return resignPoint;
	    	 play.addVisitNum();
	    	 int[] p=play.getMove();
	    	 simulateDoAction(p,nextColor);
	    	 color=changeColor(nextColor);
	    	 char cannotMoveColor='n';
	    	 char winColor='n';
	    	 boolean isBreak=false;//在做randomChooseSubNode的判斷時，如果回傳null，表示對手的勝率全為0，此時break並不做winNum的判斷
	    	 for(int j=1;j<=simulateStepNum;j++){
	    		 if(simulateCheckEnding(simulateBoard,color)){
	    			 cannotMoveColor=color;
	    			 break;
	    		 }
	    		 expandNode(play,simulateBoard,color);
	    		 setGoodSteps(simulateBoard,play,color);
	    		 Node choose=chooseHighPrioritySubNode(play);
	    		 if(choose!= null)
	    			 play=choose;
	    		 else {
	    			 isBreak=true;
	    			 winColor=changeColor(color);
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
		    	 if(winNum>=0)winColor='b';
		    	 else if(winNum<0)winColor='w';
		    	 if(play.getColor() == winColor)
		    		 play.setProb(1);
		    	 else
		    		 play.setProb(0);
	    	 }
	    	 play = play.getParent();
    		 while(play != null){
    			 if(play.getColor() == winColor)
    				 play.addWinNum();
    			 play.refreshWinProb();
    			 play = play.getParent();
    		 }

    	 }
    	 
    	 int max =0;
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
    			 System.out.print(sub.getVisitNum()+" "+sub.getProb());
        		 /*for(Node ssub : sub.getChildren()){
        			 System.out.println();
        			 System.out.print("    ");
        			 int[] c=ssub.getMove();
            		 for(int y:c)
            			 System.out.print(y+" ");
        			 System.out.print(ssub.getVisitNum()+" "+ssub.getProb());
        		 }*/
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
