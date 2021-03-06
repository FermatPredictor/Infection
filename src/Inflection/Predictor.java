package Inflection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;

public class Predictor extends AI{
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	private boolean isFullBoard=true;
	private boolean isNearFullBoard=true;//def: if stones>0.8 board, then true.
	private boolean canMove=true;
	private char cannotMoveColor=' ';
	private int simulateNum=100;
	private int simulateStepNum=6;
	private int sourceLevel=4;
	private int[] levelWinProb;
	private int nowPosition=0;
	
	
	private int winStoneNum=0; //def:blackStone-whiteStone for ending
	private int endSourceLevel=11;
	private int[] levelWinStone;//record most stoneNum of black win in current level.
	
	private String information="";
	
	private int[][] AIBoard;//used in simulate, noted the points that ai can choose.
	private int[][] randomJumpBoard;
	private int[][] chooseBoard;//noted the point that ai can choose, 0:initial, 1:breed, 2:jump, 3:can breed and jump
	private int[][] allowedMove;
	private int[] allowedMoveValue;//0~100, the win probability.  
	private int[] realAllowedMoveValue;//the ending win number(count as stone)
	private int value;//the number of win times while ai simulate games many times 
	private int allowedMoveNum;
	private char[][] simulateBoard; //b:black; w:white; n:null
	
	private int exStepNum=0;
	private int exStep[][]=new int[40][4];
	private int exStepValue[]=new int[40];
    boolean useSpecial=false;


