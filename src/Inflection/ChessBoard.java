package Inflection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import controlP5.ControlP5;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PImage;

public class ChessBoard extends PApplet{
	
	private static final long serialVersionUID = 1L;
	private final static int width = 1200, height = 500;
	private ControlP5 cp5;
	private int chessX=100, chessY=50, chessBoardWidth=550;
	public PImage white, black, whiteCurrent, blackCurrent;
	public PImage whiteCat1, whiteCat2, blackCat1, blackCat2;
	private int size=5;
	private float unit=(float)chessBoardWidth/size;
	private int nowStep=1;
	public char[][] points=new char[size+1][size+1]; //b:black; w:white; n:null; j:jump
	private boolean canPlaceChess=true;
	private boolean isAllowPoint=true;
	private boolean isPrepareJump=false;
	private int PreparedJumpPoints[]=new int[2];
	public boolean isClicked = false;
	private int blackStones=0;
	private int whiteStones=0;
	String information="";
	String symmetryinformation="";
	private boolean isWhiteAIOn=false;
	private boolean isBlackAIOn=false;
	private boolean isAITurn=false;
	private boolean isEnding=false;
	private Minim minim;
	private AudioPlayer song;
	private AudioPlayer effect[]=new AudioPlayer[10];
	private Zero zero;
	private AlphaCat alphacat;
	private BetaCat betacat;
	private Predictor predictor;
	public int lastMove[]=new int[2];
	
	private char winner;//b:black, w:white, d:draw
	
	//use in check the draw(cycle)
	private boolean occurCycle=false;
	private String cycleStep;
	
	
	public ChessBoard() {
		try {
			FileReader fr = new FileReader("record.txt");
			BufferedReader br = new BufferedReader(fr);
			
			while(br.ready()) {
				//System.out.print((char)br.read());
				information  = br.readLine();
			}
			fr.close();
			System.out.println(information);
			
		} catch (IOException e) {
		}
	}
	
	public void setup() {
		size(width, height);
		cp5= new ControlP5(this);
		this.white=loadImage("white.PNG");
		this.black=loadImage("black.PNG");
		this.whiteCurrent=loadImage("white_current.png");
		this.blackCurrent=loadImage("black_current.png");
		this.whiteCat1=loadImage("WhiteCat1.png");
		this.whiteCat2=loadImage("WhiteCat2.png");
		this.blackCat1=loadImage("BlackCat1.png");
		this.blackCat2=loadImage("BlackCat2.png");
        white.resize((int)unit, (int)unit);
        black.resize((int)unit, (int)unit);
        whiteCurrent.resize((int)unit, (int)unit);
        blackCurrent.resize((int)unit, (int)unit);
        whiteCat1.resize(200, 150);
        blackCat1.resize(200, 150);
        whiteCat2.resize(200, 150);
        blackCat2.resize(200, 150);
        zero=new Zero(size,this,this);
        alphacat=new AlphaCat(size,this,this);
        betacat=new BetaCat(size,this,this);
        predictor=new Predictor(size,this,this);
		
		initial();
		loading();
		
		//button
		cp5.addButton("undo").setLabel("Undo")		
                             .setPosition(700,50)
                             .setSize(100, 50);
		cp5.addButton("resign").setLabel("Resign")
                               .setPosition(810,50)
                               .setSize(100, 50);
		cp5.addButton("blackCatOn").setLabel("Black Cat")
		                       .setPosition(920,50)
                               .setSize(100, 50);
		cp5.addButton("whiteCatOn").setLabel("White Cat")
                               .setPosition(1030,50)
                               .setSize(100, 50);
		cp5.addButton("newGame").setLabel("NewGame")
                                .setPosition(700,120)
                                .setSize(200, 50);
		cp5.addButton("test").setLabel("Test")
		                        .setPosition(920,120)
		                        .setSize(200, 50);
		
		minim = new Minim(this);
		effect[0]=minim.loadFile("Stone.wav");
	}
	
	public void draw() 
	{   
		
		if(!isEnding){
			if(isWhiteAIOn && nowStep%2==0){
				isAITurn=true;
				DoActionForAI(alphacat,'w');
				isAITurn=false;
			}
			else if(isBlackAIOn && nowStep%2==1){
				isAITurn=true;
				DoActionForAI(betacat,'b');
				isAITurn=false;
			}
			if(mousePressed && canPlaceChess && !isAITurn ){
				DoAction();
				canPlaceChess=false;
				isClicked = true;
			}
		}
		
		
		//count the number of the stones in the board
		blackStones=0;
		whiteStones=0;
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++){
				if(points[i][j]=='b')
					blackStones++;
				else if(points[i][j]=='w')
					whiteStones++;
					
			}
		
