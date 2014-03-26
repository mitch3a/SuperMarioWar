package smw.settings;

import java.util.Properties;

public abstract interface SubSetting {
  String getCategoryName();
  void add(PropertiesWrapper prop);
}
