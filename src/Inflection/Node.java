package Inflection;

import java.util.ArrayList;
import java.util.List;
 
public class Node {

  private int[] move;
  private int prob;
  public char color = ' ';
  public boolean isVisit;
  private final List<Node> children = new ArrayList<>();
  private final Node parent;
  
  public Node(Node parent) {
   this.parent = parent;
   this.move=new int[4];
   this.prob=1;
   this.isVisit=false;
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
  
  public void printTree(String appender) {
	  for(int x:getMove())
		  System.out.print(appender + x);
	  System.out.println();
     for (Node each : this.getChildren()) {
      each.printTree(" " + appender);
     }
  }
  
}