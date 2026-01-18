/*
  MIT License

  Copyright (c) 2026 Newport Robotics Group

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
*/
package com.nrg948.preferences;

import edu.wpi.first.wpilibj.Preferences;
import java.util.Map;
import java.util.Optional;

/** A class representing an enum value in the preferences store. */
public final class EnumPreference<E extends Enum<E>> extends PreferenceValue {

  private final E defaultValue;

  /**
   * Constructs an instance of this class.
   *
   * @param group The preference value's group name. This is usually the subsystem or class that
   *     uses the value.
   * @param name The preferences value name.
   * @param defaultValue The value supplied when the preference does not exist in the preferences
   *     store.
   */
  public EnumPreference(String group, String name, E defaultValue) {
    super(group, name);
    this.defaultValue = defaultValue;

    Preferences.initString(getKey(), defaultValue.name());
  }

  /**
   * Returns the default value.
   *
   * @return The default value.
   */
  public E getDefaultValue() {
    return defaultValue;
  }

  /**
   * Returns the current value.
   *
   * @return The current value.
   */
  public E getValue() {
    try {
      return E.valueOf(
          defaultValue.getDeclaringClass(), Preferences.getString(key, defaultValue.name()));
    } catch (IllegalArgumentException e) {
      return defaultValue;
    }
  }

  /**
   * Sets the current value.
   *
   * @param value The value to set.
   */
  public void setValue(E value) {
    Preferences.setString(key, value.name());
  }

  /**
   * Selects a value from a map using the current value as the key.
   *
   * @param map The map to select from.
   * @param <V> The type of the map's values.
   * @return An {@link Optional} containing the selected value, or an empty {@link Optional} if the
   *     key does not exist in the map.
   */
  public <V> Optional<V> select(Map<E, V> map) {
    return Optional.ofNullable(map.get(getValue()));
  }

  /**
   * Selects a value from a map using the current value as the key, returning a default value if the
   * key does not exist in the map.
   *
   * @param map The map to select from.
   * @param defaultValue The default value to return if the key does not exist in the map.
   * @param <V> The type of the map's values.
   * @return The selected value, or the default value if the key does not exist in the map.
   */
  public <V> V selectOrDefault(Map<E, V> map, V defaultValue) {
    return map.getOrDefault(getValue(), defaultValue);
  }

  @Override
  public void accept(PreferenceValueVisitor visitor) {
    visitor.visit(this);
  }
}
