/*
  MIT License

  Copyright (c) 2025 Newport Robotics Group

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
package com.nrg948.util;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

/** Utility class for color conversions and manipulations. */
public final class ColorUtil {
  /**
   * Packs individual ARGB components into a single integer.
   *
   * @param a The alpha component (0-255).
   * @param r The red component (0-255).
   * @param g The green component (0-255).
   * @param b The blue component (0-255).
   * @return The packed ARGB color as an integer.
   */
  public static int packARGB(int a, int r, int g, int b) {
    return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
  }

  /**
   * Converts a {@link Color8Bit} to an ARGB integer with full opacity.
   *
   * @param color The Color8Bit to convert.
   * @return The ARGB integer representation of the color.
   */
  public static int colorToARGB(Color8Bit color) {
    return packARGB(255, color.red, color.green, color.blue);
  }

  /**
   * Converts a {@link Color} to an ARGB integer with full opacity.
   *
   * @param color The Color to convert.
   * @return The ARGB integer representation of the color.
   */
  public static int colorToARGB(Color color) {
    return packARGB(
        255, (int) (color.red * 255), (int) (color.green * 255), (int) (color.blue * 255));
  }

  /**
   * Converts a color string to a {@link Color8Bit}. The color string can be in hexadecimal format
   * (e.g., "#RRGGBB") or a named color (e.g., "Red", "Blue").
   *
   * @param colorString The color string to convert.
   * @return The {@link Color8Bit} representation of the color.
   * @throws IllegalArgumentException If the color string is invalid.
   */
  public static Color8Bit stringToColor(String colorString) throws IllegalArgumentException {
    try {
      if (colorString.startsWith("#")) {
        return new Color8Bit(colorString);
      } else {
        return Colors.valueOf(colorString.replace(" ", "_").toUpperCase()).getColor();
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid color string: " + colorString, e);
    }
  }

  private ColorUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
