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

/** Represents a dashboard element that provides a radial gauge visualization. */
public class DashboardRadialGaugeElement extends DashboardWidgetElement {
  private final double min;
  private final double max;
  private final double startAngle;
  private final double endAngle;
  private final int numberOfLabels;
  private final boolean wrapValue;
  private final boolean showPointer;
  private final boolean showTickMarks;

  /**
   * Creates a new DashboardRadialGaugeElement.
   *
   * @param title the title of the dashboard radial gauge element
   * @param column the column position of the dashboard radial gauge element
   * @param row the row position of the dashboard radial gauge element
   * @param width the width of the dashboard radial gauge element
   * @param height the height of the dashboard radial gauge element
   * @param startAngle the start angle of the radial gauge
   * @param endAngle the end angle of the radial gauge
   * @param minValue the minimum value of the radial gauge
   * @param maxValue the maximum value of the radial gauge
   * @param numberOfLabels the number of labels on the radial gauge
   * @param wrapValue whether to wrap the value on the radial gauge
   * @param showPointer whether to show the pointer on the radial gauge
   * @param showTickMarks whether to show tick marks on the radial gauge
   */
  public DashboardRadialGaugeElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      double startAngle,
      double endAngle,
      double minValue,
      double maxValue,
      int numberOfLabels,
      boolean wrapValue,
      boolean showPointer,
      boolean showTickMarks) {
    super(title, column, row, width, height);
    this.min = minValue;
    this.max = maxValue;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
    this.numberOfLabels = numberOfLabels;
    this.wrapValue = wrapValue;
    this.showPointer = showPointer;
    this.showTickMarks = showTickMarks;
  }

  @Override
  public String getType() {
    return "Radial Gauge";
  }

  /** {@return the minimum value of the radial gauge} */
  public double getMin() {
    return min;
  }

  /** {@return the maximum value of the radial gauge} */
  public double getMax() {
    return max;
  }

  /** {@return the start angle of the radial gauge} */
  public double getStartAngle() {
    return startAngle;
  }

  /** {@return the end angle of the radial gauge} */
  public double getEndAngle() {
    return endAngle;
  }

  /** {@return the number of labels on the radial gauge} */
  public int getNumberOfLabels() {
    return numberOfLabels;
  }

  /** {@return whether the radial gauge wraps the value} */
  public boolean isWrapValue() {
    return wrapValue;
  }

  /** {@return whether the radial gauge shows the pointer} */
  public boolean showPointer() {
    return showPointer;
  }

  /** {@return whether the radial gauge shows tick marks} */
  public boolean showTickMarks() {
    return showTickMarks;
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }
}
