package smw.world;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class WorldBuffer {

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
  
  public float getFloat() {
    return buffer.getFloat();
  }

  public Hazard getHazard(){
    Hazard hazard = new Hazard();
    hazard.type = (short)buffer.getInt();
    hazard.x    = (short)buffer.getInt();
    hazard.y    = (short)buffer.getInt();

    for(short j = 0; j < Hazard.NUMMAPHAZARDPARAMS; j++){
      hazard.iparam[j] = (short)buffer.getInt();
    }
    
    for(short j = 0; j < Hazard.NUMMAPHAZARDPARAMS; j++){
      hazard.dparam[j] = buffer.getFloat();
    }
    
    return hazard;
  }
  
  public int getInt(){
    return buffer.getInt();
  }
  
  public Item getItem(){
    Item item = new Item();
    
    item.type = Tile.getType(buffer.getInt());
    item.x    = buffer.getInt();
    item.y    = buffer.getInt();
    
    return item;
  }
  
  public short getShort() {
    return (short)buffer.getInt();
  }

  public SpecialTile getSpecialTile() {
    short type =  getShort();
    return new SpecialTile(type);
  }
  
  public String getString() {
    final int length = buffer.getInt();
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < length ; ++i) {
      char c = getChar();
      if(c != 0){
        stringBuilder.append(c);
      }
    }
    
    return stringBuilder.toString();
  }
  
  public Tile getTile(int x, int y){
    Tile tile = new Tile(x*Tile.SIZE, y*Tile.SIZE);
    
    tile.ID              = (int)(buffer.get());
    tile.tileSheetColumn = (int)(buffer.get());
    tile.tileSheetRow    = (int)(buffer.get());

    return tile;
  }
 
  public int getVersion() {
    int version = 0;
    for (int i = 0; i < 4; i++) {
      version = 10*version + buffer.getInt();
    }
    
    return version;
  }
  
  public Warp getWarp() {
    Warp warp = new Warp();
    
    warp.direction  = (short)buffer.getInt();
    warp.connection = (short)buffer.getInt();
    warp.id         = (short)buffer.getInt();
    
    return (warp.id != -1) ? warp : null;
  }
}
