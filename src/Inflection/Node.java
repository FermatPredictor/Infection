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
  private final List<Node> children = new ArrayList<>();
  private final Node parent;
  
  public Node(Node parent) {
   this.parent = parent;
   this.move=new int[4];
   this.visitNum=0;
   this.winNum=0;
   this.isVisit=false;
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
  
  public void setProb(double prob) {
	  this.prob = prob;
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
  
  public int getVisitNum() {
	   return visitNum;
  }
  
  public int getScore(){
	  if(!isVisit)return 3;
	  else if(this.prob>=0.9)return 20;
	  else if(this.prob>=0.8)return 10;
	  else if(visitNum<20)return 3;
	  else if(this.prob>=0.6)return 4;
	  else if(this.prob>=0.4)return 3;
	  else return 1;
	
  }
  
  public void refreshWinProb(Node subRefreshNode){
	   int subnodeNum = 0, subnodeVisitNum = 0;
	   for (Node each : this.getChildren()) {
		   subnodeNum ++;
		   if(each.isVisit)
			   subnodeVisitNum ++;
	   }
	   if(5*subnodeVisitNum >= 4*subnodeNum) //有勝率的subnode過8成
		   if(5*(subnodeVisitNum-1) >= 4*subnodeNum)//不需要重刷
			   if(this.prob == -1 || this.prob > 1 - subRefreshNode.getProb())
				   this.prob = 1 - subRefreshNode.getProb();
		   else {//需要重刷
			   double min = 1;
			   for (Node each : this.getChildren()) {
				   if(each.isVisit && 1 - each.getProb() < min)
					   min = 1 - each.getProb();
			   }
			   this.prob = min;
		   }
	   else
		   this.prob = winNum/(double)visitNum;
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