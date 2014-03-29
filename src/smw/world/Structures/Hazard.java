package smw.world.Structures;

import smw.world.Tile;
import smw.world.World;
import smw.world.hazards.AnimatedHazard;

import smw.world.hazards.BulletBillyCannon;
import smw.world.hazards.FireBallPole;
import smw.world.hazards.OilFlame;

/* 
 * TODO mk justify why we need this....
 */
public class Hazard {
  final static int NUMMAPHAZARDPARAMS = 5;
  short type;
  short x;
  short y;
  short[] iparam  = new short[NUMMAPHAZARDPARAMS];
  float[] dparam = new float[NUMMAPHAZARDPARAMS];

  public Hazard(){

  }
  
  //TODO design of this is terrible but will work for now
  AnimatedHazard getAnimatedHazard(World world){
    int xParam = x*Tile.SIZE/2;
    int yParam = y*Tile.SIZE/2;
    
    switch(type){
      case 0: return new FireBallPole(xParam, yParam, iparam[0], dparam[0]);
      //TODO not sure i love this but this doesn't really fit under animated hazard
      case 2: BulletBillyCannon temp = new BulletBillyCannon(xParam, yParam, iparam[0], dparam[0]);
              world.addUpdatable(temp);
              world.addMovingCollidable(temp);
              world.addDrawable(temp, 3);
              break;
      case 3: if(iparam[1] == 1){
                return new OilFlame.OilFlameRightToLeft(xParam, yParam);
              }
              else{
                return new OilFlame.OilFlameLeftToRight(xParam, yParam);
              }
    }
    
    return null;
  }
}
