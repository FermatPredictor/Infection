package Inflection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;

public class AlphaCat extends AI{
	
	protected PApplet parent;
	protected ChessBoard board;
	int size;
	private boolean isFullBoard=true;
	private boolean isNearFullBoard=true;//def: if stones>0.8 board, then true.
	private boolean canMove=true;
	private char cannotMoveColor=' ';
	private int simulateNum=500;
	private int simulateStepNum=6;
	
	private String information="";
	
	private int[][] AIBoard;//used in simulate, noted the points that ai can choose.
	private int[][] randomJumpBoard;
	private int[][] chooseBoard;//noted the point that ai can choose, 0:initial, 1:breed, 2:jump, 3:can breed and jump
	private int[][][] allowedMove;
	private int[] allowedMoveValue;//the number of win times while ai simulate games many times 
	private int value;//the number of win times while ai simulate games many times 
	private int[] allowedMoveNum;
	private char[][] simulateBoard; //b:black; w:white; n:null
	
	
	/*private int exStepNum=0;
	private int exStep[][]=new int[40][4];
	private int exStepValue[]=new int[40];*/
	


	public AlphaCat(int size, PApplet parent, ChessBoard board){
		this.parent=parent;
		this.board=board;
		this.size=size;
		AIBoard=new int[size+1][size+1];
		randomJumpBoard=new int[size+1][size+1];
		chooseBoard=new int[size+1][size+1];
		allowedMove=new int[2][40][4];
		allowedMoveValue=new int[40];
		allowedMoveNum=new int[2];
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
		 if(c=='b' && blackStones>=whiteStones)
			 return true;
		 else if(c=='w' && whiteStones>=blackStones)
			 return true;
		 else return false;
			
	}
	 
