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

/** Represents a dashboard element that displays a number bar visualization. */
public class DashboardNumberBarElement extends DashboardWidgetElement {
  private final double min;
  private final double max;
  private final int divisions;
  private final boolean inverted;
  private final Orientation orientation;

  /**
   * Creates a new DashboardNumberBarElement.
   *
   * @param title the title of the dashboard number bar element
   * @param column the column position of the dashboard number bar element
   * @param row the row position of the dashboard number bar element
   * @param width the width of the dashboard number bar element
   * @param height the height of the dashboard number bar element
   * @param minValue the minimum value of the number bar
   * @param maxValue the maximum value of the number bar
   * @param divisions the number of divisions in the number bar
   * @param inverted whether the number bar is inverted
   * @param orientation the orientation of the number bar
   */
  public DashboardNumberBarElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      double minValue,
      double maxValue,
      int divisions,
      boolean inverted,
      Orientation orientation) {
    super(title, column, row, width, height);
    this.min = minValue;
    this.max = maxValue;
    this.divisions = divisions;
    this.inverted = inverted;
    this.orientation = orientation;
  }

  @Override
  public String getType() {
    return "Number Bar";
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }

  /** {@return the minimum value of the number bar} */
  public double getMin() {
    return min;
  }

  /** {@return the maximum value of the number bar} */
  public double getMax() {
    return max;
  }

  /** {@return the number of divisions in the number bar} */
  public int getDivisions() {
    return divisions;
  }

  /** {@return whether the number bar is inverted} */
  public boolean isInverted() {
    return inverted;
  }

  /** {@return the orientation of the number bar} */
  public Orientation getOrientation() {
    return orientation;
  }
}
