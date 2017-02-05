package Inflection;

import java.util.ArrayList;
import java.util.List;
 
public class TreeNode {

  private int[] move;
  private char color = ' ';
  private int result;// the winNum after n steps
  public boolean isVisit;
  
  private final List<TreeNode> children = new ArrayList<>();
  private final TreeNode parent;
  
  public TreeNode(TreeNode parent) {
   this.parent = parent;
   this.move=new int[4];
   this.isVisit=false;
   this.result=9999;
  }
  
  public TreeNode deleteTree(){
	  if(!this.children.isEmpty())
		  for (TreeNode each : this.getChildren()) {
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
  

  public char getColor() {
	   return color;
  }
  
  public void setResult(int result) {
	  this.result=result;
 }
  
  public int getResult() {
	   return result;
 }
  
  public int getSubNodeNum() {
	   int subNodeNum=0;
	   for(TreeNode nodes:this.getChildren())
		   subNodeNum++;
	   return subNodeNum;
 }
  
  
  public List<TreeNode> getChildren() {
   return children;
  }
  
  public TreeNode getParent() {
   return parent;
  }
  
  public TreeNode addChild(int[] move) {
     TreeNode node = new TreeNode(this);
     node.setMove(move);
     this.getChildren().add(node);
     return node;
  }
  
  public TreeNode addChild(int[] move, char c) {
	     TreeNode node = new TreeNode(this);
	     node.setColor(c);
	     node.setMove(move);
	     this.getChildren().add(node);
	     return node;
	  }
  
  public void printTree(String appender) {
	  for(int x:getMove())
		  System.out.print(appender + x);
	  System.out.println();
     for (TreeNode each : this.getChildren()) {
      each.printTree(" " + appender);
     }
  }
  
  //used in printAdviceTree
  private void printParentMove(){
	  
	  if(parent.getColor()=='b' || parent.getColor()=='w')
		  parent.printParentMove();
	  System.out.print("( ");
	  for(int x:getMove())
		  System.out.print(x+" ");
	  System.out.print(")");
	  
  } 
  // print the best result computed by ai
  public void printAdviceTree(int height) {
	  if(height==0 || this.getSubNodeNum()==0){
		  printParentMove();
		  System.out.println();
		  return;
	  }
	  int max=-9999;
      for(TreeNode each : this.getChildren()) {
    	 if(each.result!=-9999 && each.result>max)
    		 max=each.result;
     }
      for(TreeNode each : this.getChildren()) 
    	 if(each.result==max)
	   		  each.printAdviceTree(height-1);
     
  }
  
}