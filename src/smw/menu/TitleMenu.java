package smw.menu;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import smw.Game;
import smw.gfx.Font;
import smw.gfx.Palette;
import smw.menu.MenuInput.Action;

public class TitleMenu extends Menu {
  
  /** List of menu items (a list of lists because there can be varying numbers of options on the X axis). */
  private List<List<MenuItem>> menuItems = new ArrayList<List<MenuItem>>();
  
  /** The current user selection. */
  private int selectionY;
  private int selectionX;
  private int verticalOptionCount;
  
  /** Indicates if sound setup is needed for the menu. */
  private boolean setupSound = true; 
  
  /** The last user input action. */
  private Action lastAction;
    
  public TitleMenu() {
    ArrayList<MenuItem> optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Start", 310, 120, 210));
    optionRow.add(new MenuItem(ItemType.PIPE_GRAY, "Go!", 80, 440, 210));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PLAYER_SELECT, "Players", 400, 120, 250));
    // TODO - add player select buttons
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Options", 400, 120, 322));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Controls", 400, 120, 362));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Exit", 400, 120, 402));
    menuItems.add(optionRow);
    
    verticalOptionCount = menuItems.size();
  }

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    drawBackground(g, io);

    // Figure out which item was selected.
    List<MenuItem> selRow = menuItems.get(selectionY);
    MenuItem selItem = selRow.get(selectionX);
 
    for (List<MenuItem> row : menuItems) {
      for (MenuItem m : row) {
        boolean selected = selectionY == 2 ? selRow == row : selItem == m; 
        drawMenuItem(g, m, selected);
      }
    }
  }

  @Override
  public void update(double deltaTime_ms) {
    input.update();
    
    if (setupSound) {
      Game.soundPlayer.playMenuMusic();
      setupSound = false;
    }
        
    Action currentAction = input.getLastAction();
    if (currentAction != lastAction)
    {
      lastAction = currentAction;
      switch (lastAction) {
      case DOWN:
        handleDown();
        break;
      case ESC:
        handleEsc();
        break;
      case LEFT:
        handleLeft();
        break;
      case RIGHT:
        handleRight();
        break;
      case SELECT:
        handleSelect();
        break;
      case UP:
        handleUp();
        break;
      default:
        break;
      }
    }
  }
  
  private void handleUp() {
    selectionY--;
    if (selectionY < 0) {
      selectionY += verticalOptionCount;
    }
    selectionX = 0;
  }
  
  private void handleDown() {
    selectionY++;
    if (selectionY >= verticalOptionCount) {
      selectionY -= verticalOptionCount;
    }
    selectionX = 0;
  }
  
  private void handleLeft() {
    int size = menuItems.get(selectionY).size();
    if (size > 1) {
      selectionX--;
      if (selectionX < 0) {
        selectionX += size;
      }
    }
  }
  
  private void handleRight() {
    int size = menuItems.get(selectionY).size();
    if (size > 1) {
      selectionX++;
      if (selectionX >= size) {
        selectionX -= size;
      }
    }
  }
  
  private void handleSelect() {
    List<MenuItem> selRow = menuItems.get(selectionY);
    MenuItem selItem = selRow.get(selectionX);

    // TODO - select stuff ... string compare sucks, could attach a function pointer to a menu item, not sure how to do that in java...
    if (selItem.label == "Exit") {
      handleEsc();
    } else if (selItem.label == "Go!") {
      
    } else if (selItem.label == "Start") {
      
    } else if (selItem.label == "Players") {
      
    } else if (selItem.label == "Options") {
      Game.setMenu(new OptionsMenu());
    } else if (selItem.label == "Controls") {
      
    }
  }
  
  private void handleEsc() {
    Game.shutdown();
    System.exit(0);
  }
  
}