package Inflection;

import java.util.ArrayList;
import java.util.List;
 
public class Node {

  private int[] move;
  private double prob;
  private int visitNum;
  private int winNum;
  private char color = ' ';
  public boolean isVisit;
  private boolean isBreedStep;
  private boolean isPerfectStep;
  private boolean isRealProb;
  private final List<Node> children = new ArrayList<>();
  private final Node parent;
  
  public Node(Node parent) {
   this.parent = parent;
   this.move=new int[4];
   this.visitNum=0;
   this.winNum=0;
   this.isVisit=false;
   this.isBreedStep=false;
   this.isPerfectStep=false;
   this.isRealProb=false;
   this.prob = -1;
  }
  
  public Node deleteTree(){
	  if(!this.children.isEmpty())
		  for (Node each : this.getChildren()) {
			  each = each.deleteTree();
		  }
	  return null;
  }
  
  public int[] getMove() {
   return move;
  }
  
  public void setMove(int[] move) {
	  for(int i=0;i<=3;i++)
		  this.move[i] = move[i];
  }
  
  public void setColor(char c) {
	  this.color = c;
  }
  
  public void markBreedStep() {
	 this.isBreedStep=true;
  }
  
  public void markPerfectStep() {
	 this.isPerfectStep=true;
  }
  
  public void setProb(double prob) {
	  this.prob = prob;
	  this.isRealProb=true;
  }
  
  public char getColor() {
	   return color;
  }
  
  public void addVisitNum() {
	  this.visitNum++;
  }
  
  public void addWinNum() {
	  this.winNum++;
  }
  
  public double getProb() {
	   return this.prob;
 }
  
  public void addProb(double p) {
	   this.prob+=p;
}
  
  public int getVisitNum() {
	   return visitNum;
  }
  
  public int getScore(){
	  
	  if(this.isRealProb && this.prob==1)return 1000;
	  else if(this.isRealProb && this.prob==0)return 0;
	  else if(!isVisit || visitNum<20){
		  if(isPerfectStep && isBreedStep)return 80;
		  else if(isPerfectStep)return 50;
		  else if(isBreedStep)return 20;
		  else return 5;
	  }
	  else if(this.prob>=0.9)return 80;
	  else if(this.prob>=0.8)return 50;
	  else if((isPerfectStep || isBreedStep) && this.prob>=0.6) return 50;
	  else if((isPerfectStep || isBreedStep) && this.prob>=0.5) return 30;
	  else if(this.prob>=0.7)return 30;
	  else if(this.prob>=0.6)return 20;
	  else if(this.prob>=0.5)return 15;
	  else if(this.prob>=0.4)return 5;
	  else return 1;
	
  }
  
  public void refreshWinProb(){
	   int subnodeNum = 0, subnodeVisitNum = 0;
	   int estiWinNum=this.winNum;
	   int estivisitNum=this.visitNum;
	   boolean isThisProbBeOne=true;
	   boolean isThisProbBeZero=false;
	   for (Node each : this.getChildren()) {
		   if(each.isRealProb && each.prob==0){//算勝率時，排除對手勝率=0的點，因為對手不會選那個點
			   estiWinNum-=each.visitNum;
			   estivisitNum-=each.visitNum;
		   }
		   if(isThisProbBeOne && (!each.isRealProb || each.prob!=0))
			   isThisProbBeOne=false;
		   if(each.isRealProb && each.prob==1)
			   isThisProbBeZero=true;
		   subnodeNum ++;
		   if((each.isVisit && each.visitNum>=20)||each.isRealProb)
			   subnodeVisitNum ++;
	   }
	   if(isThisProbBeOne){
		   this.prob=1;
		   this.isRealProb=true;
	   }
	   else if(isThisProbBeZero){
		   this.prob=0;
		   this.isRealProb=true;
	   }
	   else{
		   if(5*subnodeVisitNum >= 4*subnodeNum){ //有勝率且拜訪次數夠多的subnode過8成
				   double min = 1;
				   for (Node each : this.getChildren()) {
					   if(1 - each.getProb() < min && each.visitNum>=20)
						   min = 1 - each.getProb();
				   }
				   this.prob = min;
			   }
		   else
		   {
			   if(estivisitNum!=0)//有可能因為排除對手勝率=0的點，使得estivisitNum變成0
				   this.prob = estiWinNum/(double)estivisitNum;
			   else this.prob =-1;
		   }
	   }
  }
  
  public boolean isBreed(){
	  if(this.isBreedStep)return true;
	  else return false;
  }
  
  public boolean isPerfect(){
	  if(this.isPerfectStep)return true;
	  else return false;
  }
  
  public List<Node> getChildren() {
   return children;
  }
  
  public Node getParent() {
   return parent;
  }
  
  public Node addChild(int[] move) {
     Node node = new Node(this);
     node.setMove(move);
     this.getChildren().add(node);
     return node;
  }
  
  public Node addChild(int[] move, char c) {
	     Node node = new Node(this);
	     node.setColor(c);
	     node.setMove(move);
	     this.getChildren().add(node);
	     return node;
	  }
  
  public void printTree(String appender) {
	  for(int x:getMove())
		  System.out.print(appender + x);
	  System.out.println();
     for (Node each : this.getChildren()) {
      each.printTree(" " + appender);
     }
  }
  
}