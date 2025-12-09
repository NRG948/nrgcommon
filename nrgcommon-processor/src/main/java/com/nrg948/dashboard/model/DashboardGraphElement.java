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

public class DashboardGraphElement extends DashboardWidgetElement {
  private final double duration;
  private final double min;
  private final double max;
  private final Color8Bit color;
  private final double strokeWidth;

  public DashboardGraphElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      double duration,
      double min,
      double max,
      Color8Bit color,
      double strokeWidth) {
    super(title, column, row, width, height);
    this.duration = duration;
    this.min = min;
    this.max = max;
    this.color = color;
    this.strokeWidth = strokeWidth;
  }

  public DashboardGraphElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      double duration,
      double min,
      double max,
      String color,
      double strokeWidth) {
    this(title, column, row, width, height, duration, min, max, stringToColor(color), strokeWidth);
  }

  @Override
  public String getType() {
    return "Graph";
  }

  public double getDuration() {
    return duration;
  }

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  public Color8Bit getColor() {
    return color;
  }

  public double getStrokeWidth() {
    return strokeWidth;
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }
}
