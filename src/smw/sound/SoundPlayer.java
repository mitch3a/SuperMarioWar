package smw.sound;

import java.io.File;
import java.net.MalformedURLException;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import smw.Utilities;
import smw.settings.Settings;
import smw.settings.MusicAndSoundSettings.Volume;
import smw.world.World;

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
  /** Identifier for controlling background music playback. */
  private static final String BGM = "BGM";
  /** The sound system instance. */
  private SoundSystem soundSystem = null;
  /** The master volume. */
  private float masterVol = 1.0f;
  /** The sound effect volume. */
  private float sfxVol = 1.0f;
  /** The background music volume. */
  private float bgmVol = 0.33f;
  /** The list of BGM that will be indexed by a value from the map file. */
  private String[][] bgmList;
  /** The track list number for the current world. */
  private int trackList;
  
  /** Constructs the sound player. */
  public SoundPlayer() {
	  try {
      soundSystem = SoundSystem.libraryCompatible(LibraryLWJGLOpenAL.class) ?
        new SoundSystem(LibraryLWJGLOpenAL.class) :
        new SoundSystem();
        
      SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      SoundSystemConfig.setCodec("wav", CodecWav.class);
    } catch (SoundSystemException e) {
      System.err.println("Error linking sound plugins!");
    }

    Settings settings = Settings.getInstance();
    settings.saveSettings();
    setMasterVolume(1.0f);
    setBGMVolume(Volume.getValue(settings.getMusicAndSound().getMusicVolume()));
    setSFXVolume(Volume.getValue(settings.getMusicAndSound().getSoundVolume()));
    
    setupBGMList();
    setupSfx();
  }
  
  /** Sets up the BGM track list for each music category number that exists in a map file. */
  private void setupBGMList() {
    // TODO - eventually setup based on read from a config file like \res\music\game\Standard\Music.txt
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
    	try {
        soundSystem.backgroundMusic(BGM, f.toURI().toURL(), name, true);
        soundSystem.setVolume(BGM, 0.35f);
    	} catch (NullPointerException e) {
          e.printStackTrace();
    	}
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }
  
  // TODO - is this what we want? Should stop other stuff that's playing if any?
  public void playMenuMusic() {
    File f = new File("res/music/game/Standard/Menu/menu.ogg");
    if (f.exists()) {
      try {        
        if (soundSystem.playing(BGM)) { // TODO - DO WE NEED THIS?
          soundSystem.stop(BGM);
        }
        soundSystem.backgroundMusic(BGM, f.toURI().toURL(), "menu.ogg", true);
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
  private void playSfx(String name) {
    if (soundSystem.playing(name)) {
      soundSystem.stop(name);
    }
    // To adjust volume always need to set it because volume is tied to each sound.
    soundSystem.setVolume(name, sfxVol);
    soundSystem.play(name);
  }
  
  /** Sets up the sound effects based on the classic sound pack. */
  public void setupSfx() {
    File[] listOfFiles = new File(World.class.getClassLoader().getResource("sfx/packs/Classic").getFile()).listFiles();
    for(File f : listOfFiles) {
      String name = f.getName();
      try {
        soundSystem.newSource(false, name, f.toURI().toURL(), name, false, 0, 0, 0,
          SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff());
        soundSystem.setPriority(name, false);
        soundSystem.setPosition(name, 0, 0, 0);
        soundSystem.setAttenuation(name, SoundSystemConfig.ATTENUATION_ROLLOFF);
        soundSystem.setDistOrRoll(name, SoundSystemConfig.getDefaultRolloff());
        soundSystem.setPitch(name, 1.0f);
        soundSystem.setVolume(name, sfxVol);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }
  
  /** This should be called when exiting the game. */
  public void shutDown() {
    if (soundSystem != null) {
      Utilities.disableOutput();
      soundSystem.cleanup();
      Utilities.enableOutput();
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
  
  // Helper functions to play each sound effect (note: these need to exist in the sfx pack resource dir).
  public void sfxMip() {
    playSfx("mip.wav");
  }
  public void sfxDeath() {
    playSfx("death.wav");
  }
  public void sfxJump() {
    playSfx("jump.wav");
  }
  public void sfxSkid() {
    playSfx("skid.wav");
  }
  public void sfxCapeJump() {
    playSfx("capejump.wav");
  }
  public void sfxInvincible() {
    playSfx("invincible.wav");
  }
  public void sfx1Up() {
    playSfx("1up.wav");
  }
  public void sfxSprout() {
    playSfx("sprout.wav");
  }
  public void sfxCollectPowerup() {
    playSfx("collectpowerup.wav");
  }
  public void sfxFeather() {
    playSfx("feather.wav");
  }
  public void sfxTail() {
    playSfx("tail.wav");
  }
  public void sfxStoreItem() {
    playSfx("storeitem.wav");
  }
  public void sfxBreakBlock() {
    playSfx("breakblock.wav");
  }
  public void sfxBump() {
    playSfx("bump.wav");
  }
  public void sfxCoin() {
    playSfx("coin.wav");
  }
  public void sfxFireball() {
    playSfx("fireball.wav");
  }
  public void sfxSpringJump() {
    playSfx("springjump.wav");
  }
  public void sfxTimeWarning() {
    playSfx("timewarning.wav");
  }
  public void sfxHit() {
    playSfx("hit.wav");
  }
  public void sfxChicken() {
    playSfx("chicken.wav");
  }
  public void sfxTransform() {
    playSfx("transform.wav");
  }
  public void sfxYoshi() {
    playSfx("yoshi.wav");
  }
  public void sfxPause() {
    playSfx("pause.wav");
  }
  public void sfxBobomb() {
    playSfx("bob-omb.wav");
  }
  public void sfxDCoin() {
    playSfx("dcoin.wav");
  }
  public void sfxCannon() {
    playSfx("cannon.wav");
  }
  public void sfxBurnup() {
    playSfx("burnup.wav");
  }
  public void sfxWarp() {
    playSfx("warp.wav");
  }
  public void sfxThunder() {
    playSfx("thunder.wav");
  }
  public void sfxClock() {
    playSfx("clock.wav");
  }
  public void sfxSlowDown() {
    playSfx("slowdown.wav");
  }
  public void sfxStoredPowerup() {
    playSfx("storedpowerup.wav");
  }
  public void sfxKick() {
    playSfx("kick.wav");
  }
  public void sfxRace() {
    playSfx("race.wav");
  }
  public void sfxBulletBill() {
    playSfx("bulletbill.wav");
  }
  public void sfxBoomerang() {
    playSfx("boomerang.wav");
  }
  public void sfxSpit() {
    playSfx("spit.wav");
  }
  public void sfxStarWarning() {
    playSfx("starwarning.wav");
  }
  public void sfxPowerDown() {
    playSfx("powerdown.wav");
  }
  public void sfxSwitchPress() {
    playSfx("switchpress.wav");
  }
  public void sfxSuperSpring() {
    playSfx("superspring.wav");
  }
  public void sfxStun() {
    playSfx("stun.wav");
  }
  public void sfxInventory() {
    playSfx("inventory.wav");
  }
  public void sfxMapMove() {
    playSfx("mapmove.wav");
  }
  public void sfxTreasureChest() {
    playSfx("treasurechest.wav");
  }
  public void sfxFlameCannon() {
    playSfx("flamecannon.wav");
  }
  public void sfxWand() {
    playSfx("wand.wav");
  }
  public void sfxEnterStage() {
    playSfx("enter-stage.wav");
  }
  public void sfxGameover() {
    playSfx("gameover.wav");
  }
  public void sfxPickup() {
    playSfx("pickup.wav");
  }
}
