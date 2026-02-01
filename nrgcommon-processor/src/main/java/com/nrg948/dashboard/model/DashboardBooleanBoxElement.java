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
package com.nrg948.dashboard.model;

import static com.nrg948.util.ColorUtil.stringToColor;

import edu.wpi.first.wpilibj.util.Color8Bit;

/**
 * Represents a dashboard element that displays a boolean value with customizable colors and icons.
 */
public class DashboardBooleanBoxElement extends DashboardWidgetElement {
  private final Color8Bit trueColor;
  private final TrueIcon trueIcon;
  private final Color8Bit falseColor;
  private final FalseIcon falseIcon;

  /**
   * Creates a new DashboardBooleanBoxElement.
   *
   * @param title the title of the boolean box element
   * @param column the column position of the element
   * @param row the row position of the element
   * @param width the width of the element
   * @param height the height of the element
   * @param trueColor the color to display when the value is true
   * @param trueIcon the icon to display when the value is true
   * @param falseColor the color to display when the value is false
   * @param falseIcon the icon to display when the value is false
   */
  public DashboardBooleanBoxElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      Color8Bit trueColor,
      TrueIcon trueIcon,
      Color8Bit falseColor,
      FalseIcon falseIcon) {
    super(title, column, row, width, height);
    this.trueColor = trueColor;
    this.trueIcon = trueIcon;
    this.falseColor = falseColor;
    this.falseIcon = falseIcon;
  }

  /**
   * Creates a new DashboardBooleanBoxElement.
   *
   * @param title the title of the boolean box element
   * @param column the column position of the element
   * @param row the row position of the element
   * @param width the width of the element
   * @param height the height of the element
   * @param trueColor the color to display when the value is true
   * @param trueIcon the icon to display when the value is true
   * @param falseColor the color to display when the value is false
   * @param falseIcon the icon to display when the value is false
   */
  public DashboardBooleanBoxElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      String trueColor,
      TrueIcon trueIcon,
      String falseColor,
      FalseIcon falseIcon) {
    this(
        title,
        column,
        row,
        width,
        height,
        stringToColor(trueColor),
        trueIcon,
        stringToColor(falseColor),
        falseIcon);
  }

  @Override
  public String getType() {
    return "Boolean Box";
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }

  /** {@return the color to display when the value is true} */
  public Color8Bit getTrueColor() {
    return trueColor;
  }

  /** {@return the icon to display when the value is true} */
  public TrueIcon getTrueIcon() {
    return trueIcon;
  }

  /** {@return the color to display when the value is false} */
  public Color8Bit getFalseColor() {
    return falseColor;
  }

  /** {@return the icon to display when the value is false} */
  public FalseIcon getFalseIcon() {
    return falseIcon;
  }
}
