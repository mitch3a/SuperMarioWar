package smw.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;

/**
 * Provides flags to indicate whether a given function/area of code is being
 * debugged. This includes enabling logging and disabling certain features of
 * the game. There are also some miscellaneous utilities in this class.
 */
public class Settings {  
  private static Settings INSTANCE;

  static final String CONFIG_FILE = "config.properties";
  
  @Getter @Setter GamePlaySettings gamePlay;
  @Getter @Setter TeamSettings team;
  @Getter @Setter ItemSelectionSettings itemSelection;
  @Getter @Setter ItemSettings item;
  @Getter @Setter WeaponsAndProjectilesSettings weaponsAndProjectiles;
  @Getter @Setter WeaponUseLimitsSettings weaponUseLimits;
  @Getter @Setter GraphicsSettings graphics;
  @Getter @Setter EyeCandySettings eyeCandy;
  @Getter @Setter MusicAndSoundSettings musicAndSound;
  
  public synchronized static Settings getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Settings();
    }

    return INSTANCE;
  }

  private Settings() {
    PropertiesWrapper prop;
    try {
      prop = new PropertiesWrapper();

      gamePlay = new GamePlaySettings(prop);
      team = new TeamSettings(prop);
      itemSelection = new ItemSelectionSettings(prop);
      item = new ItemSettings(prop);
      weaponsAndProjectiles = new WeaponsAndProjectilesSettings(prop);
      weaponUseLimits = new WeaponUseLimitsSettings(prop);
      graphics = new GraphicsSettings(prop);
      eyeCandy = new EyeCandySettings(prop);
      musicAndSound = new MusicAndSoundSettings(prop);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void saveSettings() {
    //Want to store these in order
    PropertiesWrapper prop = new PropertiesWrapper();
  
    OutputStream output = null;

    try {
      File f = new File(CONFIG_FILE);
      
      if(!f.exists()){
        f.createNewFile();
      }
      
      output = new FileOutputStream(CONFIG_FILE);

      gamePlay.add(prop);
      team.add(prop);
      itemSelection.add(prop);
      item.add(prop);
      weaponsAndProjectiles.add(prop);
      weaponUseLimits.add(prop);
      graphics.add(prop);
      eyeCandy.add(prop);
      musicAndSound.add(prop);

      prop.store(output);
      
    } catch (IOException io) {
      io.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
