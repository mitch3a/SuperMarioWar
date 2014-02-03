package smw.sound;

import java.io.File;
import java.net.MalformedURLException;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

/**
 * Plays all the BGM and sounds in the game (including menus).
 * 
 * Note: SMW sound files use wav and ogg. Unfortunately ogg is not natively supported in Java.
 * Can use an open source 3D sound library by Paul Lamb (http://www.paulscode.com) to play wav and ogg:
 * http://www.java-gaming.org/index.php?PHPSESSID=621j9bs588amcpfnnh1g4ejl96&/topic,20271.0.html
 * Don't actually need 3D sound, but the library seems easy enough to use. 
 * 
 * This software is based on or using the Lightweight Java Game Library Project.
 * Copyright (c) 2002-2008 Lightweight Java Game Library Project
 * All rights reserved.
 */
public class SoundPlayer {
  
  public static final String BGM = "BGM";
  
  private SoundSystem soundSystem = null;
  
  private float masterVol = 1.0f;
  private float sfxVol = 1.0f;
  private float bgmVol = 0.33f;
  
  /** The list of BGM that will be indexed by a value from the map file. */
  private String[][] bgmList;
  
  private int trackList;
  
  public SoundPlayer() {
    setupBGMList();
    
    // TODO - figure out why the library compatibility check isn't working
    // We want to use LibraryLWJGLOpenAL if compatible on the current machine (Java Sound sounds crappy apparently)
    // Should be checking for library compatibility before attempting to construct the sound system object!
    //boolean aLCompatible = SoundSystem.libraryCompatible( LibraryLWJGLOpenAL.class ); 
    //boolean jSCompatible = SoundSystem.libraryCompatible( LibraryJavaSound.class );
        
    try {
      soundSystem = new SoundSystem(LibraryLWJGLOpenAL.class);

      SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      SoundSystemConfig.setCodec("wav", CodecWav.class);
      
      setMasterVolume(masterVol);
    }
    catch(SoundSystemException e)
    {
      System.err.println("error linking with the plug-ins" );
    }
  }
  
  /** Sets up the BGM track list for each music category number that exists in a map file. */
  private void setupBGMList() {
    bgmList = new String[4][];
    bgmList[0] = new String[]{"smb3level1.ogg", "M2_Level1.ogg"}; // [Desert][Clouds][Snow][Platforms]
    bgmList[1] = new String[]{"M1_Underground.ogg", "M3_Boss.ogg"}; // [Ghost]
    bgmList[2] = new String[]{"M3_Underwater.ogg"}; // [Bonus]
    bgmList[3] = new String[]{"M3_Boss.ogg"}; // [Battle][Castle]
  }
  
  /** Sets up the track list based on the provided music category from the world. */
  public void setTrackList(int musicCategory) {
    // Default category mapping according to Music.txt
    switch (musicCategory) {
      // Land
      case 0:
      case 2:
      case 8:
      case 9:
      case 11:
      case 13:
      case 14:
      case 24:
        trackList = 0;
        break;
      // Castle
      case 1:
      case 6:
      case 23:
        trackList = 3;
        break;
      // Ghost
      case 3:
        trackList = 1;
        break;
      // Underground
      case 4:
      case 10: 
      case 17: 
      case 18:
        trackList = 1;
        break;
      // Platforms
      case 5: 
      case 7:
      case 12:
      case 16:
      case 20:
      case 25:
        trackList = 0;
        break;
      // Battle
      case 15:
      case 21:
        trackList = 3;
        break;
      // Bonus
      case 19:
      case 22: 
        trackList = 2;
      default:
        trackList = 0;
        break;
    }
  }
  
  /** Plays background music based on the current track list. */
  public void playBGM() {
    playBGM(bgmList[trackList][(int) (Math.random() * (bgmList[trackList].length))]);
  }
  
  /** Plays the provided background music. 
   * @param name File name located in music dir.
   */
  public void playBGM(String name) {
    File f = new File("res/music/game/Standard/" + name);
    if (f.exists()) {
      try {        
        soundSystem.backgroundMusic(BGM, f.toURI().toURL(), name, true);
        soundSystem.setVolume(BGM, 0.35f);        
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }
  
  /** Pauses the sound. */
  public void pause() {
    soundSystem.pause(BGM);
  }
  
  /** Resumes playing music. */
  public void resume() {
    soundSystem.play(BGM);
  }
  
  /** Plays the specified sound effect.
   * @param name File name of sound effect. 
   */
  public void playSfx(String name) {
    try {      
      soundSystem.newSource(false, name, new File("res/sfx/packs/Classic/" + name).toURI().toURL(), name, false, 0, 0, 0,
        SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff());
      soundSystem.setPriority(name, false);
      soundSystem.setPosition(name, 0, 0, 0);
      soundSystem.setAttenuation(name, SoundSystemConfig.ATTENUATION_ROLLOFF);
      soundSystem.setDistOrRoll(name, SoundSystemConfig.getDefaultRolloff());
      soundSystem.setPitch(name, 1.0f);
      soundSystem.setVolume(name, sfxVol);      
      soundSystem.play(name);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }
  
  /** This should be called when exiting the game. */
  public void shutDown() {
    if (soundSystem != null) {
      soundSystem.cleanup();
    }
  }
  
  /** Returns background music volume setting. */
  public float getBGMVolume() {
    return bgmVol;
  }
  
  /** Sets background music volume setting. */
  public void setBGMVolume(float level) {
    bgmVol = level;
    soundSystem.setVolume(BGM, bgmVol);
  }
  
  /** Returns sound effects volume setting. */
  public float getSFXVolume() {
    return sfxVol;
  }
  
  /** Sets sound effects volume setting. */
  public void setSFXVolume(float level) {
    sfxVol = level;
  }
  
  /** Returns master volume setting. */ 
  public float getMasterVolume() {
    return masterVol;
  }
  
  /** Sets master volume setting. */
  public void setMasterVolume(float level) {
    masterVol = level;
    soundSystem.setMasterVolume(masterVol);
  }  
}
