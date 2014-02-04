package smw.entity;

/*****************************************
 * This class will be an interface. 
 *****************************************/
public class Score {
  private int score;
  
  public Score(){
    score = 10;
  }
  
  public int getScore(){
    return score;
  }
  
  public void increaseScore(){
    //Nothing for stomp em
  }
  
  public void decreaseScore(){
    --score;
  }

  public boolean isOut() {
    return score <= 0;
  }
}
