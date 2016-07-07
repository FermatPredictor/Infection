package Inflection;

import java.io.BufferedReader;
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
	private boolean isWhiteAIOn=false;
	private boolean isBlackAIOn=false;
	private boolean isAITurn=false;
	private boolean isEnding=false;
	private Minim minim;
	private AudioPlayer song;
	private AudioPlayer effect[]=new AudioPlayer[10];
	private AI ai;
	public int lastMove[]=new int[2];
	
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
		ai=new AI(size,this,this);
		
		loading();
		initial();
		
		//button
		cp5.addButton("undo").setLabel("Undo")		
                             .setPosition(700,50)
                             .setSize(100, 50);
		cp5.addButton("resign").setLabel("Resign")
                               .setPosition(920,50)
                               .setSize(100, 50);
		cp5.addButton("newGame").setLabel("NewGame")
                                .setPosition(700,120)
                                .setSize(200, 50);
		
		minim = new Minim(this);
		effect[0]=minim.loadFile("Stone.wav");
	}
	
	public void draw() 
	{   
		if(isWhiteAIOn && nowStep%2==0){
			isAITurn=true;
			placeChessForAI('w');
			isAITurn=false;
		}
		else if(isBlackAIOn && nowStep%2==1){
			isAITurn=true;
			placeChessForAI('b');
			isAITurn=false;
		}
		
		if(mousePressed && canPlaceChess && isAllowPoint && !isAITurn ){
			placeChess();
			canPlaceChess=false;
			isClicked = true;
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
		if(nowStep%2==1) {
			if(!isPrepareJump)
				isAllowPoint=judgeAllowPoint(x,y,'b');
			else
				isAllowPoint=judgeAllowPoint2(x,y);
		}
		else if(nowStep%2==0) {
			if(!isPrepareJump)
				isAllowPoint=judgeAllowPoint(x,y,'w');
			else
				isAllowPoint=judgeAllowPoint2(x,y);
		}
		
		if(x>0 && y>0 && isAllowPoint && points[x][y]=='n'){
			if(nowStep%2==1){
				fill(0);
				ellipse((chessX+unit/2)+(getCoordinate()[0]-1)*unit, (chessY+unit/2)+(getCoordinate()[1]-1)*unit,unit*(float)0.8,unit*(float)0.8);
			}
			else if(nowStep%2==0){
				fill(255);
				ellipse((chessX+unit/2)+(getCoordinate()[0]-1)*unit, (chessY+unit/2)+(getCoordinate()[1]-1)*unit,unit*(float)0.8,unit*(float)0.8);
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
			
		if(isEnding)
			System.exit(0);
		
	}
	
	public void initial(){
		points[1][1]='b';
		points[5][5]='w';
		isPrepareJump=false;
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
	 
     public boolean judgeAllowPoint2(int x, int y){
		 
		 boolean isAllowPoint=false;
		 
		 if(x+2<=size){
			 if(x+2==PreparedJumpPoints[0] && y==PreparedJumpPoints[1])isAllowPoint=true;
		 }
		 if(x-2>0){
			 if(x-2==PreparedJumpPoints[0] && y==PreparedJumpPoints[1])isAllowPoint=true;
		 }
		 if(y+2<=size){
			 if(x==PreparedJumpPoints[0] && y+2==PreparedJumpPoints[1])isAllowPoint=true;
		 }
		 if(y-2>0){
			 if(x==PreparedJumpPoints[0] && y-2==PreparedJumpPoints[1])isAllowPoint=true;
		 }
		 
		 if(x+1<=size && y+1<=size){
			 if(x+1==PreparedJumpPoints[0] && y+1==PreparedJumpPoints[1])isAllowPoint=true;
		 }
		 if(x-1>0 && y-1>0){
			 if(x-1==PreparedJumpPoints[0] && y-1==PreparedJumpPoints[1])isAllowPoint=true;
		 }
		 if(x+1<=size && y-1>0){
			 if(x+1==PreparedJumpPoints[0] && y-1==PreparedJumpPoints[1])isAllowPoint=true;
		 }
		 if(x-1>0 && y+1<=size){
			 if(x-1==PreparedJumpPoints[0] && y+1==PreparedJumpPoints[1])isAllowPoint=true;
		 }
		 
		 return isAllowPoint;
 
	 }

   
	//judge in function "draw", if mousePressed, placed the stone.(released then can placed again)
    public void placeChess(){
    	
    	char color=' ';
    	if(nowStep%2==1)
    		color='b';
    	else if(nowStep%2==0)
    		color='w';

		int x=getCoordinate()[0];
		int y=getCoordinate()[1];
		if(x>0 && y>0 && x<=size && y<=size && isPrepareJump){
			if(nowStep%2==1){
				infection(x,y,'b');
				points[x][y]='b';
				char ch_x=(char)((int)'a'+x-1);
				char ch_y=(char)((int)'a'+y-1);
				information=information.concat(";B["+ch_x+ch_y+"]");
			}
			else if(nowStep%2==0){
				infection(x,y,'w');
				points[x][y]='w';
				char ch_x=(char)((int)'a'+x-1);
				char ch_y=(char)((int)'a'+y-1);
				information=information.concat(";W["+ch_x+ch_y+"]");
			}
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
			isPrepareJump=false;
		}
		else if(x>0 && y>0 && x<=size && y<=size && points[x][y]==color && !isPrepareJump){
			isPrepareJump=true;
			PreparedJumpPoints[0]=x;
			PreparedJumpPoints[1]=y;
		}
		else if(x>0 && y>0 && x<=size && y<=size && points[x][y]=='n'){
			lastMove[0]=x;
			lastMove[1]=y;
			if(nowStep%2==1){
				infection(x,y,'b');
				points[x][y]='b';
				char ch_x=(char)((int)'a'+x-1);
				char ch_y=(char)((int)'a'+y-1);
				information=information.concat(";B["+ch_x+ch_y+"]");
			}
			else if(nowStep%2==0){
				infection(x,y,'w');
				points[x][y]='w';
				char ch_x=(char)((int)'a'+x-1);
				char ch_y=(char)((int)'a'+y-1);
				information=information.concat(";W["+ch_x+ch_y+"]");
			}
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
    
    //load the record, and place chess from the first step to the last.
    private void loading(){
    	
    	nowStep=1;
        blackStones=0;
        whiteStones=0;
		for(int i=1; i<=size ;i++)
			for(int j=1; j<=size ;j++)
				points[i][j]='n';
		
    	int begin=information.indexOf(';',1);
    	//System.out.println(begin);
    	if(begin!=-1)
    		while(begin<information.length()){
    			int x=information.charAt(begin+3)-'a'+1;
    			int y=information.charAt(begin+4)-'a'+1;
    			if(nowStep%2==1){
    				placeChess('b',x,y);
    			}
    			else if(nowStep%2==0){
    				placeChess('w',x,y);
    			}
    			//System.out.println(x+" "+y);
    			begin+=6;
    		}
    	//System.out.println(nowStep);
    }
    
   
    
    private void placeChessForAI(char color){
    	int point[]=new int[2];
    	point=ai.AIaction(color);
		int x=point[0];
		int y=point[1];
		if(x>0 && y>0 && x<=size && y<=size && points[x][y]=='n'){
			if(nowStep%2==1){
				infection(x,y,'b');
				points[x][y]='b';
				char ch_x=(char)((int)'a'+x-1);
				char ch_y=(char)((int)'a'+y-1);
				information=information.concat(";B["+ch_x+ch_y+"]");
			}
			else if(nowStep%2==0){
				infection(x,y,'w');
				points[x][y]='w';
				char ch_x=(char)((int)'a'+x-1);
				char ch_y=(char)((int)'a'+y-1);
				information=information.concat(";W["+ch_x+ch_y+"]");
			}
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
		else{
			if(color=='b')
				isBlackAIOn=false;
			else if(color=='w')
				isWhiteAIOn=false;
		}
		
   }
    
    public void mouseReleased(){
    	canPlaceChess=true;
    }
    
    //button
    public void undo(){
    	if(nowStep>1){
    		information=information.substring(0,information.length()-6);
			try{
				FileWriter fw = new FileWriter("record.txt");
				fw.write(information + "\r\n");
				fw.flush();
				fw.close();
				//System.out.println(information);
			} catch (IOException e) {
			}
			
			loading();
			initial();
    	}
    }
    
    
    public void resign(){
    	if(nowStep%2==1){
    		JOptionPane.showMessageDialog(null,"White win by resignation.");
    		isEnding=true;
    	}
    	else if(nowStep%2==0){
    		JOptionPane.showMessageDialog(null,"Black win by resignation.");
    		isEnding=true;
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
			loading();
			initial();
	    }
    }

}
