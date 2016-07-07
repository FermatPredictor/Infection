package Inflection;

import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;

public class AI {
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	private int simulateNum=20;
	private boolean judgeing;
	private int caps=0;
	private int capsPoint[]=new int[2];//the coordinate of captured stones, only use in judge ko. 
	private int judgeCaps=0;
	private int judgeStoneNumInChain=0;
	private int judgeFreedomOfChain=0;
	private int judgeClosedAreaNumInChain=0;
	private int judgeClosedAreaPoints=0;
	private int lastMove[]=new int[2];
	
	//some information that we can use
	private int[][] valueBoard;//the number of win times while ai simulate games many times
	private char[][] ControlledAreaBoard;//the area be controlled, noted as 'b' or 'w' . 
	
	private int[][] AIBoard;//used in simulate, noted the points that ai can choose.
	private char[][] simulateBoard; //b:black; w:white; n:null
	private char[][] judgeBoard; // j:has judged
	private char[][] setChainBoard; //use in set chain, avoid put the stones in chain repeatly.
	private char[][] judgeClosedBoard; //judge whether a chain is closed.
	private float[][] priortyBoard;//decide a considerable point(0~1) 
	
	public AI(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		AIBoard=new int[size+1][size+1];
		simulateBoard=new char[size+1][size+1];
		judgeBoard=new char[size+1][size+1];
		valueBoard=new int[size+1][size+1];
		setChainBoard=new char[size+1][size+1];
		judgeClosedBoard=new char[size+1][size+1];
		ControlledAreaBoard=new char[size+1][size+1];
		priortyBoard=new float[size+1][size+1];
	}
	
