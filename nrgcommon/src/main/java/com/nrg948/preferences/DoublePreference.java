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

/** A class representing a double floating-point value in the preferences store. */
public final class DoublePreference extends PreferenceValue {

  private final double defaultValue;

  /**
   * Constructs an instance of this class.
   *
   * @param group The preference value's group name. This is usually the subsystem or class that
   *     uses the value.
   * @param name The preferences value name.
   * @param defaultValue The value supplied when the preference does not exist in the preferences
   *     store.
   */
  public DoublePreference(String group, String name, double defaultValue) {
    super(group, name);
    this.defaultValue = defaultValue;

    Preferences.initDouble(getKey(), defaultValue);
  }

  /**
   * Returns the default value.
   *
   * @return The default value.
   */
  public double getDefaultValue() {
    return defaultValue;
  }

  /**
   * Returns the current value.
   *
   * @return The current value.
   */
  public double getValue() {
    return Preferences.getDouble(key, defaultValue);
  }

  /**
   * Sets the current value.
   *
   * @param value The value to set.
   */
  public void setValue(double value) {
    Preferences.setDouble(key, value);
  }

  @Override
  public void accept(PreferenceValueVisitor visitor) {
    visitor.visit(this);
  }
}
