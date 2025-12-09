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

import edu.wpi.first.wpilibj.util.Color8Bit;

/** Enum representing common colors with their corresponding {@link Color8Bit} values. */
public enum Colors {
  RED(0xFF, 0x00, 0x00),
  GREEN(0x00, 0xFF, 0x00),
  BLUE(0x00, 0x00, 0xFF),
  LIGHT_BLUE(0xAD, 0xD8, 0xE6),
  YELLOW(0xFF, 0xFF, 0x00),
  ORANGE(0xFF, 0xA5, 0x00),
  PURPLE(0x80, 0x00, 0x80),
  WHITE(0xFF, 0xFF, 0xFF),
  BLACK(0x00, 0x00, 0x00),
  GRAY(0x80, 0x80, 0x80),
  CYAN(0x00, 0xFF, 0xFF),
  MAGENTA(0xFF, 0x00, 0xFF);

  private Color8Bit color;

  /**
   * Constructs a Colors enum constant with the specified RGB values.
   *
   * @param red The red component (0x00-0xFF).
   * @param green The green component (0x00-0xFF).
   * @param blue The blue component (0x00-0xFF).
   */
  Colors(int red, int green, int blue) {
    this.color = new Color8Bit(red, green, blue);
  }

  /**
   * Gets the {@link Color8Bit} representation of the color.
   *
   * @return The Color8Bit value of the color.
   */
  public Color8Bit getColor() {
    return color;
  }

  /**
   * Converts the color to an ARGB integer with full opacity.
   *
   * @return The ARGB integer representation of the color.
   */
  public int toARGB() {
    return ColorUtil.colorToARGB(color);
  }

  /**
   * Converts the color to a hexadecimal string representation.
   *
   * @return The hexadecimal string representation of the color.
   */
  public String toHexString() {
    return color.toHexString();
  }
}
