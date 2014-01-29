package smw.level;

public class MapHazard {
  final static int NUMMAPHAZARDPARAMS = 5;
  short type;
  short x;
  short y;
  short[] iparam;
  float[] dparam;

  public MapHazard(){
    //TODO mk YIKES
    iparam = new short[NUMMAPHAZARDPARAMS];
    dparam = new float[NUMMAPHAZARDPARAMS];
  }
}