	private void judgeing(int boardSize, int now_x, int now_y, char c){

		 if(judgeing){
			 judgeBoard[now_x][now_y]='j';
			 if(now_x+1<=boardSize){
				 if(judgeBoard[now_x+1][now_y]=='n')judgeing=false;
				 else if(judgeBoard[now_x+1][now_y]==c)judgeing(boardSize,now_x+1,now_y,c);
			 }
			 if(now_x-1>0){
				 if(judgeBoard[now_x-1][now_y]=='n')judgeing=false;
				 else if(judgeBoard[now_x-1][now_y]==c)judgeing(boardSize,now_x-1,now_y,c);
			 }
			 if(now_y+1<=boardSize){
				 if(judgeBoard[now_x][now_y+1]=='n')judgeing=false;
				 else if(judgeBoard[now_x][now_y+1]==c)judgeing(boardSize,now_x,now_y+1,c);
			 }
			 if(now_y-1>0){
				 if(judgeBoard[now_x][now_y-1]=='n')judgeing=false;
				 else if(judgeBoard[now_x][now_y-1]==c)judgeing(boardSize,now_x,now_y-1,c);
			 }
		 }
	 }
	 
	 
	 //when some chess be eaten, removed it
	private void cleaning(int boardSize, int now_x, int now_y, char c){
		 

	     caps++;
		 capsPoint[0]=now_x;
		 capsPoint[1]=now_y;

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
	
	//if the stone places at coordinate x,y and captures some stones, then clear the captured stones.
	private void judgeChessDead(int x, int y, char c){

		 char d=' ';
		 if(c=='b')d='w';
		 else if(c=='w')d='b';	
		 caps=0;
		    reset();
		    judgeBoard[x][y]=c;
		    if(x+1<=size)
		    	if(simulateBoard[x+1][y]==d){
		    		judgeing=true;
		    		judgeing(size,x+1, y, d);
		    		if(judgeing)
		    			cleaning(size,x+1, y, d);
		    }
			
			reset();
			judgeBoard[x][y]=c;
			if(x-1>0)
				if(simulateBoard[x-1][y]==d){
					judgeing=true;
					judgeing(size,x-1, y, d);
					if(judgeing)
						cleaning(size,x-1, y, d);

			}
			
			reset();
			judgeBoard[x][y]=c;
			if(y+1<=size)
				if(simulateBoard[x][y+1]==d){
					judgeing=true;
					judgeing(size,x, y+1, d);
					if(judgeing)
						cleaning(size,x, y+1, d);
			}
			
			reset();
			judgeBoard[x][y]=c;
			if(y-1>0)
				if(simulateBoard[x][y-1]==d){
					judgeing=true;
					judgeing(size,x, y-1, d);
					if(judgeing)
						cleaning(size,x, y-1, d);
			}	
		 
	 }
	 
	//use in function "judgeForbiddenPoint" that when some chess be eaten, count it(if>1, may repeat count)
	 private void countCaps(int boardSize, int now_x, int now_y, char c){
		 
		     judgeCaps++;
			 judgeBoard[now_x][now_y]='j';
			 if(now_x+1<=boardSize){
				 if(judgeBoard[now_x+1][now_y]==c)countCaps(boardSize,now_x+1,now_y,c);
			 }
			 if(now_x-1>0){
				 if(judgeBoard[now_x-1][now_y]==c)countCaps(boardSize,now_x-1,now_y,c);
			 }
			 if(now_y+1<=boardSize){
				 if(judgeBoard[now_x][now_y+1]==c)countCaps(boardSize,now_x,now_y+1,c);
			 }
			 if(now_y-1>0){
				 if(judgeBoard[now_x][now_y-1]==c)countCaps(boardSize,now_x,now_y-1,c);
			 }
	 }
	 
	 private boolean judgeForbiddenPoint(int x, int y, char c){
		 boolean isEatSomeChess=false;
		 char d=' ';
		 if(c=='b')d='w';
		 else if(c=='w')d='b';	
		 boolean isForbiddenPoint=false;
		 judgeCaps=0;
		 
		    reset();
		    judgeBoard[x][y]=c;
		    if(x+1<=size)
		    	if(simulateBoard[x+1][y]==d){
		    		judgeing=true;
		    		judgeing(size,x+1, y, d);
		    		if(judgeing){
		    			isEatSomeChess=true;
		    			reset();
		    			countCaps(size,x+1, y, d);
		    		}
		    }
			
			reset();
			judgeBoard[x][y]=c;
			if(x-1>0)
				if(simulateBoard[x-1][y]==d){
					judgeing=true;
					judgeing(size,x-1, y, d);
					if(judgeing){
						isEatSomeChess=true;
						reset();
						countCaps(size,x-1, y, d);
					}
			}
			
			reset();
			judgeBoard[x][y]=c;
			if(y+1<=size)
				if(simulateBoard[x][y+1]==d){
					judgeing=true;
					judgeing(size,x, y+1, d);
					if(judgeing){
						isEatSomeChess=true;
						reset();
						countCaps(size,x, y+1, d);
					}
			}
			
			reset();
			judgeBoard[x][y]=c;
			if(y-1>0)
				if(simulateBoard[x][y-1]==d){
					judgeing=true;
					judgeing(size,x, y-1, d);
					if(judgeing){
						isEatSomeChess=true;
						reset();
						countCaps(size,x, y-1, d);
					}
			}	
			
			if(caps==1 && x==capsPoint[0] && y==capsPoint[1] && judgeCaps==1)//rule of ko
				isForbiddenPoint=true;
			else if(!isEatSomeChess){
				judgeing=true;
				reset();
				judgeing(size,x,y,c);
				if(judgeing)
					isForbiddenPoint=true;				
			}
			
			return isForbiddenPoint;	 
	 }
	
	 private void simulatePlaceChess(char color, int x, int y){
		if(x>0 && y>0 && x<=size && y<=size && simulateBoard[x][y]=='n'){
			if(color=='b'){
				judgeChessDead(x,y,'b');
				simulateBoard[x][y]='b';
			}
			else if(color=='w'){
				judgeChessDead(x,y,'w');
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
					if(simulateBoard[i][j]=='n' && !judgeForbiddenPoint(i,j,color) && ControlledAreaBoard[i][j]=='n'){
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
	 
	 //only used in the function "setChains"
	 private void countStonesInChains(int now_x, int now_y, char c){
		     
		     judgeStoneNumInChain++;
			 judgeBoard[now_x][now_y]='j';
			 setChainBoard[now_x][now_y]='j';
			 judgeClosedBoard[now_x][now_y]=c;
			 if(now_x+1<=size){
				 if(judgeBoard[now_x+1][now_y]==c)countStonesInChains(now_x+1,now_y,c);
				 else if(judgeBoard[now_x+1][now_y]=='n'){
					 judgeFreedomOfChain++;
					 judgeBoard[now_x+1][now_y]='j';
				 }
			 }
			 if(now_x-1>0){
				 if(judgeBoard[now_x-1][now_y]==c)countStonesInChains(now_x-1,now_y,c);
				 else if(judgeBoard[now_x-1][now_y]=='n'){
					 judgeFreedomOfChain++;
					 judgeBoard[now_x-1][now_y]='j';
				 }
			 }
			 if(now_y+1<=size){
				 if(judgeBoard[now_x][now_y+1]==c)countStonesInChains(now_x,now_y+1,c);
				 else if(judgeBoard[now_x][now_y+1]=='n'){
					 judgeFreedomOfChain++;
					 judgeBoard[now_x][now_y+1]='j';
				 }
			 }
			 if(now_y-1>0){
				 if(judgeBoard[now_x][now_y-1]==c)countStonesInChains(now_x,now_y-1,c);
				 else if(judgeBoard[now_x][now_y-1]=='n'){
					 judgeFreedomOfChain++;
					 judgeBoard[now_x][now_y-1]='j';
				 }
			 }
	 }
	 
	 private void judgeClosedChain(int now_x, int now_y){
		 judgeClosedBoard[now_x][now_y]='j';
		 judgeClosedAreaPoints++;
		 if(now_x+1<=size){
			 if(judgeClosedBoard[now_x+1][now_y]=='n'){
				 judgeClosedChain(now_x+1,now_y);
			 }
		 }
		 if(now_x-1>0){
			 if(judgeClosedBoard[now_x-1][now_y]=='n'){
				 judgeClosedChain(now_x-1,now_y);
			 }
		 }
		 if(now_y+1<=size){
			 if(judgeClosedBoard[now_x][now_y+1]=='n'){
				 judgeClosedChain(now_x,now_y+1);
			 }
		 }
		 if(now_y-1>0){
			 if(judgeClosedBoard[now_x][now_y-1]=='n'){
				 judgeClosedChain(now_x,now_y-1);
			 }
		 }
	 }
	 
	 private void setControlledAreaInChain(int now_x, int now_y, char color){
		 ControlledAreaBoard[now_x][now_y]=color;
		 if(now_x+1<=size){
			 if(judgeClosedBoard[now_x+1][now_y]=='j' && ControlledAreaBoard[now_x+1][now_y]=='n'){
				 setControlledAreaInChain(now_x+1,now_y,color);
			 }
		 }
		 if(now_x-1>0){
			 if(judgeClosedBoard[now_x-1][now_y]=='j' && ControlledAreaBoard[now_x-1][now_y]=='n'){
				 setControlledAreaInChain(now_x-1,now_y,color);
			 }
		 }
		 if(now_y+1<=size){
			 if(judgeClosedBoard[now_x][now_y+1]=='j' && ControlledAreaBoard[now_x][now_y+1]=='n'){
				 setControlledAreaInChain(now_x,now_y+1,color);
			 }
		 }
		 if(now_y-1>0){
			 if(judgeClosedBoard[now_x][now_y-1]=='j' && ControlledAreaBoard[now_x][now_y-1]=='n'){
				 setControlledAreaInChain(now_x,now_y-1,color);
			 }
		 }
	 }


	 
	 private void copyBoard(){
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++)
						simulateBoard[i][j]=board.points[i][j];
		 }
	
	 //make a simple count, "true" for black win
	 private boolean simpleCount(){
		 int blackPoints=0;
		 int whitePoints=0;
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(ControlledAreaBoard[i][j]=='b')
						blackPoints++;
					else if(ControlledAreaBoard[i][j]=='w')
						whitePoints++;			 
					else if(simulateBoard[i][j]=='b')
						blackPoints++;
					else if(simulateBoard[i][j]=='w')
						whitePoints++;
				}
		 if(blackPoints>whitePoints)
			 return true;
		 else return false;
	 }
	 
	 private void setPriortyBoard(char color){
		 
		 lastMove=board.lastMove;
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					priortyBoard[i][j]=1;
		 
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(simulateBoard[i][j]=='n' && !judgeForbiddenPoint(i,j,color)){
						
						if(lastMove[0]>0 && Math.abs(lastMove[0]-i)+Math.abs(lastMove[1]-j)<=2)
							priortyBoard[i][j]=(float)1.2;
						
						if(i>2 && i<size-1 && j>2 &&j<size-1){
							if(simulateBoard[i-1][j]=='n' && simulateBoard[i+1][j]=='n' && simulateBoard[i][j-1]=='n' && simulateBoard[i][j+1]=='n')
								if(simulateBoard[i-2][j]==color||simulateBoard[i+2][j]==color||simulateBoard[i][j-2]==color||simulateBoard[i][j+2]==color
								||simulateBoard[i-1][j-1]==color||simulateBoard[i-1][j+1]==color||simulateBoard[i+1][j-1]==color||simulateBoard[i+1][j-1]==color
								||simulateBoard[i-2][j-1]==color||simulateBoard[i-2][j+1]==color||simulateBoard[i+2][j-1]==color||simulateBoard[i+2][j-1]==color
								||simulateBoard[i-1][j-2]==color||simulateBoard[i-1][j+2]==color||simulateBoard[i+1][j-2]==color||simulateBoard[i+1][j-2]==color)
								priortyBoard[i][j]=(float)1.2;
						}
						
						if(i>1 && i<size && j>1 &&j<size){
							if(simulateBoard[i-1][j]==color || simulateBoard[i+1][j]==color || simulateBoard[i][j-1]==color || simulateBoard[i][j+1]==color)
								priortyBoard[i][j]=(float)1.2;
						}
						
						//occupy the empty corner.
						if((i==3||i==4||i==size-2||i==size-3) && (j==3||j==4||j==size-2||j==size-3))
							if(simulateBoard[i-1][j]=='n' && simulateBoard[i+1][j]=='n' && simulateBoard[i][j-1]=='n' && simulateBoard[i][j+1]=='n')
							priortyBoard[i][j]=(float)1.2;
					}
				}
	 }
	 
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){
    	int point[]=new int[2];
    	
    	char d=' ';
		if(color=='b')d='w';
	    else if(color=='w')d='b';	
		
    	for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				valueBoard[i][j]=0;
    	
		int simulatePoint[]=new int[2];
		int x,y;
		boolean isEnding=false;
		
		
    	for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++){
				copyBoard();
				setPriortyBoard(color);
				if(board.points[i][j]=='n' && !board.judgeForbiddenPoint(i,j,color) && ControlledAreaBoard[i][j]=='n'){
					for(int k=1; k<=simulateNum ;k++){
						isEnding=false;
						copyBoard();
						simulatePlaceChess(color,i,j);
						while(!isEnding){
							simulatePoint=simulateAction(d);
							x=simulatePoint[0];
							y=simulatePoint[1];
							if(x>size || y>size || x<1 || y<1){
								if(simpleCount())
									valueBoard[i][j]++;
								isEnding=true;
								break;
							}
							simulatePlaceChess(d,x,y);
							simulatePoint=simulateAction(color);
							x=simulatePoint[0];
							y=simulatePoint[1];
							if(x>size || y>size || x<1 || y<1){
								if(simpleCount())
									valueBoard[i][j]++;
								isEnding=true;
								break;
							}
							simulatePlaceChess(color,x,y);
						}
					}
				}
			}	
    	
   /*for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				valueBoard[i][j]*=priortyBoard[i][j*/
    	
    	point[0]=1;
		point[1]=1;
		for(int i=1; i<=size ;i++){
			for(int j=1; j<=size ;j++){
				if(valueBoard[i][j]>valueBoard[point[0]][point[1]]){
					point[0]=i;
					point[1]=j;
				}
				System.out.printf("%2d ",valueBoard[j][i]);
			}
		System.out.println("");
	    }

    	System.out.println(point[0]+" "+point[1]);
    	System.out.println("");
    	return point;
    }

}
