package smw.menu;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import smw.Game;
import smw.menu.Menu.ItemType;
import smw.menu.Menu.MenuItem;
import smw.menu.MenuInput.Action;

public class OptionsMenu extends Menu {

  /** List of menu items (a list of lists because there can be varying numbers of options on the X axis). */
  private List<List<MenuItem>> menuItems = new ArrayList<List<MenuItem>>();
  
  private int verticalOptionCount;
  private int selectionY;
  
  private Action lastAction = Action.NO_ACTION;
  
  OptionsMenu() {
    // Setup selectable items.
    ArrayList<MenuItem> optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Gameplay", 400, 120, 40));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Team", 400, 120, 80));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Item Selection", 400, 120, 120));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Item Settings", 400, 120, 160));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Weapons & Projectiles", 400, 120, 200));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Weapon Use Limits", 400, 120, 240));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Graphics", 400, 120, 280));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Eye Candy", 400, 120, 320));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Music & Sound", 400, 120, 360));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GREEN, "Refresh Maps", 400, 120, 400));
    menuItems.add(optionRow);
    
    optionRow = new ArrayList<MenuItem>();
    optionRow.add(new MenuItem(ItemType.PIPE_GRAY, "Back", 80, 544, 432));
    menuItems.add(optionRow);
    
    verticalOptionCount = menuItems.size();  
  }
  
  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    drawBackground(g, io);
    
    drawBlueField(g, "Options Menu", 630, 0, 0);

    // Figure out which item was selected.
    List<MenuItem> selRow = menuItems.get(selectionY);
    MenuItem selItem = selRow.get(0);
 
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
  }
  
  private void handleDown() {
    selectionY++;
    if (selectionY >= verticalOptionCount) {
      selectionY -= verticalOptionCount;
    }
  }
  
  private void handleSelect() {
    // TODO - need to select stuff!
  }

  private void handleEsc() {
    Game.setMenu(new TitleMenu());
  }

}
