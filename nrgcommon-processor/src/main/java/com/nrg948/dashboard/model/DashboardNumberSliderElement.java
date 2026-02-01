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

/** Represents a dashboard element that provides a number slider visualization. */
public class DashboardNumberSliderElement extends DashboardWidgetElement {
  private final double min;
  private final double max;
  private final int divisions;
  private final DataBinding dataBinding;
  private final boolean publishWhileDragging;

  /**
   * Creates a new DashboardNumberSliderElement.
   *
   * @param title the title of the slider
   * @param column the column position of the slider
   * @param row the row position of the slider
   * @param width the width of the slider
   * @param height the height of the slider
   * @param min the minimum value of the slider
   * @param max the maximum value of the slider
   * @param divisions the number of divisions in the slider
   * @param dataBinding the data binding for the slider
   * @param publishWhileDragging whether to publish values while dragging
   */
  public DashboardNumberSliderElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      double min,
      double max,
      int divisions,
      DataBinding dataBinding,
      boolean publishWhileDragging) {
    super(title, column, row, width, height);
    this.min = min;
    this.max = max;
    this.divisions = divisions;
    this.dataBinding = dataBinding;
    this.publishWhileDragging = publishWhileDragging;
  }

  @Override
  public String getType() {
    return "Number Slider";
  }

  /** {@return the minimum value of the slider} */
  public double getMin() {
    return min;
  }

  /** {@return the maximum value of the slider} */
  public double getMax() {
    return max;
  }

  /** {@return the number of divisions in the slider} */
  public int getDivisions() {
    return divisions;
  }

  /** {@return the data binding of the slider} */
  public DataBinding getDataBinding() {
    return dataBinding;
  }

  /** {@return whether the slider publishes values while dragging} */
  public boolean publishWhileDragging() {
    return publishWhileDragging;
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }
}
