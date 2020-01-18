package registry;

import java.util.HashMap;
import java.util.Map;

/*
  Exam: Advance Programming
  Year: 2019/2020
  Author: Gaspare Ferraro
  Mail: ferraro@gaspa.re
  Mat: 520549
*/

public class KeyRegistry {

  private Map<Class, String> registry;

  /**
   * Create an empty key registry
   */
  public KeyRegistry() {
    registry = new HashMap<Class, String>();
  }

  /**
   * Add a new key key for the crypto algorithm class c
   * @param c Key class of the registry
   * @param key String key for the class c
   */
  public void add(Class c, String key) {
    this.registry.put(c, key);
  }

  /**
   * Get the last key of the type specified by c
   * @param c Key class of the registry
   * @return Last key of the type specified by c
   */
  public String get(Class c) {
    return this.registry.get(c);
  }

}
