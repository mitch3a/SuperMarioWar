package smw.settings;

public abstract interface SubSetting {
  String getCategoryName();
  void add(PropertiesWrapper prop);
}
