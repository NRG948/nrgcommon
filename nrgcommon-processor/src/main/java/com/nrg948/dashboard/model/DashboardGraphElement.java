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

/** Represents a dashboard element that displays a graph of data over time. */
public class DashboardGraphElement extends DashboardWidgetElement {
  private final double duration;
  private final double min;
  private final double max;
  private final Color8Bit color;
  private final double strokeWidth;

  /**
   * Creates a new DashboardGraphElement.
   *
   * @param title the title of the dashboard graph element
   * @param column the column position of the dashboard graph element
   * @param row the row position of the dashboard graph element
   * @param width the width of the dashboard graph element
   * @param height the height of the dashboard graph element
   * @param duration the duration of the graph in seconds
   * @param min the minimum value of the graph
   * @param max the maximum value of the graph
   * @param color the color of the graph
   * @param strokeWidth the stroke width of the graph
   */
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

  /**
   * Creates a new DashboardGraphElement.
   *
   * @param title the title of the dashboard graph element
   * @param column the column position of the dashboard graph element
   * @param row the row position of the dashboard graph element
   * @param width the width of the dashboard graph element
   * @param height the height of the dashboard graph element
   * @param duration the duration of the graph in seconds
   * @param min the minimum value of the graph
   * @param max the maximum value of the graph
   * @param color the color of the graph as a string
   * @param strokeWidth the stroke width of the graph
   */
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

  /** {@return the duration of the graph in seconds} */
  public double getDuration() {
    return duration;
  }

  /** {@return the minimum value of the graph} */
  public double getMin() {
    return min;
  }

  /** {@return the maximum value of the graph} */
  public double getMax() {
    return max;
  }

  /** {@return the color of the graph} */
  public Color8Bit getColor() {
    return color;
  }

  /** {@return the stroke width of the graph} */
  public double getStrokeWidth() {
    return strokeWidth;
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }
}
