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

/** Represents a dashboard element that provides a voltage view visualization. */
public class DashboardVoltageViewElement extends DashboardWidgetElement {
  private final double min;
  private final double max;
  private final int divisions;
  private final boolean inverted;
  private final Orientation orientation;

  /**
   * Creates a new DashboardVoltageViewElement.
   *
   * @param title the title of the dashboard voltage view element
   * @param column the column position of the dashboard voltage view element
   * @param row the row position of the dashboard voltage view element
   * @param width the width of the dashboard voltage view element
   * @param height the height of the dashboard voltage view element
   * @param minVoltage the minimum voltage of the voltage view
   * @param maxVoltage the maximum voltage of the voltage view
   * @param divisions the number of divisions in the voltage view
   * @param inverted whether the voltage view is inverted
   * @param orientation the orientation of the voltage view
   */
  public DashboardVoltageViewElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      double minVoltage,
      double maxVoltage,
      int divisions,
      boolean inverted,
      Orientation orientation) {
    super(title, column, row, width, height);
    this.min = minVoltage;
    this.max = maxVoltage;
    this.divisions = divisions;
    this.inverted = inverted;
    this.orientation = orientation;
  }

  @Override
  public String getType() {
    return "Voltage View";
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }

  /** {@return the minimum value of the voltage view} */
  public double getMin() {
    return min;
  }

  /** {@return the maximum value of the voltage view} */
  public double getMax() {
    return max;
  }

  /** {@return the number of divisions in the voltage view} */
  public int getDivisions() {
    return divisions;
  }

  /** {@return whether the voltage view is inverted} */
  public boolean isInverted() {
    return inverted;
  }

  /** {@return the orientation of the voltage view} */
  public Orientation getOrientation() {
    return orientation;
  }
}
