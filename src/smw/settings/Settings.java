package smw.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import smw.settings.SettingTitles.Language;

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
  static final Logger logger = Logger.getLogger(Settings.class.getName());
  

  
  //For example settingTitles.getTitle(ItemSelectionSettings.UseSettingsFrom));
  @Getter SettingTitles settingTitles;
  
  @Getter @Setter GamePlaySettings gamePlay;
  @Getter @Setter TeamSettings team;
  @Getter @Setter ItemSelectionSettings itemSelection;
  @Getter @Setter ItemSettings item;
  @Getter @Setter WeaponsAndProjectilesSettings weaponsAndProjectiles;
  @Getter @Setter WeaponUseLimitsSettings weaponUseLimits;
  @Getter @Setter GraphicsSettings graphics;
  @Getter @Setter EyeCandySettings eyeCandy;
  @Getter @Setter MusicAndSoundSettings musicAndSound;
  
  public synchronized static Settings getInstance(Language language) {
    if (INSTANCE == null) {
      INSTANCE = new Settings(language);
    }

    return INSTANCE;
  }

  public synchronized static Settings getInstance(){
    return getInstance(Language.english);
  }
  
  private Settings(Language language) {
    PropertiesWrapper prop = new PropertiesWrapper();

    gamePlay = new GamePlaySettings(prop);
    team = new TeamSettings(prop);
    itemSelection = new ItemSelectionSettings(prop);
    item = new ItemSettings(prop);
    weaponsAndProjectiles = new WeaponsAndProjectilesSettings(prop);
    weaponUseLimits = new WeaponUseLimitsSettings(prop);
    graphics = new GraphicsSettings(prop);
    eyeCandy = new EyeCandySettings(prop);
    musicAndSound = new MusicAndSoundSettings(prop);
    
    setLanguage(language);    
  }
  
  public void setLanguage(Language language){
    settingTitles = new SettingTitles(language);
  }

  public synchronized void saveSettings() {
    //Want to store these in order
    PropertiesWrapper prop = new PropertiesWrapper();
  
    File f = new File(CONFIG_FILE);
    
    if(!f.exists()){
      try {
        f.createNewFile();
      } catch (IOException e) {
       logger.log(Level.SEVERE, "Cannot create Settings File: ", e);
      }
    }

    try (OutputStream output = new FileOutputStream(CONFIG_FILE)){
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
    } 
  }
}
