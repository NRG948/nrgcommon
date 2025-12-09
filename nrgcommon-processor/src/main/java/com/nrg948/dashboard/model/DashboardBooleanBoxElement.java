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

public class DashboardBooleanBoxElement extends DashboardWidgetElement {
  private final Color8Bit trueColor;
  private final TrueIcon trueIcon;
  private final Color8Bit falseColor;
  private final FalseIcon falseIcon;

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

  public Color8Bit getTrueColor() {
    return trueColor;
  }

  public TrueIcon getTrueIcon() {
    return trueIcon;
  }

  public Color8Bit getFalseColor() {
    return falseColor;
  }

  public FalseIcon getFalseIcon() {
    return falseIcon;
  }
}
