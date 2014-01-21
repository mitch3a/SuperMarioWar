package smw.ui;

import java.awt.event.KeyEvent;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class GamePad  extends PlayerControl{
  public static final int NUM_BUTTONS = 13;
  final Controller controller;
  final Component[] controls;
  int left;
  int right;
  int jump;
  int run;
  
  public GamePad(){
    Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

    for(int i =0;i<ca.length;i++){
      System.out.println(ca[i].getName());
    }
    controller = ca[3];//TODO
    controls = controller.getComponents();
    if(Controller.Type.STICK == controller.getType()){
      
    }
  }
  
  void sleep(int ms){
    try{
      Thread.sleep(ms);
    }
    catch(Exception e){
      //TODO log something
      System.out.println(e.toString());
    }
  }
  
  int waitForButton(){
    while(true){
      controller.poll();
      Component[] comps = controller.getComponents();
      for(int i = 0 ; i < comps.length ; ++i){
        float value = comps[i].getPollData();
        if( value == 1.0f || value == -1.0f){
          while(comps[i].getPollData() == value){
            //wait until they release the button
            sleep(50);
            controller.poll();
          }
          return i;
        }
      }
      sleep(50);
    }
  }
  
  public void printState(){
    controller.poll();
    Component[] comps = controller.getComponents();
    for(int i = 0 ; i < comps.length ; ++i){
      System.out.print(", " + comps[i].getPollData());
    }
    System.out.println("");
  }

  //TODO right now setLeft/setRight are a little flimsy. Just wanted to get things working
  @Override
  public void setLeft() {
    left = waitForButton();
  }

  @Override
  public void setRight() {
    right = waitForButton();
  }

  @Override
  public void setJump() {
    jump = waitForButton();
  }

  @Override
  public void setRun() {
    run = waitForButton();
  }

  @Override
  public float getDirection() {
    float d = controls[right].getPollData();
    if( d > .5f){
      d = 1.0f;
    }
    else if ( d < -.5f){
      d = -1.0f;
    }
    else{
      d = 0;
    }
    
    return d;
  }

  @Override
  public boolean isJumping() {
    return controls[jump].getPollData() == 1.0f;
  }

  @Override
  public boolean isRunning() {
    return controls[run].getPollData() == 1.0f;
  }

  //TODO this stuff is only there because i wanted to build
  @Override
  public void keyPressed(KeyEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void keyReleased(KeyEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void keyTyped(KeyEvent arg0) {
    // TODO Auto-generated method stub
    
  }
}
