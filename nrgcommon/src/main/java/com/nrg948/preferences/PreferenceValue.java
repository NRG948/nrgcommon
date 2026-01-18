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
import java.util.ArrayList;

/** An abstract base class represented a keyed valued in the preferences store. */
public abstract class PreferenceValue {
  static final ArrayList<PreferenceValue> allValues = new ArrayList<>();

  /** The preferences value group name. */
  protected final String group;

  /** The preferences value name. */
  protected final String name;

  /** The preferences value key. */
  protected final String key;

  /**
   * Constructs an instance of this class.
   *
   * @param group The preference value's group name. This is usually the subsystem or class that
   *     uses the value.
   * @param name The preferences value name.
   */
  public PreferenceValue(String group, String name) {
    this.group = group;
    this.name = name;
    this.key = group + "/" + name;
    allValues.add(this);
  }

  /**
   * Visits all preference values with the given visitor.
   *
   * @param visitor The visitor to apply to all preference values.
   */
  public static void visitAll(PreferenceValueVisitor visitor) {
    for (var value : allValues) {
      value.accept(visitor);
    }
  }

  /**
   * Returns the this value's group name.
   *
   * @return The group name.
   */
  public String getGroup() {
    return group;
  }

  /**
   * Returns this value's name.
   *
   * @return The value name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns this value key used to access it in the preferences store. The value is generated from
   * a concatenation of the group name and value name separated by a forward slash ("/") character.
   *
   * @return The key name.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns whether the value exists in the preferences store.
   *
   * @return Whether the value exists in the preferences store.
   */
  public boolean exists() {
    return Preferences.containsKey(key);
  }

  /**
   * Accepts a visitor that operates on this value.
   *
   * @param visitor A visitor to operate on this value.
   */
  public abstract void accept(PreferenceValueVisitor visitor);
}
