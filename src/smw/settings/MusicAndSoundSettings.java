package smw.settings;

import lombok.Getter;
import lombok.Setter;

public class MusicAndSoundSettings implements SubSetting{
  static final String CATEGORY_NAME = "MusicAndSoundsSettings";
  
  public enum Volume{
    zero, one, two, three, four, five, six, seven, eight;
    
    public static float getValue(Volume v){
      switch(v){
        case zero:  return 0.0f;
        case one:   return 1/8.0f;
        case two:   return 2/8.0f;
        case three: return 3/8.0f;
        case four:  return 4/8.0f;
        case five:  return 5/8.0f;
        case six:   return 6/8.0f;
        case seven: return 7/8.0f;
      }
      
      return 1.0f;
    }
  }
  
  public enum Announcer{
    none;
  }
  
  public enum SoundPack{
    classic;
  }
  
  public enum MusicPack{
    standard;
  }
  
  static final KeyDefaultPairEnum<Volume> SOUND_VOLUME        = new KeyDefaultPairEnum<Volume>(CATEGORY_NAME + "." + "SoundVolume", Volume.eight);
  static final KeyDefaultPairEnum<Volume> MUSIC_VOLUME        = new KeyDefaultPairEnum<Volume>(CATEGORY_NAME + "." + "MusicVolume", Volume.four);
  static final KeyDefaultPair<Boolean> NEXT_MUSIC             = new KeyDefaultPair<Boolean>   (CATEGORY_NAME + "." + "NextMusic", true);
  static final KeyDefaultPairEnum<Announcer> ANNOUNCER        = new KeyDefaultPairEnum<Announcer>(CATEGORY_NAME + "." + "Announcer", Announcer.none);
  static final KeyDefaultPairEnum<SoundPack> SOUND_PACK       = new KeyDefaultPairEnum<SoundPack>(CATEGORY_NAME + "." + "SoundPack", SoundPack.classic);
  static final KeyDefaultPairEnum<MusicPack> GAME_MUSIC_PACK  = new KeyDefaultPairEnum<MusicPack>(CATEGORY_NAME + "." + "GameMusicPack", MusicPack.standard);
  static final KeyDefaultPairEnum<MusicPack> WORLD_MUSIC_PACK = new KeyDefaultPairEnum<MusicPack>(CATEGORY_NAME + "." + "WorldMusicPack", MusicPack.standard);
  
  @Getter @Setter Volume soundVolume;
  @Getter @Setter Volume musicVolume;
  @Getter @Setter boolean nextMusic;
  @Getter @Setter Announcer announcer;
  @Getter @Setter SoundPack soundPack;
  @Getter @Setter MusicPack gameMusicPack;
  @Getter @Setter MusicPack worldMusicPack;
  
  public MusicAndSoundSettings(PropertiesWrapper prop){
    soundVolume = prop.getEnum(SOUND_VOLUME);
    musicVolume = prop.getEnum(MUSIC_VOLUME);
    nextMusic = prop.getBoolean(NEXT_MUSIC);
    announcer = prop.getEnum(ANNOUNCER);
    soundPack = prop.getEnum(SOUND_PACK);
    gameMusicPack  = prop.getEnum(WORLD_MUSIC_PACK);
    worldMusicPack = prop.getEnum(WORLD_MUSIC_PACK);
  }
  
  @Override
  public void add(PropertiesWrapper prop) {
    prop.setProperty(SOUND_VOLUME, soundVolume);
    prop.setProperty(MUSIC_VOLUME, musicVolume);
    prop.setProperty(NEXT_MUSIC, nextMusic);
    prop.setProperty(ANNOUNCER, announcer);
    prop.setProperty(SOUND_PACK, soundPack);
    prop.setProperty(WORLD_MUSIC_PACK, gameMusicPack);
    prop.setProperty(WORLD_MUSIC_PACK, worldMusicPack);
  }
  
  @Override
  public String getCategoryName() {
    return CATEGORY_NAME;
  }

}