	 private void setAllowedMove(int position, int rx, int ry,int x, int y){
		 allowedMove[position][allowedMoveNum[position]][0]=rx;
		 allowedMove[position][allowedMoveNum[position]][1]=ry;
		 allowedMove[position][allowedMoveNum[position]][2]=x;
		 allowedMove[position][allowedMoveNum[position]][3]=y;
		 allowedMoveNum[position]++;
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
	 
     private void setAllowedMove(int position, int x, int y, char c){
		 
		 char d=' ';
		 if(c=='b')d='w';
		 else if(c=='w')d='b';
		 
		 if(simulateBoard[x][y]=='n'){
			 
			 boolean set=false;
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++){
						int distance=Math.abs(i-x)+Math.abs(j-y);
						if(distance==1 && simulateBoard[i][j]==c && !set){
							setAllowedMove(position,size+1,size+1,x,y);
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
								setAllowedMove(position,i,j,x,y);
							}
						}
			 }
		 }
		 
	 }
     
     
     private void setAllAllowedMove(int position, char color){
		 
     	for(int i=1; i<=size ;i++)
 			for(int j=1; j<=size ;j++){
 				setAllowedMove(position,i,j,color);
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
     
     
     //find the good step from experience
     /*private void looking(char color){
    	 
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
			FileReader fr = new FileReader("sz5.txt");
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
				
				System.out.println(recordList.get(0));
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
				else if(recordList.get(0).length()==25 && recordList.get(0).endsWith("("+d+")"))
					exStepValue[now]=-100;
				System.out.println(0+" "+exStep[0][0]+" "+exStep[0][1]+" "+exStep[0][2]+" "+exStep[0][3]+" "+exStepValue[0]);
				
				exStepNum++;

				
				for(int i=1; i<sz ;i++){
	                
					rx=recordList.get(i).charAt(3)-'a'+1;
				    ry=recordList.get(i).charAt(4)-'a'+1;
					x=recordList.get(i).charAt(8)-'a'+1;
					y=recordList.get(i).charAt(9)-'a'+1;
					if(exStep[now][0]==rx && exStep[now][1]==ry && exStep[now][2]==x && exStep[now][3]==y){
						if(recordList.get(i).length()==14 && recordList.get(i).endsWith("("+color+")"))
							exStepValue[now]=100;
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
						if(recordList.get(i).length()==25 && recordList.get(i).endsWith("("+d+")"))
							exStepValue[now]=-100;
					}
					System.out.println(recordList.get(i));
					System.out.println(now+" "+exStep[now][0]+" "+exStep[now][1]+" "+exStep[now][2]+" "+exStep[now][3]+" "+exStepValue[now]);
				}
			}

		} catch (IOException e) {
		} 
     }*/
     
	 
	 
	 //this will decide a coordinate that AI want to play.
     public int[] AIaction(char color){
    	
    	int point[]=new int[4];
	    allowedMoveNum[0]=0;
	    allowedMoveNum[1]=0;
	    boolean isEnding=false;
	    boolean wantResign=true;
	    
	    char d=' ';
		if(color=='b')d='w';
	    else if(color=='w')d='b';	
		
		copyBoard();
		
		/*if(size==5){
			looking(color);
			if(exStepNum>0){
				for(int i=0;i<exStepNum;i++){
					if(exStepValue[i]>=100)
						return exStep[i];
				}
			}
		}*/
		
		setAllAllowedMove(0,color);
		
		/*boolean isRepeat=false;
		for(int i=0; i<exStepNum ;i++){
			isRepeat=false;
			for(int j=0; j<allowedMoveNum[0] ;j++){
				if(exStep[i][0]==allowedMove[0][j][0] && exStep[i][1]==allowedMove[0][j][1] &&
				   exStep[i][2]==allowedMove[0][j][2] && exStep[i][3]==allowedMove[0][j][3]){
					isRepeat=true;
					break;
				}	
			}
			if(!isRepeat){
				setAllowedMove(0,exStep[i][0],exStep[i][1],exStep[i][2],exStep[i][3]);
			}
		}*/
		
    	for(int i=0; i<40;i++)
    		allowedMoveValue[i]=0;
    	int simulatePoint[]=new int[4];
    	int rx,ry,x,y;
    	
    	for(int i=0; i<allowedMoveNum[0] ;i++){
    		
    		copyBoard();
    		
    		boolean isInfection=false;
			boolean valueInfection=false;
			int InfectionStoneNum=0;
			/*boolean exBadStep=false;
			for(int j=0; j<exStepNum ;j++)
				if(allowedMove[0][i][0]==exStep[j][0] && allowedMove[0][i][1]==exStep[j][1] &&
				   allowedMove[0][i][2]==exStep[j][2] && allowedMove[0][i][3]==exStep[j][3] && exStepValue[j]<=-100){
					exBadStep=true;
					break;
				}
			
			if(exBadStep){
				allowedMoveValue[i]=0;
				continue;
			}*/
			
			//the human-setting situation that change the allowedMoveValue
			if(allowedMove[0][i][0]==size+1 && allowedMove[0][i][1]==size+1){
				isInfection=true;//breed
				for(int j=1; j<=size ;j++)
					for(int k=1; k<=size ;k++){
						if(!(j==allowedMove[0][i][2] && k==allowedMove[0][i][3]))
							if(Math.abs(j-allowedMove[0][i][2])<=1 && Math.abs(k-allowedMove[0][i][3])<=1 && simulateBoard[j][k]!=color){
							valueInfection=true;
						}
					}
			}
			for(int j=1; j<=size ;j++)
				for(int k=1; k<=size ;k++){
					if(Math.abs(j-allowedMove[0][i][2])<=1 && Math.abs(k-allowedMove[0][i][3])<=1 && simulateBoard[j][k]==d){
						InfectionStoneNum++;
					}
				}
			
			
			simulateDoAction(allowedMove[0][i][0],allowedMove[0][i][1],allowedMove[0][i][2],allowedMove[0][i][3],color);
			allowedMoveNum[1]=0;
			setAllAllowedMove(1,d);
			if(allowedMoveNum[1]==0){
				cannotMoveColor=d;
				if(simpleCount(color))
					allowedMoveValue[i]+=100*simulateNum;
			}		
			
			for(int i2=0; i2<allowedMoveNum[1] ;i2++){
				value=0;
				for(int j=1; j<=simulateNum ;j++){
					copyBoard();
					simulateDoAction(allowedMove[0][i][0],allowedMove[0][i][1],allowedMove[0][i][2],allowedMove[0][i][3],color);
					simulateDoAction(allowedMove[1][i2][0],allowedMove[1][i2][1],allowedMove[1][i2][2],allowedMove[1][i2][3],d);
					
					for(int k=1; k<=simulateStepNum ;k++){
						if(k%2==1)
							isEnding=simulateCheckEnding(color);
						else if(k%2==0)
							isEnding=simulateCheckEnding(d);
						if(isEnding || k==simulateStepNum){
							if(simpleCount(d))
								value++;
							if(!isNearFullBoard && !canMove){
								if(k==1)
									value+=100;
								else if(k==2)value-=100;
							}
							break;
						}
						if(k%2==1)
							simulatePoint=simulateGame(color);
						else if(k%2==0)
							simulatePoint=simulateGame(d);
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
				
			/*System.out.println(i+" "+allowedMove[0][i][0]+" "+allowedMove[0][i][1]+" "+allowedMove[0][i][2]+" "+allowedMove[0][i][3]
						+" "+i2+" "+allowedMove[1][i2][0]+" "+allowedMove[1][i2][1]+" "+allowedMove[1][i2][2]+" "
						+allowedMove[1][i2][3]+" "+value);*/
			
				if(i2==0)
				allowedMoveValue[i]=simulateNum-value;
				else if(simulateNum-value < allowedMoveValue[i])
				allowedMoveValue[i]=simulateNum-value;
			}	
			
			if(isInfection && !valueInfection){
				allowedMoveValue[i]*=0.4;
			}
			else if(InfectionStoneNum>=3){
				allowedMoveValue[i]*=1.1;
			}
			
	    }
    	
    	for(int i=0; i<allowedMoveNum[0] ;i++){
			if(allowedMoveValue[i]>0){
				wantResign=false;
				break;
			}
    	}
    	
    	
    	int nullPointNum=0;
    	isNearFullBoard=true;
    	copyBoard();
    	for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				if(simulateBoard[i][j]=='n'){
					nullPointNum++;
				}
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
    	point[0]=allowedMove[0][choose][0];
		point[1]=allowedMove[0][choose][1];
		point[2]=allowedMove[0][choose][2];
		point[3]=allowedMove[0][choose][3];
		
		for(int i=0; i<allowedMoveNum[0] ;i++){
				if(allowedMoveValue[i]>allowedMoveValue[choose]){
					choose=i;
					point[0]=allowedMove[0][i][0];
					point[1]=allowedMove[0][i][1];
					point[2]=allowedMove[0][i][2];
					point[3]=allowedMove[0][i][3];
				}
				System.out.println(i+" "+allowedMove[0][i][0]+" "+allowedMove[0][i][1]+" "+allowedMove[0][i][2]+" "+allowedMove[0][i][3]+" "+allowedMoveValue[i]);
			}
		System.out.println("");

    	return point;
    }

}
