package smw.world;

/* 
 * TODO mk justify why we need this....
 */
public class Hazard {
  final static int NUMMAPHAZARDPARAMS = 5;
  short type;
  short x;
  short y;
  short[] iparam;
  float[] dparam;

  public Hazard(){
    //TODO mk YIKES
    iparam = new short[NUMMAPHAZARDPARAMS];
    dparam = new float[NUMMAPHAZARDPARAMS];
  }
}
