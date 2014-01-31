package smw.sound;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
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
  
  private SoundSystem soundSystem = null;
  public static final String BGM = "BGM";
  
  public SoundPlayer() {
    
    // TODO - figure out why the library compatibility check isn't working
    // We want to use LibraryLWJGLOpenAL if compatible on the current machine (Java Sound sounds crappy apparently)
    boolean aLCompatible = SoundSystem.libraryCompatible( LibraryLWJGLOpenAL.class ); 
    boolean jSCompatible = SoundSystem.libraryCompatible( LibraryJavaSound.class );
    
    try {
      soundSystem = new SoundSystem(LibraryLWJGLOpenAL.class);

      SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      SoundSystemConfig.setCodec("wav", CodecWav.class);
    }
    catch(SoundSystemException e)
    {
      System.err.println("error linking with the plug-ins" );
    }
    
    // Set SFX path (by default SoundSystem will look in a packaged named "Sounds").
    SoundSystemConfig.setSoundFilesPackage(new File("res/sfx/packs/Classic").toURI().toString());
    
    // TODO - Setup volume to MAX for testing.
    soundSystem.setMasterVolume(1.0f);
    soundSystem.setVolume(BGM, 1.0f);
  }
  
  /** Plays the provided background music. 
   * @param name File name located in music dir.
   */
  public void playBGM(String name) {
    File f = new File("res/music/game/Standard/" + name);
    if (f.exists()) {
      try {
        URL path = f.toURI().toURL();
        System.out.println(path);
        
        soundSystem.backgroundMusic(BGM, path, name, true);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }
  
  /** Plays the specified sound effect.
   * @param name File name of sound effect. 
   */
  public void playSfx(String name) {
    try {
      soundSystem.quickPlay(false, new File("res/sfx/packs/Classic/" + name).toURI().toURL(), name, false, 0, 0, 0,
        SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }
  
  /** This should be called when exiting the game. */
  public void shutDown() {
    soundSystem.cleanup();
  }
}
