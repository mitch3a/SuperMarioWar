package smw.world.Structures;

import smw.world.Tile;
import smw.world.World;
import smw.world.hazards.AnimatedHazard;
import smw.world.hazards.BulletBillyCannon;
import smw.world.hazards.FireBallPole;
import smw.world.hazards.FirePirhanaPlant;
import smw.world.hazards.OilFlame;
import smw.world.hazards.PirhanaPlant;
import smw.world.hazards.RotoDisc;

/* 
 * TODO mk justify why we need this....
 */
public class Hazard {
  final static int NUMMAPHAZARDPARAMS = 5;
  short type;
  short tileX;
  short tileY;
  short[] iparam  = new short[NUMMAPHAZARDPARAMS];
  float[] dparam = new float[NUMMAPHAZARDPARAMS];

  public Hazard(){

  }
  
  //TODO design of this is terrible but will work for now
  AnimatedHazard getAnimatedHazard(World world){
    int xParam = tileX*Tile.SIZE/2;
    int yParam = tileY*Tile.SIZE/2;
    
    switch(type){
      case 0: return new FireBallPole(xParam, yParam, iparam[0], dparam[0]);
      case 1: return new RotoDisc(xParam, yParam, dparam[0], dparam[1], dparam[2], (int)iparam[0]);        
      //TODO not sure i love this but this doesn't really fit under animated hazard
      case 2: BulletBillyCannon temp = new BulletBillyCannon(xParam, yParam, iparam[0], dparam[0]);
              world.addUpdatable(temp);
              world.addMovingCollidable(temp);
              world.addDrawable(temp, 3);
      case 3: if(iparam[1] == 1){
                return new OilFlame.OilFlameRightToLeft(xParam, yParam);
              }
              else{
                return new OilFlame.OilFlameLeftToRight(xParam, yParam);
              }
      case 4: if(iparam[1] == 3){
                return new FirePirhanaPlant.GreenRight(xParam, yParam);
              }
              else if(iparam[1] == 2){
                return new FirePirhanaPlant.GreenLeft(xParam, yParam);
              }
              else if(iparam[1] == 1){
                return new FirePirhanaPlant.GreenDown(xParam, yParam);
              }
              else{
                return new FirePirhanaPlant.GreenUp(xParam, yParam);
              }
      case 5: if(iparam[1] == 3){
                return new FirePirhanaPlant.RedRight(xParam, yParam);
              }
              else if(iparam[1] == 2){
                return new FirePirhanaPlant.RedLeft(xParam, yParam);
              }
              else if(iparam[1] == 1){
                return new FirePirhanaPlant.RedDown(xParam, yParam);
              }
              else{
                return new FirePirhanaPlant.RedUp(xParam, yParam);
              }
      case 6: if(iparam[1] == 3){
                return new PirhanaPlant.TallRight(xParam, yParam);
              }
              else if(iparam[1] == 2){
                return new PirhanaPlant.TallLeft(xParam, yParam);
              }
              else if(iparam[1] == 1){
                return new PirhanaPlant.TallDown(xParam, yParam);
              }
              else{
                return new PirhanaPlant.TallUp(xParam, yParam);
              }
      case 7: if(iparam[1] == 3){
                return new PirhanaPlant.ShortRight(xParam, yParam);
              }
              else if(iparam[1] == 2){
                return new PirhanaPlant.ShortLeft(xParam, yParam);
              }
              else if(iparam[1] == 1){
                return new PirhanaPlant.ShortDown(xParam, yParam);
              }
              else{
                return new PirhanaPlant.ShortUp(xParam, yParam);
              }
    }
    
    return null;
  }
}
