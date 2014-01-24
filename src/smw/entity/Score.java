package smw.entity;

/*****************************************
 * This class will be an interface. 
 *****************************************/
public class Score {
  private int score;
  
  public Score(){
    score = 0;
  }
  
  public int getScore(){
    return score;
  }
  
  public void increaseScore(){
    ++score;
  }
  
  public void decreaseScore(){
    
  }
}
