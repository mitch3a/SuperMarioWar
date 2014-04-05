package smw.menu;

public class MenuItem {
  
  /** Defines the type of menu items. */
  public enum ItemType {PIPE_GREEN, PIPE_GRAY, PLAYER_SELECT, PLAYER_SELECT_BUTTON};
  
  /** Encapsulates all data needed for a menu item. */
  public ItemType type;
  public String label;
  public int length;
  public int x;
  public int y;
  public boolean centerText;
  
  // TODO - need a way to store data and/or an action (I guess) when clicked
  public int data;

  public MenuItem(ItemType type, String label, int length, int x, int y, boolean centerText) {
    this.type = type;
    this.label = label;
    this.length = length;
    this.x = x;
    this.y = y;
    this.centerText = centerText;
  }
  
  public MenuItem(ItemType type, String label, int length, int x, int y, boolean centerText, int data) {
    this.type = type;
    this.label = label;
    this.length = length;
    this.x = x;
    this.y = y;
    this.centerText = centerText;
    this.data = data;
  }
}
