package smw.world;

public class WorldPreview extends World{
  //Top left point of where the image will be drawn
  public final int x, y;
  //Size of the image
  public final float scaleWidth, scaleHeight;
  
  public WorldPreview(String filename, int x, int y, float scaleWidth, float scaleHeight){
    super(filename);
    
    this.x = x;
    this.y = y;
    this.scaleWidth = scaleWidth;
    this.scaleHeight = scaleHeight;
  }
  
  public WorldPreview(int x, int y, float scaleWidth, float scaleHeight){
    super();
    
    this.x = x;
    this.y = y;
    this.scaleWidth = scaleWidth;
    this.scaleHeight = scaleHeight;
  }
}