		 if(!isEnding && !isPrepareJump){
			 removeJumpArea();
			 CheckEnding();
			 CheckDraw();
		 }
		
		background(52,203,41);
		fill(168,134,87);
		rect(chessX-20,chessY-20,chessBoardWidth+40,chessBoardWidth+40);
		stroke(0);
		for(int i=0; i<=size ; i++){
			line(chessX+i*unit,chessY,chessX+i*unit,chessY+chessBoardWidth);
		}
		for(int i=0; i<=size ; i++){
			line(chessX,chessY+i*unit,chessX+chessBoardWidth,chessY+i*unit);
		}
		if(isPrepareJump){
			fill(84,153,237);
			rect(chessX+(float)(PreparedJumpPoints[0]-1)*unit,chessY+(float)(PreparedJumpPoints[1]-1)*unit,unit,unit);
		}
		
		//character for black and white
		if(nowStep%2==1){
			image(blackCat2,700,200);
			image(whiteCat1,900,200);
		}
		else if(nowStep%2==0){
			image(blackCat1,700,200);
			image(whiteCat2,900,200);
		}
		fill(192,107,86);
		rect(700,350,400,50);
		fill(0);
		this.textFont(createFont("Arial", 12), 24);
        this.text("stones" , 870, 380);
        this.text(blackStones , 800, 380);
        this.text(whiteStones , 1000, 380);
		
		int x=getCoordinate()[0];
		int y=getCoordinate()[1];
		
		if(!isPrepareJump){
			if(nowStep%2==1)
				isAllowPoint=judgeAllowPoint(x,y,'b');
			else if(nowStep%2==0)
				isAllowPoint=judgeAllowPoint(x,y,'w');
			removeJumpArea();
		}
		else
			setAllowJumpArea(PreparedJumpPoints[0],PreparedJumpPoints[1]);

		
		if(x>0 && y>0 && isAllowPoint && points[x][y]=='n' && !isPrepareJump){
			if(nowStep%2==1){
				fill(0);
				ellipse((chessX+unit/2)+(getCoordinate()[0]-1)*unit, (chessY+unit/2)+(getCoordinate()[1]-1)*unit,unit*(float)0.8,unit*(float)0.8);
			}
			else if(nowStep%2==0){
				fill(255);
				ellipse((chessX+unit/2)+(getCoordinate()[0]-1)*unit, (chessY+unit/2)+(getCoordinate()[1]-1)*unit,unit*(float)0.8,unit*(float)0.8);
			}
		}
		else if(isPrepareJump){
			 for(int i=1; i<=size ;i++)
					for(int j=1; j<=size ;j++)
						if(points[i][j]=='j'){
							fill(173,34,208);
							rect(chessX+(float)(i-1)*unit,chessY+(float)(j-1)*unit,unit,unit);
						}
							
		}
		