	public Predictor(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		AIBoard=new int[size+1][size+1];
		randomJumpBoard=new int[size+1][size+1];
		chooseBoard=new int[size+1][size+1];
		allowedMove=new int[40][4];
		allowedMoveValue=new int[40];
		realAllowedMoveValue=new int[40];
		simulateBoard=new char[size+1][size+1];
		levelWinProb=new int[sourceLevel];
		levelWinStone=new int[endSourceLevel];
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
	 

	 private int[] simulateRandomChoosePoint(char color){
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
					if(chooseBoard[i][j]==1 || chooseBoard[i][j]==2 || chooseBoard[i][j]==3){
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
	    					else if(chooseBoard[i][j]==3){
	    						if(random.nextInt()%2==0){
	    							jump=randomJump(i,j,color);
	    							point[0]=jump[0];
	    							point[1]=jump[1];
	    						}
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
	 
	 private void copyBoard(char [][] board){
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					simulateBoard[i][j]=board[i][j];
	 }
	
	 
        //true for ending
	 	private boolean simulateCheckEnding(char c){

			
			isFullBoard=true;
			isNearFullBoard=true;
			int blackStones=0;
			int whiteStones=0;
			int nullPointNum=0;
			
			for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++)
					if(simulateBoard[i][j]=='n'){
						nullPointNum++;
						isFullBoard=false;
					}
			
			if(nullPointNum >= size*size/5)
				isNearFullBoard=false;

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
			
			if(isFullBoard){
				return true;
			}
			else {
				canMove=false;
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
				
				if(!canMove){
					cannotMoveColor=c;
					return true;
				}
				else
					return false;
			}
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
		 
		 if(simulateBoard[x][y]=='n'){
			 
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
				 chooseBoard[x][y]+=2;
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
     
     
     private void setAllAllowedMove(char color){
		 
     	for(int i=1; i<=size ;i++)
 			for(int j=1; j<=size ;j++){
 				setAllowedMove(i,j,color);
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
    		 boolean exBadStep=false;
 			 for(int j=0; j<exStepNum ;j++)
 				if(allowedMove[i][0]==exStep[j][0] && allowedMove[i][1]==exStep[j][1] &&
 				   allowedMove[i][2]==exStep[j][2] && allowedMove[i][3]==exStep[j][3] && exStepValue[j]<=-100){
 					exBadStep=true;
 					break;
 				}
 			 if(exBadStep){
 				realAllowedMoveValue[i]=-99;
 				continue;
 			 }
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
 		
 		if(size==5){
 			if(realAllowedMoveValue[choose]>0)
 	 			RecordTheGame(color);
 	 		else if(realAllowedMoveValue[choose]<0)
 	 			RecordTheGame(d);
 		}
 		
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
     
     
     //find the good step from learning
     private void looking(char color){
    	 
    	 char d=' ';
 		 if(color=='b')d='w';
 		 else if(color=='w')d='b';	
 		 
 		 exStepNum=0;
 		 for(int i=0; i<40 ; i++)
 			exStepValue[i]=0;
 		 
    	 information=board.information.substring(1);
    	 int inflen=information.length();
    	 ArrayList<String> recordList = new ArrayList<String>();

 		try{
			FileReader fr = new FileReader("learned_sz5.txt");
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
				
				if(recordList.get(0).length()==14 && recordList.get(0).endsWith("("+color+")"))
					exStepValue[now]=100;
				if(recordList.get(0).length()==14 && recordList.get(0).endsWith("("+d+")"))
					exStepValue[now]=-100;
				if(recordList.get(0).length()==25 && recordList.get(0).endsWith("("+d+")"))
					exStepValue[now]=-100;
				if(exStepValue[now]!=0){
					//System.out.println(recordList.get(0));
					//System.out.println(now+" "+exStep[now][0]+" "+exStep[now][1]+" "+exStep[now][2]+" "+exStep[now][3]+" "+exStepValue[now]);
				}
				
				exStepNum++;

				
				for(int i=1; i<sz ;i++){
	                
					rx=recordList.get(i).charAt(3)-'a'+1;
				    ry=recordList.get(i).charAt(4)-'a'+1;
					x=recordList.get(i).charAt(8)-'a'+1;
					y=recordList.get(i).charAt(9)-'a'+1;
					if(exStep[now][0]==rx && exStep[now][1]==ry && exStep[now][2]==x && exStep[now][3]==y){
						if(recordList.get(i).length()==14 && recordList.get(i).endsWith("("+color+")"))
							exStepValue[now]=100;
						if(recordList.get(i).length()==14 && recordList.get(i).endsWith("("+d+")"))
							exStepValue[now]=-100;
						if(recordList.get(i).length()==25 && recordList.get(i).endsWith("("+d+")"))
							exStepValue[now]=-100;
					}
					else{
						now++;
						exStepNum++;
						exStep[now][0]=rx;
						exStep[now][1]=ry;
						exStep[now][2]=x;
						exStep[now][3]=y;
						if(recordList.get(i).length()==14 && recordList.get(i).endsWith("("+color+")"))
							exStepValue[now]=100;
						if(recordList.get(i).length()==14 && recordList.get(i).endsWith("("+d+")"))
							exStepValue[now]=-100;
						if(recordList.get(i).length()==25 && recordList.get(i).endsWith("("+d+")"))
							exStepValue[now]=-100;
					}
					if(exStepValue[now]!=0){
						//System.out.println(recordList.get(i));
						//System.out.println(now+" "+exStep[now][0]+" "+exStep[now][1]+" "+exStep[now][2]+" "+exStep[now][3]+" "+exStepValue[now]);
					}
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
	 
     private int countWinProb(int position, int rx, int ry, int x, int y, char color){
    	 
    	 char d=' ';
		 if(color=='b')d='w';
		 else if(color=='w')d='b';
		 
		 if(position<nowPosition)
			 for(int i=position+1;i<sourceLevel;i++)
				 levelWinProb[i]=0;
		 nowPosition=position;
		 
		 int[][] allowedMove=new int[40][4];
		 int allowedMoveNum=0;
		 int nowWinProb;
		 char[][] memoryBoard=new char[size+1][size+1];
		 
		 simulateDoAction(rx,ry,x,y,color);
		 //System.out.println(position+" "+rx+" "+ry+" "+x+" "+y);
		 
		 for(int i=1; i<=size ;i++)
 	  		for(int j=1; j<=size ;j++)
 	  			memoryBoard[i][j]=simulateBoard[i][j];
		 
		 boolean isEnding=simulateCheckEnding(d);
    	 if(isEnding){
    		 if(simpleCount(color))
    			 return 100;
    		 else return 0;
    	 }
    	 else if(position==sourceLevel)
    		 return 100-simulatePlay(d,memoryBoard);
    	 
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
  	  									allowedMoveNum++;
  									}
  								}
  					 }
  				 }
  			}
    	 
    	 nowWinProb=100-countWinProb(position+1,allowedMove[0][0],allowedMove[0][1],allowedMove[0][2],allowedMove[0][3],d);
    	 
    	 if(allowedMoveNum==1)
    		 return nowWinProb;
    	 else{
    		 for(int i=1;i<allowedMoveNum;i++){
    			 for(int j=1; j<=size ;j++)
          	  		for(int k=1; k<=size ;k++)
          	  			simulateBoard[j][k]=memoryBoard[j][k];
    			 int prob=100-countWinProb(position+1,allowedMove[i][0],allowedMove[i][1],allowedMove[i][2],allowedMove[i][3],d);
    			 if(prob<nowWinProb){
    				 nowWinProb=prob;
    			 } 
    			 if(nowWinProb<levelWinProb[position])
    				 break;
    				 
        	 }  
    		 if(nowWinProb>levelWinProb[position])
    			 levelWinProb[position]=nowWinProb;
    		 return nowWinProb;
    	 }
    	 
     }
     
     //simulate the Game from this situation and count the win probability.("color" means first)
     private int simulatePlay(char color, char[][] board){
    	 
    	 char d=' ';
		 if(color=='b')d='w';
		 else if(color=='w')d='b';
		 
		 int simulatePoint[]=new int[4];
         int rx,ry,x,y;
         boolean isEnding=false;
         value=0;
         
         for(int j=1; j<=simulateNum ;j++){
        	 copyBoard(board);
        	 for(int k=1; k<=simulateStepNum ;k++){
 				if(k%2==1)
 					isEnding=simulateCheckEnding(color);
 				else if(k%2==0)
 					isEnding=simulateCheckEnding(d);
 				if(isEnding || k==simulateStepNum){
 					if(simpleCount(color))
 						value++;
 					break;
 				}
 				if(k%2==1)
 					simulatePoint=simulateRandomChoosePoint(color);
 				else if(k%2==0)
 					simulatePoint=simulateRandomChoosePoint(d);
 				rx=simulatePoint[0];
 				ry=simulatePoint[1];
 				x=simulatePoint[2];
 				y=simulatePoint[3];
 				if(k%2==1)
 					simulateDoAction(rx,ry,x,y,color);
 				else if(k%2==0)
 					simulateDoAction(rx,ry,x,y,d);
 			}
         }
         
         return value*100/simulateNum;

     }
	 
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){
    	
    	int point[]=new int[4];
	    allowedMoveNum=0;
	    nowPosition=0;
	    boolean wantResign=true;
	    
	    char d=' ';
		if(color=='b')d='w';
	    else if(color=='w')d='b';	
		
		copyBoard();
		String information=board.information.substring(1);
		
		if(information.isEmpty()){
			useSpecial=false;
			Random ran=new Random();
			if(ran.nextInt()%2==0)
				useSpecial=true;
		}
		else if(information.startsWith(";R[aa]B[bb]"))
			useSpecial=true;
			
		
		if(size==5 && color=='b' && useSpecial){
			lookingSpecial('b');
			if(exStepNum>0){
				try {
				    Thread.sleep(500); //delay 0.5s
				} catch (Exception e) {
				}
				Random ran=new Random();
				return exStep[ran.nextInt(exStepNum)];	
			}
		}
		else if(size==5 && color=='w'){
			lookingSpecial('w');
			if(exStepNum>0){
				try {
				    Thread.sleep(500); //delay 0.5s
				} catch (Exception e) {
				}
				Random ran=new Random();
				return exStep[ran.nextInt(exStepNum)];	
			}
		}
	
	    if(size==5){
	    	looking(color);
	    	if(exStepNum>0){
	    		for(int i=0;i<exStepNum;i++){
	    			if(exStepValue[i]>=100)
	    				return exStep[i];
	    			}
	    		}
	    }
		
		int nullPointNum=0;
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				if(simulateBoard[i][j]=='n'){
					nullPointNum++;
				}
		
		if(nullPointNum<=4){
	    	for(int i=0; i<endSourceLevel;i++){
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
			return endingGameMode(color);
		}
	
		
		setAllAllowedMove(color);
		
		boolean isRepeat=false;
		for(int i=0; i<exStepNum ;i++){
			isRepeat=false;
			for(int j=0; j<allowedMoveNum ;j++){
				if(exStep[i][0]==allowedMove[j][0] && exStep[i][1]==allowedMove[j][1] &&
				   exStep[i][2]==allowedMove[j][2] && exStep[i][3]==allowedMove[j][3]){
					isRepeat=true;
					break;
				}	
			}
			if(!isRepeat && exStepValue[i]>=0){
				setAllowedMove(exStep[i][0],exStep[i][1],exStep[i][2],exStep[i][3]);
			}
		}

    	for(int i=0; i<40;i++)
    		allowedMoveValue[i]=0;
    	for(int i=0; i<sourceLevel;i++)
    		levelWinProb[i]=0;

    	
    	
    	for(int i=0; i<allowedMoveNum ;i++){
    		
    		copyBoard();
			boolean exBadStep=false;
			for(int j=0; j<exStepNum ;j++)
				if(allowedMove[i][0]==exStep[j][0] && allowedMove[i][1]==exStep[j][1] &&
				   allowedMove[i][2]==exStep[j][2] && allowedMove[i][3]==exStep[j][3] && exStepValue[j]<=-100){
					exBadStep=true;
					break;
				}
			
			if(exBadStep){
				allowedMoveValue[i]=0;
				continue;
			}
			allowedMoveValue[i]=countWinProb(1,allowedMove[i][0],allowedMove[i][1],allowedMove[i][2],allowedMove[i][3],color);

	    }
    		
    	for(int i=0; i<allowedMoveNum ;i++){
			if(allowedMoveValue[i]>0){
				wantResign=false;
				break;
			}
    	}
    	
    	boolean isLowProbWin=true;
    	boolean isHighProbWin=false;
    	for(int i=0; i<allowedMoveNum ;i++){
			if(allowedMoveValue[i]>10){
				isLowProbWin=false;
				break;
			}
    	}
    	for(int i=0; i<allowedMoveNum ;i++){
			if(allowedMoveValue[i]>90){
				isHighProbWin=true;
				break;
			}
    	}
    	
    	if(size==5){
    		if(isLowProbWin)
        		RecordTheGame(d);
    		if(isHighProbWin && !information.isEmpty())
        		RecordTheGame(color);
    	}

    	isNearFullBoard=true;
		if(nullPointNum >= (size*size)/5)
			isNearFullBoard=false;
    	if(wantResign && !isNearFullBoard){
    		point[0]=size+1;
    		point[1]=size+1;
    		point[2]=size+1;
    		point[3]=size+1;	
    		return point;
    	}
    	
    	
    	int choose=0;
    	point[0]=allowedMove[choose][0];
		point[1]=allowedMove[choose][1];
		point[2]=allowedMove[choose][2];
		point[3]=allowedMove[choose][3];
		
		//System.out.println(color);
		for(int i=0; i<allowedMoveNum ;i++){
				if(allowedMoveValue[i]>allowedMoveValue[choose]){
					choose=i;
					point[0]=allowedMove[i][0];
					point[1]=allowedMove[i][1];
					point[2]=allowedMove[i][2];
					point[3]=allowedMove[i][3];
				}
				//System.out.println(i+" "+allowedMove[i][0]+" "+allowedMove[i][1]+" "+allowedMove[i][2]+" "+allowedMove[i][3]+" "+allowedMoveValue[i]);
			}
		//System.out.println("");

    	return point;
    }

}
