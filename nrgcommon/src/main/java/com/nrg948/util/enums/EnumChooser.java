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
package com.nrg948.util.enums;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/** Utility class for creating SendableChoosers from enum types. */
public final class EnumChooser {
  /**
   * Creates a SendableChooser with the specified default value.
   *
   * @param defaultValue The default value for the chooser.
   * @param <E> The type of the enum.
   * @return A SendableChooser with the specified default value.
   */
  public static <E extends Enum<E>> SendableChooser<E> fromDefault(E defaultValue) {
    SendableChooser<E> chooser = new SendableChooser<>();

    for (E value : defaultValue.getDeclaringClass().getEnumConstants()) {
      chooser.addOption(value.toString(), value);
    }

    chooser.setDefaultOption(defaultValue.toString(), defaultValue);

    return chooser;
  }

  /**
   * Creates a SendableChooser from the specified enum class, using the first enum constant as the
   * default value.
   *
   * @param enumClass The enum class.
   * @param <E> The type of the enum.
   * @return A SendableChooser with the first enum constant as the default value.
   */
  public static <E extends Enum<E>> SendableChooser<E> fromClass(Class<E> enumClass) {
    return fromDefault(enumClass.getEnumConstants()[0]);
  }

  private EnumChooser() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
