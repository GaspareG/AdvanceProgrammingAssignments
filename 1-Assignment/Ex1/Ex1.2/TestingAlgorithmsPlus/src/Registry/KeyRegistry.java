package Registry;

import java.util.HashMap;
import java.util.Map;

public class KeyRegistry {

  private Map<Class, String> registry;

  public KeyRegistry() {
    registry = new HashMap<Class, String>();
  }

  public void add(Class c, String key) {
    this.registry.put(c, key);
  }

  public String get(Class c) {
    return this.registry.get(c);
  }

}