		 for(int i=1; i<=size ;i++)
				for(int j=1; j<=size ;j++){
					if(i==lastMove[0] && j==lastMove[1]){
						if(points[i][j]=='b')
							image(blackCurrent,(chessX+unit/2)+(i-1)*unit-unit/2, (chessY+unit/2)+(j-1)*unit-unit/2);
						else if(points[i][j]=='w')
					        image(whiteCurrent,(chessX+unit/2)+(i-1)*unit-unit/2, (chessY+unit/2)+(j-1)*unit-unit/2);
					}
					else{
						if(points[i][j]=='b')
							image(black,(chessX+unit/2)+(i-1)*unit-unit/2, (chessY+unit/2)+(j-1)*unit-unit/2);
						else if(points[i][j]=='w')
							image(white,(chessX+unit/2)+(i-1)*unit-unit/2, (chessY+unit/2)+(j-1)*unit-unit/2);
					}
				}

	}
	
	public void initial(){
		
	    blackStones=0;
	    whiteStones=0;
	    for(int i=1; i<=size ;i++)
	    	for(int j=1; j<=size ;j++)
	    		points[i][j]='n';
	    points[1][1]='b';
	    points[size][size]='w';
		isEnding=false;
		isPrepareJump=false;
	}
	
	//check whether the cycle cause draw
	private void CheckDraw(){
		
		int begin=information.indexOf(';',1);
		int unit=11;//a one step record
		int len=information.length();
		
		if(len>1){
			String s=information.substring(begin);
			if(occurCycle){
				if(s.endsWith(cycleStep)){
					JOptionPane.showMessageDialog(null,"draw by threefold repetition.");
					winner='d';
					isEnding=true;
				}
			}
				
			String halfStr;
			int stepNum=s.length()/unit;
			if(stepNum%2==1)
				begin+=unit;
			
			while(begin<len){
				s=information.substring(begin);
				halfStr=s.substring(s.length()/2);
				if(s.startsWith(halfStr)){
					occurCycle=true;
					cycleStep=halfStr.substring(0,unit);
					//System.out.println(cycleStep);
					break;
				}
				else 
					occurCycle=false;
				begin+=2*unit;
			}
		}
		
	}
	
	private void CheckEnding(){

		boolean canMove=false;
		boolean isFullBoard=true;
		int winPoints;
		char c=' ';
		if(nowStep%2==1)c='b';
		else if(nowStep%2==0)c='w';
		
		if(blackStones==0){
    		JOptionPane.showMessageDialog(null,"White infect all black stones.");
    		isEnding=true;
    		winner='w';
    	}
		else if(whiteStones==0){
    		JOptionPane.showMessageDialog(null,"Black infect all white stones.");
    		isEnding=true;
    		winner='b';
    	}	
		
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				if(points[i][j]=='n')
					isFullBoard=false;
		
		if(isFullBoard){
			winPoints=abs(blackStones-whiteStones);
			if(blackStones==whiteStones){
				JOptionPane.showMessageDialog(null,"draw.");
				winner='d';
			}
			else if(blackStones>whiteStones){
				JOptionPane.showMessageDialog(null,"Black win "+winPoints+" points");
				winner='b';
			}
			else if(blackStones<whiteStones){
				JOptionPane.showMessageDialog(null,"White win "+winPoints+" points");
				winner='w';
			}
			isEnding=true;
		}
		else if(!isEnding){
			for(int i=1; i<=size ;i++)
				 for(int j=1; j<=size ;j++)
					 if(points[i][j]=='n' && !canMove){
						 if(i+1<=size){
							 if(points[i+1][j]==c)canMove=true;
						 }
			    		 if(i-1>0){
			    			 if(points[i-1][j]==c)canMove=true;
			    		 }
			    		 if(j+1<=size){
			    			 if(points[i][j+1]==c)canMove=true;
			    		 }
			    		 if(j-1>0){
			    			 if(points[i][j-1]==c)canMove=true;
			    		 }
			    		 
			    		 if(i+2<=size){
			    			 if(points[i+2][j]==c)canMove=true;
			    		 }
			    		 if(i-2>0){
			    			 if(points[i-2][j]==c)canMove=true;
			    		 }
			    		 if(j+2<=size){
			    			 if(points[i][j+2]==c)canMove=true;
			    		 }
			    		 if(j-2>0){
			    			 if(points[i][j-2]==c)canMove=true;
			    		 }
			    			 
			    	     if(i+1<=size && j+1<=size){
			    			 if(points[i+1][j+1]==c)canMove=true;
			    	     }
			    		 if(i-1>0 && j-1>0){
			    			 if(points[i-1][j-1]==c)canMove=true;
			    		 }
			    		 if(i+1<=size && j-1>0){
			    			 if(points[i+1][j-1]==c)canMove=true;
			    		 }
			    		 if(i-1>0 && j+1<=size){
			    			 if(points[i-1][j+1]==c)canMove=true;
			    		 }
			   };
			   if(!canMove){
					
					//add the information that imply pass
		    		char ch_x=(char)((int)'a'+size);
					char ch_y=(char)((int)'a'+size);
					char ch_rx=(char)((int)'a'+size);
					char ch_ry=(char)((int)'a'+size);
					
					if(nowStep%2==1){
						information=information.concat(";R["+ch_rx+ch_ry+"]"+"B["+ch_x+ch_y+"]");
			    	}
					else if(nowStep%2==0){
			    		information=information.concat(";R["+ch_rx+ch_ry+"]"+"W["+ch_x+ch_y+"]");
			    	}
					
					nowStep++;
				}
		}
	}
	
	public void removeJumpArea(){
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				if(points[i][j]=='j')
					points[i][j]='n';
	}
	
	public int[] getCoordinate(){
		int point[]=new int[2];
		float unit=(float)chessBoardWidth/size;
		for(int i=1; i<=size;i ++){
			if(mouseX>=chessX+(i-1)*unit && mouseX<chessX+i*unit){
				point[0]=i;
			}
		}
		for(int j=1; j<=size;j ++){
			if(mouseY>=chessY+(j-1)*unit && mouseY<chessY+j*unit){
				point[1]=j;
			}
		}
		//System.out.println(point[0]+" "+point[1]);
		return point;
	}
	
	//if the stone place or move at a place, infect all the stones near the place
	 public void infection(int x, int y, char c){

		 char d=' ';
		 if(c=='b')d='w';
		 else if(c=='w')d='b';
		 if(x+1<=size){
			 if(points[x+1][y]==d)points[x+1][y]=c;
		 }
		 if(x-1>0){
			 if(points[x-1][y]==d)points[x-1][y]=c;
		 }
		 if(y+1<=size){
			 if(points[x][y+1]==d)points[x][y+1]=c;
		 }
		 if(y-1>0){
			 if(points[x][y-1]==d)points[x][y-1]=c;
		 }
		 if(x+1<=size && y+1<=size){
			 if(points[x+1][y+1]==d)points[x+1][y+1]=c;
		 }
		 if(x+1<=size && y-1>0){
			 if(points[x+1][y-1]==d)points[x+1][y-1]=c;
		 }
		 if(x-1>0 && y+1<=size){
			 if(points[x-1][y+1]==d)points[x-1][y+1]=c;
		 }
		 if(x-1>0 && y-1>0){
			 if(points[x-1][y-1]==d)points[x-1][y-1]=c;
		 }
		 
		 
	 }
	 

	 
	//use in function "draw" that always judge whether the stone can place at this place
	 public boolean judgeAllowPoint(int x, int y, char c){
		 
		 boolean isAllowPoint=false;
	
		 if(x+1<=size){
			 if(points[x+1][y]==c)isAllowPoint=true;
		 }
		 if(x-1>0){
			 if(points[x-1][y]==c)isAllowPoint=true;
		 }
		 if(y+1<=size){
			 if(points[x][y+1]==c)isAllowPoint=true;
		 }
		 if(y-1>0){
			 if(points[x][y-1]==c)isAllowPoint=true;
		 }
		 
		 return isAllowPoint;
 
	 }
	 
     
     private void setAllowJumpArea(int x, int y){
		 
		
		 if(x+2<=size){
			 if(points[x+2][y]=='n')points[x+2][y]='j';
		 }
		 if(x-2>0){
			 if(points[x-2][y]=='n')points[x-2][y]='j';
		 }
		 if(y+2<=size){
			 if(points[x][y+2]=='n')points[x][y+2]='j';
		 }
		 if(y-2>0){
			 if(points[x][y-2]=='n')points[x][y-2]='j';
		 }
			 
	     if(x+1<=size && y+1<=size){
			 if(points[x+1][y+1]=='n')points[x+1][y+1]='j';
	     }
		 if(x-1>0 && y-1>0){
			 if(points[x-1][y-1]=='n')points[x-1][y-1]='j';
		 }
		 if(x+1<=size && y-1>0){
			 if(points[x+1][y-1]=='n')points[x+1][y-1]='j';
		 }
		 if(x-1>0 && y+1<=size){
			 if(points[x-1][y+1]=='n')points[x-1][y+1]='j';
		 }
		 
		 
     }
     

   
	//judge in function "draw", if mousePressed, placed or move the stone.(released then can placed again)
    public void DoAction(){
    	
    	char color=' ';
    	if(nowStep%2==1)
    		color='b';
    	else if(nowStep%2==0)
    		color='w';

		int x=getCoordinate()[0];
		int y=getCoordinate()[1];
		if(x>0 && y>0 && x<=size && y<=size && isPrepareJump){
			if(points[x][y]=='j'){
				lastMove[0]=x;
				lastMove[1]=y;
				points[PreparedJumpPoints[0]][PreparedJumpPoints[1]]='n';
				infection(x,y,color);
				points[x][y]=color;
				char ch_x=(char)((int)'a'+x-1);
				char ch_y=(char)((int)'a'+y-1);
				char ch_rx=(char)((int)'a'+PreparedJumpPoints[0]-1);
				char ch_ry=(char)((int)'a'+PreparedJumpPoints[1]-1);
				if(nowStep%2==1)
					information=information.concat(";R["+ch_rx+ch_ry+"]"+"B["+ch_x+ch_y+"]");
				else if(nowStep%2==0)
					information=information.concat(";R["+ch_rx+ch_ry+"]"+"W["+ch_x+ch_y+"]");
				nowStep++;
				effect[0].loop();
				effect[0].play();
				
				try{
					FileWriter fw = new FileWriter("record.txt");
					fw.write(information + "\r\n");
					fw.flush();
					fw.close();
					//System.out.println(information);
				} catch (IOException e) {
				}
			}
			isPrepareJump=false;
		}
		else if(x>0 && y>0 && x<=size && y<=size && points[x][y]==color && !isPrepareJump){
			isPrepareJump=true;
			PreparedJumpPoints[0]=x;
			PreparedJumpPoints[1]=y;
		}
		else if(x>0 && y>0 && x<=size && y<=size && points[x][y]=='n' && isAllowPoint){
			lastMove[0]=x;
			lastMove[1]=y;
			infection(x,y,color);
			points[x][y]=color;
			char ch_x=(char)((int)'a'+x-1);
			char ch_y=(char)((int)'a'+y-1);
			//add the information that imply does not remove stone
			char ch_rx=(char)((int)'a'+size);
			char ch_ry=(char)((int)'a'+size);
			if(nowStep%2==1)
				information=information.concat(";R["+ch_rx+ch_ry+"]"+"B["+ch_x+ch_y+"]");
			else if(nowStep%2==0)
				information=information.concat(";R["+ch_rx+ch_ry+"]"+"W["+ch_x+ch_y+"]");
			nowStep++;
			effect[0].loop();
			effect[0].play();
				
				try{
					FileWriter fw = new FileWriter("record.txt");
					fw.write(information + "\r\n");
					fw.flush();
					fw.close();
					//System.out.println(information);
				} catch (IOException e) {
				}
		}
		
   }
   
    
	//use in the function "loading"
    private void placeChess(char color, int x, int y){

		if(x>0 && y>0 && x<=size && y<=size && points[x][y]=='n'){
			lastMove[0]=x;
			lastMove[1]=y;
			if(color=='b'){
				infection(x,y,'b');
				points[x][y]='b';
			}
			else if(color=='w'){
				infection(x,y,'w');
				points[x][y]='w';
			}
		}
		//if one pass, still add a step 
		nowStep++;	
   }
    
	//use in the function "loading"
    private void Remove(int x, int y){

		if(x>0 && y>0 && x<=size && y<=size)
			points[x][y]='n';
		
   }
    
    //load the record, and place chess from the first step to the last.
    private void loading(){
    	
    	nowStep=1;
		
    	int begin=information.indexOf(';',1);
    	//System.out.println(begin);
    	if(begin!=-1)
    		while(begin<information.length()){
    			int rx=information.charAt(begin+3)-'a'+1;
    			int ry=information.charAt(begin+4)-'a'+1;
    			Remove(rx,ry);
    			
    			int x=information.charAt(begin+8)-'a'+1;
    			int y=information.charAt(begin+9)-'a'+1;
    			if(nowStep%2==1){
    				placeChess('b',x,y);
    			}
    			else if(nowStep%2==0){
    				placeChess('w',x,y);
    			}
    			//System.out.println(x+" "+y);
    			begin+=11;
    		}
    	//System.out.println(nowStep);
    }
    
   
    
    private void DoActionForAI(AI ai,char color){
    	
    	boolean wantResign=true;
    	int point[]=new int[4];
    	point=ai.AIaction(color);
    	int rx=point[0];
		int ry=point[1];
    	int x=point[2];
		int y=point[3];
		if(rx>0 && ry>0 && rx<=size && ry<=size)
			points[rx][ry]='n';
		if(x>0 && y>0 && x<=size && y<=size && points[x][y]=='n'){
			lastMove[0]=x;
			lastMove[1]=y;
			points[x][y]=color;
			infection(x,y,color);
			wantResign=false;
		}
		else
			resign();
		
		if(!wantResign){
			char ch_x=(char)((int)'a'+x-1);
			char ch_y=(char)((int)'a'+y-1);
			char ch_rx=(char)((int)'a'+rx-1);
			char ch_ry=(char)((int)'a'+ry-1);
			if(nowStep%2==1)
				information=information.concat(";R["+ch_rx+ch_ry+"]"+"B["+ch_x+ch_y+"]");
			else if(nowStep%2==0)
				information=information.concat(";R["+ch_rx+ch_ry+"]"+"W["+ch_x+ch_y+"]");
			nowStep++;
			effect[0].loop();
			effect[0].play();
			try{
				FileWriter fw = new FileWriter("record.txt");
				fw.write(information + "\r\n");
				fw.flush();
				fw.close();
				//System.out.println(information);
			} catch (IOException e) {
			}

		}
		
   }
    
    public void mouseReleased(){
    	canPlaceChess=true;
    }
    
    //button
    public void undo(){
    	if(nowStep>1){
    		char autoPassInfor=(char)((int)'a'+size);
    		int len=information.length();
    		if(information.charAt(len-2)==autoPassInfor && information.charAt(len-3)==autoPassInfor
    		   && information.charAt(len-7)==autoPassInfor && information.charAt(len-8)==autoPassInfor)
    			information=information.substring(0,len-22);
    		else information=information.substring(0,len-11);

			try{
				FileWriter fw = new FileWriter("record.txt");
				fw.write(information + "\r\n");
				fw.flush();
				fw.close();
				//System.out.println(information);
			} catch (IOException e) {
			}
			
			initial();
			loading();

    	}
    }
    
    
    public void resign(){
    	if(nowStep%2==1){
    		JOptionPane.showMessageDialog(null,"White win by resignation.");
    		winner='w';
    		isEnding=true;
    	}
    	else if(nowStep%2==0){
    		JOptionPane.showMessageDialog(null,"Black win by resignation.");
    		winner='b';
    		isEnding=true;
    	}
    }
    
    public void blackCatOn(){
    	isBlackAIOn=!isBlackAIOn;
    }
    
    public void whiteCatOn(){
    	isWhiteAIOn=!isWhiteAIOn;
    }
    
    //return the symmetry of the information
    public String symmetry(String inf){
    	
    	int len=inf.length();
    	String s=inf;
    	char[] sym=new char[len+1];
    	sym=s.toCharArray();
    	for(int i=0;i<len;i++){
    		if(inf.charAt(i)=='['){
    			sym[i+1]=inf.charAt(i+2);
    			sym[i+2]=inf.charAt(i+1);
    		}
    	}

    	return String.valueOf(sym);
    }
    
    public void RecordEndingGame(){

    	ArrayList<String> recordList = new ArrayList<String>();
    	
    	//record the game was played
		try{
			FileReader fr = new FileReader("sz5.txt");
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
			int sign=information.indexOf(';',1);
			String s=information.substring(sign).concat("("+winner+")");
			for(int i=0; i<recordList.size() ;i++){
				if(s.equals(recordList.get(i)))
					isRepeat=true;
			}
			if(!isRepeat){
				recordList.add(s);
				recordList.sort(null);
			}
			
			symmetryinformation=symmetry(s);
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

			FileWriter fw = new FileWriter("sz5.txt");
			for(int i=0; i<sz ;i++)
				fw.write(i+record[i] + "\r\n");
			fw.flush();
			fw.close();
			//System.out.println(information);
		} catch (IOException e) {
		}  	
    }
    
    public void newGame(){
    	
    	int dialogButton = 0; 
	    dialogButton = JOptionPane.showConfirmDialog (null, "Do you want to play a new game?","Confirm", dialogButton);
	    if(dialogButton == JOptionPane.YES_OPTION){
	    	information=";";
			try{
				FileWriter fw = new FileWriter("record.txt");
				fw.write(information + "\r\n");
				fw.flush();
				fw.close();
			} catch (IOException e) {
			}
			initial();
			loading();
	    }
    }
    
    public void test(){
    	
    	BetaCat ai=new BetaCat(size,this,this);
    	//Node root = new Node(null);
    	//ai.expandNode(root,points,color);
     	//ai.setGoodSteps(points,root);
    }
    

}
