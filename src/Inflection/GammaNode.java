package Inflection;

import java.util.ArrayList;
import java.util.List;
 
public class GammaNode {

  private int[] move;
  private int visitNum;
  private int winNum;
  private char color = ' ';
  public boolean isVisit;
  private int result;//若在模擬過程中確定了某個節點的勝負值，記錄之並更新父節點 , 1:win, -1:lose 
  private int winStones=0;
  private int infectNum=0;
  private boolean nonUseful=false;//we guess that if one has a attack breed, then non-attack breeds is non-useful.
  private boolean isPerfect=false;
  private final List<GammaNode> children = new ArrayList<>();
  private final GammaNode parent;
  
  public GammaNode(GammaNode parent) {
   this.parent = parent;
   this.move=new int[4];
   this.visitNum=0;
   this.winNum=0;
   this.result=0;
   this.isVisit=false;
  }
  
  public GammaNode deleteTree(){
	  if(!this.children.isEmpty())
		  for (GammaNode each : this.getChildren()) {
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
  
  public void addVisitNum() {
	  this.visitNum++;
  }
  
  public void addWinNum() {
	  this.winNum++;
  }
  
  public int getVisitNum() {
	   return visitNum;
  }
  
  public double getProb(){
	  if(result==-1)
		  return 0;
	  else if(result==1)
		  return 1;
	  else
		  return winNum/(double)visitNum;
  }
  
  public int getResult() {
	   return result;
 }
  
  public void markNonUseful(){
	  this.nonUseful=true;
  }
  
  //決定相信「初始值」或「模擬勝率」的權重
  private double adjustCoeff(){
	  return 1/Math.sqrt(visitNum);
  }
  
  private double iniScore(){
	  double score=0.5;
	  double x=(Math.abs(winStones)-1)/(double)10;
	  if(winStones>1)
		  score+=0.5*x*x;
	  else if(winStones<1)
		  score-=0.5*x*x;
	  if(this.isPerfect)
		  score+=0.1;
	  if(nonUseful)
		  score-=0.4;
	  else 
		  score+=0.01*infectNum;
	  return Math.max(0, Math.min(1,score));
  }
  
  private double score(){
	  return adjustCoeff()*iniScore()+(winNum/(double)visitNum)*(1-adjustCoeff());
  }
  
  public double getUCB(int TotolNum){
	  //In java, "log" means "ln".
	  double exploreConst=Math.sqrt(2);
	  if(result==-1)return 0;
	  if(visitNum==0)
		  return iniScore()+0.8+0.01*Math.random();
	  else return score()+exploreConst*Math.sqrt((Math.log(TotolNum)/Math.log(10))/visitNum);
  }
  
  public void setResult(int r) {
	  this.result=r;
  }
  
  public void setWinStones(int num) {
	  this.winStones=num;
  }
  
  public int getWinStones() {
	  return this.winStones;
  }
  
  public void setInfectNum(int num) {
	  this.infectNum=num;
  }
  
  public int getInfectNum() {
	  return this.infectNum;
  }
  
  public void markPerfectStep() {
	  this.isPerfect=true;
  }
  
  public int getSubNodeNum() {
	   int subNodeNum=0;
	   for(GammaNode nodes:this.getChildren())
		   subNodeNum++;
	   return subNodeNum;
  }
  
  //refresh its parent information
  public void refresh(char winColor , boolean refreshResult){
	  
	   this.addVisitNum();
	   if(this.color == winColor)
		   this.addWinNum();   
	   //若模擬對局過程中，確定了某個節點的勝負值，更新父節點勝負值
	   if(refreshResult){
		   if(this.getSubNodeNum()>0){
			   boolean isWinPt=true;
			   boolean isLosePt=false;
			   for(GammaNode nodes : this.getChildren()){
				   if(nodes.result!=-1)
					   isWinPt=false;
				   if(nodes.result==1)
					   isLosePt=true;
			   }
			   if(isWinPt)
				   this.result=1;
			   else if(isLosePt)
				   this.result=-1;
			   else
				   refreshResult=false;
		   }
	   }
	   
	   if(this.color=='b' || this.color=='w')
		   this.getParent().refresh(winColor,refreshResult);
  }
  
  
  public List<GammaNode> getChildren() {
	  return children;
  }
  
  public GammaNode getParent() {
	  return parent;
  }
  
  public GammaNode addChild(int[] move, char c) {
	     GammaNode node = new GammaNode(this);
	     node.setColor(c);
	     node.setMove(move);
	     this.getChildren().add(node);
	     return node;
  }
  
  public void printTree(String appender) {
	  for(int x:getMove())
		  System.out.print(appender + x);
	  System.out.println();
     for (GammaNode each : this.getChildren()) {
      each.printTree(" " + appender);
     }
  }
  
}