package smw.world.Structures;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

import smw.Collidable;
import smw.world.Tile;
import smw.world.TileSheetManager;
import smw.world.World;
import smw.world.MovingPlatform.EllipticalPath;
import smw.world.MovingPlatform.Path;
import smw.world.MovingPlatform.StraightContinuousPath;
import smw.world.MovingPlatform.StraightSegmentPath;
import smw.world.hazards.AnimatedHazard;
import smw.world.warps.WarpEntrance;
import smw.world.warps.WarpExit;

public class WorldBuffer implements AutoCloseable{

  MappedByteBuffer buffer;
  FileChannel fileChannel;
  RandomAccessFile file;
  
  public WorldBuffer(String worldName) throws Exception{
    file = new RandomAccessFile(this.getClass().getClassLoader().getResource("map/" + worldName).getPath().replaceAll("%20", " "), "r");
    fileChannel = file.getChannel();
    buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
    buffer.order(ByteOrder.LITTLE_ENDIAN); // Java defaults to BIG_ENDIAN, but MAP files were probably made on a x86 PC.
    buffer.load();
  }
  
  public void close() throws IOException{
    buffer.clear();
    fileChannel.close();
    file.close();
  }
  
  public boolean getAutoFilter() {
    return buffer.getInt() > 0;
  }
    
  public boolean getBoolean(){
    return (buffer.get() != 0);
  }
  
  public byte getByte(){
    return buffer.get();
  }
  
  public char getChar(){
    return (char)buffer.get();
  }
  
  public Collidable getCollidable(int x, int y) {
    short type =  getShort();
    return Collidable.getCollideable(type, x, y);
  }
  
  public DrawArea getDrawArea(){
    DrawArea drawArea = new DrawArea();
    
    drawArea.x = getInt();//TODO this might have to be forced signed
    drawArea.y = getInt();//TODO this might have to be forced signed
    drawArea.w = getInt();
    drawArea.h = getInt();
    
    return drawArea;
  }
  
  public FlagBaseLocation getFlagBaseLocation() {
    FlagBaseLocation flagBaseLocation = new FlagBaseLocation();
    
    flagBaseLocation.x = getShort();
    flagBaseLocation.y = getShort();
    
    return flagBaseLocation;
  }
  
  public float getFloat() {
    return buffer.getFloat();
  }

  public AnimatedHazard getHazard(World world){
    Hazard hazard = new Hazard();
    hazard.type = getShort();
    hazard.x    = getShort();
    hazard.y    = getShort();

    for(short j = 0; j < Hazard.NUMMAPHAZARDPARAMS; j++){
      hazard.iparam[j] = getShort();
    }
    
    for(short j = 0; j < Hazard.NUMMAPHAZARDPARAMS; j++){
      hazard.dparam[j] = getFloat();
    }
    
    return hazard.getAnimatedHazard(world);
  }
  
  public int getInt(){
    return buffer.getInt();
  }
  
  public Item getItem(){
    Item item = new Item();
    
    item.type = Item.getType(getInt());
    item.x    = getInt();
    item.y    = getInt();
    
    return item;
  }
  
  public RaceGoalLocation getRaceGoalLocation() {
    RaceGoalLocation raceGoalLocation = new RaceGoalLocation();
    
    raceGoalLocation.x = getShort();
    raceGoalLocation.y = getShort();
    
    return raceGoalLocation;
  }
  
  public short getShort() {
    return (short)buffer.getInt();
  }
  
  public SpawnArea getSpawnArea(){
    SpawnArea spawnArea = new SpawnArea();
    
    spawnArea.left   = getShort();
    spawnArea.top    = getShort();
    spawnArea.width  = getShort();
    spawnArea.height = getShort();
    spawnArea.size   = getShort();

    return spawnArea;
  }
  
  public String getString() {
    final int length = getInt();
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < length ; ++i) {
      char c = getChar();
      if(c != 0){
        stringBuilder.append(c);
      }
    }
    
    return stringBuilder.toString();
  }

  public Tile getTile(int x, int y, Map<Integer, String> tileSheetMap){
   
    int id              = (int)(buffer.get());
    int tileSheetColumn = (int)(buffer.get());
    int tileSheetRow    = (int)(buffer.get());
    
    return new Tile(x*Tile.SIZE, y*Tile.SIZE, id, tileSheetRow, tileSheetColumn, TileSheetManager.getInstance().getTileSheet("map/tilesheets/" + tileSheetMap.get(id) + "/large.png"));
  }
 
  public int getVersion() {
    int version = 0;
    for (int i = 0; i < 4; i++) {
      version = 10*version + buffer.getInt();
    }
    
    return version;
  }
  
  public WarpEntrance getWarp(int x, int y) {
    
    
    short direction = getShort();
    short connection = getShort();
    short id         = getShort();
    
    WarpEntrance warp;
    
    switch(direction){
      case 0:  warp = new WarpEntrance.Down(x, y, connection, id);
               break;
      case 1:  warp = new WarpEntrance.Left(x, y, connection, id);
               break;
      case 2:  warp = new WarpEntrance.Up(x, y, connection, id);
               break;
      default: warp = new WarpEntrance.Right(x, y, connection, id);
    }
    
    return warp;
  }
  
  public WarpExit getWarpExit(){
    
    short direction = getShort();
    short connection = getShort();
    
    short id = getShort();
    short column = getShort();
    short row    = getShort();

    short lockx = getShort();
    short locky = getShort();
    short warpx = getShort();
    short warpy = getShort();
    
    short numblocks = getShort();
    WarpExit warp;
    
    switch(direction){
      case 0:  warp = new WarpExit.Up(connection, id, column, row, lockx, locky, warpx, warpy);
               break;
      case 1:  warp = new WarpExit.Right(connection, id, column, row, lockx, locky, warpx, warpy);
               break;
      case 2:  warp = new WarpExit.Down(connection, id, column, row, lockx, locky, warpx, warpy);
               break;
      default: warp = new WarpExit.Left(connection, id, column, row, lockx, locky, warpx, warpy);
    }
    
    return warp;
  }
 
  public Path getPath(int type, int width, int height){
    Path result = null;
    
    ////////////////////////////////////////////////////////////////////////////////////
    //For some reason, original maps use a start/end based on the center of the object.
    //In the case of a straight path, it makes no sense so correcting it here. Also,
    //the velocities are given per frame, but in this code, it should be kept
    //in milliseconds (16 milliseconds a frame)
    int xOffset = (int)(width*Tile.SIZE)/2;
    int yOffset = (int)(height*Tile.SIZE)/2;
    
    if(type == 0){
      float startX = getFloat();
      float startY = getFloat();
      float endX = getFloat();
      float endY = getFloat();
      float velocity = getFloat()/16f;
      
      result = new StraightSegmentPath(velocity, startX - xOffset, startY - yOffset, endX - xOffset, endY - yOffset);
    }
    else if(type == 1){
      float startX = getFloat();
      float startY = getFloat();
      float angle = getFloat();
      float velocity = getFloat()/16f;
  
      result = new StraightContinuousPath(velocity, startX - xOffset, startY - yOffset, angle);
    }
    else if(type == 2){
      float radiusX = getFloat();
      float radiusY = getFloat();
      float centerX = getFloat();
      float centerY = getFloat();
      float angle   = getFloat();
      float velocity = getFloat()/16f;
  
      result = new EllipticalPath(velocity, angle, radiusX, radiusY, centerX - xOffset, centerY - yOffset);
    }
    
    return result;
  }
}
